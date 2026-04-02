-- =============================================================
-- V3__sprint2_hardening.sql
-- VeganApp — Sprint 2 Hardening
-- Índices de performance, CHECK constraints, auditoría, vistas
-- =============================================================

-- Cambiar timezone de la sesión a UTC para consistency
SET time_zone = '+00:00';

-- =============================================================
-- SECCIÓN A: Columnas faltantes detectadas por Entities
-- =============================================================

-- Recipe tiene @UpdateTimestamp pero V1 puede no tener updated_at si fue creada antes
-- Verificar si ya existe; si no, agregarla con ALTER
ALTER TABLE recipes
ADD COLUMN IF NOT EXISTS updated_at DATETIME
    NOT NULL DEFAULT CURRENT_TIMESTAMP
    ON UPDATE CURRENT_TIMESTAMP;

-- Badge no es actualizada frecuentemente pero por consistency y futura extensión
-- ALTER TABLE badges
-- ADD COLUMN IF NOT EXISTS updated_at DATETIME;
-- (comentado porque Badge.java no tiene @UpdateTimestamp)

-- Ingredient puede necesitar updated_at si en futuro lo agregamos
-- Por ahora no incluir (Ingredient no tiene @UpdateTimestamp en el código)

-- =============================================================
-- SECCIÓN B: Índices de performance faltantes
-- =============================================================

-- Recipes: búsqueda por filtros combinados (el endpoint GET /recipes usa todos estos)
CREATE INDEX IF NOT EXISTS idx_recipes_meal_diet
    ON recipes(meal_type, diet_type, is_active);

CREATE INDEX IF NOT EXISTS idx_recipes_budget_active
    ON recipes(budget_level, is_active);

CREATE INDEX IF NOT EXISTS idx_recipes_gluten_free
    ON recipes(is_gluten_free, is_active);

CREATE INDEX IF NOT EXISTS idx_recipes_soy_free
    ON recipes(is_soy_free, is_active);

-- Recipe tags: búsqueda por tag (el endpoint de filtros la usa)
CREATE INDEX IF NOT EXISTS idx_recipe_tags_tag_recipe
    ON recipe_tags(tag, recipe_id);

-- User badges: verificación de duplicados (BadgeService.existsByUserIdAndBadgeId)
CREATE INDEX IF NOT EXISTS idx_user_badges_user_badge
    ON user_badges(user_id, badge_id);

-- Daily logs: el CRON consulta esto en cada iteración de usuario
CREATE INDEX IF NOT EXISTS idx_daily_logs_user_date_desc
    ON daily_logs(user_id, log_date DESC);

-- Streaks: el CRON consulta last_checkin_date para todos los usuarios activos
CREATE INDEX IF NOT EXISTS idx_streaks_last_checkin
    ON streaks(last_checkin_date, user_id);

-- Notifications: auditoría y deduplicación del scheduler
CREATE INDEX IF NOT EXISTS idx_notifications_user_type_sent
    ON notifications(user_id, type, sent_at DESC);

-- Week plan entries: carga del planificador semanal
CREATE INDEX IF NOT EXISTS idx_week_plan_entries_plan_day
    ON week_plan_entries(week_plan_id, day_of_week);

-- Shopping list items: carga de la lista
CREATE INDEX IF NOT EXISTS idx_shopping_items_list_checked
    ON shopping_list_items(shopping_list_id, is_checked);

-- Referrals: validación de código único
CREATE INDEX IF NOT EXISTS idx_referrals_code
    ON referrals(code);

-- =============================================================
-- SECCIÓN C: CHECK constraints de integridad de datos
-- =============================================================

-- Validar rangos numéricos críticos en streaks
ALTER TABLE streaks
ADD CONSTRAINT IF NOT EXISTS chk_streaks_current_days_non_negative
    CHECK (current_days >= 0);

ALTER TABLE streaks
ADD CONSTRAINT IF NOT EXISTS chk_streaks_record_days_gte_current
    CHECK (record_days >= current_days);

ALTER TABLE streaks
ADD CONSTRAINT IF NOT EXISTS chk_streaks_total_points_non_negative
    CHECK (total_points >= 0);

-- Validar que la racha nunca fue hacia atrás (redundancia de lógica)
ALTER TABLE daily_logs
ADD CONSTRAINT IF NOT EXISTS chk_daily_logs_points_non_negative
    CHECK (points_earned >= 0);

-- Validar rangos en recipes
ALTER TABLE recipes
ADD CONSTRAINT IF NOT EXISTS chk_recipes_prep_time_non_negative
    CHECK (prep_time_minutes IS NULL OR prep_time_minutes >= 0);

ALTER TABLE recipes
ADD CONSTRAINT IF NOT EXISTS chk_recipes_cook_time_non_negative
    CHECK (cook_time_minutes IS NULL OR cook_time_minutes >= 0);

ALTER TABLE recipes
ADD CONSTRAINT IF NOT EXISTS chk_recipes_servings_positive
    CHECK (servings > 0);

ALTER TABLE recipes
ADD CONSTRAINT IF NOT EXISTS chk_recipes_calories_positive
    CHECK (calories_approx IS NULL OR calories_approx > 0);

-- Validar rangos en plate_options
ALTER TABLE plate_options
ADD CONSTRAINT IF NOT EXISTS chk_plate_options_calories_positive
    CHECK (calories IS NULL OR calories > 0);

-- =============================================================
-- SECCIÓN D: Auditoría — guardar updated_at donde no exista
-- =============================================================

-- Badges: agregar updated_at si el DBA quiere auditar cambios en el catálogo
-- (Opcional, comentado porque Badge.java no tiene @UpdateTimestamp)
-- ALTER TABLE badges
-- ADD COLUMN IF NOT EXISTS updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;

-- Ingredients: similar a badges
-- (Opcional, comentado porque Ingredient.java no tiene @UpdateTimestamp)

-- =============================================================
-- SECCIÓN E: Trigger de consistencia para streak record_days
-- =============================================================

-- Garantizar que record_days nunca sea menor que current_days
-- NOTA: Esto es redundancia de la lógica en CheckInServiceImpl, pero es una
-- doble seguridad en el nivel de base de datos.
DELIMITER $$
CREATE TRIGGER IF NOT EXISTS trg_streaks_record_days_before_update
BEFORE UPDATE ON streaks
FOR EACH ROW
BEGIN
    IF NEW.current_days > NEW.record_days THEN
        SET NEW.record_days = NEW.current_days;
    END IF;
END$$
DELIMITER ;

-- =============================================================
-- SECCIÓN F: Vista materializada (auxiliar) de dashboard
-- =============================================================

-- Vista para el endpoint GET /streaks/me (evita JOINs repetidos)
-- Nota: MySQL no tiene vistas materializadas como PostgreSQL, pero podemos
-- usar CREATE VIEW. No es materializada, pero el índice en streaks ayuda.
CREATE OR REPLACE VIEW v_user_dashboard AS
SELECT
    u.id           AS user_id,
    u.email,
    u.name,
    u.motivation,
    u.tone_pref,
    u.timezone,
    s.current_days,
    s.record_days,
    s.total_points,
    s.level,
    s.last_checkin_date,
    s.streak_started_at,
    (SELECT COUNT(*) FROM user_badges ub WHERE ub.user_id = u.id) AS badges_earned
FROM users u
LEFT JOIN streaks s ON s.user_id = u.id
WHERE u.is_active = TRUE AND u.deleted_at IS NULL;

-- =============================================================
-- SECCIÓN G: Seed data adicional (si falta alguna configuración)
-- =============================================================

-- Verificar que los badges principales existan
-- (Si ya están en V1, esto causa un duplicate key, que es OK porque
-- la sintaxis de INSERT ignora duplicados)
INSERT IGNORE INTO badges (code, name, description, trigger_type, trigger_value) VALUES
('BADGE_SYSTEM_HEALTH', 'Sistema de Badges', 'El sistema está funcionando correctamente', 'SPECIAL', 0);

-- Verificar que el usuario de test existe (para CI/CD)
-- Comentado: esto debería estar en datos de test, no en migración de producción
-- INSERT IGNORE INTO users (email, password_hash, name, profile_type, motivation, tone_pref, timezone, created_at)
-- VALUES ('test@internal.local', 'HASH_NOT_SET', 'Test User', 'VEGAN', 'HEALTH', 'MOTIVATIONAL', 'UTC', NOW());

-- =============================================================
-- VALIDACIÓN FINAL
-- =============================================================

-- Verificar que los índices se crearon
-- SELECT count(*) FROM INFORMATION_SCHEMA.STATISTICS 
-- WHERE TABLE_NAME = 'recipes' AND INDEX_NAME LIKE 'idx_recipes_%';

-- Verificar que los triggers están activos
-- SELECT TRIGGER_SCHEMA, TRIGGER_NAME, ACTION_STATEMENT 
-- FROM INFORMATION_SCHEMA.TRIGGERS 
-- WHERE TRIGGER_SCHEMA = 'veganapp' AND TRIGGER_NAME LIKE 'trg_%';

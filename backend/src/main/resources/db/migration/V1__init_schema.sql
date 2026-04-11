-- =============================================================
-- V1__init_schema.sql
-- VeganApp — Esquema inicial de base de datos (PostgreSQL 16)
-- Gestionado por Flyway. NO modificar este archivo.
-- Para cambios, crear V2__descripcion.sql
-- =============================================================

-- Configurar timezone de la sesión en UTC
SET timezone = 'UTC';

-- =============================================================
-- TIPOS ENUM PERSONALIZADOS
-- =============================================================
CREATE TYPE profile_type_enum AS ENUM ('VEGAN','VEGETARIAN','FLEXITARIAN','TRANSITIONING');
CREATE TYPE motivation_enum AS ENUM ('HEALTH','ETHICS','FITNESS','CURIOSITY');
CREATE TYPE tone_pref_enum AS ENUM ('MOTIVATIONAL','SARCASTIC','NEUTRAL');
CREATE TYPE experience_level_enum AS ENUM ('BEGINNER','EXPERIENCED');
CREATE TYPE budget_level_enum AS ENUM ('LOW','NORMAL','HIGH');
CREATE TYPE difficulty_enum AS ENUM ('EASY','MEDIUM','HARD');
CREATE TYPE meal_type_enum AS ENUM ('BREAKFAST','LUNCH','SNACK','DINNER');
CREATE TYPE diet_type_enum AS ENUM ('VEGAN','VEGETARIAN','BOTH');
CREATE TYPE ingredient_category_enum AS ENUM ('GRAIN','PROTEIN','VEGETABLE','FRUIT','FAT','DAIRY_FREE','SEASONING','OTHER');
CREATE TYPE recipe_tag_enum AS ENUM ('HIGH_PROTEIN','RICH_IRON','RICH_CALCIUM','GLUTEN_FREE','SOY_FREE','QUICK','ECONOMIC','NUT_FREE');
CREATE TYPE level_enum AS ENUM ('SEED','SPROUT','PLANT','TREE','FOREST');
CREATE TYPE day_of_week_enum AS ENUM ('MONDAY','TUESDAY','WEDNESDAY','THURSDAY','FRIDAY','SATURDAY','SUNDAY');
CREATE TYPE notification_type_enum AS ENUM ('PUSH_REMINDER','PUSH_MILESTONE','PUSH_STREAK_DANGER','EMAIL_WELCOME','EMAIL_SUMMARY');
CREATE TYPE notification_status_enum AS ENUM ('SENT','FAILED','OPENED');
CREATE TYPE trigger_type_enum AS ENUM ('STREAK_DAYS','TOTAL_POINTS','RECIPES_SAVED','PLANNER_COMPLETED','SPECIAL');
CREATE TYPE plate_category_enum AS ENUM ('BASE','PROTEIN','VEGETABLE','SAUCE');

-- =============================================================
-- USUARIOS
-- =============================================================
CREATE TABLE users (
    id               BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    email            VARCHAR(255)           NOT NULL UNIQUE,
    password_hash    VARCHAR(255)           NOT NULL,
    name             VARCHAR(100)           NOT NULL,
    profile_type     profile_type_enum      NOT NULL,
    motivation       motivation_enum        NOT NULL,
    tone_pref        tone_pref_enum         NOT NULL DEFAULT 'MOTIVATIONAL',
    experience_level experience_level_enum  NOT NULL DEFAULT 'BEGINNER',
    budget_level     budget_level_enum      NOT NULL DEFAULT 'NORMAL',
    timezone         VARCHAR(50)            NOT NULL DEFAULT 'UTC',
    notif_push       BOOLEAN                NOT NULL DEFAULT TRUE,
    notif_email      BOOLEAN                NOT NULL DEFAULT TRUE,
    is_active        BOOLEAN                NOT NULL DEFAULT TRUE,
    deleted_at       TIMESTAMPTZ            NULL,
    created_at       TIMESTAMPTZ            NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMPTZ            NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_active ON users(is_active, deleted_at);

-- =============================================================
-- RACHAS (1 fila por usuario — se crea al registrarse)
-- =============================================================
CREATE TABLE streaks (
    id                  BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    user_id             BIGINT        NOT NULL UNIQUE,
    current_days        INTEGER       NOT NULL DEFAULT 0,
    record_days         INTEGER       NOT NULL DEFAULT 0,
    total_points        BIGINT        NOT NULL DEFAULT 0,
    level               level_enum    NOT NULL DEFAULT 'SEED',
    last_checkin_date   DATE          NULL,
    streak_started_at   TIMESTAMPTZ   NULL,
    updated_at          TIMESTAMPTZ   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- =============================================================
-- LOGS DIARIOS DE CHECK-IN
-- =============================================================
CREATE TABLE daily_logs (
    id            BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    user_id       BIGINT        NOT NULL,
    log_date      DATE          NOT NULL,
    category      meal_type_enum NOT NULL DEFAULT 'BREAKFAST',
    notes         TEXT          NULL,
    points_earned INTEGER       NOT NULL DEFAULT 0,
    created_at    TIMESTAMPTZ   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    UNIQUE (user_id, log_date),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
CREATE INDEX idx_daily_logs_user_date ON daily_logs(user_id, log_date);

-- =============================================================
-- PAUSAS DE RACHA (máx 2 por mes por usuario)
-- =============================================================
CREATE TABLE streak_pauses (
    id          BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    user_id     BIGINT        NOT NULL,
    applied_at  TIMESTAMPTZ   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at  TIMESTAMPTZ   NOT NULL,
    month_year  VARCHAR(7)    NOT NULL,
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
CREATE INDEX idx_streak_pauses_user_month ON streak_pauses(user_id, month_year);
CREATE INDEX idx_streak_pauses_expires ON streak_pauses(expires_at);

-- =============================================================
-- BADGES (catálogo estático)
-- =============================================================
CREATE TABLE badges (
    id            BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    code          VARCHAR(50)   NOT NULL UNIQUE,
    name          VARCHAR(100)  NOT NULL,
    description   VARCHAR(255)  NOT NULL,
    icon_url      VARCHAR(255)  NULL,
    trigger_type  trigger_type_enum NOT NULL,
    trigger_value INTEGER       NOT NULL
);

-- =============================================================
-- BADGES DESBLOQUEADOS POR USUARIO
-- =============================================================
CREATE TABLE user_badges (
    id         BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    user_id    BIGINT        NOT NULL,
    badge_id   BIGINT        NOT NULL,
    earned_at  TIMESTAMPTZ   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    UNIQUE (user_id, badge_id),
    FOREIGN KEY (user_id)   REFERENCES users(id)   ON DELETE CASCADE,
    FOREIGN KEY (badge_id)  REFERENCES badges(id)
);

-- =============================================================
-- RECETAS
-- =============================================================
CREATE TABLE recipes (
    id              BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    title           VARCHAR(200)      NOT NULL,
    description     TEXT              NULL,
    instructions    TEXT              NOT NULL,
    prep_time_min   INTEGER           NOT NULL DEFAULT 0,
    cook_time_min   INTEGER           NOT NULL DEFAULT 0,
    servings        INTEGER           NOT NULL DEFAULT 2,
    difficulty      difficulty_enum   NOT NULL DEFAULT 'EASY',
    meal_type       meal_type_enum    NOT NULL,
    diet_type       diet_type_enum    NOT NULL DEFAULT 'VEGAN',
    budget_level    budget_level_enum NOT NULL DEFAULT 'NORMAL',
    is_gluten_free  BOOLEAN           NOT NULL DEFAULT FALSE,
    is_soy_free     BOOLEAN           NOT NULL DEFAULT FALSE,
    calories_approx INTEGER           NULL,
    protein_g       NUMERIC(5,1)      NULL,
    iron_mg         NUMERIC(5,1)      NULL,
    calcium_mg      NUMERIC(5,1)      NULL,
    fat_g           NUMERIC(5,1)      NULL,
    carbs_g         NUMERIC(5,1)      NULL,
    image_url       VARCHAR(255)      NULL,
    is_active       BOOLEAN           NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMPTZ       NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_recipes_meal_type ON recipes(meal_type);
CREATE INDEX idx_recipes_diet_type ON recipes(diet_type);
CREATE INDEX idx_recipes_budget ON recipes(budget_level);
CREATE INDEX idx_recipes_active ON recipes(is_active);

-- =============================================================
-- ETIQUETAS NUTRICIONALES (para el buscador con filtros)
-- =============================================================
CREATE TABLE recipe_tags (
    id        BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    recipe_id BIGINT        NOT NULL,
    tag       recipe_tag_enum NOT NULL,
    
    UNIQUE (recipe_id, tag),
    FOREIGN KEY (recipe_id) REFERENCES recipes(id) ON DELETE CASCADE
);
CREATE INDEX idx_recipe_tags_tag ON recipe_tags(tag);

-- =============================================================
-- INGREDIENTES (maestro)
-- =============================================================
CREATE TABLE ingredients (
    id             BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    name           VARCHAR(100)         NOT NULL,
    category       ingredient_category_enum NOT NULL,
    is_gluten_free BOOLEAN              NOT NULL DEFAULT FALSE,
    is_soy_free    BOOLEAN              NOT NULL DEFAULT FALSE,
    unit_default   VARCHAR(20)          NOT NULL DEFAULT 'g'
);
CREATE INDEX idx_ingredients_category ON ingredients(category);

-- =============================================================
-- RECETA <-> INGREDIENTE (N:M)
-- =============================================================
CREATE TABLE recipe_ingredients (
    id            BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    recipe_id     BIGINT        NOT NULL,
    ingredient_id BIGINT        NOT NULL,
    quantity      NUMERIC(8,2)  NOT NULL,
    unit          VARCHAR(20)   NOT NULL,
    notes         VARCHAR(100)  NULL,
    
    UNIQUE (recipe_id, ingredient_id),
    FOREIGN KEY (recipe_id)     REFERENCES recipes(id)     ON DELETE CASCADE,
    FOREIGN KEY (ingredient_id) REFERENCES ingredients(id)
);

-- =============================================================
-- RECETAS FAVORITAS DEL USUARIO
-- =============================================================
CREATE TABLE user_favorite_recipes (
    id        BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    user_id   BIGINT        NOT NULL,
    recipe_id BIGINT        NOT NULL,
    saved_at  TIMESTAMPTZ   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    UNIQUE (user_id, recipe_id),
    FOREIGN KEY (user_id)   REFERENCES users(id)   ON DELETE CASCADE,
    FOREIGN KEY (recipe_id) REFERENCES recipes(id) ON DELETE CASCADE
);

-- =============================================================
-- ARMADO DE PLATOS — opciones por categoría
-- =============================================================
CREATE TABLE plate_options (
    id          BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    category    plate_category_enum NOT NULL,
    name        VARCHAR(100)    NOT NULL,
    calories    INTEGER         NULL,
    protein_g   NUMERIC(5,1)    NULL,
    fat_g       NUMERIC(5,1)    NULL,
    icon_url    VARCHAR(255)    NULL,
    is_active   BOOLEAN         NOT NULL DEFAULT TRUE
);
CREATE INDEX idx_plate_options_category ON plate_options(category);

-- =============================================================
-- PLANIFICADOR SEMANAL
-- =============================================================
CREATE TABLE week_plans (
    id         BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    user_id    BIGINT        NOT NULL,
    week_start DATE          NOT NULL,
    created_at TIMESTAMPTZ   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    UNIQUE (user_id, week_start),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE week_plan_entries (
    id           BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    week_plan_id BIGINT        NOT NULL,
    day_of_week  day_of_week_enum NOT NULL,
    meal_type    meal_type_enum   NOT NULL,
    recipe_id    BIGINT        NULL,
    plate_config JSONB         NULL,
    
    UNIQUE (week_plan_id, day_of_week, meal_type),
    FOREIGN KEY (week_plan_id) REFERENCES week_plans(id) ON DELETE CASCADE,
    FOREIGN KEY (recipe_id)    REFERENCES recipes(id)
);

-- =============================================================
-- LISTA DE SUPERMERCADO
-- =============================================================
CREATE TABLE shopping_lists (
    id           BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    user_id      BIGINT        NOT NULL,
    name         VARCHAR(100)  NOT NULL DEFAULT 'Mi lista',
    week_plan_id BIGINT        NULL,
    created_at   TIMESTAMPTZ   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id)      REFERENCES users(id)      ON DELETE CASCADE,
    FOREIGN KEY (week_plan_id) REFERENCES week_plans(id)
);

CREATE TABLE shopping_list_items (
    id               BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    shopping_list_id BIGINT        NOT NULL,
    ingredient_id    BIGINT        NOT NULL,
    total_quantity   NUMERIC(8,2)  NOT NULL,
    unit             VARCHAR(20)   NOT NULL,
    is_checked       BOOLEAN       NOT NULL DEFAULT FALSE,
    
    FOREIGN KEY (shopping_list_id) REFERENCES shopping_lists(id) ON DELETE CASCADE,
    FOREIGN KEY (ingredient_id)    REFERENCES ingredients(id)
);

-- =============================================================
-- NOTIFICACIONES (auditoría)
-- =============================================================
CREATE TABLE notifications (
    id        BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    user_id   BIGINT                  NOT NULL,
    type      notification_type_enum  NOT NULL,
    status    notification_status_enum NOT NULL DEFAULT 'SENT',
    sent_at   TIMESTAMPTZ             NOT NULL DEFAULT CURRENT_TIMESTAMP,
    opened_at TIMESTAMPTZ             NULL,
    metadata  JSONB                   NULL,
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
CREATE INDEX idx_notifications_user_sent ON notifications(user_id, sent_at);
CREATE INDEX idx_notifications_type ON notifications(type);

-- =============================================================
-- REFERIDOS
-- =============================================================
CREATE TABLE referrals (
    id               BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    referrer_user_id BIGINT        NOT NULL,
    referred_user_id BIGINT        NOT NULL UNIQUE,
    code             VARCHAR(20)   NOT NULL UNIQUE,
    bonus_applied    BOOLEAN       NOT NULL DEFAULT FALSE,
    created_at       TIMESTAMPTZ   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (referrer_user_id) REFERENCES users(id),
    FOREIGN KEY (referred_user_id) REFERENCES users(id)
);

-- =============================================================
-- DATOS INICIALES: Badges del sistema
-- =============================================================
INSERT INTO badges (code, name, description, trigger_type, trigger_value) VALUES
('STREAK_3',    '3 días seguidos',     'Completaste 3 días consecutivos con Sir Pippin',     'STREAK_DAYS', 3),
('STREAK_7',    'Una semana completa',  '7 días seguidos. Sir Pippin está muy orgulloso',     'STREAK_DAYS', 7),
('STREAK_14',   'Dos semanas',          'Dos semanas de hábitos veganos. ¡Increíble!',        'STREAK_DAYS', 14),
('STREAK_30',   'Un mes de racha',      '30 días seguidos. Sos un ejemplo para Sir Pippin',  'STREAK_DAYS', 30),
('STREAK_60',   'Dos meses invicto',    '60 días. A esta altura ya es un estilo de vida',    'STREAK_DAYS', 60),
('STREAK_100',  '100 días',             '100 días consecutivos. Leyenda pura.',               'STREAK_DAYS', 100),
('STREAK_180',  'Medio año',            '180 días. Sir Pippin no para de aletear de alegría','STREAK_DAYS', 180),
('STREAK_365',  'Un año entero',        '365 días seguidos. Rango máximo desbloqueado.',      'STREAK_DAYS', 365),
('POINTS_100',  'Primeros 100 puntos',  'Acumulaste tus primeros 100 puntos',                'TOTAL_POINTS', 100),
('POINTS_500',  '500 puntos',           'Ya tenés 500 puntos. Sir Pippin te ve crecer',      'TOTAL_POINTS', 500),
('POINTS_2000', '2000 puntos',          'Nivel Árbol desbloqueado. Sos un bosque en proceso','TOTAL_POINTS', 2000);

-- =============================================================
-- DATOS INICIALES: Opciones para Armado de Platos
-- =============================================================
INSERT INTO plate_options (category, name, calories, protein_g, fat_g) VALUES
-- Bases
('BASE', 'Arroz integral',  130, 2.7, 1.0),
('BASE', 'Quinoa',          120, 4.4, 1.9),
('BASE', 'Pasta integral',  175, 7.5, 1.1),
('BASE', 'Papa hervida',     90, 2.0, 0.1),
('BASE', 'Batata',          103, 2.3, 0.1),
-- Proteínas
('PROTEIN', 'Tofu salteado',     80, 8.0, 4.5),
('PROTEIN', 'Lentejas cocidas', 116, 9.0, 0.4),
('PROTEIN', 'Garbanzos cocidos',164, 8.9, 2.6),
('PROTEIN', 'Tempeh',          195, 19.0,11.0),
('PROTEIN', 'Seitán',           99, 21.0, 1.5),
-- Verduras
('VEGETABLE', 'Brócoli al vapor',  34, 2.8, 0.4),
('VEGETABLE', 'Espinaca salteada', 23, 2.9, 0.4),
('VEGETABLE', 'Zucchini',          17, 1.2, 0.3),
('VEGETABLE', 'Zanahoria',         41, 0.9, 0.2),
('VEGETABLE', 'Berenjena',         25, 1.0, 0.2),
-- Salsas / Grasas saludables
('SAUCE', 'Palta (1/4)',       80,  1.0, 7.5),
('SAUCE', 'Hummus (2 cdas)',   70,  3.0, 5.0),
('SAUCE', 'Tahini (1 cda)',    89,  2.6, 8.0),
('SAUCE', 'Aceite de oliva',  120,  0.0,14.0);
-- =============================================================
-- V1__init_schema.sql
-- VeganApp — Esquema inicial de base de datos
-- Gestionado por Flyway. NO modificar este archivo.
-- Para cambios, crear V2__descripcion.sql
-- =============================================================

-- Configurar timezone de la sesión en UTC
SET time_zone = '+00:00';

-- =============================================================
-- USUARIOS
-- =============================================================
CREATE TABLE users (
    id               BIGINT PRIMARY KEY AUTO_INCREMENT,
    email            VARCHAR(255)    NOT NULL UNIQUE,
    password_hash    VARCHAR(255)    NOT NULL,
    name             VARCHAR(100)    NOT NULL,
    profile_type     ENUM('VEGAN','VEGETARIAN','FLEXITARIAN','TRANSITIONING') NOT NULL,
    motivation       ENUM('HEALTH','ETHICS','FITNESS','CURIOSITY')            NOT NULL,
    tone_pref        ENUM('MOTIVATIONAL','SARCASTIC','NEUTRAL')               NOT NULL DEFAULT 'MOTIVATIONAL',
    experience_level ENUM('BEGINNER','EXPERIENCED')                           NOT NULL DEFAULT 'BEGINNER',
    budget_level     ENUM('LOW','NORMAL','HIGH')                              NOT NULL DEFAULT 'NORMAL',
    timezone         VARCHAR(50)     NOT NULL DEFAULT 'UTC',
    notif_push       BOOLEAN         NOT NULL DEFAULT TRUE,
    notif_email      BOOLEAN         NOT NULL DEFAULT TRUE,
    is_active        BOOLEAN         NOT NULL DEFAULT TRUE,
    deleted_at       DATETIME        NULL,
    created_at       DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    INDEX idx_users_email (email),
    INDEX idx_users_active (is_active, deleted_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================================
-- RACHAS (1 fila por usuario — se crea al registrarse)
-- =============================================================
CREATE TABLE streaks (
    id                  BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id             BIGINT          NOT NULL UNIQUE,
    current_days        INT             NOT NULL DEFAULT 0,
    record_days         INT             NOT NULL DEFAULT 0,
    total_points        BIGINT          NOT NULL DEFAULT 0,
    level               ENUM('SEED','SPROUT','PLANT','TREE','FOREST') NOT NULL DEFAULT 'SEED',
    last_checkin_date   DATE            NULL     COMMENT 'Fecha LOCAL del usuario, no UTC',
    streak_started_at   DATETIME        NULL     COMMENT 'Timestamp UTC del inicio de la racha actual',
    updated_at          DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY fk_streaks_user (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================================
-- LOGS DIARIOS DE CHECK-IN
-- =============================================================
CREATE TABLE daily_logs (
    id            BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id       BIGINT          NOT NULL,
    log_date      DATE            NOT NULL  COMMENT 'Fecha LOCAL del usuario, calculada con su timezone',
    category      ENUM('FOOD','RECIPE','REFLECTION','ACTIVITY') NOT NULL DEFAULT 'FOOD',
    notes         TEXT            NULL,
    points_earned INT             NOT NULL DEFAULT 0,
    created_at    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,

    UNIQUE KEY uq_daily_log_user_date (user_id, log_date),
    FOREIGN KEY fk_daily_logs_user (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_daily_logs_user_date (user_id, log_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================================
-- PAUSAS DE RACHA (máx 2 por mes por usuario)
-- =============================================================
CREATE TABLE streak_pauses (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id     BIGINT          NOT NULL,
    applied_at  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at  DATETIME        NOT NULL  COMMENT 'applied_at + 48 horas',
    month_year  VARCHAR(7)      NOT NULL  COMMENT 'Formato YYYY-MM para agrupar por mes',

    FOREIGN KEY fk_streak_pauses_user (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_streak_pauses_user_month (user_id, month_year),
    INDEX idx_streak_pauses_expires (expires_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================================
-- BADGES (catálogo estático)
-- =============================================================
CREATE TABLE badges (
    id            BIGINT PRIMARY KEY AUTO_INCREMENT,
    code          VARCHAR(50)     NOT NULL UNIQUE  COMMENT 'Ej: STREAK_7, STREAK_30, FIRST_RECIPE',
    name          VARCHAR(100)    NOT NULL,
    description   VARCHAR(255)    NOT NULL,
    icon_url      VARCHAR(255)    NULL,
    trigger_type  ENUM('STREAK_DAYS','TOTAL_POINTS','RECIPES_SAVED','PLANNER_COMPLETED','SPECIAL') NOT NULL,
    trigger_value INT             NOT NULL  COMMENT 'Ej: 7 para STREAK_7, 100 para STREAK_100'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================================
-- BADGES DESBLOQUEADOS POR USUARIO
-- =============================================================
CREATE TABLE user_badges (
    id         BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id    BIGINT          NOT NULL,
    badge_id   BIGINT          NOT NULL,
    earned_at  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,

    UNIQUE KEY uq_user_badges (user_id, badge_id),
    FOREIGN KEY fk_user_badges_user  (user_id)  REFERENCES users(id)  ON DELETE CASCADE,
    FOREIGN KEY fk_user_badges_badge (badge_id) REFERENCES badges(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================================
-- RECETAS
-- =============================================================
CREATE TABLE recipes (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    title           VARCHAR(200)    NOT NULL,
    description     TEXT            NULL,
    instructions    TEXT            NOT NULL,
    prep_time_min   INT             NOT NULL DEFAULT 0,
    cook_time_min   INT             NOT NULL DEFAULT 0,
    servings        INT             NOT NULL DEFAULT 2,
    difficulty      ENUM('EASY','MEDIUM','HARD')                         NOT NULL DEFAULT 'EASY',
    meal_type       ENUM('BREAKFAST','LUNCH','SNACK','DINNER')           NOT NULL,
    diet_type       ENUM('VEGAN','VEGETARIAN','BOTH')                    NOT NULL DEFAULT 'VEGAN',
    budget_level    ENUM('LOW','NORMAL','HIGH')                          NOT NULL DEFAULT 'NORMAL',
    is_gluten_free  BOOLEAN         NOT NULL DEFAULT FALSE,
    is_soy_free     BOOLEAN         NOT NULL DEFAULT FALSE,
    calories_approx INT             NULL,
    protein_g       DECIMAL(5,1)    NULL,
    iron_mg         DECIMAL(5,1)    NULL,
    calcium_mg      DECIMAL(5,1)    NULL,
    fat_g           DECIMAL(5,1)    NULL,
    carbs_g         DECIMAL(5,1)    NULL,
    image_url       VARCHAR(255)    NULL,
    is_active       BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,

    INDEX idx_recipes_meal_type (meal_type),
    INDEX idx_recipes_diet_type (diet_type),
    INDEX idx_recipes_budget    (budget_level),
    INDEX idx_recipes_active    (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================================
-- ETIQUETAS NUTRICIONALES (para el buscador con filtros)
-- =============================================================
CREATE TABLE recipe_tags (
    id        BIGINT PRIMARY KEY AUTO_INCREMENT,
    recipe_id BIGINT NOT NULL,
    tag       ENUM('HIGH_PROTEIN','RICH_IRON','RICH_CALCIUM','GLUTEN_FREE',
                   'SOY_FREE','QUICK','ECONOMIC','NUT_FREE') NOT NULL,

    UNIQUE KEY uq_recipe_tag (recipe_id, tag),
    FOREIGN KEY fk_recipe_tags_recipe (recipe_id) REFERENCES recipes(id) ON DELETE CASCADE,
    INDEX idx_recipe_tags_tag (tag)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================================
-- INGREDIENTES (maestro)
-- =============================================================
CREATE TABLE ingredients (
    id             BIGINT PRIMARY KEY AUTO_INCREMENT,
    name           VARCHAR(100)    NOT NULL,
    category       ENUM('GRAIN','PROTEIN','VEGETABLE','FRUIT','FAT','DAIRY_FREE','SEASONING','OTHER') NOT NULL,
    is_gluten_free BOOLEAN         NOT NULL DEFAULT FALSE,
    is_soy_free    BOOLEAN         NOT NULL DEFAULT FALSE,
    unit_default   VARCHAR(20)     NOT NULL DEFAULT 'g'  COMMENT 'Unidad por defecto: g, ml, unidad, taza',

    INDEX idx_ingredients_category (category)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================================
-- RECETA <-> INGREDIENTE (N:M)
-- =============================================================
CREATE TABLE recipe_ingredients (
    id            BIGINT PRIMARY KEY AUTO_INCREMENT,
    recipe_id     BIGINT          NOT NULL,
    ingredient_id BIGINT          NOT NULL,
    quantity      DECIMAL(8,2)    NOT NULL,
    unit          VARCHAR(20)     NOT NULL,
    notes         VARCHAR(100)    NULL  COMMENT 'Ej: picado fino, a temperatura ambiente',

    UNIQUE KEY uq_recipe_ingredient (recipe_id, ingredient_id),
    FOREIGN KEY fk_recipe_ing_recipe     (recipe_id)     REFERENCES recipes(id)     ON DELETE CASCADE,
    FOREIGN KEY fk_recipe_ing_ingredient (ingredient_id) REFERENCES ingredients(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================================
-- RECETAS FAVORITAS DEL USUARIO
-- =============================================================
CREATE TABLE user_favorite_recipes (
    id        BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id   BIGINT          NOT NULL,
    recipe_id BIGINT          NOT NULL,
    saved_at  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,

    UNIQUE KEY uq_user_favorite_recipe (user_id, recipe_id),
    FOREIGN KEY fk_fav_user   (user_id)   REFERENCES users(id)   ON DELETE CASCADE,
    FOREIGN KEY fk_fav_recipe (recipe_id) REFERENCES recipes(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================================
-- ARMADO DE PLATOS — opciones por categoría
-- =============================================================
CREATE TABLE plate_options (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    category    ENUM('BASE','PROTEIN','VEGETABLE','SAUCE')  NOT NULL,
    name        VARCHAR(100)    NOT NULL,
    calories    INT             NULL,
    protein_g   DECIMAL(5,1)   NULL,
    fat_g       DECIMAL(5,1)   NULL,
    icon_url    VARCHAR(255)    NULL,
    is_active   BOOLEAN         NOT NULL DEFAULT TRUE,

    INDEX idx_plate_options_category (category)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================================
-- PLANIFICADOR SEMANAL
-- =============================================================
CREATE TABLE week_plans (
    id         BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id    BIGINT          NOT NULL,
    week_start DATE            NOT NULL  COMMENT 'Fecha del lunes de la semana',
    created_at DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    UNIQUE KEY uq_week_plan_user_week (user_id, week_start),
    FOREIGN KEY fk_week_plans_user (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE week_plan_entries (
    id           BIGINT PRIMARY KEY AUTO_INCREMENT,
    week_plan_id BIGINT          NOT NULL,
    day_of_week  ENUM('MONDAY','TUESDAY','WEDNESDAY','THURSDAY','FRIDAY','SATURDAY','SUNDAY') NOT NULL,
    meal_type    ENUM('BREAKFAST','LUNCH','SNACK','DINNER') NOT NULL,
    recipe_id    BIGINT          NULL  COMMENT 'Puede ser una receta...',
    plate_config JSON            NULL  COMMENT '...o una combinación de Armado de Platos',

    UNIQUE KEY uq_plan_entry (week_plan_id, day_of_week, meal_type),
    FOREIGN KEY fk_plan_entries_plan   (week_plan_id) REFERENCES week_plans(id) ON DELETE CASCADE,
    FOREIGN KEY fk_plan_entries_recipe (recipe_id)    REFERENCES recipes(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================================
-- LISTA DE SUPERMERCADO
-- =============================================================
CREATE TABLE shopping_lists (
    id           BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id      BIGINT          NOT NULL,
    name         VARCHAR(100)    NOT NULL DEFAULT 'Mi lista',
    week_plan_id BIGINT          NULL  COMMENT 'Si fue generada desde el planificador',
    created_at   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY fk_shopping_list_user (user_id)      REFERENCES users(id)      ON DELETE CASCADE,
    FOREIGN KEY fk_shopping_list_plan (week_plan_id) REFERENCES week_plans(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE shopping_list_items (
    id               BIGINT PRIMARY KEY AUTO_INCREMENT,
    shopping_list_id BIGINT          NOT NULL,
    ingredient_id    BIGINT          NOT NULL,
    total_quantity   DECIMAL(8,2)    NOT NULL,
    unit             VARCHAR(20)     NOT NULL,
    is_checked       BOOLEAN         NOT NULL DEFAULT FALSE  COMMENT 'El usuario tilda lo que ya tiene',

    FOREIGN KEY fk_shopping_items_list       (shopping_list_id) REFERENCES shopping_lists(id) ON DELETE CASCADE,
    FOREIGN KEY fk_shopping_items_ingredient (ingredient_id)    REFERENCES ingredients(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================================
-- NOTIFICACIONES (auditoría)
-- =============================================================
CREATE TABLE notifications (
    id        BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id   BIGINT          NOT NULL,
    type      ENUM('PUSH_REMINDER','PUSH_MILESTONE','PUSH_STREAK_DANGER',
                   'EMAIL_WELCOME','EMAIL_SUMMARY') NOT NULL,
    status    ENUM('SENT','FAILED','OPENED')        NOT NULL DEFAULT 'SENT',
    sent_at   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    opened_at DATETIME        NULL,
    metadata  JSON            NULL  COMMENT 'Payload adicional: badge_id, days_count, etc.',

    FOREIGN KEY fk_notifications_user (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_notifications_user_sent (user_id, sent_at),
    INDEX idx_notifications_type      (type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================================
-- REFERIDOS
-- =============================================================
CREATE TABLE referrals (
    id               BIGINT PRIMARY KEY AUTO_INCREMENT,
    referrer_user_id BIGINT          NOT NULL,
    referred_user_id BIGINT          NOT NULL UNIQUE,
    code             VARCHAR(20)     NOT NULL UNIQUE,
    bonus_applied    BOOLEAN         NOT NULL DEFAULT FALSE,
    created_at       DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY fk_referrals_referrer (referrer_user_id) REFERENCES users(id),
    FOREIGN KEY fk_referrals_referred (referred_user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================================
-- DATOS INICIALES: Badges del sistema
-- =============================================================
INSERT INTO badges (code, name, description, trigger_type, trigger_value) VALUES
('STREAK_3',    '3 días seguidos',     'Completaste 3 días consecutivos con Sir Pippin',     'STREAK_DAYS', 3),
('STREAK_7',    'Una semana completa',  '7 días seguidos. Sir Pippin está muy orgulloso',     'STREAK_DAYS', 7),
('STREAK_14',   'Dos semanas',          'Dos semanas de hábitos veganos. ¡Increíble!',        'STREAK_DAYS', 14),
('STREAK_30',   'Un mes de racha',      '30 días seguidos. Sos un ejemplo para Sir Pippin',  'STREAK_DAYS', 30),
('STREAK_60',   'Dos meses invicto',    '60 días. A esta altura ya es un estilo de vida',    'STREAK_DAYS', 60),
('STREAK_100',  '100 días',             '100 días consecutivos. Leyenda pura.',               'STREAK_DAYS', 100),
('STREAK_180',  'Medio año',            '180 días. Sir Pippin no para de aletear de alegría','STREAK_DAYS', 180),
('STREAK_365',  'Un año entero',        '365 días seguidos. Rango máximo desbloqueado.',      'STREAK_DAYS', 365),
('POINTS_100',  'Primeros 100 puntos',  'Acumulaste tus primeros 100 puntos',                'TOTAL_POINTS', 100),
('POINTS_500',  '500 puntos',           'Ya tenés 500 puntos. Sir Pippin te ve crecer',      'TOTAL_POINTS', 500),
('POINTS_2000', '2000 puntos',          'Nivel Árbol desbloqueado. Sos un bosque en proceso','TOTAL_POINTS', 2000);

-- =============================================================
-- DATOS INICIALES: Opciones para Armado de Platos
-- =============================================================
INSERT INTO plate_options (category, name, calories, protein_g, fat_g) VALUES
-- Bases
('BASE', 'Arroz integral',  130, 2.7, 1.0),
('BASE', 'Quinoa',          120, 4.4, 1.9),
('BASE', 'Pasta integral',  175, 7.5, 1.1),
('BASE', 'Papa hervida',     90, 2.0, 0.1),
('BASE', 'Batata',          103, 2.3, 0.1),
-- Proteínas
('PROTEIN', 'Tofu salteado',     80, 8.0, 4.5),
('PROTEIN', 'Lentejas cocidas', 116, 9.0, 0.4),
('PROTEIN', 'Garbanzos cocidos',164, 8.9, 2.6),
('PROTEIN', 'Tempeh',          195, 19.0,11.0),
('PROTEIN', 'Seitán',           99, 21.0, 1.5),
-- Verduras
('VEGETABLE', 'Brócoli al vapor',  34, 2.8, 0.4),
('VEGETABLE', 'Espinaca salteada', 23, 2.9, 0.4),
('VEGETABLE', 'Zucchini',          17, 1.2, 0.3),
('VEGETABLE', 'Zanahoria',         41, 0.9, 0.2),
('VEGETABLE', 'Berenjena',         25, 1.0, 0.2),
-- Salsas / Grasas saludables
('SAUCE', 'Palta (1/4)',       80,  1.0, 7.5),
('SAUCE', 'Hummus (2 cdas)',   70,  3.0, 5.0),
('SAUCE', 'Tahini (1 cda)',    89,  2.6, 8.0),
('SAUCE', 'Aceite de oliva',  120,  0.0,14.0);

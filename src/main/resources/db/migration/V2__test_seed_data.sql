-- =============================================================
-- V2__test_seed_data.sql — Datos de prueba determinísticos (dev/QA)
-- Idempotente: INSERT IGNORE + filas condicionales donde aplica.
-- Contraseña de los 3 usuarios (plano, solo entornos de prueba): test
-- Hash BCrypt cost 12 ($2a$) — ver SeedFlywayPasswordHashTest.
-- =============================================================

SET time_zone = '+00:00';

-- Usuarios fijos (IDs altos para no chocar con auto_increment habitual)
INSERT IGNORE INTO users (
    id, email, password_hash, name,
    profile_type, motivation, tone_pref, experience_level, budget_level,
    timezone, notif_push, notif_email, is_active, deleted_at
) VALUES
(
    9001, 'constante@veganapp.com',
    '$2a$12$XodbOuISPCPQijlY8MIRUepDeURhxDe09/4VQU0Cno5zkTEKjZouO',
    'Seed Constante',
    'VEGAN', 'HEALTH', 'MOTIVATIONAL', 'BEGINNER', 'NORMAL',
    'America/Argentina/Buenos_Aires', TRUE, TRUE, TRUE, NULL
),
(
    9002, 'colgado@veganapp.com',
    '$2a$12$XodbOuISPCPQijlY8MIRUepDeURhxDe09/4VQU0Cno5zkTEKjZouO',
    'Seed Colgado',
    'VEGAN', 'HEALTH', 'NEUTRAL', 'BEGINNER', 'NORMAL',
    'America/Argentina/Buenos_Aires', TRUE, TRUE, TRUE, NULL
),
(
    9003, 'salvado@veganapp.com',
    '$2a$12$XodbOuISPCPQijlY8MIRUepDeURhxDe09/4VQU0Cno5zkTEKjZouO',
    'Seed Salvado',
    'VEGAN', 'ETHICS', 'MOTIVATIONAL', 'EXPERIENCED', 'NORMAL',
    'Europe/Madrid', TRUE, TRUE, TRUE, NULL
);

-- Rachas: user 1 → 6 días, último check-in ayer (fecha LOCAL coherente con time_zone de sesión UTC + columna DATE)
INSERT IGNORE INTO streaks (
    id, user_id, current_days, record_days, total_points, level,
    last_checkin_date, streak_started_at
) VALUES (
    9901, 9001, 6, 6, 60, 'SEED',
    (DATE_SUB(CURDATE(), INTERVAL 1 DAY)),
    (UTC_TIMESTAMP() - INTERVAL 6 DAY)
);

-- User 2: último check-in hace 3 días (racha “colgada”)
INSERT IGNORE INTO streaks (
    id, user_id, current_days, record_days, total_points, level,
    last_checkin_date, streak_started_at
) VALUES (
    9902, 9002, 8, 12, 100, 'SPROUT',
    (DATE_SUB(CURDATE(), INTERVAL 3 DAY)),
    (UTC_TIMESTAMP() - INTERVAL 14 DAY)
);

-- User 3: racha base + pausa activa (expires_at en el futuro)
INSERT IGNORE INTO streaks (
    id, user_id, current_days, record_days, total_points, level,
    last_checkin_date, streak_started_at
) VALUES (
    9903, 9003, 3, 5, 30, 'SEED',
    (DATE_SUB(CURDATE(), INTERVAL 1 DAY)),
    (UTC_TIMESTAMP() - INTERVAL 3 DAY)
);

-- Pausa activa para 9003: una sola fila aunque se re-ejecute el script en entornos sucios
INSERT INTO streak_pauses (user_id, applied_at, expires_at, month_year)
SELECT 9003,
       (UTC_TIMESTAMP() - INTERVAL 12 HOUR),
       (UTC_TIMESTAMP() + INTERVAL 36 HOUR),
       DATE_FORMAT(UTC_TIMESTAMP(), '%Y-%m')
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM streak_pauses sp
    WHERE sp.user_id = 9003
      AND sp.expires_at > UTC_TIMESTAMP()
);

# ROUTER.md — VeganApp
## Fuente de verdad para agentes IA (Cursor, Claude, Copilot, etc.)
## ⚠️ LEER COMPLETO ANTES DE GENERAR CUALQUIER CÓDIGO ⚠️

---

## 0. INSTRUCCIONES PARA EL AGENTE IA

Antes de escribir una sola línea de código, internalizá estas reglas sin excepción:

1. **Este archivo es la ley.** Si algo no está acá, preguntá antes de asumir.
2. **KISS y DRY siempre.** Simple es mejor. Sin código duplicado.
3. **No agregar dependencias** fuera de las listadas en la sección de Stack.
4. **Manejo de errores obligatorio.** Toda excepción capturada y logueada. Nunca swallowed.
5. **Sin hardcodeo.** Strings, URLs, credenciales y constantes numéricas van en application.properties o .env.
6. **Responsabilidad única.** Cada clase/función hace UNA sola cosa.
7. **No mezclar capas.** Lógica de negocio solo en Services. Nunca en Controllers ni Repositories.
8. **Test por endpoint.** Cada endpoint nuevo tiene su test de integración.
9. **Timezone siempre.** Toda operación con fechas usa ZonedDateTime o Instant. NUNCA LocalDateTime sin timezone explícito.
10. **Deuda técnica visible.** Si detectás un problema en código existente, marcalo con // TODO: [descripción] sin corregirlo salvo que se pida explícitamente.
11. **Frontend ya existe.** Las pantallas están diseñadas en Stitch. No rediseñar UI. Solo implementar la lógica que las pantallas requieren.
12. **Assets ya existen.** Sir Pippin como imagen/SVG ya está diseñado. No generar ni sugerir imágenes alternativas.

---

## 1. ESTADO ACTUAL DEL PROYECTO

### Qué ya está hecho (NO recrear esto)
- DONE: Diseño de pantallas — Completo en Stitch. Exportar assets y seguir ese diseño exacto.
- DONE: Sir Pippin — Imagen/SVG de la cotorra ya diseñada. Colores y forma definidos.
- DONE: Investigación de contenido nutricional — Documentada (nutrientes, alimentos, categorías).
- DONE: Arquitectura técnica — Definida en este documento.
- DONE: Requerimientos funcionales y no funcionales — Documento externo generado.

### Qué falta construir
- TODO: Backend Spring Boot (monolito modular)
- TODO: Integración React Native con pantallas de Stitch
- TODO: Base de datos MySQL + Redis
- TODO: Lógica de rachas, check-in, notificaciones
- TODO: Motor de filtros de recetas y Armado de Platos
- TODO: Planificador semanal + Lista de supermercado
- TODO: Infraestructura Docker + CI/CD

### Timeline objetivo: 14 días (no consecutivos)
```
Días 1-2:   Infraestructura base (Docker, MySQL, Redis, Spring Boot scaffold)
Días 3-4:   Módulo Auth completo (registro, login, JWT, refresh tokens)
Días 5-6:   Módulo Streak + Check-in (core del producto)
Días 7-8:   Sir Pippin reactions + Notificaciones (FCM + Email)
Días 9-10:  Módulo Recetas + Filtros + Armado de Platos
Días 11-12: Planificador semanal + Lista de supermercado
Días 13-14: Share cards + Testing + Deploy
```

---

## 2. VISIÓN DEL PRODUCTO

**Nombre:** VeganApp (nombre final por definir)
**Tagline:** El hábito vegano que no te aburre.

**Qué es:** Aplicación mobile que combina dos ejes:
- EJE 1: Gamificación de hábitos veganos — sistema de rachas diarias con Sir Pippin como coach con personalidad
- EJE 2: Asistente nutricional — recetas, nutrientes, planificador semanal y lista de supermercado automática

**El insight clave:** La app no es solo un recetario. Es una herramienta de transición y mantenimiento de hábitos que guía nutricionalmente con criterio (alto en proteína, rico en hierro, sin gluten, económico) y facilita la compra semanal automáticamente.

**Diferenciador 1:** Sir Pippin — no es un logo, es un agente narrativo con personalidad que reacciona al estado del usuario.

**Diferenciador 2:** Armado de Platos — el usuario construye su comida eligiendo categorías (base + proteína + verdura + salsa), sin tener que seguir una receta entera. Ideal para quien no quiere seguir una receta pero tampoco sabe qué combinar.

**Diferenciador 3:** Lista de supermercado automática — el usuario guarda recetas favoritas o completa el planificador, y la app genera la lista de compras consolidada automáticamente.

---

## 3. SIR PIPPIN — EL PERSONAJE

**Descripción física:** Cotorra/loro pequeño, redondo, apachuchable. Come lechuga con cara de felicidad. Diseño ya finalizado — usar el asset existente.

### Paleta de colores
```
Verde plumaje principal:  #2D8B4E
Amarillo pico:            #F5C518
Rojo acento:              #E84040
Blanco ojos:              #FFFFFF
Fondo app:                #F7FFF3
Accent principal:         #1A6B3A
Accent secundario:        #4CAF50
Card background:          #FFFFFF
Texto primario:           #1A1A1A
Texto secundario:         #6B7280
Border/Divider:           #E5E7EB
Tag Alto en proteina:     #FF6B35
Tag Sin gluten:           #8B5CF6
Tag Economico:            #10B981
Tag Rico en hierro:       #EF4444
Tag Rico en calcio:       #3B82F6
```

### Estados de animación (el backend devuelve el string, el frontend maneja la animación)
```
idle          -> Come lechuga tranquilo (pantalla principal sin novedad)
celebration   -> Aleteo rápido (hito de racha: 7, 30, 100 días)
disappointed  -> Cabeza baja, alita caída (racha rota)
surprised     -> Pico abierto (primer check-in del día)
sarcastic     -> Mira de reojo (recordatorio 48hs sin check-in)
proud         -> Pecho inflado (récord personal superado)
excited       -> Salta (primera vez que completa el planificador semanal)
```

### Tonos de Sir Pippin (elegido en onboarding)
```
MOTIVATIONAL  -> "¡Lo estás rompiendo! 5 días seguidos es un logro real."
SARCASTIC     -> "Mirá vos, apareciste. Ya pensaba que te habías ido a comer un asado."
NEUTRAL       -> "Check-in registrado. Llevás 5 días consecutivos."
```

### Motivación del usuario (afecta el copy de Sir Pippin)
```
HEALTH    -> Mensajes sobre energía, digestión, bienestar físico
ETHICS    -> Mensajes sobre impacto ambiental, animales, planeta
FITNESS   -> Mensajes sobre proteína, músculo, rendimiento plant-based
CURIOSITY -> Mensajes exploratorios, recetas nuevas, datos curiosos
```

---

## 4. CONTENIDO NUTRICIONAL (DEL CUADERNO)

Este es el contenido que la app debe conocer para etiquetar recetas y alimentos.

### Nutrientes principales

**Proteína** (clave para músculo, energía y saciedad)
Fuentes: Lentejas, Garbanzos, Porotos, Tofu, Tempeh, Soja texturizada, Seitán, Quinoa, Avena, Frutos secos y semillas
Etiqueta en app: "Alto en proteína" (color #FF6B35)

**Hierro** (crítico para veganos — puede faltar si no se come bien)
Fuentes: Lentejas, Garbanzo, Espinaca, Acelga, Semillas de calabaza, Quinoa, Porotos
Nota para Sir Pippin: El hierro vegetal se absorbe mejor con vitamina C. Puede sugerir esto.
Etiqueta: "Rico en hierro" (color #EF4444)

**Calcio** (fuentes veganas — no lácteos)
Fuentes: Leche vegana fortificada, Yogurt vegetal fortificado, Tofu, Sésamo, Almendras, Brócoli, Chía
Etiqueta: "Rico en calcio" (color #3B82F6)

**Carbohidratos saludables**
Fuentes: Avena, Arroz integral, Quinoa, Pan integral, Papa, Batata, Pastas integrales

**Grasas saludables**
Fuentes: Chía, Lino (linaza), Nueces, Palta (aguacate), Semillas

### Categorias por secciones de recetas 

RECETAS CON SEPARACION POR CATEGORIAS DEPENDIENDO DEL MOMENTO DEL DIA Y COMPLEJIDAD

### Filtros de búsqueda de recetas (implementar todos)
```
por tipo de dieta:   VEGAN / VEGETARIAN
por nutriente:       HIGH_PROTEIN / RICH_IRON / RICH_CALCIUM
por restricción:     GLUTEN_FREE / SOY_FREE / NUT_FREE
por economía:        LOW / NORMAL / HIGH
por tiempo:          QUICK (<30 min) / NORMAL / ELABORATE
por momento:         BREAKFAST / LUNCH / SNACK / DINNER
```

---

## 5. FEATURES PRINCIPALES

### Feature 1: Sistema de Rachas + Check-in
- Check-in diario registra que el usuario cumplió su hábito vegano del día
- Racha se rompe si no hay check-in en 24hs (con modo pausa disponible 2x/mes)
- Hitos: 3, 7, 14, 30, 60, 100, 180, 365 días → animación especial + badge
- Sir Pippin reacciona a cada check-in según tono + motivación del usuario

### Feature 2: Buscador de Recetas con Filtros
Búsqueda múltiple combinando filtros. Ejemplo:
"Quiero algo vegano, alto en proteína, económico y rápido para el almuerzo"
→ La app devuelve recetas que cumplen TODOS los filtros seleccionados

### Feature 3: Armado de Platos (feature única del cuaderno)
Para quien no quiere seguir una receta entera. El usuario elige:
```
1 BASE     -> Arroz / Quinoa / Pasta / Papa / Batata
1 PROTEINA -> Tofu / Lentejas / Garbanzo / Tempeh / Seitan
1 VERDURA  -> Brocoli / Espinaca / Zucchini / Zanahoria
1 SALSA    -> Palta / Hummus / Tahini / Aceite de oliva
```
La app calcula y muestra:
- Calorías aproximadas
- Grasas aproximadas
- Si la combinación es balanceada o no
- Sugerencia de Sir Pippin sobre la elección

### Feature 4: Planificador Semanal
El usuario arma su semana de comidas:
```
Cada día: Desayuno | Almuerzo | Merienda | Cena
```
Puede asignar recetas existentes o combinaciones del Armado de Platos.

### Feature 5: Lista de Supermercado Automática
Flujo exacto del cuaderno:
```
1. Usuario guarda recetas favoritas o completa el planificador
2. Toca "Generar lista de supermercado"
3. La app consolida TODOS los ingredientes
4. Elimina duplicados y suma cantidades del mismo ingrediente
5. Organiza por categoría (frutas, verduras, granos, proteínas)
6. El usuario puede tildar lo que ya tiene en casa
```

### Feature 6: Onboarding Personalizado (5 preguntas)
```
1. ¿Sos vegano, vegetariano, flexitariano o estás empezando?
2. ¿Qué te trajo acá? (Salud / Ética / Fitness / Curiosidad)
3. ¿Cuál es tu situación económica para las comidas? (Baja / Normal / Alta)
4. ¿Sos principiante o experimentado en la cocina?
5. ¿Qué tono preferís de Sir Pippin? (Motivacional / Sarcástico / Neutral)
```
Esto personaliza: tono de Sir Pippin, filtros por defecto, recetas sugeridas en el home.

### Feature 7: Share Cards
Al alcanzar hitos de racha → imagen 1080x1080px con Sir Pippin + número de días para Instagram Stories.

---

## 6. STACK TECNOLÓGICO — VERSIONES EXACTAS

### Backend
```
Java:                  21 (LTS)
Spring Boot:           3.3.x
Spring Security:       6.x
Spring Data JPA:       incluido en Spring Boot
Spring Cloud Gateway:  4.x (módulo separado)
Spring Actuator:       incluido
Hibernate:             6.x
MySQL Connector:       8.x
Redis Client:          Lettuce (via spring-boot-starter-data-redis)
JWT Library:           jjwt 0.12.x (io.jsonwebtoken)
WebClient:             spring-boot-starter-webflux (SOLO para llamadas HTTP salientes, NO para reemplazar MVC)
Bean Validation:       spring-boot-starter-validation
Micrometer:            incluido en Actuator
MapStruct:             1.5.x (mapeo Entity <-> DTO)
Lombok:                1.18.x (reducir boilerplate)
```

### Base de datos
```
MySQL:  8.0.x
Redis:  7.2.x
```

### Frontend Mobile
```
React Native:      0.74.x
Reanimated:        4.3.x  (animaciones de Sir Pippin)
React Scan:        latest (SOLO en desarrollo)
Axios:             1.7.x
React Navigation:  6.x
AsyncStorage:      @react-native-async-storage 2.x
i18next:           latest (ES/EN)
React Native SVG:  latest (renderizar Sir Pippin)
```

IMPORTANTE: Las pantallas ya están diseñadas en Stitch. El trabajo de frontend es:
1. Exportar los assets de Stitch (colores, tipografías, componentes)
2. Replicar el diseño exacto en React Native
3. Conectar a los endpoints del backend

### Infraestructura
```
Docker:         24.x
Docker Compose: 2.x
GitHub Actions: CI/CD
Prometheus:     2.x
Grafana:        10.x
Firebase FCM:   push notifications
Resend API:     email transaccional
MinIO/AWS S3:   almacenamiento de imágenes
```

---

## 7. ARQUITECTURA DEL SISTEMA

```
[React Native App]  <- pantallas de Stitch + assets de Sir Pippin ya existentes
        |
        | HTTPS / TLS 1.3
        v
[Spring Cloud Gateway]
  - Rate limiting por IP y por usuario
  - Validación centralizada de JWT
  - Routing a módulos del monolito
        |
        v
[Spring Boot — Monolito Modular]
  |- auth/          Registro, login, refresh tokens
  |- user/          Perfil, preferencias, motivación, situación económica
  |- streak/        Rachas, check-in, puntos, niveles, modo pausa
  |- pippin/        Mensajes, estados de animación, banco de copy
  |- recipe/        Recetas, ingredientes, filtros, búsqueda, favoritos
  |- plate/         Armado de Platos (base + proteína + verdura + salsa)
  |- planner/       Planificador semanal
  |- shopping/      Lista de supermercado automática
  |- notification/  Push (FCM), email (Resend), scheduler
  |- badge/         Catálogo de logros, desbloqueo
  |- share/         Generación de share cards PNG
  |- common/        Excepciones, DTOs base, seguridad, configs

        |
   +---------+--------+
   v         v        v
[MySQL 8] [Redis 7] [MinIO/S3]
```

### Reglas de capas (NO violar)
```
Controller -> Service -> Repository -> Database
     |              |
   DTOs         Entities (solo dentro del dominio)
```

### Estructura de paquetes
```
com.veganapp
|- auth
|  |- controller/    AuthController.java
|  |- service/       AuthService.java, JwtService.java, RefreshTokenService.java
|  |- repository/    UserRepository.java
|  |- entity/        User.java
|  |- dto/           RegisterRequest.java, LoginRequest.java, AuthResponse.java
|- streak
|  |- controller/    StreakController.java
|  |- service/       StreakService.java, CheckInService.java, BadgeTriggerService.java
|  |- repository/    StreakRepository.java, DailyLogRepository.java
|  |- entity/        Streak.java, DailyLog.java
|  |- dto/           CheckInRequest.java, StreakResponse.java, DashboardResponse.java
|- pippin
|  |- service/       PippinMessageService.java
|  |- dto/           PippinReactionResponse.java
|  (mensajes en resources/pippin/messages_es.json)
|- recipe
|  |- controller/    RecipeController.java
|  |- service/       RecipeService.java, RecipeFilterService.java
|  |- repository/    RecipeRepository.java, IngredientRepository.java
|  |- entity/        Recipe.java, Ingredient.java, RecipeIngredient.java
|  |- dto/           RecipeResponse.java, RecipeFilterRequest.java
|- plate
|  |- controller/    PlateController.java
|  |- service/       PlateBuilderService.java
|  |- dto/           PlateRequest.java, PlateResponse.java
|- planner
|  |- controller/    PlannerController.java
|  |- service/       PlannerService.java
|  |- repository/    WeekPlanRepository.java
|  |- entity/        WeekPlan.java, WeekPlanEntry.java
|  |- dto/           WeekPlanRequest.java, WeekPlanResponse.java
|- shopping
|  |- controller/    ShoppingListController.java
|  |- service/       ShoppingListService.java
|  |- dto/           ShoppingListResponse.java, ShoppingItemResponse.java
|- notification
|  |- service/       NotificationService.java, PushService.java, EmailService.java
|  |- scheduler/     StreakReminderScheduler.java
|- badge
|  |- service/       BadgeService.java
|  |- repository/    BadgeRepository.java, UserBadgeRepository.java
|  |- entity/        Badge.java, UserBadge.java
|- share
|  |- service/       ShareCardService.java
|- user
|  |- controller/    UserController.java
|  |- service/       UserService.java
|  |- dto/           UserProfileResponse.java, UpdateProfileRequest.java
|- common
   |- exception/     GlobalExceptionHandler.java, AppException.java, ErrorCode.java
   |- dto/           ApiResponse.java
   |- security/      JwtAuthFilter.java, SecurityConfig.java
   |- config/        RedisConfig.java, WebClientConfig.java, S3Config.java
```

---

## 8. BASE DE DATOS — ESQUEMA COMPLETO

### REGLA CRITICA DE TIMEZONE
Todas las columnas DATETIME se almacenan en UTC.
daily_logs.log_date almacena la fecha LOCAL del usuario (calculada con users.timezone antes de guardar).
La conversión al timezone local se hace SOLO en la capa de presentación.

```sql
-- USUARIOS
CREATE TABLE users (
    id               BIGINT PRIMARY KEY AUTO_INCREMENT,
    email            VARCHAR(255) NOT NULL UNIQUE,
    password_hash    VARCHAR(255) NOT NULL,
    name             VARCHAR(100) NOT NULL,
    profile_type     ENUM('VEGAN','VEGETARIAN','FLEXITARIAN','TRANSITIONING') NOT NULL,
    motivation       ENUM('HEALTH','ETHICS','FITNESS','CURIOSITY') NOT NULL,
    tone_pref        ENUM('MOTIVATIONAL','SARCASTIC','NEUTRAL') NOT NULL DEFAULT 'MOTIVATIONAL',
    experience_level ENUM('BEGINNER','EXPERIENCED') NOT NULL DEFAULT 'BEGINNER',
    budget_level     ENUM('LOW','NORMAL','HIGH') NOT NULL DEFAULT 'NORMAL',
    timezone         VARCHAR(50) NOT NULL DEFAULT 'UTC',
    notif_push       BOOLEAN NOT NULL DEFAULT TRUE,
    notif_email      BOOLEAN NOT NULL DEFAULT TRUE,
    is_active        BOOLEAN NOT NULL DEFAULT TRUE,
    deleted_at       DATETIME NULL,
    created_at       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- RACHAS (1 fila por usuario)
CREATE TABLE streaks (
    id                BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id           BIGINT NOT NULL UNIQUE,
    current_days      INT NOT NULL DEFAULT 0,
    record_days       INT NOT NULL DEFAULT 0,
    total_points      BIGINT NOT NULL DEFAULT 0,
    level             ENUM('SEED','SPROUT','PLANT','TREE','FOREST') NOT NULL DEFAULT 'SEED',
    last_checkin_date DATE NULL,
    streak_started_at DATETIME NULL,
    updated_at        DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- LOGS DIARIOS
CREATE TABLE daily_logs (
    id            BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id       BIGINT NOT NULL,
    log_date      DATE NOT NULL,
    category      ENUM('FOOD','RECIPE','REFLECTION','ACTIVITY') NOT NULL DEFAULT 'FOOD',
    notes         TEXT NULL,
    points_earned INT NOT NULL DEFAULT 0,
    created_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uq_user_date (user_id, log_date),
    FOREIGN KEY (user_id) REFERENCES users(id),
    INDEX idx_user_date (user_id, log_date)
);

-- PAUSAS DE RACHA
CREATE TABLE streak_pauses (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id     BIGINT NOT NULL,
    applied_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at  DATETIME NOT NULL,
    month_year  VARCHAR(7) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id),
    INDEX idx_user_month (user_id, month_year)
);

-- BADGES (catalogo)
CREATE TABLE badges (
    id            BIGINT PRIMARY KEY AUTO_INCREMENT,
    code          VARCHAR(50) NOT NULL UNIQUE,
    name          VARCHAR(100) NOT NULL,
    description   VARCHAR(255) NOT NULL,
    icon_url      VARCHAR(255) NULL,
    trigger_type  ENUM('STREAK_DAYS','TOTAL_POINTS','RECIPES_SAVED','PLANNER_COMPLETED','SPECIAL') NOT NULL,
    trigger_value INT NOT NULL
);

-- BADGES DEL USUARIO
CREATE TABLE user_badges (
    id         BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id    BIGINT NOT NULL,
    badge_id   BIGINT NOT NULL,
    earned_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uq_user_badge (user_id, badge_id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (badge_id) REFERENCES badges(id)
);

-- RECETAS
CREATE TABLE recipes (
    id             BIGINT PRIMARY KEY AUTO_INCREMENT,
    title          VARCHAR(200) NOT NULL,
    description    TEXT NULL,
    instructions   TEXT NOT NULL,
    prep_time_min  INT NOT NULL DEFAULT 0,
    cook_time_min  INT NOT NULL DEFAULT 0,
    servings       INT NOT NULL DEFAULT 2,
    difficulty     ENUM('EASY','MEDIUM','HARD') NOT NULL DEFAULT 'EASY',
    meal_type      ENUM('BREAKFAST','LUNCH','SNACK','DINNER') NOT NULL,
    diet_type      ENUM('VEGAN','VEGETARIAN','BOTH') NOT NULL DEFAULT 'VEGAN',
    budget_level   ENUM('LOW','NORMAL','HIGH') NOT NULL DEFAULT 'NORMAL',
    is_gluten_free BOOLEAN NOT NULL DEFAULT FALSE,
    is_soy_free    BOOLEAN NOT NULL DEFAULT FALSE,
    calories_approx INT NULL,
    protein_g      DECIMAL(5,1) NULL,
    iron_mg        DECIMAL(5,1) NULL,
    calcium_mg     DECIMAL(5,1) NULL,
    fat_g          DECIMAL(5,1) NULL,
    carbs_g        DECIMAL(5,1) NULL,
    image_url      VARCHAR(255) NULL,
    is_active      BOOLEAN NOT NULL DEFAULT TRUE,
    created_at     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_meal_type (meal_type),
    INDEX idx_diet_type (diet_type),
    INDEX idx_budget (budget_level)
);

-- INGREDIENTES (maestro)
CREATE TABLE ingredients (
    id             BIGINT PRIMARY KEY AUTO_INCREMENT,
    name           VARCHAR(100) NOT NULL,
    category       ENUM('GRAIN','PROTEIN','VEGETABLE','FRUIT','FAT','DAIRY_FREE','SEASONING','OTHER') NOT NULL,
    is_gluten_free BOOLEAN NOT NULL DEFAULT FALSE,
    is_soy_free    BOOLEAN NOT NULL DEFAULT FALSE,
    unit_default   VARCHAR(20) NOT NULL DEFAULT 'g',
    INDEX idx_category (category)
);

-- RECETA <-> INGREDIENTE
CREATE TABLE recipe_ingredients (
    id            BIGINT PRIMARY KEY AUTO_INCREMENT,
    recipe_id     BIGINT NOT NULL,
    ingredient_id BIGINT NOT NULL,
    quantity      DECIMAL(8,2) NOT NULL,
    unit          VARCHAR(20) NOT NULL,
    notes         VARCHAR(100) NULL,
    FOREIGN KEY (recipe_id) REFERENCES recipes(id),
    FOREIGN KEY (ingredient_id) REFERENCES ingredients(id),
    UNIQUE KEY uq_recipe_ingredient (recipe_id, ingredient_id)
);

-- RECETAS FAVORITAS DEL USUARIO
CREATE TABLE user_favorite_recipes (
    id         BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id    BIGINT NOT NULL,
    recipe_id  BIGINT NOT NULL,
    saved_at   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uq_user_recipe (user_id, recipe_id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (recipe_id) REFERENCES recipes(id)
);

-- ETIQUETAS NUTRICIONALES DE RECETAS (para el buscador)
CREATE TABLE recipe_tags (
    id        BIGINT PRIMARY KEY AUTO_INCREMENT,
    recipe_id BIGINT NOT NULL,
    tag       ENUM('HIGH_PROTEIN','RICH_IRON','RICH_CALCIUM','GLUTEN_FREE',
                   'SOY_FREE','QUICK','ECONOMIC','NUT_FREE') NOT NULL,
    FOREIGN KEY (recipe_id) REFERENCES recipes(id),
    INDEX idx_recipe_tag (tag)
);

-- ARMADO DE PLATOS — opciones por categoria
CREATE TABLE plate_options (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    category    ENUM('BASE','PROTEIN','VEGETABLE','SAUCE') NOT NULL,
    name        VARCHAR(100) NOT NULL,
    calories    INT NULL,
    protein_g   DECIMAL(5,1) NULL,
    fat_g       DECIMAL(5,1) NULL,
    icon_url    VARCHAR(255) NULL,
    is_active   BOOLEAN NOT NULL DEFAULT TRUE,
    INDEX idx_category (category)
);

-- PLANIFICADOR SEMANAL
CREATE TABLE week_plans (
    id         BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id    BIGINT NOT NULL,
    week_start DATE NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uq_user_week (user_id, week_start),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE week_plan_entries (
    id           BIGINT PRIMARY KEY AUTO_INCREMENT,
    week_plan_id BIGINT NOT NULL,
    day_of_week  ENUM('MONDAY','TUESDAY','WEDNESDAY','THURSDAY','FRIDAY','SATURDAY','SUNDAY') NOT NULL,
    meal_type    ENUM('BREAKFAST','LUNCH','SNACK','DINNER') NOT NULL,
    recipe_id    BIGINT NULL,
    plate_config JSON NULL,
    UNIQUE KEY uq_plan_day_meal (week_plan_id, day_of_week, meal_type),
    FOREIGN KEY (week_plan_id) REFERENCES week_plans(id),
    FOREIGN KEY (recipe_id) REFERENCES recipes(id)
);

-- LISTA DE SUPERMERCADO
CREATE TABLE shopping_lists (
    id           BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id      BIGINT NOT NULL,
    name         VARCHAR(100) NOT NULL DEFAULT 'Mi lista',
    week_plan_id BIGINT NULL,
    created_at   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (week_plan_id) REFERENCES week_plans(id)
);

CREATE TABLE shopping_list_items (
    id               BIGINT PRIMARY KEY AUTO_INCREMENT,
    shopping_list_id BIGINT NOT NULL,
    ingredient_id    BIGINT NOT NULL,
    total_quantity   DECIMAL(8,2) NOT NULL,
    unit             VARCHAR(20) NOT NULL,
    is_checked       BOOLEAN NOT NULL DEFAULT FALSE,
    FOREIGN KEY (shopping_list_id) REFERENCES shopping_lists(id),
    FOREIGN KEY (ingredient_id) REFERENCES ingredients(id)
);

-- NOTIFICACIONES (auditoria)
CREATE TABLE notifications (
    id        BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id   BIGINT NOT NULL,
    type      ENUM('PUSH_REMINDER','PUSH_MILESTONE','PUSH_STREAK_DANGER',
                   'EMAIL_WELCOME','EMAIL_SUMMARY') NOT NULL,
    status    ENUM('SENT','FAILED','OPENED') NOT NULL DEFAULT 'SENT',
    sent_at   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    opened_at DATETIME NULL,
    metadata  JSON NULL,
    FOREIGN KEY (user_id) REFERENCES users(id),
    INDEX idx_user_sent (user_id, sent_at)
);

-- REFERIDOS
CREATE TABLE referrals (
    id               BIGINT PRIMARY KEY AUTO_INCREMENT,
    referrer_user_id BIGINT NOT NULL,
    referred_user_id BIGINT NOT NULL UNIQUE,
    code             VARCHAR(20) NOT NULL UNIQUE,
    bonus_applied    BOOLEAN NOT NULL DEFAULT FALSE,
    created_at       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (referrer_user_id) REFERENCES users(id),
    FOREIGN KEY (referred_user_id) REFERENCES users(id)
);
```

---

## 9. CONTRATOS DE API

### Formato de respuesta universal
```json
// Éxito
{
  "success": true,
  "data": {},
  "message": "Descripción opcional",
  "timestamp": "2025-03-28T12:00:00Z"
}

// Error
{
  "success": false,
  "error": {
    "code": "ALREADY_CHECKED_IN",
    "message": "Ya hiciste check-in hoy."
  },
  "timestamp": "2025-03-28T12:00:00Z"
}
```

### Todos los endpoints
```
AUTH
POST   /api/v1/auth/register
POST   /api/v1/auth/login
POST   /api/v1/auth/refresh
POST   /api/v1/auth/logout
POST   /api/v1/auth/forgot-password
POST   /api/v1/auth/reset-password

USUARIO
GET    /api/v1/users/me
PUT    /api/v1/users/me
DELETE /api/v1/users/me
PUT    /api/v1/users/me/tone
PUT    /api/v1/users/me/notifications

RACHAS
POST   /api/v1/streaks/checkin
GET    /api/v1/streaks/me
GET    /api/v1/streaks/me/calendar
GET    /api/v1/streaks/me/history?page=0&size=20
POST   /api/v1/streaks/pause

SIR PIPPIN
GET    /api/v1/pippin/reaction?context=checkin
       context: checkin | milestone | comeback | reminder | plate_built | plan_completed

RECETAS
GET    /api/v1/recipes?mealType=LUNCH&tag=HIGH_PROTEIN&budget=LOW&glutenFree=true&page=0&size=20
GET    /api/v1/recipes/{id}
POST   /api/v1/recipes/{id}/favorite
DELETE /api/v1/recipes/{id}/favorite
GET    /api/v1/recipes/favorites

ARMADO DE PLATOS
GET    /api/v1/plates/options?category=BASE
GET    /api/v1/plates/options?category=PROTEIN
GET    /api/v1/plates/options?category=VEGETABLE
GET    /api/v1/plates/options?category=SAUCE
POST   /api/v1/plates/calculate
       body: { baseId, proteinId, vegetableId, sauceId }
       response: { calories, fat, protein, isBalanced, pippinReaction }

PLANIFICADOR SEMANAL
GET    /api/v1/planner/current
GET    /api/v1/planner/{weekStart}
PUT    /api/v1/planner/{weekStart}/entry
       body: { dayOfWeek, mealType, recipeId?, plateConfig? }
DELETE /api/v1/planner/{weekStart}/entry/{entryId}

LISTA DE SUPERMERCADO
POST   /api/v1/shopping/generate
GET    /api/v1/shopping/current
PATCH  /api/v1/shopping/items/{id}/check

BADGES
GET    /api/v1/badges
GET    /api/v1/badges/me

SHARE
POST   /api/v1/share/card
```

METER MAS PRUEBAS DE APIS A FUTURO

---

## 10. SEGURIDAD

### Flujo JWT + Redis
```
Login exitoso -> access_token (JWT, 15 min) + refresh_token (UUID, 30 días)
Redis key: "refresh:{token}" -> user_id (TTL 30 días)
Logout: DELETE "refresh:{token}" de Redis
Logout all: DELETE "refresh:*:{user_id}" de Redis
```

### Rate limiting (Spring Cloud Gateway)
```
Login/Register:          10 req/min por IP
Endpoints autenticados:  100 req/min por user_id
Check-in:                5 req/min por user_id
Generación de lista:     10 req/min por user_id
```

### Contraseñas
```
BCrypt cost factor: 12
Nunca loguear contraseñas, tokens ni emails completos
```

---

## 11. LÓGICA DE NEGOCIO CRÍTICA

### Algoritmo de check-in (CheckInService.java) — 11 pasos en orden exacto
```
1.  Obtener fecha LOCAL del usuario (timezone de users.timezone)
2.  Verificar duplicado: existe daily_log(user_id, log_date == hoy_local)? -> 409 ALREADY_CHECKED_IN
3.  Verificar pausa activa: existe streak_pause(user_id, expires_at > NOW())?
    Si hay pausa -> no penalizar ausencia de ayer
4.  Calcular continuidad de racha:
      last_checkin_date == ayer_local  -> current_days + 1
      last_checkin_date == NULL        -> current_days = 1 (primer check-in)
      cualquier otro caso              -> current_days = 1 (racha nueva)
5.  Calcular multiplicador:
      current_days >= 100 -> x3.0
      current_days >= 30  -> x2.0
      current_days >= 7   -> x1.5
      default             -> x1.0
      base_points = ${app.base-points}
6.  @Transactional: guardar DailyLog + actualizar Streak en un solo commit
7.  Actualizar record_days si current_days > record_days
8.  Calcular nivel por total_points:
      SEED:   0 - 99
      SPROUT: 100 - 499
      PLANT:  500 - 1999
      TREE:   2000 - 9999
      FOREST: 10000+
9.  Verificar hito de badge (días: 3, 7, 14, 30, 60, 100, 180, 365)
    -> Si alcanzado: BadgeTriggerService en @Async
10. Obtener reacción de Sir Pippin: PippinMessageService(context, tone, motivation, days)
11. Devolver CheckInResponse: { newDays, pointsEarned, currentLevel, badgeUnlocked?, pippinReaction }
```

### Algoritmo de Lista de Supermercado (ShoppingListService.java)
```
1. Obtener planificador semana actual del usuario
2. Recolectar todos los recipe_id de los entries
3. Para cada recipe_id, obtener recipe_ingredients (ingredient_id, quantity, unit)
4. Agrupar por ingredient_id sumando quantities del mismo unit
5. Si hay unidades distintas para el mismo ingrediente: crear dos items separados (no convertir en MVP)
6. Ordenar por category: GRAIN -> PROTEIN -> VEGETABLE -> FRUIT -> FAT -> OTHER
7. @Transactional: crear ShoppingList + ShoppingListItems
8. Devolver lista completa con is_checked = false
```

### CRON de recordatorios (StreakReminderScheduler.java)
```
@Scheduled(cron = "0 0 * * * *") <- cada hora exacta

Por cada hora H:
1. Buscar users cuya hora local actual == 20:00
2. Filtrar: NO tienen daily_log con log_date == hoy_local
3. Sub-filtro A: last_checkin_date == ayer_local -> PUSH_REMINDER
4. Sub-filtro B: last_checkin_date < ayer_local  -> PUSH_STREAK_DANGER
5. No enviar si ya se envió el mismo tipo en las últimas 20hs
```

---

## 12. BANCO DE MENSAJES DE SIR PIPPIN

Los mensajes viven en resources/pippin/messages_es.json y messages_en.json.
NO hardcodear mensajes en código Java.

### Estructura del JSON
```json
{
  "checkin": {
    "MOTIVATIONAL": {
      "HEALTH": [
        "¡Muy bien! Tu cuerpo te lo agradece.",
        "Un día más, un paso más hacia tu mejor versión."
      ],
      "FITNESS": [
        "Check-in done. Tu músculo plant-based está creciendo.",
        "Proteína vegetal al poder."
      ],
      "ETHICS": [
        "Otro día, otro impacto positivo en el planeta.",
        "Los animales también te lo agradecen."
      ],
      "CURIOSITY": [
        "¡Chequeado! ¿Probaste algo nuevo hoy?",
        "Seguís explorando. Eso me gusta."
      ]
    },
    "SARCASTIC": {
      "HEALTH": ["Mirá vos, apareciste.", "Uy, hoy te acordaste. Impresionante."],
      "FITNESS": ["Bien, no te olvidaste de existir.", "A ver cuánto durás."],
      "ETHICS": ["El planeta sigue girando, en parte gracias a vos.", "Otro día sin caer."],
      "CURIOSITY": ["Apareciste. Sorpresa.", "Mirá qué constante estás siendo."]
    },
    "NEUTRAL": {
      "HEALTH": ["Check-in registrado. Seguís en racha."],
      "FITNESS": ["Anotado. Llevas N días."],
      "ETHICS": ["Registrado correctamente."],
      "CURIOSITY": ["Listo. Un día más."]
    }
  },
  "milestone_7": { ... },
  "milestone_30": { ... },
  "milestone_100": { ... },
  "comeback": { ... },
  "reminder": { ... },
  "plate_built": { ... },
  "plan_completed": { ... }
}
```

### Regla de selección de mensaje
1. Buscar por: context -> tone -> motivation
2. Elegir aleatoriamente de la lista
3. Verificar que no sea el mismo que el último mensaje enviado (last_message_key en Redis)

---

## 13. VARIABLES DE ENTORNO

### Backend (application.properties con valores reales en .env — nunca commitear .env)
```properties
spring.datasource.url=${DB_URL:jdbc:mysql://localhost:3306/veganapp}
spring.datasource.username=${DB_USERNAME:veganapp_user}
spring.datasource.password=${DB_PASSWORD:dev_password}

spring.data.redis.host=${REDIS_HOST:localhost}
spring.data.redis.port=${REDIS_PORT:6379}

app.jwt.secret=${JWT_SECRET:dev_secret_minimo_256_bits}
app.jwt.expiration-ms=${JWT_EXPIRATION_MS:900000}
app.jwt.refresh-expiration-days=${REFRESH_EXPIRATION_DAYS:30}

app.fcm.server-key=${FCM_SERVER_KEY:}
app.resend.api-key=${RESEND_API_KEY:}
app.resend.from=${RESEND_FROM:hola@veganapp.com}

app.s3.endpoint=${S3_ENDPOINT:http://localhost:9000}
app.s3.access-key=${S3_ACCESS_KEY:minioadmin}
app.s3.secret-key=${S3_SECRET_KEY:minioadmin}
app.s3.bucket=${S3_BUCKET:veganapp-assets}

app.base-points=${APP_BASE_POINTS:10}
app.max-streak-pauses-per-month=${APP_MAX_PAUSES:2}

management.endpoints.web.exposure.include=health,info,metrics,prometheus
```

### Mobile (.env React Native)
```
API_BASE_URL_DEV=http://10.0.2.2:8080/api/v1
API_BASE_URL_PROD=https://api.veganapp.com/api/v1
FIREBASE_PROJECT_ID=veganapp-prod
```

---

## 14. DOCKER COMPOSE — ENTORNO LOCAL

```yaml
version: '3.8'
services:
  mysql:
    image: mysql:8.0
    environment:
      MYSQL_DATABASE: veganapp
      MYSQL_USER: veganapp_user
      MYSQL_PASSWORD: dev_password
      MYSQL_ROOT_PASSWORD: root_dev
      TZ: UTC
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql
      - ./sql/init.sql:/docker-entrypoint-initdb.d/init.sql

  redis:
    image: redis:7.2-alpine
    ports:
      - "6379:6379"
    command: redis-server --appendonly yes
    volumes:
      - redis_data:/data

  minio:
    image: minio/minio
    command: server /data --console-address ":9001"
    ports:
      - "9000:9000"
      - "9001:9001"
    environment:
      MINIO_ROOT_USER: minioadmin
      MINIO_ROOT_PASSWORD: minioadmin
    volumes:
      - minio_data:/data

  prometheus:
    image: prom/prometheus:latest
    ports:
      - "9090:9090"
    volumes:
      - ./infra/prometheus.yml:/etc/prometheus/prometheus.yml

  grafana:
    image: grafana/grafana:latest
    ports:
      - "3000:3000"
    environment:
      GF_SECURITY_ADMIN_PASSWORD: admin
    depends_on:
      - prometheus
    volumes:
      - grafana_data:/var/lib/grafana

volumes:
  mysql_data:
  redis_data:
  minio_data:
  grafana_data:
```

---

## 15. DECISIONES DE ARQUITECTURA (ADR)

| ADR | Decisión | Razón | Cuándo revisar |
|-----|----------|-------|----------------|
| 001 | Spring MVC (no WebFlux) | Stack conocido, debugging simple | Si supera 10k req/s |
| 002 | Monolito modular (no microservicios) | Velocidad en MVP, extracción fácil en Fase 2 | Sprint 8 con datos reales |
| 003 | @Scheduled (no Kafka) | Sin overhead para < 50k usuarios | Si hay lag medible |
| 004 | log_date = fecha LOCAL del usuario | Evitar bugs de racha por diferencia UTC | No cambiar sin migración |
| 005 | Frontend desde Stitch (no diseñar) | Pantallas ya aprobadas, ahorra días | No aplica |
| 006 | Mensajes de Pippin en JSON (no DB) | Más fácil editar copy sin deploy | Si superan 1000 mensajes |
| 007 | Shopping list desde planificador | Flujo natural; usuario entiende de dónde viene | Agregar modo manual Fase 2 |

---

## 16. LO QUE EL AGENTE IA NUNCA DEBE HACER

1. NO usar LocalDateTime sin timezone para datos de usuario
2. NO poner lógica de negocio en Controllers ni en Repositories
3. NO devolver Entity JPA directamente en un endpoint (siempre DTO)
4. NO hacer queries N+1 (usar JOIN FETCH o @EntityGraph)
5. NO agregar @Transactional en Controllers
6. NO usar System.out.println (usar SLF4J: log.info/warn/error)
7. NO hardcodear mensajes de Sir Pippin en código Java (están en JSON)
8. NO hardcodear puntos, niveles ni días de hito (están en application.properties)
9. NO crear endpoint sin test de integración
10. NO usar Optional.get() sin verificar isPresent() primero
11. NO generar clase de más de 150 líneas sin proponer refactorización
12. NO rediseñar las pantallas — el diseño de Stitch es el final
13. NO sugerir imágenes alternativas de Sir Pippin — el asset ya existe
14. NO modificar el esquema DB sin script en sql/migrations/
15. NO commitear credenciales, tokens ni API keys

---

## 17. HISTORIAL DE CAMBIOS

| Versión | Cambio |
|---------|--------|
| 1.0 | Creación inicial |
| 2.0 | Incorporación completa del cuaderno: Armado de Platos, Planificador, Lista de Supermercado, nutrientes detallados, filtros de búsqueda, onboarding de 5 preguntas. Assets de Sir Pippin y pantallas de Stitch marcados como existentes. Timeline de 14 días agregado. |

Este archivo es un documento vivo. Actualizar con cada decisión técnica relevante.
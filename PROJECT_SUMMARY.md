# VeganApp – Resumen Técnico y Estado Actual

> Este documento resume el contexto real del proyecto, los puntos débiles que hoy impiden considerarlo "listo para producción" y qué se refactorizó para poder emularlo mejor.

---

## 1. Visión del Producto

VeganApp es una app móvil que gamifica hábitos veganos mediante:

- Sistema de rachas diarias (check‑in en 11 pasos)
- Planificador semanal de comidas
- Recetas filtrables y lista de supermercado consolidada
- Personaje central: **Sir Pippin**, que reacciona al comportamiento del usuario

Objetivo: MVP sólido en Android (emulador/dispositivo) con backend estable para iterar sobre métricas reales, NO un "demo frágil".

---

## 2. Arquitectura Actual

### 2.1 Backend (carpeta `backend/`)

- **Stack**: Java 21, Spring Boot 3.3.x, Spring MVC
- **Módulos principales (`com.veganapp`)**:
  - `auth/` – login, registro, JWT, refresh tokens
  - `user/` – perfil, preferencias, FCM token
  - `streak/` – rachas, check‑in, pausas
  - `pippin/` – lógica de mensajes de Sir Pippin
  - `recipe/` – recetas y filtros
  - `plate/` – armado de platos
  - `planner/` – planificador semanal
  - `shopping/` – lista de supermercado
  - `badge/` – logros
  - `notification/` – notificaciones (mail/push)
  - `test/` – endpoints utilitarios
  - `common/` – errores, filtros, utilidades

- **Persistencia actual**:
  - Driver: **MySQL 8** (`com.mysql.cj.jdbc.Driver`)
  - URL por defecto: `jdbc:mysql://localhost:3306/veganapp?...`
  - Migraciones Flyway (`backend/src/main/resources/db/migration/V1__...sql`) escritas en sintaxis MySQL (ENGINE=InnoDB, AUTO_INCREMENT, DATETIME, etc.)

- **Configuración clave** (`backend/src/main/resources/application.properties` + `application-dev.properties`):
  - `server.address=0.0.0.0` → acepta conexiones externas (Docker, emulador)
  - `spring.jpa.hibernate.ddl-auto=validate` → esquema controlado por Flyway
  - CORS abierto en `dev` (`allowed-origins=*`)
  - JWT, FCM, Resend, S3 configurables vía propiedades

- **Infraestructuras auxiliares** (vía `docker-compose.yml` en raíz):
  - MySQL, Redis, MinIO, Prometheus, Grafana

### 2.2 Frontend (carpeta `frontend/`)

- **Stack**: React Native 0.73.6 (CLI bare) + TypeScript
- **Estado global**: Zustand (`src/core/store/authStore.ts`)
- **HTTP**: Axios + `apiClient` (`src/core/api/client.ts`) con:
  - Base URL dev: `http://10.0.2.2:8080/api` (correcto para emulador Android)
- **Navegación** (`src/navigation/`):
  - `RootNavigator.tsx` – decide entre Login y AppStack según `isAuthenticated`
  - `AppStackNavigator.tsx` – tabs principales (Home, Recetas, Shopping, Perfil)
- **Pantallas actuales** (`src/features/`):
  - `auth/LoginScreen.tsx` – login con validaciones básicas
  - `home/HomeScreen.tsx` – dashboard (stats + estado backend)
  - `recipes/RecipeListScreen.tsx` – listado de recetas
  - `shopping/ShoppingListScreen.tsx` – lista de compras
  - `profile/ProfileScreen.tsx` – perfil + logout

- **Entry points**:
  - `App.tsx` – envuelve `RootNavigator` en `GestureHandlerRootView` + `SafeAreaProvider`
  - `index.js` – registra `App` con `AppRegistry` (ya sin duplicados)

- **Nota importante**: la carpeta `frontend/android/` se eliminó a pedido tuyo. Eso significa que la parte nativa Android deberá regenerarse (por ejemplo, con una plantilla limpia de React Native CLI 0.73.x) antes de poder compilar APKs nativos directamente.

### 2.3 Infraestructura (raíz del repo)

- `.env` – variables para MySQL, Redis, MinIO, puertos y perfil Spring
- `docker-compose.yml` – levanta MySQL, Redis, MinIO, Prometheus, Grafana vinculados a `0.0.0.0`
- `README.md` – guía rápida actualizada (backend + frontend + emulador)

---

## 3. Puntos Débiles / Bloqueantes para Producción

### 3.1 Backend

1. **Proveedor de BD vs diseño original**
   - Diseño conceptual hablaba de PostgreSQL 16; el código real usa **MySQL 8** (migraciones, driver, URL).
   - No es un bug funcional, pero sí una desviación de arquitectura a documentar (o revertir en el futuro con una migración seria).

2. **Secrets y configuración sensible en `application.properties`**
   - `JWT_SECRET`, `FCM_SERVER_KEY`, `RESEND_API_KEY`, `S3_ACCESS_KEY`, etc. tienen valores por defecto tipo `dev_*`.
   - Para producción real deben provenir **exclusivamente de variables de entorno / vault**.

3. **CORS permisivo en dev**
   - `spring.web.cors.allowed-origins=*` está bien para desarrollo, pero en producción debería limitarse a los dominios reales y apps móviles conocidas.

4. **Logging muy verboso**
   - `logging.level.org.springframework.security=DEBUG` y SQL en DEBUG/TRACE.
   - En producción conviene bajar a INFO/WARN y activar logging estructurado + auditoría puntual.

5. **Seguridad avanzada pendiente**
   - Falta rate‑limiting, protección anti‑brute force y endurecimiento de cabeceras HTTP (CSP, HSTS, etc.).

### 3.2 Frontend

1. **Carpeta `android/` ausente**
   - Se eliminó para limpiar el repo de una configuración nativa potencialmente corrupta.
   - Sin `android/`, no se puede construir el APK directamente; hay que regenerar el proyecto nativo.

2. **Integraciones no completamente desplegadas**
   - FCM, MMKV avanzado, Sir Pippin animado, etc., no están presentes en el código actual del repo (pertenecen al diseño objetivo, no al estado actual).

3. **Faltan capas de robustez para producción**
   - Manejo de errores global (error boundary), timeouts de red ajustados por entorno, analítica, logging de eventos clave, etc.

### 3.3 DevOps / Operación

- No hay pipeline CI/CD definido en el repo (build + tests + quality gate + deploy).
- No hay scripts automatizados para generar y firmar APKs release.

---

## 4. Qué se Refactorizó en Esta Iteración

1. **Normalización del backend (MySQL)**
   - `backend/pom.xml` actualizado para usar `mysql-connector-j` como driver runtime.
   - Plugin de Flyway configurado a `jdbc:mysql://localhost:3306/veganapp` con driver `com.mysql.cj.jdbc.Driver`.
   - Esto alinea: `application.properties` + Flyway + `docker-compose.yml` + `.env`.

2. **Normalización del frontend para el emulador Android**
   - `frontend/src/core/api/client.ts` → base URL dev: `http://10.0.2.2:8080/api` (emulador ↔ host).
   - `frontend/App.tsx` → ahora importa `./src/navigation/RootNavigator` (ya no apunta al backend) y envuelve correctamente con `GestureHandlerRootView` + `SafeAreaProvider`.
   - `frontend/src/navigation/RootNavigator.tsx` → versión única y coherente, usando `useAuthStore` del frontend y dirigiendo a `LoginScreen` o `AppNavigator`.
   - `frontend/index.js` → se eliminaron duplicados; hay un único registro de `App` en `AppRegistry`.

3. **Documentación alineada al estado actual**
   - `README.md` actualizado a:
     - Reflejar que la BD actual es MySQL (no PostgreSQL).
     - Explicar cómo levantar el backend con `cd backend && mvn spring-boot:run`.
     - Explicar cómo levantar el frontend con `npm start` + `npm run android`.
     - Describir correctamente la estructura `backend/` y `frontend/`.

4. **Limpieza de archivos Markdown en raíz**
   - Se mantiene sólo `README.md` + este `PROJECT_SUMMARY.md` como fuente principal de verdad.

---

## 5. Cómo Emular la App en Android (flujo recomendado)

### 5.1 Infraestructura + Backend

```bash
# Desde la raíz del repo
# 1) Levantar MySQL, Redis, MinIO, Prometheus, Grafana
docker-compose up -d

# 2) Backend Spring Boot
cd backend
mvn clean install
mvn spring-boot:run
# API host:       http://localhost:8080/api
# API emulador:   http://10.0.2.2:8080/api
```

### 5.2 Frontend (emulador Android)

```bash
cd frontend

# 1) Instalar dependencias
npm install

# 2) Iniciar Metro bundler
npm start

# 3) Lanzar la app en el emulador
npm run android
# o
npx react-native run-android
```

> Nota: hoy la carpeta `frontend/android/` no existe en el repo. Si el comando `npm run android` falla por falta del proyecto nativo, deberás generar uno nuevo con React Native CLI 0.73.x o restaurar la carpeta desde un commit previo conocido.

---

## 6. Próximos Pasos Recomendados (para producción)

1. **Decidir definitivamente el proveedor de base de datos**
   - Si el ADR de PostgreSQL 16 sigue vigente, habrá que migrar los scripts Flyway y la config.
   - Si se adopta MySQL 8 como estándar, actualizar documentación de arquitectura y ADRs.

2. **Externalizar configuración sensible**
   - Quitar valores por defecto para `JWT_SECRET`, claves de S3, FCM, Resend.
   - Usar `.env` + variables de entorno + (idealmente) un vault de secretos.

3. **Endurecer CORS y logging para producción**
   - Orígenes específicos, no `*`.
   - Reducir logging a INFO/WARN en componentes sensibles.

4. **Completar el frontend según el diseño objetivo**
   - Integrar Sir Pippin animado (Reanimated + SVG).
   - Conectar todas las pantallas con los endpoints reales.
   - Añadir manejo de errores global y analítica básica.

5. **Definir un pipeline CI/CD mínimo**
   - Backend: build + tests + report de cobertura + Sonar (opcional) + empaquetado.
   - Frontend: TypeScript check + lint + build Android debug/release.

Con estos pasos, el proyecto pasa de "beta avanzada" a una base sólida para salir a producción con menos riesgo y mejor capacidad de observación y operación.

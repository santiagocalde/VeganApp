# 🚀 Setup Guía Rápida - Backend + Frontend Integrado

## Estado Actual ✅
- **Backend**: Completo y funcional (Spring Boot 3, Java 21)
- **Frontend**: Base limpia con API integration lista (React Native 0.73.6)
- **BD**: PostgreSQL con Flyway migrations (v1-v3)
- **Git**: Todos los cambios commiteados

---

## 1️⃣ Iniciar Backend (Terminal 1)

```bash
cd backend
./mvnw spring-boot:run
```

Esperado:
```
...
Started VeganAppApplication in X.XXX seconds
Tomcat started on port(s): 8080
```

Verificar: `curl http://localhost:8080/api/health`

---

## 2️⃣ Instalar Dependencias Frontend (Terminal 2)

```bash
cd frontend
npm install
```

Esperado: Sin errores, ~400 packages

---

## 3️⃣ Iniciar Metro Bundler (Terminal 3)

```bash
cd frontend
npm start
```

Esperado:
```
Welcome to Metro...
To reload the app press r
To open developer menu press d
```

---

## 4️⃣ Construir y Instalar APK en Emulator (Terminal 4)

```bash
cd frontend
./gradlew assembleDebug    # Compilar APK
# Luego: Android Studio > emulator > instalar APK
```

O:
```bash
./gradlew installDebug  # Si emulator ya está corriendo
```

---

## 📱 Qué Verás en la App

1. **Pantalla inicial**: "VeganApp" con estado del backend
2. **Si backend está corriendo**: ✅ Connected status
3. **Botón "Retry Connection"**: Verificar conexión bajo demanda
4. **URL mostrada**: http://localhost:8080/api

---

## 🔧 Estructura API (Ya Implementada)

| Servicio | Endpoints |
|----------|-----------|
| **Auth** | `POST /api/auth/login`, `POST /api/auth/register` |
| **Health** | `GET /api/health` |
| **Recipes** | `GET /api/recipes`, `POST /api/recipes/{id}` |
| **Shopping** | `GET /api/shopping-list`, `POST /api/shopping-list` |
| **Planner** | `GET /api/planner`, `POST /api/planner` |

---

## 🛠 Archivo Mapping

```
frontend/
├── src/
│   ├── core/
│   │   ├── api/
│   │   │   ├── client.ts          ← Axios instance (baseURL, interceptors)
│   │   │   ├── authService.ts     ← Login/Register/Health
│   │   │   ├── recipeService.ts   ← Recipes CRUD
│   │   │   ├── shoppingService.ts ← Shopping list CRUD
│   │   │   └── plannerService.ts  ← Meal planning CRUD
│   │   └── store/
│   │       └── authStore.ts       ← Zustand auth state
│   └── navigation/
│       └── AppNavigator.tsx       ← Status display + retry button
└── App.tsx                        ← Root component
```

---

## 🐛 Debugging

**Error: "cannnot connect to localhost:8080"**
- ✅ Backend no está corriendo? Ejecutar primero: `./mvnw spring-boot:run`

**Error: "ENOTFOUND localhost"**
- Android emulator usa `10.0.2.2` en lugar de `localhost`
- Actualizar en `frontend/src/core/api/client.ts`: baseURL a `http://10.0.2.2:8080/api`

**Metro bundler no inicia**
- Limpiar cache: `npm start -- --reset-cache`

**APK no instala**
- Limpiar build: `./gradlew clean`
- Intentar de nuevo: `./gradlew assembleDebug`

---

## ✨ Próximos Pasos

1. **Pantallas de UI** (Sin hacer aún):
   - LoginScreen.tsx / RegisterScreen.tsx
   - HomeScreen.tsx (Dashboard)
   - RecipeListScreen.tsx
   - ShoppingListScreen.tsx
   - Navigation tabs

2. **Conectar Screens a APIs**:
   - Usar `authService`, `recipeService`, etc.
   - Mostrar datos desde backend en listas

3. **Testing End-to-End**:
   - Login en app → verificar en DB backend
   - Crear receta → ver en app
   - Shopping list sync

---

## 📝 Notas Importantes

- **Token JWT**: Auth store está listo para guardar token (descomentar interceptor)
- **Axios Interceptors**: Request/Response setup completo para auth handling
- **Error Handling**: Ya implementado para 401 unauthorized
- **Zustand Store**: Gestor de estado listo, solo falta conectar a pantallas

---

## Commit Actual

```
a32dff0 FEAT: Backend-Frontend API integration layer
```

✅ Todos los servicios, stores, y API client están listos para usar en componentes React.

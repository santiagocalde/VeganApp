# 🌱 VeganApp - Mobile Application

> **A comprehensive vegan nutrition and recipe management mobile app built with React Native and Spring Boot**

## 📱 Overview

VeganApp is a full-stack mobile application that helps vegans manage recipes, meal planning, shopping lists, and track achievements. The app connects to a Spring Boot backend with MySQL database and Redis/MinIO support.

**Current Version**: v1.5  
**Status**: ⚠️ Beta (no lista para producción)  
**Last Updated**: April 10, 2026

---

## 🚀 Quick Start (5 minutes)

### Prerequisites
- Java 21 LTS
- Node.js 18+
- Maven 3.9+
- Docker & Docker Compose
- Android SDK (for APK building)

### Backend Setup

```bash
# 1. Start infrastructure (MySQL, Redis, MinIO, Prometheus, Grafana)
docker-compose up -d

# 2. Install & Run Backend
cd backend
mvn clean install
mvn spring-boot:run

# Backend API (host):       http://localhost:8080/api
# Backend API (emulador):   http://10.0.2.2:8080/api
```

### Frontend Setup

```bash
cd frontend

# 3. Install dependencies
npm install

# 4. Start Metro bundler (Terminal A)
npm start

# 5. Launch app on Android emulator (Terminal B)
npm run android
# or
npx react-native run-android
```

**Expected Result**: App launches with login screen, connects to backend ✅

---

## 📁 Project Structure

```
VeganApp/
├── backend/                              (Spring Boot backend)
│   ├── pom.xml
│   └── src/main/java/com/veganapp/
│       ├── auth/                         JWT authentication
│       ├── badge/                        Gamification
│       ├── notification/                 Notifications
│       ├── recipe/                       Recipe management
│       ├── planner/                      Meal planning
│       ├── plate/                        Plate builder
│       ├── streak/                       Achievement tracking
│       ├── user/                         User management
│       ├── pippin/                       Sir Pippin domain
│       ├── common/                       Utilities & exceptions
│       └── VeganAppApplication.java      Main class
│
├── frontend/                            (React Native mobile app)
│   ├── src/
│   │   ├── navigation/
│   │   │   ├── RootNavigator.tsx         Auth flow management
│   │   │   └── AppStackNavigator.tsx     Bottom tabs navigation
│   │   ├── features/
│   │   │   ├── auth/LoginScreen.tsx
│   │   │   ├── home/HomeScreen.tsx
│   │   │   ├── recipes/RecipeListScreen.tsx
│   │   │   ├── shopping/ShoppingListScreen.tsx
│   │   │   └── profile/ProfileScreen.tsx
│   │   └── core/
│   │       ├── api/                      API services (5 total)
│   │       └── store/                    Zustand state management
│   ├── App.tsx                          Root component
│   ├── index.js                        Entry file for React Native
│
├── docker-compose.yml                    MySQL + Redis + MinIO + Monitoring
└── README.md                             This file
```

---

## 📱 App Features

### Authentication
- ✅ Email/Password login
- ✅ JWT token management
- ✅ Auto-logout on token expiry
- TODO: Social login, password reset

### Dashboard (Home)
- ✅ User greeting
- ✅ Statistics cards (recipes, saved, streak)
- ✅ Backend connection status
- ✅ Quick action buttons

### Recipes
- ✅ Browse all recipes
- ✅ View recipe details
- ✅ Loading & error states

### Shopping List
- ✅ View shopping items
- ✅ Check/uncheck items
- ✅ Delete items
- ✅ Clear completed

### Profile
- ✅ View user info
- ✅ Settings panel
- ✅ Logout functionality

---

## 🚀 Quick Commands

```bash
# Backend
cd backend
mvn spring-boot:run

# Frontend
cd frontend
npm install
npm start
npm run android

# Database / Infra
cd ..
docker-compose up -d
docker-compose down
```

---

## 🛠️ Tech Stack

| Layer | Technology |
|-------|-----------|
| Backend | Java 21, Spring Boot 3, MySQL 8, Redis, MinIO |
| Frontend | React Native 0.73.6, TypeScript |
| State | Zustand |
| HTTP | Axios |
| Navigation | React Navigation 6 |

---

## ✅ Project Status

- ✅ Backend completo (11 módulos funcionales, stack MySQL)
- ✅ Frontend funcional (6 pantallas principales + navegación)
- ✅ Conectividad emulador Android ↔ backend (10.0.2.2)
- ⚠️ Falta pipeline CI/CD y hardening de seguridad para producción
- ⚠️ Claves y secretos deben externalizarse en entornos productivos

---

**Built with ❤️ for the vegan community | v1.5 | April 2026**

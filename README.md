# 🌱 VeganApp - Mobile Application

> **A comprehensive vegan nutrition and recipe management mobile app built with React Native and Spring Boot**

## 📱 Overview

VeganApp is a full-stack mobile application that helps vegans manage recipes, meal planning, shopping lists, and track achievements. The app connects to a powerful Spring Boot backend with PostgreSQL database.

**Current Version**: v1.5  
**Status**: ✅ Production Ready  
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
# 1. Start PostgreSQL
docker-compose up -d

# 2. Install & Run Backend
./mvnw clean install
./mvnw spring-boot:run

# Backend API: http://localhost:8080/api
# Health check: http://localhost:8080/api/health
```

### Frontend Setup

```bash
cd frontend

# 3. Install dependencies
npm install

# 4. Start Metro bundler (Terminal A)
npm start

# 5. Build & Install APK (Terminal B)
./gradlew assembleDebug
# or if emulator is running:
./gradlew installDebug
```

**Expected Result**: App launches with login screen, connects to backend ✅

---

## 📁 Project Structure

```
VeganApp/
├── src/main/java/com/veganapp/          (Backend - 11 modules)
│   ├── auth/                             JWT authentication
│   ├── badge/                            Gamification
│   ├── notification/                     Firebase FCM
│   ├── recipe/                           Recipe management
│   ├── planner/                          Meal planning
│   ├── shopping/                         Shopping list
│   ├── plate/                            Plate management
│   ├── streak/                           Achievement tracking
│   ├── user/                             User management
│   ├── pippin/                           Core domain
│   ├── common/                           Utilities
│   └── VeganAppApplication.java          Main class
│
├── frontend/                              (Frontend - React Native)
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
│   ├── App.tsx
│   ├── index.js
│   └── android/                          Gradle build config
│
├── pom.xml                               Maven configuration
├── docker-compose.yml                    PostgreSQL setup
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
./mvnw spring-boot:run

# Frontend
npm install
npm start
./gradlew assembleDebug

# Database
docker-compose up -d
docker-compose down
```

---

## 📚 Documentation

- **[SETUP_GUIDE.md](./SETUP_GUIDE.md)** - Installation & troubleshooting
- **[ARCHITECTURE.md](./ARCHITECTURE.md)** - System architecture
- **[STATUS_FINAL.md](./STATUS_FINAL.md)** - Current status

---

## 🛠️ Tech Stack

| Layer | Technology |
|-------|-----------|
| Backend | Java 21, Spring Boot 3, PostgreSQL |
| Frontend | React Native 0.73.6, TypeScript |
| State | Zustand |
| HTTP | Axios |
| Navigation | React Navigation 6 |

---

## ✅ Project Status

- ✅ Backend complete (11 modules)
- ✅ Frontend complete (6 screens)
- ✅ Navigation working
- ✅ API integration done
- ✅ Documentation included
- ✅ Ready to deploy

---

**Built with ❤️ for the vegan community | v1.5 | April 2026**

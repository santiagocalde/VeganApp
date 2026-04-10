# Android Emulator ↔ Backend Connectivity Guide

## El Problema (¿Por qué `localhost` no funciona?)

El emulador de Android es una **máquina virtual con su propia interfaz de red** aislada de tu PC. Para el emulador:

- `localhost` → **él mismo**, no tu PC
- `127.0.0.1` → **él mismo**, no tu PC
- Tu PC (donde corre el backend) → es una máquina **externa**

Por eso, el emulador **no puede alcanzar** `http://localhost:8080` en tu máquina.

## La Solución

### 1. **IP Especial: 10.0.2.2**

Android proporciona un alias especial: **`10.0.2.2`** que apunta al "host" (tu máquina).

```typescript
// ❌ INCORRECTO (no funciona en emulador)
const API_BASE_URL = 'http://localhost:8080/api';

// ✅ CORRECTO (funciona en emulador)
const API_BASE_URL = 'http://10.0.2.2:8080/api';
```

**Implementado en**: `frontend/src/core/api/client.ts`

### 2. **Backend Escucha en Todas las Interfaces**

Si el backend escucha en `127.0.0.1` (localhost), **rechaza conexiones externas** (incluyendo del emulador).

Solución: Backend debe escuchar en `0.0.0.0` (todas las interfaces de red).

```properties
# ✅ CORRECTO
server.address=0.0.0.0
server.port=8080

# ❌ INCORRECTO (solo localhost)
# server.address=127.0.0.1
```

**Implementado en**: `src/main/resources/application.properties`

### 3. **CORS (Cross-Origin Resource Sharing)**

React Native no enforza CORS como lo hace un navegador web, **pero** algunos interceptores y librerías sí hacen validaciones.

En desarrollo, permitimos todos los orígenes:

```properties
# ─── CORS (desarrollo: permitir todos los orígenes)
spring.web.cors.allowed-origins=*
spring.web.cors.allowed-methods=GET,POST,PUT,DELETE,OPTIONS,PATCH
spring.web.cors.allowed-headers=*
spring.web.cors.allow-credentials=false
spring.web.cors.max-age=3600
```

**Implementado en**: `src/main/resources/application-dev.properties`

Para **producción**, acota los orígenes permitidos:

```properties
spring.web.cors.allowed-origins=https://app.tudominio.com
```

## Arquitectura de Red

```
┌─ Tu PC (Windows) ─────────────────────┐
│  Backend Spring Boot                  │
│  Escucha en: 0.0.0.0:8080             │ ← Todas las interfaces
│                                       │
│  ┌─ Docker Container ────────────────┐│
│  │ Microservicios + PostgreSQL       ││
│  └───────────────────────────────────┘│
└───────────────────────────────────────┘
         ↑ 10.0.2.2:8080
         │ (alias especial)
         │
    ┌────────────────────────┐
    │ Android Emulator (VM)  │
    │ React Native App       │
    │ Intenta conectar a:    │
    │ http://10.0.2.2:8080/api
    └────────────────────────┘
```

## Checklist para Desarrollo Local

- [x] Frontend usa `10.0.2.2:8080` (vía `__DEV__` flag)
- [x] Backend escucha en `0.0.0.0` (dentro de Docker)
- [x] CORS habilitado en `application-dev.properties`
- [x] Puerto 8080 mapeado en `docker-compose.yml`
- [x] Emulador ejecutándose y con red funcional

## Comandos para Verificar

```bash
# 1. Verificar que el backend escucha en todas las interfaces
docker ps  # Confirmar que el contenedor corre
docker logs <container-id>  # Ver que escucha en 0.0.0.0:8080

# 2. Desde el emulador (en adb shell)
ping 10.0.2.2  # Debe responder
curl http://10.0.2.2:8080/api/health  # Debe retornar 200

# 3. Verificar CORS en respuesta
curl -H "Origin: http://10.0.2.2" \
     -H "Access-Control-Request-Method: POST" \
     -H "Access-Control-Request-Headers: Content-Type" \
     -X OPTIONS http://localhost:8080/api/auth/login
# Debe incluir Access-Control-Allow-* headers
```

## Diferencias por Entorno

| Entorno | API URL | server.address | CORS |
|---------|---------|----------------|------|
| **Development (Emulator)** | `http://10.0.2.2:8080/api` | `0.0.0.0:8080` | `*` (wildcard) |
| **Production (APK on Device)** | `https://api.veganapp.com/api` | Depende del hosting | Dominios específicos |

## Notas Importantes

1. **10.0.2.2 solo funciona en emulador**: En dispositivo físico, usar la IP local de tu PC (ej: `192.168.1.100:8080`)
2. **Docker + Emulator**: Si el backend corre en Docker, el contenedor DEBE mapear el puerto hacia el host
3. **Firewall**: Asegúrate que Windows Firewall no bloquea conexiones entrada en puerto 8080

## Referencias

- [Android Emulator Documentation - Network](https://developer.android.com/studio/run/emulator-networking)
- [Spring Boot - server.address Property](https://docs.spring.io/spring-boot/docs/current/reference/html/application-properties.html#server)
- [CORS in Spring Boot](https://spring.io/guides/gs/cors-rest-persisted/)

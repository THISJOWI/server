# Documentación Centralizada de APIs

Este gateway (Cloud) centraliza la documentación OpenAPI/Swagger de todos los microservicios conectados.

## 🚀 Acceso a la Documentación

### Swagger UI Centralizado

Una vez que el gateway esté en ejecución, puedes acceder a la documentación interactiva de todas las APIs en:

```
http://localhost:8100/swagger-ui.html
```

### Endpoints de Documentación OpenAPI

Cada servicio expone su documentación OpenAPI en formato JSON:

| Servicio | Endpoint OpenAPI |
|----------|------------------|
| **Authentication Service** | `http://localhost:8100/v3/api-docs/authentication-service` |
| **Notes Service** | `http://localhost:8100/v3/api-docs/notes-service` |
| **Password Service** | `http://localhost:8100/v3/api-docs/password-service` |

## 📋 Servicios Documentados

### 1. Authentication Service
- **Base Path**: `/api/auth`
- **Puerto directo**: 8082
- **Descripción**: Servicio de autenticación y autorización con JWT
- **Endpoints principales**:
  - Login/Registro de usuarios
  - Validación de tokens
  - Gestión de sesiones

### 2. Notes Service
- **Base Path**: `/api/v1/notes`
- **Puerto directo**: 8083
- **Descripción**: Servicio para gestión de notas de usuario
- **Endpoints principales**:
  - CRUD de notas
  - Búsqueda y filtrado
  - Compartir notas

### 3. Password Service
- **Base Path**: `/api/v1/passwords`
- **Puerto directo**: 8084
- **Descripción**: Servicio para gestión de contraseñas
- **Endpoints principales**:
  - CRUD de contraseñas
  - Encriptación/Desencriptación
  - Generación de contraseñas seguras

## 🔧 Configuración

La configuración de OpenAPI se encuentra en:

### Gateway (Cloud)
- **Clase**: `uk.thisjowi.Cloud.config.OpenApiConfig`
- **Configuración**: `Cloud/src/main/resources/application.yml`

### Microservicios
Cada microservicio tiene su propia configuración de SpringDoc en su archivo `application.yml`:

```yaml
springdoc:
  api-docs:
    enabled: true
    path: /v3/api-docs
  swagger-ui:
    enabled: true
    path: /swagger-ui.html
```

## 🛠️ Desarrollo

### Añadir un nuevo servicio a la documentación

1. **Añadir la ruta en el Gateway** (`Cloud/src/main/resources/application.yml`):
```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: nuevo-servicio-api
          uri: http://nuevo-servicio:PUERTO
          predicates:
            - Path=/api/nuevo/**
          filters:
            - StripPrefix=0
```

2. **Añadir el grupo OpenAPI** (`Cloud/src/main/java/uk/thisjowi/Cloud/config/OpenApiConfig.java`):
```java
groups.add(GroupedOpenApi.builder()
    .group("nuevo-servicio")
    .pathsToMatch("/api/nuevo/**")
    .build());
```

3. **Añadir a la configuración SpringDoc** (`Cloud/src/main/resources/application.yml`):
```yaml
springdoc:
  swagger-ui:
    urls:
      - name: nuevo-servicio
        url: /v3/api-docs/nuevo-servicio
  group-configs:
    - group: nuevo-servicio
      display-name: Nuevo Servicio
      paths-to-match: /api/nuevo/**
```

4. **En el microservicio**, añadir la dependencia SpringDoc OpenAPI:
```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.3.0</version>
</dependency>
```

## 🎯 Características

- ✅ **Documentación centralizada**: Todas las APIs en un solo lugar
- ✅ **Interfaz interactiva**: Prueba los endpoints directamente desde el navegador
- ✅ **Actualización automática**: Los cambios en los controladores se reflejan automáticamente
- ✅ **Agrupación por servicio**: Fácil navegación entre diferentes microservicios
- ✅ **Esquemas compartidos**: Visualización de modelos de datos

## 📦 Dependencias Necesarias

### Gateway (Spring Cloud Gateway)
```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webflux-ui</artifactId>
    <version>2.3.0</version>
</dependency>
```

### Microservicios (Spring Boot Web)
```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.3.0</version>
</dependency>
```

## 🔐 Seguridad

Para probar endpoints protegidos en Swagger UI:

1. Obtén un token JWT desde el endpoint de login
2. Haz clic en el botón **Authorize** en la parte superior de Swagger UI
3. Introduce el token en formato: `Bearer <tu-token-jwt>`
4. Haz clic en **Authorize**

Ahora puedes probar los endpoints protegidos directamente desde la interfaz.

## 📝 Notas

- El gateway debe estar ejecutándose en el puerto 8100
- Todos los microservicios deben estar registrados en Eureka
- La documentación se genera automáticamente basándose en las anotaciones de Spring
- Para personalizar la documentación, usa anotaciones de SpringDoc como `@Operation`, `@ApiResponse`, etc.

## 🔍 Troubleshooting

### La documentación no aparece
- Verifica que el servicio esté registrado en Eureka
- Confirma que el servicio tenga la dependencia SpringDoc
- Revisa que las rutas en el gateway coincidan con las del servicio

### Errores de CORS
- Los filtros CORS están configurados en el gateway
- Verifica la configuración de `default-filters` en `application.yml`

### Endpoints no visibles
- Asegúrate de que los controladores tengan las anotaciones correctas (`@RestController`, `@RequestMapping`)
- Verifica la propiedad `packages-to-scan` en la configuración de SpringDoc

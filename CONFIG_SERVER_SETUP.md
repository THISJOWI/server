# Microservicios conectados al Config Server

## Resumen de la configuración

Todos los servicios ahora están conectados al **Config Server** y obtienen su configuración desde un repositorio Git centralizado.

### Estructura de Servicios

1. **Config Server** (puerto 8888) - Servidor de configuración centralizado
2. **Eureka Server** (puerto 8761) - Registro y descubrimiento de servicios
3. **API Gateway** (puerto 8081) - Gateway de entrada
4. **Authentication Service** (puerto 8082) - Servicio de autenticación
5. **Notes Service** (puerto 8083) - Servicio de notas
6. **Password Service** (puerto 8084) - Servicio de contraseñas
7. **Admin Server** (puerto 8090) - Panel de administración

### Configuración realizada

#### 1. Dependencies en `pom.xml`
Todos los servicios tienen la dependencia de Config Client:
```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-config</artifactId>
</dependency>
```

#### 2. Archivos `bootstrap.yaml`
Cada servicio tiene configurado su `bootstrap.yaml` para conectarse al Config Server:
```yaml
spring:
  application:
    name: [nombre-del-servicio]
  cloud:
    config:
      uri: http://${CONFIG_SERVER_HOST:config-server}:${CONFIG_SERVER_PORT:8888}
      fail-fast: true
      retry:
        initial-interval: 1000
        max-attempts: 6
        max-interval: 2000
        multiplier: 1.1
```

#### 3. Archivos `compose.yaml` individuales
Cada servicio tiene su `compose.yaml` con las variables de entorno necesarias:
```yaml
services:
  [service-name]:
    environment:
      - CONFIG_SERVER_HOST=config-server
      - CONFIG_SERVER_PORT=8888
      - EUREKA_HOST=eureka-server
      - EUREKA_PORT=8761
    depends_on:
      - config-server
      - eureka-server
```

#### 4. Docker Compose principal
Se creó un `docker-compose.yaml` en la raíz que orquesta todos los servicios con:
- Health checks para Config Server y Eureka
- Orden de inicio correcto (Config Server → Eureka → demás servicios)
- Red compartida entre todos los contenedores

## Cómo usar

### Opción 1: Ejecutar todos los servicios con Docker Compose

```bash
# Crear la red (primera vez)
docker network create microservices-network

# Iniciar todos los servicios
docker-compose up -d

# Ver los logs
docker-compose logs -f

# Detener todos los servicios
docker-compose down
```

### Opción 2: Ejecutar servicios individuales

```bash
# Asegurarse de tener la red creada
docker network create microservices-network

# Iniciar Config Server primero
cd Configuration
docker-compose up -d

# Luego Eureka
cd ../Eureka
docker-compose up -d

# Después los demás servicios
cd ../Authentication
docker-compose up -d
```

### Opción 3: Ejecutar localmente (desarrollo)

```bash
# Iniciar Config Server
cd Configuration
./mvnw spring-boot:run

# En otra terminal, iniciar Eureka
cd Eureka
./mvnw spring-boot:run

# En otra terminal, iniciar otros servicios
cd Authentication
./mvnw spring-boot:run
```

## Verificación

1. **Config Server**: http://localhost:8888/actuator/health
2. **Eureka Server**: http://localhost:8761
3. **API Gateway**: http://localhost:8081/actuator/health
4. **Admin Server**: http://localhost:8090

## Variables de entorno

Puedes personalizar la configuración usando variables de entorno:

```bash
# Ejemplo: cambiar el repositorio Git del Config Server
export CONFIG_GIT_URI=https://github.com/tu-usuario/tu-repo-config
export CONFIG_GIT_BRANCH=develop

docker-compose up -d
```

## Estructura del repositorio de configuración

El Config Server está configurado para buscar archivos en:
```
https://github.com/THISJOWI/configuration
├── admin-server/
│   └── application.yaml
├── auth-service/
│   └── application.yaml
├── api-gateway/
│   └── application.yaml
├── notes-service/
│   └── application.yaml
├── password-service/
│   └── application.yaml
└── eureka-server/
    └── application.yaml
```

## Orden de inicio recomendado

1. **Config Server** - Primero siempre
2. **Eureka Server** - Segundo
3. **Servicios de aplicación** - Después (pueden iniciarse en paralelo)
4. **API Gateway** - Último (aunque puede iniciarse junto con otros servicios)

## Troubleshooting

### Los servicios no pueden conectarse al Config Server
- Verificar que Config Server esté corriendo: `curl http://localhost:8888/actuator/health`
- Revisar los logs del Config Server
- Verificar que las variables de entorno estén correctas

### Los servicios no se registran en Eureka
- Verificar que Eureka esté corriendo: `curl http://localhost:8761/actuator/health`
- Verificar que Config Server tenga la configuración de Eureka para cada servicio
- Revisar los logs de cada servicio

### Problemas de conexión entre contenedores
- Verificar que la red Docker exista: `docker network ls`
- Verificar que todos los contenedores estén en la misma red
- Usar nombres de contenedor para la comunicación (config-server, eureka-server, etc.)

## Endpoints útiles

### Config Server
- Health: http://localhost:8888/actuator/health
- Configuración específica: http://localhost:8888/{application}/{profile}
- Ejemplo: http://localhost:8888/auth-service/default

### Eureka Server
- Dashboard: http://localhost:8761
- API: http://localhost:8761/eureka/apps

### Admin Server
- Dashboard: http://localhost:8090

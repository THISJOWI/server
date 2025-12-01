# Integración Kafka: Authentication ↔ OTP

## Resumen

Los servicios **Authentication** y **OTP** ahora están conectados vía Kafka para vincular automáticamente usuarios con sus códigos OTP.

## Flujo de Integración

### 1. Registro de Usuario (Authentication Service)

Cuando un usuario se registra en el sistema:

```
Usuario registra → Authentication Service → Guarda usuario en DB
                                          ↓
                                    Envía evento Kafka
                                          ↓
                              Topic: auth-events
                              Event: USER_REGISTERED
```

**Estructura del evento:**
```json
{
  "userId": 123,
  "username": "johndoe",
  "email": "john@example.com",
  "eventType": "USER_REGISTERED",
  "timestamp": 1701436800
}
```

### 2. Creación Automática de OTP (OTP Service)

El servicio OTP escucha el topic `auth-events`:

```
Topic: auth-events → OTP Service Consumer → Crea OTP automáticamente
                                          ↓
                                    Guarda en DB con userId
                                          ↓
                                    Envía evento Kafka
                                          ↓
                              Topic: otp-events
                              Event: OTP_CREATED
```

**Estructura del evento OTP:**
```json
{
  "otpId": 456,
  "userId": 123,
  "username": "johndoe",
  "type": "TOTP",
  "eventType": "OTP_CREATED",
  "timestamp": 1701436810,
  "expiresAt": 1704028810
}
```

## Archivos Modificados/Creados

### Servicio OTP

#### Nuevos archivos:
- `kafka/KafkaConfig.java` - Configuración de Kafka producer/consumer
- `kafka/KafkaProducerService.java` - Servicio para enviar eventos
- `kafka/KafkaConsumerService.java` - Servicio para recibir eventos de usuarios
- `dto/UserRegisteredEvent.java` - DTO para eventos de registro de usuario
- `dto/OtpCreatedEvent.java` - DTO para eventos de creación de OTP

#### Archivos modificados:
- `entity/otp.java` - Agregado campo `userId` para vincular con el usuario
- `service/OtpService.java` - 
  - Agregado método `createOtpForUser()` que acepta userId
  - Envía eventos Kafka cuando se crea un OTP
- `pom.xml` - Corregido y agregadas dependencias de Kafka y Jackson
- `application.yaml` - Configuración de topics y conexión Kafka

### Servicio Authentication

#### Nuevos archivos:
- `dto/UserRegisteredEvent.java` - DTO para eventos de registro

#### Archivos modificados:
- `kafka/KafkaProducerService.java` - Agregado método `sendUserRegisteredEvent()`
- `service/UserService.java` - Método `saveUser()` ahora envía evento cuando se registra un nuevo usuario

## Configuración Requerida

### Variables de Entorno

Ambos servicios necesitan las siguientes variables de entorno:

```yaml
KAFKA_HOST: localhost  # O el host de tu Kafka broker
KAFKA_PORT: 9092
```

### Topics de Kafka

Asegúrate de que los siguientes topics existan en Kafka:

- `auth-events` - Eventos de autenticación (registro, login, etc.)
- `otp-events` - Eventos de OTP (creación, validación, etc.)

## Pruebas

### 1. Registrar un Usuario

```bash
POST http://localhost:8080/api/auth/register
Content-Type: application/json

{
  "username": "testuser",
  "email": "test@example.com",
  "password": "SecurePass123"
}
```

### 2. Verificar Evento en Kafka (opcional)

```bash
# Consumir mensajes del topic auth-events
kafka-console-consumer --bootstrap-server localhost:9092 --topic auth-events --from-beginning

# Consumir mensajes del topic otp-events
kafka-console-consumer --bootstrap-server localhost:9092 --topic otp-events --from-beginning
```

### 3. Verificar OTP en Base de Datos

```sql
-- El OTP debe haberse creado automáticamente
SELECT * FROM otp WHERE user_id = <userId del usuario registrado>;
```

## Comportamiento

1. **Registro automático**: Cuando un usuario se registra, automáticamente se le crea un OTP TOTP con validez de 30 días.

2. **Vinculación**: El OTP queda vinculado al usuario mediante el campo `userId`.

3. **Eventos**: Ambos servicios emiten eventos que pueden ser consumidos por otros microservicios del sistema.

4. **Resiliencia**: Si falla el envío a Kafka, se registra en logs pero no se interrumpe el flujo principal.

## Mejoras Futuras

- [ ] Agregar reintentos automáticos en caso de fallo de Kafka
- [ ] Implementar Dead Letter Queue (DLQ) para mensajes fallidos
- [ ] Agregar eventos para actualización y eliminación de usuarios
- [ ] Implementar eventos para validación exitosa/fallida de OTP
- [ ] Agregar métricas y monitoreo de eventos Kafka
- [ ] Implementar esquemas Avro para validación de mensajes

## Monitoreo

### Logs a Revisar

**Authentication Service:**
```
User registered event sent to Kafka: userId=123, username=johndoe
```

**OTP Service:**
```
Received message from Kafka: {...}
Processing user registration event for user: johndoe
OTP automatically created for user: johndoe
OTP created for user: johndoe with userId: 123
```

## Troubleshooting

### El OTP no se crea automáticamente

1. Verificar que Kafka esté ejecutándose
2. Verificar conectividad entre servicios y Kafka
3. Revisar logs del servicio OTP
4. Verificar que el consumer group esté activo

### Eventos duplicados

- Verificar configuración de `auto-offset-reset` y `enable-auto-commit`
- Revisar consumer groups en Kafka

### Errores de serialización

- Verificar que ambos servicios usen la misma estructura de DTOs
- Revisar logs de Jackson para errores de deserialización

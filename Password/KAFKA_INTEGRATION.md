# Password Service - Kafka Integration con Authentication

## ✅ Cambios Realizados

### 1. **KafkaConfig.java** - Configuración mejorada
- ✅ Agregado `@Value` para `bootstrap-servers` desde `application.yaml`
- ✅ Agregado `ConcurrentKafkaListenerContainerFactory` para mejor manejo de consumers
- ✅ Configuración simétrica: producción y consumo de mensajes

### 2. **KafkaConsumerService.java** - Recepción de eventos
- ✅ Escucha el topic `auth-events` (grupo: `password-service-group`)
- ✅ Almacena JWT tokens en `LAST_TOKEN` (AtomicReference)
- ✅ Soporta tokens con o sin prefijo "Bearer "
- ✅ Logging con SLF4J

### 3. **KafkaProducerService.java** - Envío de eventos (NUEVO)
- ✅ Permite enviar mensajes a cualquier topic de Kafka
- ✅ Usado para broadcast de eventos de Password a otros servicios
- ✅ Inyectado automáticamente por Spring

### 4. **PasswordService.java** - Extracción dual de tokens
```
1️⃣ Intenta obtener del header Authorization (Bearer <token>)
   ↓
2️⃣ Si no hay header válido, usa token de Kafka (LAST_TOKEN)
   ↓
3️⃣ Extrae userId con JwtUtil.extractUserId()
```

### 5. **PasswordController.java** - Endpoints completos
- `GET /api/v1/passwords` → Lista (200)
- `POST /api/v1/passwords` → Crear (201)
- `PUT /api/v1/passwords/{id}` → Actualizar (200)
- `DELETE /api/v1/passwords/{id}` → Eliminar (204)
- Retorna **401 UNAUTHORIZED** para errores de autenticación

### 6. **Gateway (Cloud)** - Routing correcto
- ✅ Route: `/api/v1/passwords/**` → `lb://password-service`
- ✅ `StripPrefix=0` para mantener ruta completa

## 🔄 Flujo de Autenticación

```
┌─────────────────┐
│  Authentication │
│     Service     │
└────────┬────────┘
         │ (envía JWT)
         ▼
    ┌────────────┐
    │   Kafka    │ (topic: auth-events)
    │ auth-events│
    └────────┬───┘
             │
    ┌────────▼────────┐
    │  Notes Service  │
    │  Password Svc   │ ◄── Ambos consumen tokens
    │  (otros...)     │
    └─────────────────┘
```

## 🔐 Seguridad

- **JWT Secret compartido**: `VK6DuSKkEu2EaGUJzxwRfYpTTvGG6rGE` (en `application.yaml`)
- **Token Validation**: `JwtUtil.extractUserId()` valida firma y extrae `userId`
- **Fallback Kafka**: Si no hay header Authorization, usa token más reciente de Kafka
- **Error Handling**: 
  - 401 UNAUTHORIZED: Token inválido/expirado
  - 403 FORBIDDEN: Usuario no autorizado para recurso
  - 204 NO CONTENT: Eliminación exitosa

## 📝 Configuración (application.yaml)

```yaml
kafka:
  bootstrap-servers: localhost:9092
  
app:
  jwt:
    secret: VK6DuSKkEu2EaGUJzxwRfYpTTvGG6rGE
```

## 🚀 Próximos pasos

1. **Rebuild servicios:**
   ```bash
   cd backend/Password && ./mvnw -DskipTests package
   cd ../Cloud && ./mvnw -DskipTests package
   ```

2. **Restart en orden:**
   ```
   1. Eureka (8761)
   2. Authentication (8082)
   3. Cloud Gateway (8100)
   4. Password Service (8084)
   ```

3. **Test desde Frontend:**
   - Login → obtiene token
   - GET `/api/v1/passwords` → debe devolver 200 con lista
   - Si 401: verificar que token se envía en header `Authorization: Bearer <token>`

4. **Verificar Kafka:**
   ```bash
   # Consumir mensajes desde auth-events
   kafka-console-consumer --bootstrap-server localhost:9092 \
     --topic auth-events --from-beginning
   ```

## 🐛 Debugging

**Si hay error 401:**
1. Verifica en logs del Password service: `[Password] Received message from Kafka...`
2. Confir que `JwtUtil.extractUserId()` extrae userId > 0
3. Comprueba que secret en Password coincide con Authentication

**Si Kafka no funciona:**
1. Verifica que Kafka está corriendo: `localhost:9092`
2. Crea topic si no existe: `kafka-topics --bootstrap-server localhost:9092 --create --topic auth-events`


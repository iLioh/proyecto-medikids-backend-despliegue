# Pruebas Unitarias

### 1. **JwtServiceTest** (10 casos)
**Objetivo:** Validar generación y validación de tokens JWT

#### Casos de Prueba:
- Debe generar un token JWT válido
- Debe extraer el email correctamente del token
- Debe validar un token válido correctamente
- Debe rechazar un token con email incorrecto
- Debe rechazar un token malformado
- Debe extraer todos los claims del token
- Debe generar token de admin con expiration diferente
- Debe rechazar un token vacío
- Debe rechazar un token null

**Métodos Testeados:**
- `generateToken(Usuario usuario)`
- `generateAdminToken(Usuario usuario)`
- `extractEmail(String token)`
- `extractAllClaims(String token)`
- `validateToken(String token, String email)`

---

### 2. **UsuarioServiceTest** (11 casos)
**Objetivo:** Gestión de usuarios y operaciones de perfil

#### Casos de Prueba:
- Debe obtener un usuario por ID
- Debe retornar null cuando usuario no existe
- Debe cambiar contraseña correctamente
- Debe fallar al cambiar contraseña con contraseña actual incorrecta
- Debe fallar al cambiar contraseña de usuario inexistente
- Debe eliminar (soft delete) un usuario
- Debe retornar false al eliminar usuario inexistente
- Debe actualizar perfil del usuario
- Debe retornar null al actualizar perfil de usuario inexistente

**Métodos Testeados:**
- `getById(int id)`
- `changePassword(int id, String currentPassword, String newPassword)`
- `delete(int id)`
- `updateProfile(int id, UsuarioRequest usuario)`
- `update(int id, UsuarioRequest usuario)`

---

### 3. **RefreshTokenServiceTest** (11 casos)
**Objetivo:** Gestión de refresh tokens y sesiones

#### Casos de Prueba:
- Debe crear un refresh token correctamente
- Debe encontrar un refresh token por su valor
- Debe retornar vacío cuando token no existe
- Debe validar una sesión correcta
- Debe rechazar sesión con fingerprint incorrecto
- Debe rechazar sesión con IP incorrecta
- Debe validar sesión sin validaciones (null)
- Debe revocar un token correctamente
- Debe revocar todos los tokens de un usuario
- Debe no hacer nada al revocar tokens de usuario sin tokens activos

**Métodos Testeados:**
- `createRefreshToken(Integer userId, String fingerprint, String ipOrigen)`
- `findByToken(String token)`
- `isSessionValid(RefreshToken rt, String currentFingerprint, String currentIp)`
- `revokeToken(String token)`
- `revokeAllUserTokens(Integer userId)`

---

### 4. **MedicoServiceTest** (11 casos)
**Objetivo:** Gestión de médicos y especialidades

#### Casos de Prueba:
- Debe obtener todos los médicos
- Debe obtener médico desde cache si existe
- Debe obtener un médico por ID
- Debe retornar null al obtener médico inexistente
- Debe obtener médico por ID de usuario
- Debe cambiar el estado de un médico (toggle status)
- Debe retornar null al cambiar estado de médico inexistente
- Debe eliminar un médico
- Debe retornar false al eliminar médico inexistente
- Debe guardar un nuevo médico
- Debe actualizar un médico existente

**Métodos Testeados:**
- `getAll()`
- `getById(int id)`
- `getByIdUsuario(int idUsuario)`
- `toggleStatus(int id)`
- `delete(int id)`
- `save(MedicoRequest medico)`
- `update(int id, MedicoRequest medico)`

---

### 5. **CitaServiceTest** (11 casos)
**Objetivo:** Gestión de citas médicas

#### Casos de Prueba:
- Debe obtener todas las citas
- Debe obtener una cita por ID
- Debe retornar null al obtener cita inexistente
- Debe obtener citas de un paciente
- Debe obtener citas de un médico
- Debe obtener citas del día de hoy
- Debe guardar una nueva cita
- Debe cancelar una cita
- Debe cambiar el estado de una cita
- Debe retornar false al cancelar cita inexistente
- Debe obtener citas por estado

**Métodos Testeados:**
- `getAll()`
- `getById(int id)`
- `getByIdPaciente(int idPaciente)`
- `getByIdMedico(int idMedico)`
- `getHoyDates(LocalDate fecha)`
- `save(CitaRequest cita)`
- `cancelCita(int id, String motivo)`
- `changeStatus(int id, String nuevoEstado)`
- `getByEstado(String estado)`

---

### 6. **PacienteServiceTest** (11 casos)
**Objetivo:** Gestión de perfiles de pacientes pediátricos

#### Casos de Prueba:
- Debe obtener todos los pacientes
- Debe obtener un paciente por ID
- Debe retornar null al obtener paciente inexistente
- Debe obtener pacientes de un cliente
- Debe guardar un nuevo paciente
- Debe actualizar un paciente existente
- Debe retornar null al actualizar paciente inexistente
- Debe eliminar un paciente
- Debe retornar false al eliminar paciente inexistente
- Debe calcular la edad del paciente correctamente

**Métodos Testeados:**
- `getAll()`
- `getById(int id)`
- `getByIdCliente(int idCliente)`
- `save(PacienteRequest paciente)`
- `update(int id, PacienteRequest paciente)`
- `delete(int id)`

---

### 7. **HorarioServiceTest** (11 casos)
**Objetivo:** Gestión de horarios y disponibilidad de médicos

#### Casos de Prueba:
- Debe obtener horarios de un médico
- Debe obtener horarios disponibles de un médico
- Debe obtener horarios en un rango de fechas
- Debe retornar lista vacía cuando no hay horarios disponibles
- Debe guardar horarios de una semana
- Debe limpiar horarios previos al guardar una nueva semana
- Debe guardar múltiples bloques horarios en una semana

**Métodos Testeados:**
- `getByMedico(int idMedico)`
- `getDisponiblesByMedico(int idMedico)`
- `getHorariosBySemana(int idMedico, LocalDate inicio, LocalDate fin)`
- `saveSemana(SemanaRequest request)`

---

### 8. **EspecialidadServiceTest** (10 casos)
**Objetivo:** Gestión de especialidades médicas

#### Casos de Prueba:
- Debe obtener todas las especialidades
- Debe obtener una especialidad por ID
- Debe retornar null al obtener especialidad inexistente
- Debe guardar una nueva especialidad
- Debe actualizar una especialidad existente
- Debe retornar null al actualizar especialidad inexistente
- Debe retornar lista vacía cuando no hay especialidades
- Debe obtener múltiples especialidades

**Métodos Testeados:**
- `getAll()`
- `getById(int id)`
- `save(EspecialidadRequest especialidad)`
- `update(int id, EspecialidadRequest especialidad)`

---

### 9. **ClienteServiceTest** (12 casos)
**Objetivo:** Gestión de clientes (padres)

#### Casos de Prueba:
- Debe obtener todos los clientes
- Debe obtener un cliente por ID
- Debe retornar null al obtener cliente inexistente
- Debe obtener un cliente por ID de usuario
- Debe obtener un cliente por DNI
- Debe retornar null al buscar cliente por DNI inexistente
- Debe guardar un nuevo cliente
- Debe actualizar un cliente existente
- Debe retornar null al actualizar cliente inexistente
- Debe retornar lista vacía cuando no hay clientes

**Métodos Testeados:**
- `getAll()`
- `getById(int id)`
- `getByIdUsuario(int idUsuario)`
- `getByDni(String dni)`
- `save(ClienteRequest cliente)`
- `update(int id, ClienteRequest cliente)`
- `getByNombre(String nombre)`

---

### 10. **RolServiceTest** (10 casos)
**Objetivo:** Gestión de roles y permisos

#### Casos de Prueba:
- Debe obtener todos los roles
- Debe obtener un rol por ID
- Debe retornar null al obtener rol inexistente
- Debe guardar un nuevo rol
- Debe actualizar un rol existente
- Debe retornar null al actualizar rol inexistente
- Debe obtener múltiples roles
- Debe retornar lista vacía cuando no hay roles

**Métodos Testeados:**
- `getAll()`
- `getById(int id)`
- `save(RolRequest rol)`
- `update(int id, RolRequest rol)`

---

### 11. **IncidenteServiceTest** (10 casos)
**Objetivo:** Gestión de reportes de incidentes

#### Casos de Prueba:
- Debe obtener todos los incidentes
- Debe obtener un incidente por ID
- Debe retornar null al obtener incidente inexistente
- Debe obtener incidentes de un médico
- Debe guardar un nuevo incidente
- Debe retornar lista vacía cuando no hay incidentes
- Debe retornar lista vacía de incidentes para médico inexistente
- Debe obtener múltiples incidentes de un médico

**Métodos Testeados:**
- `getAll()`
- `getById(int id)`
- `getByMedico(int idMedico)`
- `save(IncidenteRequest incidente)`

---

### 12. **PagoServiceTest** (9 casos)
**Objetivo:** Gestión de transacciones de pago

#### Casos de Prueba:
- Debe obtener todos los pagos
- Debe guardar un nuevo pago
- Debe listar pagos de un cliente específico
- Debe retornar lista vacía cuando no hay pagos
- Debe retornar lista vacía de pagos para cliente sin transacciones
- Debe obtener múltiples pagos de un cliente
- Debe guardar pago con validación de monto

**Métodos Testeados:**
- `listarPagos()`
- `guardarPago(PagoRequest pago)`
- `listarPagosPorCliente(int idCliente)`

---

### 13. **HistorialClinicoServiceTest** (11 casos)
**Objetivo:** Gestión de historiales clínicos

#### Casos de Prueba:
- Debe obtener todos los registros del historial clínico
- Debe obtener un registro de historial clínico por ID
- Debe retornar null al obtener registro inexistente
- Debe guardar un nuevo registro de historial clínico
- Debe obtener historial clínico de un paciente
- Debe obtener historial clínico de una cita
- Debe retornar lista vacía cuando no hay registros
- Debe retornar lista vacía de historial para paciente sin registros
- Debe obtener múltiples registros de historial de un paciente
- Debe registrar diagnóstico correctamente

**Métodos Testeados:**
- `getAll()`
- `getById(int id)`
- `getByIdPaciente(int idPaciente)`
- `getByIdCita(int idCita)`
- `save(HistorialClinicoRequest historial)`

---

### 14. **TarjetaGuardadaServiceTest** (10 casos)
**Objetivo:** Gestión segura de tarjetas de crédito guardadas

#### Casos de Prueba:
- Debe listar tarjetas activas de un usuario
- Debe retornar lista vacía cuando usuario no tiene tarjetas
- Debe guardar una nueva tarjeta
- Debe eliminar (desactivar) una tarjeta guardada
- Debe lanzar excepción al eliminar tarjeta inexistente
- Debe lanzar excepción si usuario intenta eliminar tarjeta ajena
- Debe obtener múltiples tarjetas de un usuario
- Debe enmascarar número de tarjeta correctamente
- Debe solo listar tarjetas activas, no desactivadas

**Métodos Testeados:**
- `listarPorUsuario(int idUsuario)`
- `guardar(TarjetaGuardadaRequest request, int idUsuario)`
- `eliminar(int idTarjeta, int idUsuario)`

---

### 15. **IpAutorizadaServiceTest** (12 casos)
**Objetivo:** Gestión de whitelist de IPs autorizadas

#### Casos de Prueba:
- Debe verificar que una IP está autorizada
- Debe rechazar una IP no autorizada
- Debe obtener todas las IPs autorizadas
- Debe obtener una IP autorizada por ID
- Debe retornar null al obtener IP inexistente
- Debe obtener IPs autorizadas de un usuario
- Debe guardar una nueva IP autorizada
- Debe actualizar una IP autorizada existente
- Debe desactivar una IP autorizada
- Debe retornar lista vacía cuando no hay IPs autorizadas
- Debe obtener múltiples IPs de un usuario
- Debe validar formato de IP

**Métodos Testeados:**
- `isIpAuthorized(String ip)`
- `getAll()`
- `getById(int id)`
- `getByUsuario(int idUsuario)`
- `save(IpAutorizadaRequest request)`
- `update(int id, IpAutorizadaRequest request)`
- `disable(int id)`

---


## 🏃 Cómo Ejecutar los Tests

Ejecutar para validar que no hay errores:
```bash
mvn clean compile test -DskipTests=false
```

### Desde Maven:
```bash
# Ejecutar todos los tests
mvn test

# Ejecutar un test específico
mvn test -Dtest=JwtServiceTest

# Ejecutar con cobertura
mvn test jacoco:report
```

### Desde IDE (como IntelliJ o Eclipse):
1. Click derecho en `src/test/java/com/medikids/medikids/process/service`
2. Seleccionar "Run Tests" o "Run with Coverage"

---

## Notas

- Todos los tests usan **Mockito** para simular dependencias
- No se accede a BD real durante tests (H2 in-memory)
- Los tests son **aislados** y **independientes**
- Cada test debe ejecutarse en **< 100ms**
- Se sigue patrón **AAA** (Arrange, Act, Assert)

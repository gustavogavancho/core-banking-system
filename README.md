# Core Banking System

Sistema bancario básico implementado como proyecto multimódulo con microservicios en Spring Boot.

## 🔗 Repositorio

**GitHub:** [https://github.com/gustavogavancho/core-banking-system](https://github.com/gustavogavancho/core-banking-system)

## Arquitectura

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Client        │    │   Account       │    │   MySQL         │
│   Service       │◄──►│   Service       │◄──►│   Database      │
│   (Puerto 8081) │    │   (Puerto 8080) │    │   (Puerto 3306) │
└─────────────────┘    └─────────────────┘    └─────────────────���
```

## Servicios

### 1. Client Service (Puerto 8081)
- Gestión de clientes del banco
- Operaciones CRUD para clientes
- Validaciones de datos de cliente

### 2. Account Service (Puerto 8080)
- Gestión de cuentas bancarias
- Procesamiento de transacciones
- Validación de fondos y operaciones
- Integración con Client Service para validar existencia de clientes

## Base de Datos

- **MySQL 8.0** con esquema `core-banking-system`
- Puerto: 3306 (3307 en Docker para evitar conflictos)
- Credenciales: root/1234
- Script de inicialización: `BaseDatos.sql`

## Testing

### Pruebas Unitarias
- Implementadas con JUnit en ambos servicios
- Ubicación: `{servicio}/src/test/java/`

### Pruebas de Integración con Karate
Cada servicio incluye pruebas completas de API:

**Client Service:**
- `client.feature` - CRUD básico
- `client-edge-cases.feature` - Casos extremos y validaciones
- Reportes generados en: `client/target/karate-reports/`

**Account Service:**
- `account.feature` - Gestión de cuentas
- `transaction.feature` - Procesamiento de transacciones
- `account-edge-cases.feature` - Casos extremos
- `integration.feature` - **Pruebas de integración completas**
- Reportes generados en: `account/target/karate-reports/`

## 📮 Colección Postman

Se incluye una colección completa de Postman para probar todos los endpoints:

**Archivo:** `core-banking-system.postman_collection.json`

### Endpoints incluidos:
- **Account Service (Puerto 8080)**
  - POST `/accounts` - Crear cuenta
  - GET `/accounts` - Listar todas las cuentas
  - GET `/accounts/{id}` - Obtener cuenta por ID
  - PUT `/accounts/{id}` - Actualizar cuenta
  - DELETE `/accounts/{id}` - Eliminar cuenta
  - POST `/transactions` - Crear transacción
  - GET `/transactions` - Listar transacciones
  - GET `/transactions/{id}` - Obtener transacción por ID

- **Client Service (Puerto 8081)**
  - POST `/clients` - Crear cliente
  - GET `/clients` - Listar todos los clientes
  - GET `/clients/{id}` - Obtener cliente por ID
  - PUT `/clients/{id}` - Actualizar cliente
  - DELETE `/clients/{id}` - Eliminar cliente

### Importar en Postman:
1. Abrir Postman
2. Click en "Import"
3. Seleccionar el archivo `core-banking-system.postman_collection.json`
4. La colección aparecerá en tu workspace

## Tecnologías

- **Java 21**
- **Spring Boot 3.5.4**
- **MySQL 8.0**
- **Docker & Docker Compose**
- **Gradle** (proyecto multimódulo)
- **Karate** para testing de APIs
- **JUnit** para pruebas unitarias

## Despliegue

### Docker
```bash
# Windows
deploy.bat

# Linux/macOS
chmod +x deploy.sh && ./deploy.sh
```

### Local
```bash
./gradlew bootRun
```

## Módulos del Proyecto

- `shared/` - Componentes compartidos
- `client/` - Servicio de gestión de clientes
- `account/` - Servicio de gestión de cuentas y transacciones

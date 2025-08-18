# Core Banking System - Despliegue con Docker

Sistema bancario básico con microservicios `account` y `client` con base de datos MySQL.

## Arquitectura

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Account       │    │   Client        │    │   MySQL         │
│   Service       │◄──►│   Service       │◄──►│   Database      │
│   (Port 8080)   │    │   (Port 8081)   │    │   (Port 3306)   │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

## Servicios

- **MySQL Database**: Base de datos con esquema `core-banking-system`
- **Client Service**: Gestión de clientes (Puerto 8081)
- **Account Service**: Gestión de cuentas y transacciones (Puerto 8080)

## Requisitos Previos

- Docker Desktop instalado
- Docker Compose instalado
- Puertos 3306, 8080, 8081 disponibles

## Despliegue Rápido

### Opción 1: Script Automatizado

**Windows:**
```batch
deploy.bat
```

**Linux/macOS:**
```bash
chmod +x deploy.sh
./deploy.sh
```

### Opción 2: Manual

```bash
# Construir y levantar todos los servicios
docker-compose up --build -d

# Verificar estado
docker-compose ps

# Ver logs
docker-compose logs -f
```

## Comandos Útiles

```bash
# Ver logs de un servicio específico
docker-compose logs -f account-service
docker-compose logs -f client-service
docker-compose logs -f mysql

# Reiniciar servicios
docker-compose restart

# Detener servicios
docker-compose down

# Detener y eliminar volúmenes
docker-compose down -v

# Reconstruir imágenes
docker-compose build --no-cache
```

## Verificación del Despliegue

### 1. Verificar MySQL
```bash
docker exec -it core-banking-mysql mysql -u root -p1234 -e "SHOW DATABASES;"
```

### 2. Verificar Client Service
```bash
curl http://localhost:8081/actuator/health
```

### 3. Verificar Account Service
```bash
curl http://localhost:8080/actuator/health
```

## Endpoints de la API

### Client Service (Puerto 8081)
- `GET /api/clients` - Listar clientes
- `POST /api/clients` - Crear cliente
- `GET /api/clients/{id}` - Obtener cliente
- `PUT /api/clients/{id}` - Actualizar cliente
- `DELETE /api/clients/{id}` - Eliminar cliente

### Account Service (Puerto 8080)
- `GET /api/accounts` - Listar cuentas
- `POST /api/accounts` - Crear cuenta
- `GET /api/accounts/{id}` - Obtener cuenta
- `PUT /api/accounts/{id}` - Actualizar cuenta
- `DELETE /api/accounts/{id}` - Eliminar cuenta
- `POST /api/transactions` - Crear transacción
- `GET /api/transactions` - Listar transacciones

## Configuración de Red

Los servicios se comunican a través de una red Docker personalizada:
- Red: `banking-network`
- El servicio Account se conecta al servicio Client usando: `http://client-service:8081`
- Ambos servicios se conectan a MySQL usando: `mysql:3306`

## Variables de Entorno

### MySQL
- `MYSQL_ROOT_PASSWORD=1234`
- `MYSQL_DATABASE=core-banking-system`

### Client Service
- `SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/core-banking-system`
- `SERVER_PORT=8081`

### Account Service
- `SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/core-banking-system`
- `CLIENT_SERVICE_URL=http://client-service:8081`
- `SERVER_PORT=8080`

## Datos de Prueba

La base de datos se inicializa con datos de ejemplo:
- Cliente: Juan Pérez (ID: 1)
- Cuenta: 0000000001-ABCDEF (Tipo: AHORROS)

## Troubleshooting

### Problema: Servicio no inicia
```bash
# Ver logs detallados
docker-compose logs service-name

# Reiniciar servicio específico
docker-compose restart service-name
```

### Problema: Puerto ocupado
```bash
# Verificar puertos en uso
netstat -an | findstr "8080\|8081\|3306"

# Cambiar puertos en docker-compose.yml si es necesario
```

### Problema: Base de datos no conecta
```bash
# Verificar health check de MySQL
docker-compose ps mysql

# Conectar manualmente a MySQL
docker exec -it core-banking-mysql mysql -u root -p1234
```

## Estructura del Proyecto

```
core-banking-system/
├── docker-compose.yml          # Orquestación de servicios
├── BaseDatos.sql              # Schema de base de datos
├── deploy.sh / deploy.bat     # Scripts de despliegue
├── account/
│   ├── Dockerfile
│   ├── build.gradle
│   └── src/
├── client/
│   ├── Dockerfile
│   ├── build.gradle
│   └── src/
└── shared/
    └── src/
```

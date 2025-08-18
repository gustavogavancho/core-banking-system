# Pruebas de Karate para Core Banking System

Este documento describe las pruebas de API implementadas usando Karate para los servicios `client` y `account` del sistema bancario.

## Estructura de las Pruebas

### Servicio Client (Puerto 8081)

**Ubicación:** `client/src/test/resources/com/swiftline/client/`

1. **client.feature** - Pruebas básicas CRUD
   - Crear cliente
   - Obtener cliente por ID
   - Listar todos los clientes
   - Actualizar cliente
   - Eliminar cliente
   - Validaciones de datos inválidos

2. **client-edge-cases.feature** - Casos extremos y validaciones
   - Clientes con identificación duplicada
   - Validaciones de edad (negativa, muy alta)
   - Formatos de teléfono inválidos
   - Operaciones en lote
   - Manejo de errores 404

3. **create-client.feature** - Función auxiliar para creación de clientes

### Servicio Account (Puerto 8080)

**Ubicación:** `account/src/test/resources/com/swiftline/account/`

1. **account.feature** - Pruebas básicas de cuentas
   - Crear cuenta
   - Obtener cuenta por ID
   - Listar todas las cuentas
   - Actualizar cuenta
   - Validaciones de datos

2. **transaction.feature** - Pruebas de transacciones
   - Crear transacciones (depósito/retiro)
   - Obtener transacción por ID
   - Listar transacciones por cuenta
   - Actualizar transacción
   - Validaciones de montos y tipos

3. **account-edge-cases.feature** - Casos extremos
   - Números de cuenta duplicados
   - Tipos de cuenta inválidos
   - Balances extremos (cero, muy alto)
   - Transacciones con fondos insuficientes
   - Tipos de transacción inválidos
   - Fechas futuras
   - Procesamiento en lote y concurrente

4. **integration.feature** - Pruebas de integración
   - Flujo completo: cliente → cuentas → transacciones
   - Validación de existencia de cliente
   - Múltiples cuentas por cliente

5. **create-account.feature** - Función auxiliar para creación de cuentas
6. **create-transaction.feature** - Función auxiliar para creación de transacciones

## Configuración

### Dependencias Agregadas

**client/build.gradle** y **account/build.gradle:**
```gradle
testImplementation 'com.intuit.karate:karate-junit5:1.4.1'
```

### Archivos de Configuración

**karate-config.js** (en ambos proyectos):
- Configuración de URLs base
- Timeouts de conexión
- Variables de entorno

## Cómo Ejecutar las Pruebas

### Prerrequisitos
1. Servicios en funcionamiento:
   - Client service en puerto 8081
   - Account service en puerto 8080
   - Base de datos MySQL configurada

### Comandos de Ejecución

#### Ejecutar todas las pruebas del servicio Client:
```bash
cd client
./gradlew test --tests ClientKarateTest
```

#### Ejecutar todas las pruebas del servicio Account:
```bash
cd account
./gradlew test --tests AccountKarateTest
```

#### Ejecutar pruebas específicas:
```bash
# Solo pruebas básicas de cliente
./gradlew test --tests ClientKarateTest.testClient

# Solo casos extremos de cuenta
./gradlew test --tests AccountKarateTest.testAccountEdgeCases

# Solo pruebas de integración
./gradlew test --tests AccountKarateTest.testIntegration
```

#### Ejecutar con perfil específico:
```bash
./gradlew test -Dkarate.env=test
```

## Tipos de Pruebas Implementadas

### 1. Pruebas Funcionales
- ✅ CRUD completo para clientes y cuentas
- ✅ Operaciones de transacciones
- ✅ Validaciones de negocio

### 2. Pruebas de Validación
- ✅ Datos inválidos o faltantes
- ✅ Restricciones de formato
- ✅ Reglas de negocio (fondos insuficientes, etc.)

### 3. Pruebas de Casos Extremos
- ✅ Valores límite (balances, edades)
- ✅ Duplicados y conflictos
- ✅ Operaciones en lote

### 4. Pruebas de Integración
- ✅ Flujos end-to-end
- ✅ Interacción entre servicios
- ✅ Validaciones cruzadas

### 5. Pruebas de Rendimiento (Simulación)
- ✅ Operaciones concurrentes
- ✅ Procesamiento en lote

## Escenarios de Prueba Cubiertos

### Cliente
- Crear cliente con datos válidos
- Validar campos obligatorios
- Manejar identificaciones duplicadas
- Actualizar información del cliente
- Eliminar cliente
- Validar formatos de datos (edad, teléfono)

### Cuenta
- Crear cuentas de diferentes tipos
- Validar balance inicial
- Manejar números de cuenta únicos
- Asociar cuentas con clientes existentes
- Validar tipos de cuenta válidos

### Transacciones
- Depósitos y retiros
- Validar fondos suficientes
- Manejar diferentes tipos de transacción
- Fechas de transacción válidas
- Historial de transacciones

### Integración
- Crear cliente y sus cuentas
- Realizar múltiples transacciones
- Validar consistencia de datos
- Flujos de trabajo completos

## Reportes

Los reportes de Karate se generan automáticamente en:
- `client/build/reports/tests/`
- `account/build/reports/tests/`

Incluyen:
- Resultados detallados por escenario
- Tiempo de ejecución
- Logs de requests/responses
- Screenshots de fallos (si aplica)

## Buenas Prácticas Implementadas

1. **Separación de Responsabilidades**: Archivos separados por tipo de prueba
2. **Reutilización**: Funciones auxiliares para operaciones comunes
3. **Configuración Centralizada**: karate-config.js para configuraciones
4. **Datos de Prueba**: Uso de datos realistas y variados
5. **Validaciones Comprehensivas**: Verificación de status codes, estructura de respuesta y datos
6. **Manejo de Errores**: Pruebas específicas para casos de error
7. **Documentación**: Comentarios claros en los escenarios

## Próximos Pasos Sugeridos

1. **Datos de Prueba Dinámicos**: Implementar generación automática de datos
2. **Pruebas de Carga**: Usar Karate Gatling para pruebas de rendimiento reales
3. **Integración CI/CD**: Configurar ejecución automática en pipeline
4. **Pruebas de Seguridad**: Agregar validaciones de autenticación/autorización
5. **Mocking**: Implementar mocks para dependencias externas

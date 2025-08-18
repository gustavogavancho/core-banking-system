@echo off
echo === Gestión del Sistema Bancario en Contenedores ===
echo.

:menu
echo Selecciona una opción:
echo.
echo [1] 🛑 APAGAR todos los servicios
echo [2] 🚀 ENCENDER todos los servicios
echo [3] 🔄 REINICIAR todos los servicios
echo [4] 📊 VER ESTADO de los servicios
echo [5] 📋 VER LOGS de los servicios
echo [6] 🗑️ LIMPIAR TODO (eliminar contenedores y datos)
echo [0] ❌ SALIR
echo.
set /p choice="Ingresa tu opción (0-6): "

if "%choice%"=="1" goto stop
if "%choice%"=="2" goto start
if "%choice%"=="3" goto restart
if "%choice%"=="4" goto status
if "%choice%"=="5" goto logs
if "%choice%"=="6" goto clean
if "%choice%"=="0" goto exit
echo Opción inválida. Intenta de nuevo.
goto menu

:stop
echo.
echo 🛑 APAGANDO todos los servicios...
docker-compose down
echo.
echo ✅ Todos los servicios han sido APAGADOS
echo.
echo 📋 Los servicios apagados fueron:
echo   - Client Service (puerto 8081)
echo   - Account Service (puerto 8080)
echo   - MySQL Database (puerto 3307)
echo.
echo 💡 Para volver a encender, ejecuta este script y selecciona opción [2]
echo.
pause
goto menu

:start
echo.
echo 🚀 ENCENDIENDO todos los servicios...
echo.
echo 🔍 Verificando Docker...
docker --version >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo ❌ Docker no está disponible
    echo 💡 Asegúrate de que Docker Desktop esté ejecutándose
    pause
    goto menu
)

echo ✅ Docker está disponible
echo.
echo 🏗️ Construyendo y desplegando servicios...
docker-compose up --build -d

echo.
echo ⏳ Esperando a que los servicios inicien (15 segundos)...
timeout /t 15 /nobreak >nul

echo.
echo 📊 Estado de los servicios:
docker-compose ps

echo.
echo ✅ Servicios ENCENDIDOS y disponibles en:
echo.
echo 🌐 URLs de los servicios:
echo   - Client Service: http://localhost:8081
echo   - Account Service: http://localhost:8080
echo   - MySQL Database: localhost:3307
echo.
echo 📖 Documentación de APIs:
echo   - Client Swagger: http://localhost:8081/swagger-ui.html
echo   - Account Swagger: http://localhost:8080/swagger-ui.html
echo.
pause
goto menu

:restart
echo.
echo 🔄 REINICIANDO todos los servicios...
echo.
echo 🛑 Apagando servicios...
docker-compose down
echo.
echo 🚀 Encendiendo servicios...
docker-compose up --build -d
echo.
echo ⏳ Esperando a que los servicios inicien (15 segundos)...
timeout /t 15 /nobreak >nul
echo.
echo 📊 Estado de los servicios:
docker-compose ps
echo.
echo ✅ Servicios REINICIADOS correctamente
echo.
pause
goto menu

:status
echo.
echo 📊 ESTADO ACTUAL de los servicios:
echo.
docker-compose ps
echo.
echo 🔍 Verificando conectividad:
echo.
echo Probando Client Service...
curl -s http://localhost:8081/actuator/health >nul 2>&1
if %ERRORLEVEL% EQU 0 (
    echo ✅ Client Service - FUNCIONANDO
) else (
    echo ❌ Client Service - NO RESPONDE
)

echo Probando Account Service...
curl -s http://localhost:8080/actuator/health >nul 2>&1
if %ERRORLEVEL% EQU 0 (
    echo ✅ Account Service - FUNCIONANDO
) else (
    echo ❌ Account Service - NO RESPONDE
)

echo.
pause
goto menu

:logs
echo.
echo 📋 LOGS de los servicios:
echo.
echo Selecciona qué logs ver:
echo [1] Todos los servicios
echo [2] Solo Client Service
echo [3] Solo Account Service
echo [4] Solo MySQL
echo [0] Volver al menú principal
echo.
set /p logChoice="Ingresa tu opción (0-4): "

if "%logChoice%"=="1" (
    echo.
    echo 📋 LOGS DE TODOS LOS SERVICIOS:
    docker-compose logs --tail=50
) else if "%logChoice%"=="2" (
    echo.
    echo 📋 LOGS DEL CLIENT SERVICE:
    docker-compose logs --tail=50 client-service
) else if "%logChoice%"=="3" (
    echo.
    echo 📋 LOGS DEL ACCOUNT SERVICE:
    docker-compose logs --tail=50 account-service
) else if "%logChoice%"=="4" (
    echo.
    echo 📋 LOGS DE MYSQL:
    docker-compose logs --tail=50 mysql
) else if "%logChoice%"=="0" (
    goto menu
) else (
    echo Opción inválida.
)
echo.
pause
goto menu

:clean
echo.
echo ⚠️ ADVERTENCIA: Esta opción eliminará TODOS los contenedores y datos
echo.
set /p confirm="¿Estás seguro? (s/N): "
if /i not "%confirm%"=="s" goto menu

echo.
echo 🗑️ LIMPIANDO todo el sistema...
echo.
echo 🛑 Deteniendo servicios...
docker-compose down

echo 🗑️ Eliminando contenedores y volúmenes...
docker-compose down -v

echo 🧹 Limpiando imágenes no utilizadas...
docker system prune -f

echo.
echo ✅ Sistema COMPLETAMENTE LIMPIO
echo.
echo 💡 Para volver a usar el sistema, selecciona opción [2] para encender
echo.
pause
goto menu

:exit
echo.
echo 👋 ¡Hasta luego!
echo.
echo 💡 Recuerda:
echo   - Los servicios siguen ejecutándose en segundo plano
echo   - Para apagarlos, ejecuta este script y selecciona opción [1]
echo.
pause
exit


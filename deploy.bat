@echo off
REM Script para desplegar el sistema bancario en contenedores Windows

echo === Desplegando Core Banking System ===
echo Servicios: account, client, mysql
echo.

REM Verificar que Docker esté disponible
docker --version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Docker no está instalado o no está en el PATH
    pause
    exit /b 1
)

docker-compose --version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Docker Compose no está instalado o no está en el PATH
    pause
    exit /b 1
)

echo ✅ Docker y Docker Compose están disponibles

REM Construir y levantar los servicios
echo.
echo 🔨 Construyendo imágenes y desplegando servicios...
docker-compose up --build -d

echo.
echo 📊 Estado de los contenedores:
docker-compose ps

echo.
echo 📋 Servicios disponibles:
echo   - MySQL Database: localhost:3306
echo   - Client Service: http://localhost:8081
echo   - Account Service: http://localhost:8080
echo.
echo 📖 Para ver logs:
echo   docker-compose logs -f [service_name]
echo.
echo 🛑 Para detener los servicios:
echo   docker-compose down
echo.
echo 🔄 Para reiniciar los servicios:
echo   docker-compose restart
echo.
pause

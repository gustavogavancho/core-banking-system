@echo off
echo === Instalación y Configuración Completa del Sistema Bancario ===
echo.

echo 📋 INSTRUCCIONES PASO A PASO:
echo.

echo 1️⃣ INSTALAR DOCKER DESKTOP:
echo    - Ve a: https://www.docker.com/products/docker-desktop/
echo    - Descarga Docker Desktop para Windows
echo    - Instálalo y reinicia tu computadora
echo    - Asegúrate de que WSL 2 esté habilitado
echo.

echo 2️⃣ VERIFICAR DOCKER:
echo    - Busca "Docker Desktop" en el menú inicio
echo    - Ejecútalo como administrador
echo    - Espera a que aparezca el ícono verde en la bandeja del sistema
echo.

echo 3️⃣ EJECUTAR DESPLIEGUE:
echo    - Una vez que Docker esté funcionando, ejecuta:
echo      docker-compose up --build -d
echo.

echo 📊 VERIFICAR INSTALACIÓN ACTUAL:
echo.

echo Verificando Docker...
docker --version >nul 2>&1
if %ERRORLEVEL% EQU 0 (
    echo ✅ Docker está instalado
    docker ps >nul 2>&1
    if %ERRORLEVEL% EQU 0 (
        echo ✅ Docker está funcionando
        echo.
        echo 🚀 ¡Docker está listo! Ejecutando despliegue...
        docker-compose up --build -d
        timeout /t 10 /nobreak >nul
        echo.
        echo 📊 Estado de los contenedores:
        docker-compose ps
        echo.
        echo 🌐 URLs disponibles:
        echo   - Client Service: http://localhost:8081
        echo   - Account Service: http://localhost:8080
        echo   - Client Swagger: http://localhost:8081/swagger-ui.html
        echo   - Account Swagger: http://localhost:8080/swagger-ui.html
    ) else (
        echo ❌ Docker está instalado pero no está ejecutándose
        echo 💡 Solución: Inicia Docker Desktop desde el menú inicio
    )
) else (
    echo ❌ Docker NO está instalado
    echo.
    echo 💡 INSTALACIÓN REQUERIDA:
    echo    1. Ve a https://www.docker.com/products/docker-desktop/
    echo    2. Descarga e instala Docker Desktop
    echo    3. Reinicia tu computadora
    echo    4. Ejecuta este script nuevamente
)

echo.
echo Verificando Java 17...
set "JAVA_HOME=C:\Program Files\Java\jdk-17"
if exist "%JAVA_HOME%\bin\java.exe" (
    echo ✅ Java 17 encontrado en %JAVA_HOME%
) else (
    echo ❌ Java 17 no encontrado
    echo 💡 Los JARs ya están construidos, solo necesitas Docker
)

echo.
echo 📁 Archivos del proyecto verificados:
if exist "docker-compose.yml" (
    echo ✅ docker-compose.yml
) else (
    echo ❌ docker-compose.yml faltante
)

if exist "client\build\libs\client.jar" (
    echo ✅ client.jar construido
) else (
    echo ❌ client.jar faltante
)

if exist "account\build\libs\account.jar" (
    echo ✅ account.jar construido
) else (
    echo ❌ account.jar faltante
)

echo.
echo 🎯 SIGUIENTE PASO:
if exist "C:\Program Files\Docker\Docker\Docker Desktop.exe" (
    echo 1. Inicia Docker Desktop desde el menú inicio
    echo 2. Espera a que esté listo (ícono verde en bandeja)
    echo 3. Ejecuta: docker-compose up --build -d
) else (
    echo 1. Instala Docker Desktop desde: https://www.docker.com/products/docker-desktop/
    echo 2. Reinicia tu computadora
    echo 3. Ejecuta este script nuevamente
)

echo.
pause

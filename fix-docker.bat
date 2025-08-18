@echo off
echo === Diagnóstico y Solución de Docker ===
echo.

echo 🔍 Verificando Docker Desktop...
tasklist /FI "IMAGENAME eq Docker Desktop.exe" 2>nul | find /I "Docker Desktop.exe" >nul
if %ERRORLEVEL% EQU 0 (
    echo ✅ Docker Desktop está ejecutándose
) else (
    echo ❌ Docker Desktop NO está ejecutándose
    echo.
    echo 🚀 Intentando iniciar Docker Desktop...
    start "" "C:\Program Files\Docker\Docker\Docker Desktop.exe"
    echo.
    echo ⏳ Esperando a que Docker Desktop inicie (30 segundos)...
    timeout /t 30 /nobreak >nul
)

echo.
echo 🔍 Verificando Docker Engine...
docker --version >nul 2>&1
if %ERRORLEVEL% EQU 0 (
    echo ✅ Docker Engine está funcionando
    docker ps >nul 2>&1
    if %ERRORLEVEL% EQU 0 (
        echo ✅ Docker está listo para usar
        echo.
        echo 🚀 Iniciando despliegue del sistema bancario...
        docker-compose up --build -d
        echo.
        echo 📊 Estado de los contenedores:
        docker-compose ps
    ) else (
        echo ⚠️ Docker está iniciando, espera unos minutos más
    )
) else (
    echo ❌ Docker Engine no está disponible
    echo.
    echo 💡 Soluciones:
    echo 1. Asegúrate de que Docker Desktop esté instalado
    echo 2. Ejecuta Docker Desktop como administrador
    echo 3. Reinicia Docker Desktop desde el menú de la bandeja del sistema
    echo 4. Reinicia tu computadora si es necesario
)

echo.
echo 📖 Instrucciones manuales:
echo 1. Busca "Docker Desktop" en el menú de inicio
echo 2. Haz clic derecho y selecciona "Ejecutar como administrador"
echo 3. Espera a que aparezca el ícono de Docker en la bandeja del sistema
echo 4. Cuando esté verde, ejecuta este script nuevamente

pause

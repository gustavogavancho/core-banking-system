@echo off
echo === Despliegue Local del Sistema Bancario ===
echo (Sin Docker - usando MySQL local y Java)
echo.

REM Verificar Java 17
echo 🔍 Verificando Java 17...
set "JAVA_HOME=C:\Program Files\Java\jdk-17"
"%JAVA_HOME%\bin\java" -version >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo ❌ Java 17 no encontrado
    echo Por favor instala OpenJDK 17 desde: https://adoptium.net/
    pause
    exit /b 1
)
echo ✅ Java 17 encontrado

REM Verificar MySQL
echo.
echo 🔍 Verificando MySQL...
mysql --version >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo ❌ MySQL no encontrado en PATH
    echo Por favor instala MySQL Server desde: https://dev.mysql.com/downloads/mysql/
    pause
    exit /b 1
)
echo ✅ MySQL encontrado

REM Crear base de datos
echo.
echo 🗄️ Configurando base de datos...
echo Ejecutando script de base de datos...
mysql -u root -p1234 < BaseDatos.sql
if %ERRORLEVEL% NEQ 0 (
    echo ⚠️ Error al ejecutar script de BD. Verifica que MySQL esté ejecutándose
    echo y que la contraseña sea '1234'
)

REM Iniciar servicios
echo.
echo 🚀 Iniciando servicios...

REM Client Service
echo Iniciando Client Service en puerto 8081...
start "Client Service" cmd /c "cd client && "%JAVA_HOME%\bin\java" -jar build\libs\client.jar"
timeout /t 5 /nobreak >nul

REM Account Service
echo Iniciando Account Service en puerto 8080...
start "Account Service" cmd /c "cd account && "%JAVA_HOME%\bin\java" -jar build\libs\account.jar"
timeout /t 5 /nobreak >nul

echo.
echo ✅ Servicios iniciados:
echo   - Client Service: http://localhost:8081
echo   - Account Service: http://localhost:8080
echo   - MySQL Database: localhost:3306
echo.
echo 📖 Documentación API:
echo   - Client Swagger: http://localhost:8081/swagger-ui.html
echo   - Account Swagger: http://localhost:8080/swagger-ui.html
echo.
echo 🛑 Para detener los servicios, cierra las ventanas que se abrieron
echo.
pause

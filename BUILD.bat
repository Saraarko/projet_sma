@echo off
REM Build script for SRUU Emergency Response System (Windows)

echo ==========================================
echo Building SRUU Emergency System
echo ==========================================
echo.

REM Check Java
echo [1/3] Checking Java installation...
where java >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Java not found. Please install Java 11 or higher.
    echo Visit: https://www.oracle.com/java/technologies/downloads/
    pause
    exit /b 1
)
for /f "tokens=3" %%A in ('java -version 2^>^&1 ^| findstr /R "version"') do set JAVA_VERSION=%%A
echo Visual Java found: %JAVA_VERSION%
echo.

REM Check Maven
echo [2/3] Checking Maven installation...
where mvn >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Maven not found. Please install Maven 3.6 or higher.
    echo Visit: https://maven.apache.org/download.cgi
    echo.
    echo Windows Installation Steps:
    echo 1. Download Maven from https://maven.apache.org/download.cgi
    echo 2. Extract to a folder (e.g., C:\apache-maven-3.9.0)
    echo 3. Add to PATH: Control Panel ^> System ^> Environment Variables
    echo 4. Add: C:\apache-maven-3.9.0\bin
    echo 5. Restart Command Prompt
    pause
    exit /b 1
)
for /f "tokens=3" %%A in ('mvn --version ^| findstr /R "Apache Maven"') do set MVN_VERSION=%%A
echo Visual Maven found: %MVN_VERSION%
echo.

REM Build
echo [3/3] Compiling project (this may take 1-2 minutes)...
call mvn clean package -DskipTests -q
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Build failed. Check error messages above.
    pause
    exit /b 1
)

if exist target\sruu-emergency-system-1.0.0.jar (
    echo Visual Build successful!
    echo.
    for /F "usebackq" %%A in ('target\sruu-emergency-system-1.0.0.jar') do set JAR_SIZE=%%~zA
    setlocal enabledelayedexpansion
    set /A JAR_SIZE_MB=!JAR_SIZE! / 1048576
    echo JAR location: target\sruu-emergency-system-1.0.0.jar
    echo JAR size: !JAR_SIZE_MB! MB
    endlocal
    echo.
    echo To run the system:
    echo   java -jar target\sruu-emergency-system-1.0.0.jar
    echo.
    echo Or double-click: RUN.bat
) else (
    echo ERROR: Build failed. JAR not found.
    pause
    exit /b 1
)

pause

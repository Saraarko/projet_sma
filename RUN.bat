@echo off
REM Run script for SRUU Emergency Response System (Windows)

echo ==========================================
echo Starting SRUU Emergency System
echo ==========================================
echo.

REM Check if JAR exists
if not exist target\sruu-emergency-system-1.0.0.jar (
    echo ERROR: JAR file not found!
    echo Please build the project first:
    echo   1. Run: BUILD.bat
    echo   2. Or run: mvn clean package
    pause
    exit /b 1
)

REM Check Java
where java >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Java not found. Please install Java 11 or higher.
    pause
    exit /b 1
)

echo Starting agents...
echo.
echo Press Ctrl+C to stop the system.
echo.

java -jar target\sruu-emergency-system-1.0.0.jar

pause

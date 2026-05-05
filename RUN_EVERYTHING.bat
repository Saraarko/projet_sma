@echo off
REM ============================================================================
REM SRUU - Urban Emergency Response System
REM Complete Automated Setup & Execution Script
REM ============================================================================

setlocal enabledelayedexpansion

echo.
echo ============================================================================
echo SRUU Emergency Response System - Automated Setup & Run
echo ============================================================================
echo.
echo This script will:
echo   1. Check Java installation
echo   2. Check Maven installation
echo   3. Compile the project (if needed)
echo   4. Run the simulation
echo   5. Display results
echo.
echo ============================================================================
echo.

REM ============================================================================
REM STEP 1: CHECK JAVA
REM ============================================================================

echo [1/5] Checking Java installation...
where java >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Java not found!
    echo Please install Java 11 or higher from:
    echo   https://www.oracle.com/java/technologies/downloads/
    echo.
    pause
    exit /b 1
)

for /f "tokens=3" %%A in ('java -version 2^>^&1 ^| findstr /R "version"') do set JAVA_VER=%%A
echo OK: Java found (version: %JAVA_VER%)
echo.

REM ============================================================================
REM STEP 2: CHECK MAVEN
REM ============================================================================

echo [2/5] Checking Maven installation...
where mvn >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Maven not found!
    echo.
    echo Maven is required. Please install it:
    echo.
    echo OPTION A: Automatic Installation
    echo   1. Download: https://maven.apache.org/download.cgi
    echo   2. Extract to: C:\apache-maven-3.9.6
    echo   3. Add to PATH: Control Panel ^> System ^> Environment Variables
    echo   4. Add variable MAVEN_HOME = C:\apache-maven-3.9.6
    echo   5. Add to PATH: C:\apache-maven-3.9.6\bin
    echo   6. Restart Command Prompt
    echo.
    echo OPTION B: Use IntelliJ IDEA (has built-in Maven)
    echo   - Download: https://www.jetbrains.com/idea/download/
    echo   - Open the projet_sma folder
    echo   - Right-click pom.xml ^> Run 'Maven'
    echo.
    pause
    exit /b 1
)

for /f "tokens=3" %%A in ('mvn --version ^| findstr /R "Apache Maven"') do set MVN_VER=%%A
echo OK: Maven found (version: %MVN_VER%)
echo.

REM ============================================================================
REM STEP 3: COMPILE PROJECT
REM ============================================================================

echo [3/5] Compiling project...
if exist target\sruu-emergency-system-1.0.0.jar (
    echo JAR already exists, skipping compilation...
) else (
    echo Running: mvn clean package -DskipTests
    echo This may take 1-2 minutes on first run...
    echo.
    call mvn clean package -DskipTests -q
    if !ERRORLEVEL! NEQ 0 (
        echo ERROR: Compilation failed!
        echo Try running manually:
        echo   mvn clean package -DskipTests
        pause
        exit /b 1
    )
)

if exist target\sruu-emergency-system-1.0.0.jar (
    for /F "usebackq" %%A in ('target\sruu-emergency-system-1.0.0.jar') do set JAR_SIZE=%%~zA
    echo OK: Compilation successful
    echo JAR created: target\sruu-emergency-system-1.0.0.jar
) else (
    echo ERROR: JAR not found after compilation!
    pause
    exit /b 1
)
echo.

REM ============================================================================
REM STEP 4: CREATE LOGS DIRECTORY
REM ============================================================================

echo [4/5] Setting up directories...
if not exist logs (
    mkdir logs
    echo Created logs directory
)
echo OK: Directories ready
echo.

REM ============================================================================
REM STEP 5: RUN THE SYSTEM
REM ============================================================================

echo [5/5] Starting SRUU Emergency Response System...
echo.
echo ============================================================================
echo SIMULATION RUNNING
echo ============================================================================
echo.
echo The system will:
echo   - Initialize 14 agents (9 types)
echo   - Generate 3-9 random incidents
echo   - Coordinate emergency response
echo   - Generate JSON reports
echo.
echo Watch for:
echo   [Sensor-X] Incident reported: FIRE/MEDICAL/BIOHAZARD...
echo   [Dispatcher] Selected unit: FireTruck-1/Ambulance-1...
echo   [FireTruck-1] Water depleting (watch for auto-abort)
echo   [Logger] Generated report at: logs/incident_report_*.json
echo.
echo To stop: Press Ctrl + C
echo.
echo ============================================================================
echo.

java -jar target\sruu-emergency-system-1.0.0.jar

REM ============================================================================
REM AFTER SHUTDOWN
REM ============================================================================

echo.
echo ============================================================================
echo SIMULATION COMPLETED
echo ============================================================================
echo.

REM Check if reports were generated
if exist logs\incident_report_*.json (
    echo SUCCESS! Reports generated:
    echo.
    dir /B logs\incident_report_*.json
    echo.
    echo To view reports:
    echo   type logs\incident_report_*.json
    echo   OR
    echo   python -m json.tool ^< logs\incident_report_*.json
    echo.
) else (
    echo WARNING: No reports found in logs directory
    echo Make sure the simulation ran for at least 30 seconds
)

echo.
echo Next steps:
echo   1. Review TECHNICAL_REPORT.md for design details
echo   2. Follow PRESENTATION_OUTLINE.md for demo
echo   3. Check JSON reports in logs\ folder
echo.
echo ============================================================================
echo.

pause

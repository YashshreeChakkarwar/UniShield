@echo off
REM ============================================================
REM UniShield - Build and Run Script
REM ============================================================

echo ========================================
echo   UniShield - Build and Run
echo ========================================

SET JAVAFX_PATH=lib\javafx-sdk-23.0.1\lib
SET MYSQL_JAR=lib\mysql-connector-j-8.3.0.jar
SET SRC=src\com\unishield
SET OUT=out

REM Check if dependencies exist
if not exist "%JAVAFX_PATH%" (
    echo ERROR: JavaFX SDK not found. Run setup.ps1 first.
    echo   powershell -ExecutionPolicy Bypass -File setup.ps1
    pause
    exit /b 1
)

REM Create output directory
if not exist "%OUT%" mkdir "%OUT%"

REM Copy resources
echo [1/3] Copying resources...
if not exist "%OUT%\resources" mkdir "%OUT%\resources"
copy /Y resources\styles.css "%OUT%\resources\" >nul 2>&1

REM Compile all Java files
echo [2/3] Compiling Java sources...
javac --module-path "%JAVAFX_PATH%" --add-modules javafx.controls -cp "%MYSQL_JAR%" -d "%OUT%" %SRC%\*.java

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo COMPILATION FAILED! Check errors above.
    pause
    exit /b 1
)

echo   Compilation successful!

REM Run the application
echo [3/3] Launching UniShield...
echo.
java --module-path "%JAVAFX_PATH%" --add-modules javafx.controls -cp "%OUT%;%MYSQL_JAR%" com.unishield.Launcher

pause

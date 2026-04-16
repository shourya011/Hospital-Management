@echo off
REM Run script for Hospital Management System
REM Usage: run.bat

echo ======================================================
echo RBH Hospital Management System
echo ======================================================
echo.

REM Check if bin directory exists
if not exist "bin" (
    echo ERROR: bin directory not found.
    echo Please compile the project first using: compile.bat
    pause
    exit /b 1
)

REM Check for MySQL JDBC driver
dir lib\mysql-connector-java-*.jar >nul 2>&1
if errorlevel 1 (
    echo ERROR: MySQL JDBC driver not found in lib\ directory
    echo Please download mysql-connector-java-8.x.jar and place it in lib\
    pause
    exit /b 1
)

echo Starting Hospital Management System...
echo.

REM Run the application
for /f "tokens=*" %%A in ('dir /b lib\mysql-connector-java-*.jar') do (
    java -cp "bin;lib\%%A" Main
)

pause

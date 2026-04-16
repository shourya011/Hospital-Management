@echo off
REM Compile script for Hospital Management System
REM Usage: compile.bat

echo ======================================================
echo RBH Hospital Management System - Compilation Script
echo ======================================================
echo.

REM Check if bin directory exists
if not exist "bin" (
    echo Creating bin directory...
    mkdir bin
)

REM Check if javac is available
javac -version >nul 2>&1
if errorlevel 1 (
    echo ERROR: javac not found. Please install Java 11+
    exit /b 1
)

echo Using Java compiler: javac
echo.
echo Compiling Java files...
echo --------------------------------------------------
echo.

REM Compile db package
echo Compiling db package...
javac -d bin src\db\*.java
if errorlevel 1 (
    echo ERROR: Failed to compile db package
    exit /b 1
)

REM Compile model package
echo Compiling model package...
javac -d bin src\model\*.java
if errorlevel 1 (
    echo ERROR: Failed to compile model package
    exit /b 1
)

REM Compile dao package
echo Compiling dao package...
javac -cp "bin;lib\*" -d bin src\dao\*.java
if errorlevel 1 (
    echo ERROR: Failed to compile dao package
    exit /b 1
)

REM Compile datastructure package
echo Compiling datastructure package...
javac -d bin src\datastructure\*.java
if errorlevel 1 (
    echo ERROR: Failed to compile datastructure package
    exit /b 1
)

REM Compile ui package
echo Compiling ui package...
javac -cp "bin;lib\*" -d bin src\ui\*.java
if errorlevel 1 (
    echo ERROR: Failed to compile ui package
    exit /b 1
)

REM Compile Main
echo Compiling Main.java...
javac -cp "bin;lib\*" -d bin src\Main.java
if errorlevel 1 (
    echo ERROR: Failed to compile Main.java
    exit /b 1
)

echo.
echo --------------------------------------------------
echo.
echo ✓ Compilation successful!
echo.
echo To run the application, use:
echo java -cp "bin;lib\mysql-connector-java-8.x.jar" Main
echo.
echo ======================================================
pause

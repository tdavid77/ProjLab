@echo off
REM Zuzmaravaros - GUI valtozat fordito es inditoszkript
REM Hasznalat: kettos kattintas ezen a fajlon, vagy `run-gui.bat` a parancssorban

cd /d "%~dp0"

if not exist bin mkdir bin

echo [1/2] Forditas...
javac -d bin -encoding UTF-8 -sourcepath src src\gui\Main.java
if errorlevel 1 (
    echo.
    echo ===========================================
    echo  FORDITASI HIBA - lasd a fenti uzeneteket
    echo ===========================================
    pause
    exit /b 1
)

echo [2/2] Inditas...
echo.
java -cp bin gui.Main

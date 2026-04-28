@echo off
setlocal enabledelayedexpansion
cd /d "%~dp0"

REM Beallitjuk a konzolt UTF-8 kodolasra, hogy ne legyen kódlap hiba
chcp 65001 > nul

echo === KOD TISZTITASA ===
if exist "bin" rmdir /s /q "bin"
mkdir "bin"

echo === KOD FORDITASA (JAVAC) ===
if exist sources_tmp.txt del sources_tmp.txt
REM A dir parancs sokkal biztonsagosabb a fajllista letrehozasara Windows alatt
dir /s /b src\*.java > sources_tmp.txt

javac -d bin -encoding UTF-8 @sources_tmp.txt
del sources_tmp.txt
echo Forditas kesz!
echo.

echo === AUTOMATIKUS TESZTFUTTATO ===
if not exist "tesztek\out" mkdir "tesztek\out"

for %%f in (tesztek\*_in.txt) do (
    set "in_file=%%f"
    set "base_name=%%~nf"
    set "base_name=!base_name:_in=!"
    
    set "exp_file=tesztek\!base_name!_exp.txt"
    set "out_file=tesztek\out\!base_name!_out.txt"
    
    REM Mivel a SzkeletonProgram.java fajlban benne van a "package skeletonprogram;" sor, 
    REM igy a teljes nevevel kell futtatni:
    java -cp bin motor.SzkeletonProgram --input="!in_file!" > "!out_file!"
    
    fc /W "!exp_file!" "!out_file!" > nul
    
    if !errorlevel! equ 0 (
        echo [PASS] !base_name!
    ) else (
        echo [FAIL] !base_name! - Elteres talalhato!
        fc /W "!exp_file!" "!out_file!"
    )
)
echo ================================
pause
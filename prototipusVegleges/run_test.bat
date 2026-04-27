@echo off
setlocal enabledelayedexpansion
cd /d "%~dp0"

echo === KOD FORDITASA (JAVAC) ===
REM Osszes .java fajl osszegyujtese egy ideiglenes listaba
if exist sources_tmp.txt del sources_tmp.txt
for /r . %%f in (*.java) do echo %%f >> sources_tmp.txt
javac -encoding UTF-8 @sources_tmp.txt
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
    
    REM Java program futtatasa
    java skeletonprogram.SzkeletonProgram --input="!in_file!" > "!out_file!"
    
    REM Osszehasonlitas
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
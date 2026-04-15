@echo off
REM Prototipus futtatas Windows alatt.
REM Hasznalat:
REM   run_test.bat [script.txt]
REM Pelda:
REM   run_test.bat input.txt

set SCRIPT=%~1
if "%SCRIPT%"=="" set SCRIPT=input.txt

if not exist "%SCRIPT%" (
  echo Hiba: a script fajl nem talalhato: %SCRIPT%
  exit /b 1
)

echo Futtatas script alapjan: %SCRIPT%
java skeletonprogram.SzkeletonProgram --input="%SCRIPT%"

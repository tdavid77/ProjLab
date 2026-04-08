@echo off
REM Prototipus teszt futtatas Windows alatt.
REM Hasznalat: run_test.bat <testszam> [bemenet.txt] [kimenet.txt]

if "%~1"=="" (
  echo Hasznalat: run_test.bat ^<testszam^> [bemenet.txt] [kimenet.txt]
  echo Pelda: run_test.bat 4 input.txt output.txt
  goto :eof
)

set TEST=%~1
set INPUT=%~2
set OUTPUT=%~3

set CMD=java skeletonprogram.SzkeletonProgram --test=%TEST%
if not "%INPUT%"=="" set CMD=%CMD% --input=%INPUT%
if not "%OUTPUT%"=="" set CMD=%CMD% --output=%OUTPUT%

echo Futtatas: %CMD%
%CMD%

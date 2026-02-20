@echo off
REM Build mod
cd /d "C:\FluffyFox"
call gradlew.bat build --offline

REM Copy jar to MultiMC instance
set "SRC=%CD%\build\libs\fluffyfox-1.0.0.jar"
set "DEST=C:\MultiMC\instances\wynn dev\.minecraft\mods"

if exist "%SRC%" (
    copy /y "%SRC%" "%DEST%"
) else (
    echo ERROR: Could not find "%SRC%"
    pause
    exit /b 1
)

REM Launch MultiMC
start "" "C:\MultiMC\MultiMC.exe" -l "wynn dev"
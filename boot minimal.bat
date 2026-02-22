@echo off
REM Build mod
cd /d "C:\FluffyFox"
call gradlew.bat build --offline

REM Copy jar to MultiMC instance
set "SRC=%CD%\build\libs\fluffyfox-1.0.0.jar"
set "DEST=C:\MultiMC\instances\wynn dev minimal\.minecraft\mods"
set "DEST1=C:\MultiMC\instances\wynn\.minecraft\mods"
set "DEST2=C:\MultiMC\instances\wynn 2\.minecraft\mods"
set "DEST3=C:\MultiMC\instances\wynn 3\.minecraft\mods"

if exist "%SRC%" (
    copy /y "%SRC%" "%DEST%"
    copy /y "%SRC%" "%DEST1%"
    copy /y "%SRC%" "%DEST2%"
    copy /y "%SRC%" "%DEST3%"
) else (
    echo ERROR: Could not find "%SRC%"
    pause
    exit /b 1
)

REM Launch MultiMC
start "" "C:\MultiMC\MultiMC.exe" -l "wynn dev minimal"
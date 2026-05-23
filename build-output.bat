@echo off
setlocal

set "DEFAULT_JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-21.0.10.7-hotspot"

if not defined JAVA_HOME (
    if exist "%DEFAULT_JAVA_HOME%\bin\java.exe" (
        set "JAVA_HOME=%DEFAULT_JAVA_HOME%"
    )
)

if defined JAVA_HOME (
    set "PATH=%JAVA_HOME%\bin;%PATH%"
)

where java >nul 2>nul
if errorlevel 1 (
    echo Java was not found.
    echo Install JDK 21 or set JAVA_HOME before running this script.
    exit /b 1
)

if not exist "Library-of-Exile-Rework\build.gradle" (
    echo Submodules are missing.
    echo Run: git submodule update --init --recursive
    exit /b 1
)

if not exist "the_harvest\build.gradle" (
    echo Submodules are missing.
    echo Run: git submodule update --init --recursive
    exit /b 1
)

if not exist "dungeon_realm\build.gradle" (
    echo Submodules are missing.
    echo Run: git submodule update --init --recursive
    exit /b 1
)

if not exist "ancient_obelisks\build.gradle" (
    echo Submodules are missing.
    echo Run: git submodule update --init --recursive
    exit /b 1
)

call gradlew.bat clean :library_of_exile:jar :jar :dungeon_realm:jar :the_harvest:jar :ancient_obelisks:jar
if errorlevel 1 exit /b %errorlevel%

powershell -ExecutionPolicy Bypass -File scripts\copy-playable-jar.ps1
exit /b %errorlevel%

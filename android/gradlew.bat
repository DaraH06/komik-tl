@echo off
setlocal
rem Gradle Wrapper — delegates ke distribusi lokal di C:\dev\gradle\gradle-9.9.2
cd /d "%~dp0"
call "C:\dev\gradle\gradle-9.9.2\bin\gradle.bat" %*
endlocal

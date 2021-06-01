@echo off
setlocal
set JAVA_OPTS=%JAVA_OPTS% -Dofbiz.home=%~dp0%
rem set JAVA_OPTS=-Xdebug -Xrunjdwp:transport=dt_socket,address=8787,server=y,suspend=y %JAVA_OPTS%
..\..\..\..\bin\run.bat

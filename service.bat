@echo off

if "%OS%" == "Windows_NT" setlocal
set "PR_COMMAND_DIR=%~dp0"

set PR_INSTALL=GZoomTrunk

set PR_COMMAND_ARGS=%PR_COMMAND_ARGS% --Description "http://www.gzoom.it/"
set PR_COMMAND_ARGS=%PR_COMMAND_ARGS% --LogPath "%PR_COMMAND_DIR%runtime\logs"
set PR_COMMAND_ARGS=%PR_COMMAND_ARGS% --LogPrefix "commons-daemon"
set PR_COMMAND_ARGS=%PR_COMMAND_ARGS% --JvmOptions "-Xms128M;-Xmx512M;-XX:PermSize=128m;-XX:MaxPermSize=128m"

set PR_COMMAND_ARGS=%PR_COMMAND_ARGS% --Classpath ofbiz.jar
set PR_COMMAND_ARGS=%PR_COMMAND_ARGS% --StartMode jvm
set PR_COMMAND_ARGS=%PR_COMMAND_ARGS% --StartClass org.ofbiz.base.start.Start
set PR_COMMAND_ARGS=%PR_COMMAND_ARGS% --StartParams -start
set PR_COMMAND_ARGS=%PR_COMMAND_ARGS% --StopMode jvm
set PR_COMMAND_ARGS=%PR_COMMAND_ARGS% --StopClass org.ofbiz.base.start.Start
set PR_COMMAND_ARGS=%PR_COMMAND_ARGS% --StopParams -shutdown

if "%JRE_HOME%" == "" set "JRE_HOME=%JAVA_HOME%\jre"
if not exist "%JRE_HOME%" set "JRE_HOME=%JAVA_HOME%"

set "PR_JVM=%JRE_HOME%\bin\server\jvm.dll"
if exist "%PR_JVM%" goto foundJvm
set "PR_JVM=%JRE_HOME%\bin\client\jvm.dll"
if exist "%PR_JVM%" goto foundJvm
set PR_JVM=auto
:foundJvm
set PR_COMMAND_ARGS=%PR_COMMAND_ARGS% --Jvm "%PR_JVM%"

set PR_COMMAND=%1
shift
if "%PR_COMMAND%" == "" set PR_COMMAND=help

echo on
%PR_INSTALL% %PR_COMMAND% %PR_COMMAND_ARGS% %1 %2 %3 %4 %5 %6 %7 %8 %9

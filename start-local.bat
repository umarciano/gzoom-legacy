@echo off
rem ============================================================
rem  GZOOM - Avvio rapido ambiente LOCALE
rem  Doppio click su questo file per avviare OFBiz in locale
rem ============================================================
set GZOOM_ENV=local
echo [GZOOM] Avvio in ambiente: %GZOOM_ENV%
echo.
cd /d "%~dp0tools\apache-tomcat-9.0.37\bin"
call catalina.bat start

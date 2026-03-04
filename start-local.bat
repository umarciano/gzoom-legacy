@echo off
rem ============================================================
rem  GZOOM - Avvio rapido ambiente LOCALE
rem  Doppio click su questo file per avviare OFBiz in locale
rem ============================================================
set GZOOM_ENV=local
rem Path al file saml.properties (senza suffisso .gzoom/.collaudo - lo aggiunge AuthWrapper)
set "SP_CONF=%~dp0..\gzoom-samlweb\ext-conf\saml.properties"
echo [GZOOM] Avvio in ambiente: %GZOOM_ENV%
echo [GZOOM] SAML config: %SP_CONF%
echo.
cd /d "%~dp0tools\apache-tomcat-9.0.37\bin"
set "CATALINA_OPTS=%CATALINA_OPTS% -DGZOOM_ENV=%GZOOM_ENV% -Dsp.conf=%SP_CONF%"
call catalina.bat start

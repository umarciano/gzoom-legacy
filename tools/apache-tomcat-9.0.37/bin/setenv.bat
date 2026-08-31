@echo off
rem =============================================================================
rem GZOOM - Tomcat setenv.bat (Windows)
rem Caricato automaticamente da catalina.bat prima dell'avvio JVM.
rem
rem Imposta GZOOM_ENV prima di avviare Tomcat:
rem   set GZOOM_ENV=local      → overlay custom-dev.properties (localhost:5433/preprod)
rem   set GZOOM_ENV=collaudo   → overlay custom-collaudo.properties (172.20.145.104:5432/cardarelli)
rem   set GZOOM_ENV=prod       → overlay custom-prod.properties
rem
rem Se GZOOM_ENV non è impostato: usa solo custom.properties base (localhost:5432/cardarelli)
rem
rem SVILUPPATORI: personalizzare custom-dev.properties per la propria config locale.
rem NON committare custom-dev.properties se si cambiano i valori DB.
rem =============================================================================

rem Default: local se non impostato
if "%GZOOM_ENV%"=="" set GZOOM_ENV=local

rem Calcola OFBIZ_HOME = 3 livelli sopra CATALINA_HOME
rem  CATALINA_HOME = ..\tools\apache-tomcat-9.0.37
rem  OFBIZ_HOME    = ..\gzoom-legacy
for %%I in ("%CATALINA_HOME%\..\..\..") do set "OFBIZ_HOME=%%~fI"

rem Path base per i file saml.properties
set "GZOOM_SAMLWEB_CONF=%OFBIZ_HOME%\..\gzoom-samlweb\ext-conf"

echo ==========================================
echo  GZOOM - Ambiente: %GZOOM_ENV%
echo  OFBIZ_HOME: %OFBIZ_HOME%
echo ==========================================

rem Seleziona il file saml.properties in base all'ambiente
if "%GZOOM_ENV%"=="collaudo" (
    set "SAML_PROPS=%GZOOM_SAMLWEB_CONF%\saml.properties.collaudo"
) else if "%GZOOM_ENV%"=="prod" (
    set "SAML_PROPS=%GZOOM_SAMLWEB_CONF%\saml.properties.prod"
) else (
    set "SAML_PROPS=%GZOOM_SAMLWEB_CONF%\saml.properties.gzoom"
)

rem Passa l'ambiente come system property Java: letta da UtilProperties per
rem caricare il file custom-{env}.properties in overlay su custom.properties
set "CATALINA_OPTS=%CATALINA_OPTS% -DGZOOM_ENV=%GZOOM_ENV%"

rem Configura il path SAML per AuthWrapper (-Dsp.conf)
if exist "%SAML_PROPS%" (
    echo [ENV] SAML config: %SAML_PROPS%
    set "CATALINA_OPTS=%CATALINA_OPTS% -Dsp.conf=%SAML_PROPS%"
) else (
    echo [ENV] ATTENZIONE: %SAML_PROPS% non trovato, AuthWrapper usera' %%USERPROFILE%%\sp\saml.properties
)

echo [ENV] CATALINA_OPTS: %CATALINA_OPTS%
echo ==========================================
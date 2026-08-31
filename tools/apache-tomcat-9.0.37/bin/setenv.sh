#!/bin/sh
# =============================================================================
# GZOOM - Tomcat setenv.sh (Linux/Mac)
# Caricato automaticamente da catalina.sh prima dell'avvio.
#
# Imposta GZOOM_ENV prima di avviare Tomcat:
#   export GZOOM_ENV=local      → overlay custom-dev.properties (localhost:5433/preprod)
#   export GZOOM_ENV=collaudo   → overlay custom-collaudo.properties (172.20.145.104:5432/cardarelli)
#   export GZOOM_ENV=prod       → overlay custom-prod.properties
#
# Se GZOOM_ENV non è impostato: usa solo custom.properties base (localhost:5432/cardarelli)
#
# Per rendere permanente su un server (es. collaudo):
#   echo "export GZOOM_ENV=collaudo" >> ~/.bashrc
#
# SVILUPPATORI: personalizzare custom-dev.properties per la propria config locale.
# NON committare custom-dev.properties se si cambiano i valori DB.
# =============================================================================

GZOOM_ENV=${GZOOM_ENV:-local}

# OFBIZ_HOME viene già impostato da catalina.sh prima di caricare setenv.sh.
# Struttura: .../gzoom-legacy/tools/apache-tomcat-9.0.37/bin/setenv.sh
# OFBIZ_HOME = .../workspace/ (non gzoom-legacy, ma la dir padre)
# Fallback nel caso setenv.sh venga eseguito manualmente
if [ -z "$OFBIZ_HOME" ]; then
    OFBIZ_HOME="$(cd "$(dirname "$0")/../../.." 2>/dev/null && pwd)"
fi

# gzoom-samlweb si trova in OFBIZ_HOME/gzoom-samlweb (se OFBIZ_HOME=workspace)
# oppure in OFBIZ_HOME/../gzoom-samlweb (se OFBIZ_HOME=gzoom-legacy)
# Proviamo entrambi i path
if [ -d "$OFBIZ_HOME/gzoom-samlweb/ext-conf" ]; then
    GZOOM_SAMLWEB_CONF="$OFBIZ_HOME/gzoom-samlweb/ext-conf"
else
    GZOOM_SAMLWEB_CONF="$OFBIZ_HOME/../gzoom-samlweb/ext-conf"
fi

echo "=========================================="
echo " GZOOM - Ambiente: $GZOOM_ENV"
echo " OFBIZ_HOME: $OFBIZ_HOME"
echo "=========================================="

# Seleziona il file saml.properties e le system properties in base all'ambiente
case "$GZOOM_ENV" in
    collaudo)
        SAML_PROPS="$GZOOM_SAMLWEB_CONF/saml.properties.collaudo"
        ;;
    prod)
        SAML_PROPS="$GZOOM_SAMLWEB_CONF/saml.properties.prod"
        ;;
    local|dev|*)
        SAML_PROPS="$GZOOM_SAMLWEB_CONF/saml.properties.gzoom"
        ;;
esac

# Passa l'ambiente come system property Java: letta da UtilProperties per
# caricare il file custom-{env}.properties al posto di custom.properties
# sso.frontend.url NON è più necessario come -D flag: viene letto direttamente
# da custom-{GZOOM_ENV}.properties tramite il meccanismo overlay di UtilProperties
CATALINA_OPTS="$CATALINA_OPTS -DGZOOM_ENV=$GZOOM_ENV"

# Configura il path SAML per AuthWrapper (-Dsp.conf)
if [ -f "$SAML_PROPS" ]; then
    echo "[ENV] SAML config: $SAML_PROPS"
    CATALINA_OPTS="$CATALINA_OPTS -Dsp.conf=$SAML_PROPS"
else
    echo "[ENV] ATTENZIONE: $SAML_PROPS non trovato, AuthWrapper usera' ~/sp/saml.properties"
fi

export CATALINA_OPTS
echo "[ENV] CATALINA_OPTS: $CATALINA_OPTS"
echo "=========================================="

# Java 11 richiesto da OFBiz/Tomcat (incompatibile con Java 17)
# Su prod è installato java-11-amazon-corretto, su collaudo java-11-openjdk
# Rileva automaticamente quale Java 11 è disponibile
if [ -d "/usr/lib/jvm/java-11-amazon-corretto" ]; then
    export JAVA_HOME=/usr/lib/jvm/java-11-amazon-corretto
elif [ -d "/usr/lib/jvm/java-11-openjdk" ]; then
    export JAVA_HOME=$(ls -d /usr/lib/jvm/java-11-openjdk* 2>/dev/null | head -1)
fi
if [ -n "$JAVA_HOME" ]; then
    export JRE_HOME=$JAVA_HOME
    echo "[ENV] JAVA_HOME: $JAVA_HOME"
fi

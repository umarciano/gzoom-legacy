#!/bin/sh
#####################################################################
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
# 
# http://www.apache.org/licenses/LICENSE-2.0
# 
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.
#####################################################################

# =============================================================================
# GZOOM - Gestione ambiente
# =============================================================================
# Imposta GZOOM_ENV prima di avviare (o aggiungilo a ~/.bashrc per renderlo permanente):
#   export GZOOM_ENV=local      → overlay custom-dev.properties    (localhost:5433/preprod)
#   export GZOOM_ENV=collaudo   → overlay custom-collaudo.properties (172.20.145.104:5432/cardarelli)
#   export GZOOM_ENV=prod       → overlay custom-prod.properties
#
# Se GZOOM_ENV non è impostato: usa solo custom.properties base (localhost:5432/cardarelli)
#
# NON viene modificato nessun file su disco: l'overlay viene applicato
# in memoria da UtilProperties leggendo -DGZOOM_ENV al boot di OFBiz.
# =============================================================================

GZOOM_ENV=${GZOOM_ENV:-local}
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
GZOOM_SAMLWEB_CONF="$SCRIPT_DIR/../gzoom-samlweb/ext-conf"

echo "=========================================="
echo " GZOOM - Ambiente: $GZOOM_ENV"
echo "=========================================="

# Seleziona il file saml.properties in base all'ambiente
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

if [ -f "$SAML_PROPS" ]; then
    echo "[ENV] SAML config: $SAML_PROPS"
    SP_CONF_ARG="-Dsp.conf=$SAML_PROPS"
else
    echo "[ENV] ATTENZIONE: $SAML_PROPS non trovato, AuthWrapper userà ~/sp/saml.properties"
    SP_CONF_ARG=""
fi

echo "=========================================="

# shutdown settings
ADMIN_PORT=10523
ADMIN_KEY=so3du5kasd5dn
ADMIN_HOST=127.0.0.1

# console log file
OFBIZ_LOG=runtime/logs/console.log

# delete the last log
rm -f $OFBIZ_LOG

# VM args
ADMIN=" -Dofbiz.admin.host=$ADMIN_HOST -Dofbiz.admin.port=$ADMIN_PORT -Dofbiz.admin.key=$ADMIN_KEY"
#DEBUG="-Dsun.rmi.server.exceptionTrace=true"
#DEBUG="-Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=8091"
#automatic IP address for linux
#IPADDR=`/sbin/ifconfig eth0 | grep 'inet addr:' | cut -d: -f2 | awk '{ print $1}'`
#RMIIF="-Djava.rmi.server.hostname=$IPADDR"
MEMIF="-Xms256M -Xmx1024M"
#MISC="-Duser.language=en"
VMARGS="$MEMIF $MISC $DEBUG $RMIIF $ADMIN -DGZOOM_ENV=$GZOOM_ENV $SP_CONF_ARG"

# Worldpay Config
#VMARGS="-Xbootclasspath/p:applications/accounting/lib/cryptix.jar $VMARGS"

# location of java executable
if [ -f "$JAVA_HOME/bin/java" ]; then
  JAVA="$JAVA_HOME/bin/java"
else
  JAVA=java
fi

# start ofbiz
#$JAVA $VMARGS -jar ofbiz.jar $* >>$OFBIZ_LOG 2>>$OFBIZ_LOG&
exec "$JAVA" $VMARGS -jar ofbiz.jar "$@"

import org.ofbiz.base.util.*;

/**
 * applyScopingBSFilter - scoping della LISTA schede CTX_BS.
 *
 * Traduce il perimetro calcolato da getBSPerimetroOrgUnits (punto unico, condiviso con la guardia
 * anti-IDOR del dettaglio) nella CSV parameters.orgUnitId che il template SQL rende come
 * "A.ORG_UNIT_ID IN (...)".
 *
 * ADMIN e ruoli senza gruppo direttore (NONE) non ricevono filtro: mantengono il comportamento
 * storico di lista non limitata. Per tutti gli altri, lista vuota -> "__NONE__" (zero risultati).
 */

GroovyUtil.runScriptAtLocation("com/mapsengineering/stratperf/getBSPerimetroOrgUnits.groovy", context);

String kind = context.bsPerimetroKind;
if ("ADMIN".equals(kind) || "NONE".equals(kind)) {
    return;
}

def perimetro = context.bsPerimetroOrgUnits;
parameters.orgUnitId = (perimetro == null || perimetro.isEmpty()) ? "__NONE__" : perimetro.join(",");
Debug.log("### [applyScopingBSFilter] kind=" + kind + " orgUnitId=" + parameters.orgUnitId);

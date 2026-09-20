import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GroovyUtil;
import org.ofbiz.entity.condition.EntityCondition;

final String noteUoName = "Note Direttore UO";
final String noteDirName = "Note Direttore Amministrativo/Sanitario";
final String userLoginId = userLogin?.getString("userLoginId");
final boolean isSystemAdmin = "admin" == userLoginId;

// "Direttore della UO di QUESTA scheda" = direttore (UO|DIP|SAN|AMM) + ORG_RESPONSIBLE della sua UO.
// Il solo gruppo STRATPERF_DIR_UO non basta: la gerarchia dei profili lo rimuove a chi sta piu' in
// alto (POST_IMPORT_ASSEGNA_PROFILI.sql), quindi un Dir Dipartimento che dirige una propria UO ne e'
// il direttore pur non avendo il gruppo. Punto unico: checkBSDirettoreUo.
GroovyUtil.runScriptAtLocation("com/mapsengineering/stratperf/checkBSDirettoreUo.groovy", context);
final boolean isDirUoWe = context.bsIsDirettoreUoWe;
final boolean isDirSanAmm = context.bsIsDirSanAmm;
final boolean isDirettore = context.bsIsDirettore;

// Stato REALE dall'entita' WorkEffort, non context.currentStatusId: nel form root
// loadWorkEffortViewCard non gira e context.currentStatusId resta il filtro di ricerca
// (vuoto nel Portale -> le note risultavano in sola lettura anche al direttore della UO).
final String currentStatusId = context.bsWeCurrentStatusId;

// Interrogazione (rootInqyTree=Y) e' sola consultazione: nessuna nota editabile, come per i
// bottoni di validazione (isDefinizioneScreen in checkDirettoreRole.groovy).
final boolean isDefinizioneScreen = !"Y".equals(parameters.rootInqyTree);

// Fase di validazione CTX_BS (workflow WEORCARD_*): TOVALIDATE (nota Direttore UO), VALPART (nota Direttore Amm/San).
// Le fasi successive (TOACCOUNT, ACCOUNTED, REVIEWED, CLOSED) sono consuntivazione: le note restano sempre in sola lettura.
final Set<String> validazioneStatuses = ['WEORCARD_TOVALIDATE', 'WEORCARD_TOCLRFY_DUO', 'WEORCARD_VALPART', 'WEORCARD_TOCLRFY_DSA'] as Set;
def isInValidazione = { String statusId -> validazioneStatuses.contains(statusId) };

def normalizeNoteName = { String value ->
    if (value == null) {
        return "";
    }
    return value
        .replace("&#47;", "/")
        .replace("&#x2F;", "/")
        .replace("&amp;#47;", "/")
        .trim();
};

Debug.log("### [checkStratPerfValidationNotes] " + userLoginId + " isDirettore=" + isDirettore
        + " isDirUoWe=" + isDirUoWe + " isDirSanAmm=" + isDirSanAmm
        + " statoReale=" + currentStatusId + " isDefinizioneScreen=" + isDefinizioneScreen);

def configureNote = { int index ->
    final String suffix = index.toString();
    final String noteNameRaw = context.get("noteName" + suffix);
    final String noteName = normalizeNoteName(noteNameRaw);
    final boolean isUoNote = noteUoName == noteName;
    final boolean isDirNote = noteDirName == noteName;
    final boolean isStrategicNote = isUoNote || isDirNote;
    // Vedere le note: qualsiasi direttore (il perimetro e' gia' garantito da checkBSDetailAccess).
    // Scriverle: la nota UO solo al direttore di QUELLA UO, la nota Dir San/Amm ai soli San/Amm.
    final boolean canView = isSystemAdmin || isDirettore;
    final boolean canEdit = isDefinizioneScreen && isInValidazione(currentStatusId) &&
        ((isUoNote && isDirUoWe && ("WEORCARD_TOVALIDATE" == currentStatusId || "WEORCARD_TOCLRFY_DUO" == currentStatusId)) ||
         (isDirNote && isDirSanAmm && ("WEORCARD_VALPART" == currentStatusId || "WEORCARD_TOCLRFY_DSA" == currentStatusId)));

    if (Debug.verboseOn()) Debug.logVerbose("checkStratPerfValidationNotes: note${suffix} raw='${noteNameRaw}', normalized='${noteName}', isStrategic=${isStrategicNote}, canView=${canView}, canEdit=${canEdit}", "checkStratPerfValidationNotes");

    context.put("stratPerfMainNote" + suffix, isStrategicNote);
    context.put("canEditNoteInfo" + suffix, canEdit);
    if (isStrategicNote && !canView) {
        context.remove("noteId" + suffix);
        context.remove("noteName" + suffix);
        context.remove("noteName" + suffix + "Lang");
        context.remove("noteInfo" + suffix);
        context.remove("noteInfo" + suffix + "Lang");
    }
};

configureNote(1);
configureNote(2);

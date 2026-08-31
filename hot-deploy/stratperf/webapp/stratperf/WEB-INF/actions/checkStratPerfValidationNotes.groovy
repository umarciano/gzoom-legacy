import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.condition.EntityCondition;

final String noteUoName = "Note Direttore UO";
final String noteDirName = "Note Direttore Amministrativo/Sanitario";
final String userLoginId = userLogin?.getString("userLoginId");
final boolean isSystemAdmin = "admin" == userLoginId;
final def groupIds = userLoginId ? delegator.findByAnd("UserLoginSecurityGroup", UtilMisc.toMap("userLoginId", userLoginId))*.getString("groupId") : [];
final boolean isDirUo = groupIds.contains("STRATPERF_DIR_UO");
final boolean isDirSanAmm = groupIds.contains("STRATPERF_DIR_SAN") || groupIds.contains("STRATPERF_DIR_AMM");
final String currentStatusId = context.currentStatusId ?: parameters.currentStatusId;

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

if (Debug.verboseOn()) Debug.logVerbose("checkStratPerfValidationNotes: userLoginId=${userLoginId}, groups=${groupIds}, currentStatusId=${currentStatusId}", "checkStratPerfValidationNotes");

def configureNote = { int index ->
    final String suffix = index.toString();
    final String noteNameRaw = context.get("noteName" + suffix);
    final String noteName = normalizeNoteName(noteNameRaw);
    final boolean isUoNote = noteUoName == noteName;
    final boolean isDirNote = noteDirName == noteName;
    final boolean isStrategicNote = isUoNote || isDirNote;
    final boolean canView = isSystemAdmin || isDirUo || isDirSanAmm;
    final boolean canEdit = isInValidazione(currentStatusId) &&
        ((isUoNote && isDirUo && ("WEORCARD_TOVALIDATE" == currentStatusId || "WEORCARD_TOCLRFY_DUO" == currentStatusId)) ||
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

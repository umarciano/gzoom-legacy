import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.condition.EntityCondition;

final String noteUoName = "Note Direttore UO";
final String noteDirName = "Note Direttore Amministrativo/Sanitario";
final String userLoginId = userLogin?.getString("userLoginId");
final boolean isSystemAdmin = "admin" == userLoginId;
final def groupIds = userLoginId ? delegator.findByAnd("UserLoginSecurityGroup", UtilMisc.toMap("userLoginId", userLoginId))*.getString("groupId") : [];
final boolean isDirUo = groupIds.contains("STRATPERF_DIR_UO");
final boolean isDirSanAmm = groupIds.contains("STRATPERF_DIR_SAN") || groupIds.contains("STRATPERF_DIR_AMM");
final String currentStatusId = context.currentStatusId ?: parameters.currentStatusId;

def configureNote = { int index ->
    final String suffix = index.toString();
    final String noteName = context.get("noteName" + suffix);
    final boolean isUoNote = noteUoName == noteName;
    final boolean isDirNote = noteDirName == noteName;
    final boolean isStrategicNote = isUoNote || isDirNote;
    final boolean canView = isSystemAdmin || isDirUo || isDirSanAmm;
    final boolean canEdit = (isUoNote && isDirUo && "WEORCARD_TOVALIDATE" == currentStatusId)
        || (isDirNote && isDirSanAmm && "WEORCARD_VALPART" == currentStatusId);

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

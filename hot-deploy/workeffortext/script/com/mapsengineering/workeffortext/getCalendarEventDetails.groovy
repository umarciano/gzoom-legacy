import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.*;

def workEffort = context.get("workEffort");
def calendarEventDetails = [:];
//Debug.log("*** workEffort=" + workEffort);

if (UtilValidate.isNotEmpty(workEffort)) {

    def listWorkEffortPartyAssignment = workEffort.getRelated("WorkEffortPartyAssignment", ["roleTypeId": "CAL_ATTENDEE"], ["-fromDate"]);
    def workEffortPartyAssignAttendee = EntityUtil.getFirst(listWorkEffortPartyAssignment);
    //Debug.log("*** workEffortPartyAssignAttendee=" + workEffortPartyAssignAttendee);
    if (UtilValidate.isNotEmpty(workEffortPartyAssignAttendee)) {
        calendarEventDetails.workEffortPartyAssignAttendee = workEffortPartyAssignAttendee;
        def hostParty = workEffortPartyAssignAttendee.getRelatedOne("Party");
        //Debug.log("*** hostParty=" + hostParty);
        if (UtilValidate.isNotEmpty(hostParty)) {
            calendarEventDetails.hostParty = hostParty;
            if (UtilValidate.isNotEmpty(hostParty)) {
                calendarEventDetails.hostPartyId = hostParty.partyId;
                calendarEventDetails.hostPartyName = hostParty.partyName;
            }
        }
    }
}

context.calendarEventDetails = calendarEventDetails;

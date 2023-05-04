import org.ofbiz.base.util.*;


context.onlyChild = "N";
context.onlyGrandchild = "N";
// con i params onlyChild e onlyGrandchild viene utilizzato context.entityNameExtended = "Rollup";
context.showDates = "N";
context.showRoleType = "Y";
context.showRoleTypeWeight = "Y";
context.showComment = "N";
context.detailEnabled = "Y";
context.showSequence = "N";
context.arrayNumRows = "0";

GroovyUtil.runScriptAtLocation("component://workeffortext/webapp/workeffortext/WEB-INF/actions/executeChildPerformFindWorkEffortAssignmentView.groovy", context);

import org.ofbiz.base.util.*;


context.contentIdInd = 'WEFLD_IND4';
parameters.contentIdInd = 'WEFLD_IND4';
context.contentIdSecondary = 'WEFLD_AIND4';
parameters.contentIdSecondary = 'WEFLD_AIND4';

Debug.log("executeChildPerformFindWETIndicator4.groovy");

GroovyUtil.runScriptAtLocation("component:/workeffortext/webapp/workeffortext/WEB-INF/actions/executeChildPerformFindWETIndicator.groovy", context);

import org.ofbiz.base.util.*;


context.contentIdInd = 'WEFLD_IND3';
parameters.contentIdInd = 'WEFLD_IND3';
context.contentIdSecondary = 'WEFLD_AIND3';
parameters.contentIdSecondary = 'WEFLD_AIND3';

Debug.log("executeChildPerformFindWETIndicator3.groovy");

GroovyUtil.runScriptAtLocation("component:/workeffortext/webapp/workeffortext/WEB-INF/actions/executeChildPerformFindWETIndicator.groovy", context);

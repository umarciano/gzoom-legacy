import org.ofbiz.base.util.*;


context.contentIdInd = 'WEFLD_IND5'
parameters.contentIdInd = 'WEFLD_IND5';
context.contentIdSecondary = 'WEFLD_AIND5';
parameters.contentIdSecondary = 'WEFLD_AIND5';

GroovyUtil.runScriptAtLocation("component:/workeffortext/webapp/workeffortext/WEB-INF/actions/executeChildPerformFindWEMIndicator.groovy", context);

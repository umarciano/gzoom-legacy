import org.ofbiz.base.util.*;


context.contentIdInd = 'WEFLD_IND2';
parameters.contentIdInd = 'WEFLD_IND2';
context.contentIdSecondary = 'WEFLD_AIND2';
parameters.contentIdSecondary = 'WEFLD_AIND2';

GroovyUtil.runScriptAtLocation("component:/workeffortext/webapp/workeffortext/WEB-INF/actions/executeChildPerformFindWEMIndicator.groovy", context);

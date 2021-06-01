package com.mapsengineering.base.test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.string.FlexibleStringExpander;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;



/**
 * Test WorkEffort StandardImport from xml
 *
 */
public class TestWorkEffortFieldsGenerationByFrameCode extends BaseTestCase {

    /**
     * Enumeration of field
     *
     */
    public enum E {
    	WorkEffortAndWorkEffortTypeView, workEffortTypeId, orgUnitId, workEffortParentId, //
    	estimatedStartDate, createdStamp, orgUnitCode, //
    	frameEnumId, frameCode, codePrefix, seqDigit, seqOnlyId, //
    	WorkEffortSequence, seqId, seqName, //
        sourceReferenceId, etch, //
        PFAAUOCP, N
    }
    
	private static final String FRAME_PF = "PF";
	private static final String FRAME_AA = "AA";
	private static final String FRAME_UO = "UO";
	private static final String FRAME_PG = "PG";
	private static final String FRAME_ET = "ET";
	private static final String FRAME_DOT_PG = ".${PG}";
	private static final String FRAME_PF_DOT = "${PF}.";
	private static final String FRAME_AA_DOT = "${AA}.";
	private static final String FRAME_PFAA_DOT = "${PF}${AA}.";
	private static final String FRAME_AAPF_DOT = "${AA}${PF}.";
   

    /**
     * Test sourceReferenceId and etch generation form frameEnumId
     * @throws GenericEntityException
     */
    public void testWorkEffortFieldsGenerationByFrameCode() throws GenericEntityException {
    	List<GenericValue> workEffortAndTypeList = getWorkEffortAndWorkEffortTypeList();
    	String previousPrefixSeq = "";

    	for(GenericValue record : workEffortAndTypeList) { 
    		Map<String, Object> frameContextMap = prepareFrameContextMap(record);
    		previousPrefixSeq = checkWorkEffortFields(record, frameContextMap, previousPrefixSeq);
    	}
    }
    
    /**
     * 
     * @return
     * @throws GenericEntityException
     */
    private List<GenericValue> getWorkEffortAndWorkEffortTypeList() throws GenericEntityException {
    	EntityCondition entityCondition =  EntityCondition.makeCondition(E.workEffortTypeId.name(), EntityOperator.LIKE, "%TFC%");
    	return checkListSize(delegator.findList(E.WorkEffortAndWorkEffortTypeView.name(), entityCondition, null, UtilMisc.toList(E.createdStamp.name()), null, false), 13, 13);
    }
    
    /**
     * 
     * @param record
     * @param frameContextMap
     * @param previousProgr
     * @return
     * @throws GenericEntityException 
     */
    private String checkWorkEffortFields(GenericValue record, Map<String, Object> frameContextMap, String previousPrefixSeq) throws GenericEntityException {    	
    	String frameEnumId = record.getString(E.frameEnumId.name());
    	String frameCode = record.getString(E.frameCode.name());
    	String prefixSeq = getPrefixSeq(frameCode, frameContextMap);
    	String progressivo = "";
    	if(UtilValidate.isNotEmpty(frameEnumId) && frameEnumId.contains(FRAME_PG)) {
    		progressivo = getProgressivo(prefixSeq, previousPrefixSeq, record.getLong(E.seqDigit.name()));
    		frameContextMap.put(FRAME_PG, progressivo);
    	} 	
    	if(! frameCode.contains(FRAME_ET) && ! "X13".equals(record.getString(E.sourceReferenceId.name()))) {
			String expectedEtch = (E.N.name().equals(record.getString(E.seqOnlyId.name())) 
					|| E.PFAAUOCP.name().equals(frameEnumId)) 
					? FlexibleStringExpander.expandString(getTemplEtch(frameCode), frameContextMap)
					: progressivo;
			assertEquals(expectedEtch, record.getString(E.etch.name()));
    	}
    	return prefixSeq;
    }
    
    /**
     * 
     * @param frameCode
     * @return
     */
    private String getTemplEtch(String frameCode) {
		String templEtch = StringUtil.replaceString(frameCode, FRAME_PFAA_DOT, "");
		templEtch = StringUtil.replaceString(templEtch, FRAME_AAPF_DOT, "");
		templEtch = StringUtil.replaceString(templEtch, FRAME_PF_DOT, "");
		templEtch = StringUtil.replaceString(templEtch, FRAME_AA_DOT, "");
		return templEtch;
    }
    
    /**
     * 
     * @param frameCode
     * @param frameContextMap
     * @return
     */
    private String getPrefixSeq(String frameCode, Map<String, Object> frameContextMap) {
    	String templPrefisso = StringUtil.replaceString(frameCode, FRAME_DOT_PG, "");
    	return FlexibleStringExpander.expandString(templPrefisso, frameContextMap);
    }
    
    /**
     * 
     * @param prefixSeq
     * @param previousPrefixSeq
     * @param seqDigit
     * @return
     * @throws GenericEntityException
     */
    private String getProgressivo(String prefixSeq, String previousPrefixSeq, Long seqDigit) throws GenericEntityException {
    	Long progressivo = 0L;
    	GenericValue workEffortSequence = delegator.findOne(E.WorkEffortSequence.name(), UtilMisc.toMap(E.seqName.name(), prefixSeq), false);
    	if(UtilValidate.isNotEmpty(workEffortSequence)) {
    		progressivo = workEffortSequence.getLong(E.seqId.name());
    	}
    	if(! prefixSeq.equals(previousPrefixSeq)) {
    		progressivo --;
    	}
    	int seqDigitToInt = (seqDigit != null && seqDigit.intValue() > 0) ? seqDigit.intValue() : 1;
    	return String.format("%0" + seqDigitToInt + "d", progressivo);
    }
    
    /**
     * 
     * @param record
     * @return
     */
	private Map<String, Object> prepareFrameContextMap(GenericValue record) {		
		Map<String, Object> frameContextMap = new HashMap<String, Object>();
		frameContextMap.put(FRAME_PF, record.getString(E.codePrefix.name()) != null ? record.getString(E.codePrefix.name()) : "");
		frameContextMap.put(FRAME_AA, String.format("%ty", record.getTimestamp(E.estimatedStartDate.name())));
		frameContextMap.put(FRAME_UO, record.getString(E.orgUnitCode.name()) != null ? record.getString(E.orgUnitCode.name()) : "");
		frameContextMap.put(FRAME_ET, record.getString(E.etch.name()) != null ? record.getString(E.etch.name()) : "");
		
		return frameContextMap;
	}
		
}

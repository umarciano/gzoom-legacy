package com.mapsengineering.workeffortext.services.sequence;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Map;

import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.ObjectType;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;

import com.ibm.icu.util.Calendar;
import com.mapsengineering.workeffortext.services.E;
import com.mapsengineering.workeffortext.services.sequence.common.WorkEffortSequenceConstants;
import com.mapsengineering.workeffortext.services.sequence.helper.FrameCodeHelper;

public class InputParametersHandler {
	
	private Delegator delegator;
	private Locale locale;
	private String workEffortTypeId;
	private String workEffortParentId;
	private Date estimatedStartDate;
	private String periodFromDate;
	private String periodThruDate2;
	private String orgUnitId;
	private String weTATOWorkEffortIdFrom;	
	private GenericValue workEffortType;
	private String frameEnumId;
	private String frameCode;
	private String seqOnlyId;
	private String codePrefix;
	private String etch;
	
	private String templEtch;
	private String templPrefisso;
	private String yearString;
	private String year2String;
	private String codiceUO;
	private String codicePAR;
	
	private boolean frameManual;
	private boolean processWeSequence;
	
	private FrameCodeHelper frameCodeHelper;		
	
	/**
	 * 
	 * @param delegator
	 * @param parameters
	 * @throws GeneralException
	 */
	public InputParametersHandler(Delegator delegator, Map<String, Object> parameters, Locale locale) throws GeneralException {
		this.delegator = delegator;
		this.locale = locale;
		this.workEffortTypeId = (String) parameters.get(E.workEffortTypeId.name());
		this.workEffortParentId = (String) parameters.get(E.workEffortIdFrom.name());
		this.periodFromDate = (String) parameters.get(E.periodFromDate.name());
		this.periodThruDate2 = (String) parameters.get(E.periodThruDate2.name());
		setEstimamtedStartDate(parameters);
		this.orgUnitId = (String) parameters.get(E.orgUnitId.name());
		this.weTATOWorkEffortIdFrom = getWeTATOWorkEffortIdFrom(parameters);
		this.etch = (String) parameters.get(E.etch.name());
	}
	
	/**
	 * 
	 * @throws GeneralException
	 */
	public void run() throws GeneralException {
		this.workEffortType = delegator.findOne(E.WorkEffortType.name(), UtilMisc.toMap(E.workEffortTypeId.name(), workEffortTypeId), false);
		setFrameFields();
				
		frameManual = WorkEffortSequenceConstants.MANUAL.equals(frameEnumId);
    	if(frameManual) {
    		return;
    	} 
    	//flag che dice se gestire o no il progressivo nella WorkEffortSequence
    	processWeSequence = frameCode.contains(WorkEffortSequenceConstants.FRAME_PG);
    	if(frameCode.contains(WorkEffortSequenceConstants.FRAME_AAAA)) {
    		this.yearString = String.format("%tY", estimatedStartDate);
    	} else if(frameCode.contains(WorkEffortSequenceConstants.FRAME_AA)) {
            this.yearString = String.format("%ty", estimatedStartDate);
        }
    	if(frameCode.contains(WorkEffortSequenceConstants.FRAME_A2)) {
    		setYear2String();
    	}
    	setCodePrefix();
		if(frameCode.contains(WorkEffortSequenceConstants.FRAME_UO)) {
			setCodiceUo();
		}
		if(frameCode.contains(WorkEffortSequenceConstants.FRAME_PR)) {
			setCodicePAR();
		}
    	this.seqOnlyId = workEffortType.getString(E.seqOnlyId.name());
    	setTemplEtch();
    	this.templPrefisso = StringUtil.replaceString(frameCode, WorkEffortSequenceConstants.FRAME_DOT_PG, "");
	}
	
	/**
	 * valorizzazione frameEnumId e frameCode
	 */
	private void setFrameFields() throws GeneralException {
		this.frameEnumId = UtilValidate.isNotEmpty(workEffortType.getString(E.frameEnumId.name())) ? workEffortType.getString(E.frameEnumId.name()) : "";
		if(UtilValidate.isNotEmpty(frameEnumId)) {
			GenericValue enumeration  = delegator.findOne(E.Enumeration.name(), UtilMisc.toMap(E.enumId.name(), frameEnumId), false);
			if(UtilValidate.isNotEmpty(enumeration)) {
				this.frameCode = UtilValidate.isNotEmpty(enumeration.getString(E.enumCode.name())) ? enumeration.getString(E.enumCode.name()) : "";
			}
		}
	}
	
	/**
	 * prefisso obbligatorio con formato PFPG
	 * @throws GeneralException
	 */
	private void setCodePrefix() throws GeneralException {
		this.codePrefix = UtilValidate.isNotEmpty(workEffortType.getString(E.codePrefix.name())) ? workEffortType.getString(E.codePrefix.name()) : "";
		if (WorkEffortSequenceConstants.PFPG.equals(frameEnumId) && UtilValidate.isEmpty(codePrefix)) {
			throw new GeneralException("Set code Prefix for workEffort Type");
		}
	}	
	
	/**
	 * valorizzazione del template etichetta
	 */
	private void setTemplEtch() {
		if(! frameCode.contains(WorkEffortSequenceConstants.FRAME_ET)) {
			templEtch = StringUtil.replaceString(frameCode, WorkEffortSequenceConstants.FRAME_PFAA_DOT, "");
			templEtch = StringUtil.replaceString(templEtch, WorkEffortSequenceConstants.FRAME_AAPF_DOT, "");
			templEtch = StringUtil.replaceString(templEtch, WorkEffortSequenceConstants.FRAME_PF_DOT, "");
			templEtch = StringUtil.replaceString(templEtch, WorkEffortSequenceConstants.FRAME_AA_DOT, "");
			templEtch = StringUtil.replaceString(templEtch, WorkEffortSequenceConstants.AAPFUOPR_EL, WorkEffortSequenceConstants.FRAME_UO_EL);
            templEtch = StringUtil.replaceString(templEtch, WorkEffortSequenceConstants.AAPFUO_EL, WorkEffortSequenceConstants.FRAME_UO_EL);
			templEtch = StringUtil.replaceString(templEtch, WorkEffortSequenceConstants.AAA2PFUO_EL, WorkEffortSequenceConstants.FRAME_UO_EL);
		}
	}
	
	/**
	 * prende dai parametri il weTATOWorkEffortIdFrom
	 * @param parameters
	 * @return
	 */
	private String getWeTATOWorkEffortIdFrom(Map<String, Object> parameters) {
		String weTATOWorkEffortIdFrom = (String) parameters.get(E.WETATOWorkEffortIdFrom.name());
		if (UtilValidate.isEmpty(weTATOWorkEffortIdFrom)) {
			weTATOWorkEffortIdFrom = (String) parameters.get(E.WETATOWorkEffortIdFrom_2.name());
		}
		if (UtilValidate.isEmpty(weTATOWorkEffortIdFrom)) {
			weTATOWorkEffortIdFrom = (String) parameters.get(E.WETATOWorkEffortIdFrom_3.name());
		}
		if (UtilValidate.isEmpty(weTATOWorkEffortIdFrom)) {
			weTATOWorkEffortIdFrom = (String) parameters.get(E.WETATOWorkEffortIdFrom_4.name());
		}
		return weTATOWorkEffortIdFrom;
	}
	
	/**
	 * imposta anno+2
	 */
	private void setYear2String() {
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(estimatedStartDate);
		cal.add(Calendar.YEAR, 2);
		this.year2String = String.format("%ty", cal.getTime());
	}
		
	/**
	 * 
	 */
	private void setFrameCodeHelper() {
		if(frameCodeHelper == null) {
			frameCodeHelper = new FrameCodeHelper(delegator, workEffortParentId, orgUnitId, weTATOWorkEffortIdFrom);
		}
	}
	
	/**
	 * valorizzazione codiceUO
	 * @throws GeneralException
	 */
	private void setCodiceUo() throws GeneralException {
		setFrameCodeHelper();
		codiceUO = frameCodeHelper.getCodiceUoByPartyParentRole();
	}
	
	/**
	 * valorizzazione codicePAR
	 * @throws GeneralException
	 */
	private void setCodicePAR() throws GeneralException {
		setFrameCodeHelper();
		codicePAR = frameCodeHelper.getCodiceParent();
	}
	
	/**
	 * imposta la data
	 * @param parameters
	 * @throws GeneralException
	 */
	private void setEstimamtedStartDate(Map<String, Object> parameters) throws GeneralException {
		Date estimatedStartDate = (Date) parameters.get(E.estimatedStartDate.name());
		if (UtilValidate.isNotEmpty(estimatedStartDate)) {
			this.estimatedStartDate = estimatedStartDate;
			return;
		}
		String estimatedCompletionDate2Str = (String) parameters.get(E.estimatedCompletionDate2.name());
		if (UtilValidate.isNotEmpty(estimatedCompletionDate2Str)) {
			this.estimatedStartDate = (Date) ObjectType.simpleTypeConvert(estimatedCompletionDate2Str, "Date", null, locale);
			return;
		}
		if (UtilValidate.isNotEmpty(this.periodFromDate)) {
			GenericValue customTimePeriod = getCustomTimePriod(this.periodFromDate);
			if (UtilValidate.isNotEmpty(customTimePeriod)) {
				this.estimatedStartDate = customTimePeriod.getTimestamp(E.fromDate.name());
			}
			return;
		}
		if (UtilValidate.isNotEmpty(this.periodThruDate2)) {
			GenericValue customTimePeriod = getCustomTimePriod(this.periodThruDate2);
			if (UtilValidate.isNotEmpty(customTimePeriod)) {
				this.estimatedStartDate = customTimePeriod.getTimestamp(E.fromDate.name());
			}
			return;
		}
	}
	
	/**
	 * ritorna il customTimePeriod
	 * @param customTimePeriodId
	 * @return
	 * @throws GeneralException
	 */
	private GenericValue getCustomTimePriod(String customTimePeriodId) throws GeneralException {
		return delegator.findOne(E.CustomTimePeriod.name(), UtilMisc.toMap(E.customTimePeriodId.name(), customTimePeriodId), false);
	}
	
	
	public GenericValue getWorkEffortType() {
		return workEffortType;
	}

	public String getTemplEtch() {
		return templEtch;
	}

	public String getFrameEnumId() {
		return frameEnumId;
	}
	
	public String getFrameCode() {
		return frameCode;
	}

	public String getSeqOnlyId() {
		return seqOnlyId;
	}

	public String getCodePrefix() {
		return codePrefix;
	}
	
	public String getEtch() {
		return etch;
	}
	
	public String getTemplPrefisso() {
		return templPrefisso;
	}
	
	public String getYearString() {
		return yearString;
	}
	
	public String getYear2String() {
		return year2String;
	}
	
	public String getCodiceUO() {
		return codiceUO;
	}
	
	public String getCodicePAR() {
		return codicePAR;
	}
	
	public boolean isFrameManual() {
		return frameManual;
	}

	public boolean isProcessWeSequence() {
		return processWeSequence;
	}
}

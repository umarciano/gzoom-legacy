package com.mapsengineering.emplperf.update.acctgtrans;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;

import com.mapsengineering.base.services.ServiceLogger;
import com.mapsengineering.base.standardimport.common.ImportManagerConstants;
import com.mapsengineering.base.standardimport.common.ImportManagerConstants.MessageCode;
import com.mapsengineering.base.util.JobLogger;
import com.mapsengineering.emplperf.update.EmplPerfServiceEnum;
import com.mapsengineering.emplperf.update.EmplPerfValueUpdate;
import com.mapsengineering.emplperf.update.ParamsEnum;

/**
 * Create AcctgTransInterface for emplperf 
 *
 */
public class EmplPerfValueUpdateAcctgTrans extends EmplPerfValueUpdate {

    /**
     * DataSource for StandardImport
     */
    public static final String DATA_SOURCE_EMPL_PERF_UPDATE = "EMPL_PERF_UPDATE";

    @Override
    protected JobLogger doAction(JobLogger jobLogger, Delegator delegator, GenericValue genericValue, Map<String, Object> context) throws GeneralException {
        jobLogger.addMessage(ServiceLogger.makeLogInfo("Update mov for measure with id = " + genericValue.getString(EmplPerfServiceEnum.workEffortMeasureId.name()), MessageCode.INFO_GENERIC.toString(), genericValue.getString(EmplPerfServiceEnum.workEffortMeasureId.name()), null, null));
        makeAcctgTransInterface(delegator, genericValue, context);
        return jobLogger;
    }

    /**
     * 
     * @param delegator
     * @return
     * @throws GeneralException
     */
    private GenericValue makeAcctgTransInterface(Delegator delegator, GenericValue genericValue, Map<String, Object> context) throws GeneralException {
        GenericValue gv = delegator.makeValue(EmplPerfServiceEnum.AcctgTransInterface.name());

        Timestamp writeDate = (Timestamp)context.get(ParamsEnum.writeDate.name());

        gv.put(EmplPerfServiceEnum.id.name(), delegator.getNextSeqId(EmplPerfServiceEnum.AcctgTransInterface.name()));
        gv.put(EmplPerfServiceEnum.dataSource.name(), DATA_SOURCE_EMPL_PERF_UPDATE);
        gv.put(EmplPerfServiceEnum.refDate.name(), writeDate);
        gv.put(EmplPerfServiceEnum.voucherRef.name(), genericValue.getString(EmplPerfServiceEnum.workEffortMeasureId.name()));
        gv.put(EmplPerfServiceEnum.uomDescr.name(), EmplPerfServiceEnum._NA_.name());
        gv.put(EmplPerfServiceEnum.glAccountCode.name(), genericValue.getString(EmplPerfServiceEnum.accountCode.name()));
        gv.put(EmplPerfServiceEnum.amount.name(), genericValue.getBigDecimal(EmplPerfServiceEnum.amount.name()));
        gv.put(EmplPerfServiceEnum.glFiscalTypeId.name(), genericValue.getString(EmplPerfServiceEnum.glFiscalTypeId.name()));
        gv.put(EmplPerfServiceEnum.glAccountTypeEnumId.name(), EmplPerfServiceEnum.INDICATOR.name());
        gv.put(EmplPerfServiceEnum.uorgCode.name(), EmplPerfServiceEnum._NA_.name());
        gv.put(EmplPerfServiceEnum.productCode.name(), EmplPerfServiceEnum._NA_.name());
        gv.put(EmplPerfServiceEnum.workEffortCode.name(), EmplPerfServiceEnum._NA_.name());
        gv.put(EmplPerfServiceEnum.partyCode.name(), EmplPerfServiceEnum._NA_.name());
        gv.create();

        return gv;
    }

    @Override
    protected List<String> getKey() {
        return UtilMisc.toList(EmplPerfServiceEnum.workEffortMeasureId.name());
    }

    @Override
    protected Set<String> getFieldsToSelect() {

        Set<String> toSelect = new HashSet<String>();
        toSelect.add(EmplPerfServiceEnum.evalPartyId.name());
        toSelect.add(EmplPerfServiceEnum.workEffortMeasureId.name());
        toSelect.add(EmplPerfServiceEnum.accountCode.name());
        toSelect.add(EmplPerfServiceEnum.amount.name());
        toSelect.add(EmplPerfServiceEnum.glFiscalTypeId.name());
        toSelect.add(EmplPerfServiceEnum.orgUnitId.name());

        return toSelect;
    }

    @Override
    protected boolean executeStandardImport() {
        return true;
    }

	@Override
	protected String getEntityListToImport() {
		return ImportManagerConstants.ACCTG_TRANS_INTERFACE;
	}

	@Override
	protected List<String> getOrderBy() {
		return null;
	}
}

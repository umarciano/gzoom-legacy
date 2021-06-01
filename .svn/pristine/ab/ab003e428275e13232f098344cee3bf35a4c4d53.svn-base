package com.mapsengineering.workeffortext.birt.comparingVersions;

import java.sql.SQLException;

import com.mapsengineering.base.birt.util.UtilDateTime;
import com.mapsengineering.base.report.OfbizReportContext;

public class ValoreUpdateComparingVersion extends ValoreComparingVersion {

    private static final long serialVersionUID = 1L;

    private transient final OfbizReportContext ctx = OfbizReportContext.get();

    public Double getOldTmAmount() throws SQLException {
        return getRs().getDouble(E.OLD_TM_AMOUNT.name());
    }

    public String getOldRUomCode() throws SQLException {
        return getRs().getString(E.OLD_R_UOM_CODE.name());
    }

    public String getOldTmDescription() throws SQLException {
        return getRs().getString(E.OLD_TM_DESCRIPTION.name());
    }

    public String getOldTtDescription() throws SQLException {
        return getRs().getString(E.OLD_TT_DESCRIPTION.name());
    }

    /**
     * Formatazzione dell importo
     * @return
     * @throws SQLException
     */
    public String getOldValoreNumber() throws SQLException {
        if (!this.getGUomTypeId().equals(E.RATING_SCALE.name()) && !this.getGUomTypeId().equals(E.DATE_MEASURE.name())) {
            return formatAmountValue(this.getOldTmAmount());
        }
        return null;
    }

    /**
     * Formattazione nel caso data
     * @return
     * @throws SQLException
     */
    public String getOldValoreDate() throws SQLException {
        if (this.getGUomTypeId().equals(E.DATE_MEASURE.name())) {
            return UtilDateTime.numberConvertToDate(this.getOldTmAmount(), ctx.getLocale());
        }
        return null;
    }
}

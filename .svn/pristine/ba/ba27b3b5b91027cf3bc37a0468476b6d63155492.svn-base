package com.mapsengineering.workeffortext.birt.comparingVersions;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.ofbiz.base.util.UtilValidate;

import com.mapsengineering.base.birt.util.UtilDateTime;
import com.mapsengineering.base.birt.util.UtilNumber;
import com.mapsengineering.base.jdbc.ResultSetWrapper;
import com.mapsengineering.base.report.OfbizReportContext;

public class ValoreComparingVersion extends ResultSetWrapper {

    private static final long serialVersionUID = 1L;

    private transient final OfbizReportContext ctx = OfbizReportContext.get();

    public String getGAccountName() throws SQLException {
        return getRs().getString(E.G_ACCOUNT_NAME.name());
    }

    public String getMProductDescription() throws SQLException {
        return getRs().getString(E.M_PRODUCT_DESCRIPTION.name());
    }

    public String getUomDescr() throws SQLException {
        return getRs().getString(E.M_UOM_DESCR.name());
    }

    public String getMPartyName() throws SQLException {
        return getRs().getString(E.TT_DESCRIPTION.name());
    }

    public String getMPeriodName() throws SQLException {
        return getRs().getString(E.M_PERIOD_NAME.name());
    }

    public String getTtFiscalTypeDescription() throws SQLException {
        return getRs().getString(E.TT_FISCAL_TYPE_DESCRIPTION.name());
    }

    public Double getGUomDecimalScale() throws SQLException {
        return getRs().getDouble(E.G_UOM_DECIMAL_SCALE.name());
    }

    public String getGUomTypeId() throws SQLException {
        return getRs().getString(E.G_UOM_TYPE_ID.name());
    }

    public Double getTmAmount() throws SQLException {
        return getRs().getDouble(E.TM_AMOUNT.name());
    }

    public String getGUomCode() throws SQLException {
        return getRs().getString(E.G_UOM_CODE.name());
    }

    public String getTmDescription() throws SQLException {
        return getRs().getString(E.TM_DESCRIPTION.name());
    }

    public String getTtDescription() throws SQLException {
        return getRs().getString(E.TT_DESCRIPTION.name());
    }

    public String getRUomCode() throws SQLException {
        return getRs().getString(E.R_UOM_CODE.name());
    }

    public String getTitle() throws SQLException {
        String title = "";
        if (UtilValidate.isNotEmpty(getUomDescr())) {
            title = getUomDescr();
        } else if (UtilValidate.isNotEmpty(getMProductDescription())) {
            title = getMProductDescription();
        } else {
            title = getGAccountName();
        }

        return title;
    }

    protected String formatAmountValue(Double amount) throws SQLException {
        String formatString = UtilNumber.getFormatPattern(this.getGUomDecimalScale().intValue(), this.getGUomCode());
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getNumberInstance(ctx.getLocale());
        formatter.applyPattern(formatString);
        if (formatString.contains("%")) {
            amount = amount / 100;
        }
        return formatter.format(amount);
    }

    /**
     * Formatazzione dell importo
     * @return
     * @throws SQLException
     */
    public String getValoreNumber() throws SQLException {
        if (!this.getGUomTypeId().equals(E.RATING_SCALE.name()) && !this.getGUomTypeId().equals(E.DATE_MEASURE.name())) {
            return formatAmountValue(this.getTmAmount());
        }
        return null;
    }

    /**
     * Formattazione nel caso data
     * @return
     * @throws SQLException
     */
    public String getValoreDate() throws SQLException {
        if (this.getGUomTypeId().equals(E.DATE_MEASURE.name())) {
            return UtilDateTime.numberConvertToDate(this.getTmAmount(), ctx.getLocale());
        }
        return null;
    }
}

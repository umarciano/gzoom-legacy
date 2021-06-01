package com.mapsengineering.workeffortext.birt.comparingVersions;

import java.sql.SQLException;
import java.sql.Timestamp;

import org.ofbiz.base.util.UtilValidate;

import com.mapsengineering.base.jdbc.ResultSetWrapper;

public class MisuraComparingVersion extends ResultSetWrapper {

    private static final long serialVersionUID = 1L;

    public String getAccountName() throws SQLException {
        return getRs().getString(E.ACCOUNT_NAME.name());
    }

    public String getPrUomDescr() throws SQLException {
        return getRs().getString(E.PR_UOM_DESCR.name());
    }

    public String getWmUomDescr() throws SQLException {
        return getRs().getString(E.WM_UOM_DESCR.name());
    }

    public String getComments() throws SQLException {
        return getRs().getString(E.COMMENTS.name());
    }

    public Double getKpiScoreWeight() throws SQLException {
        return getRs().getDouble(E.KPI_SCORE_WEIGHT.name());
    }

    public String getSequenceId() throws SQLException {
        return getRs().getString(E.SEQUENCE_ID.name());
    }

    public Timestamp getFromDate() throws SQLException {
        return getRs().getTimestamp(E.FROM_DATE.name());
    }

    public Timestamp getThruDate() throws SQLException {
        return getRs().getTimestamp(E.THRU_DATE.name());
    }

    public String getTitle() throws SQLException {
        String title = "";
        if (UtilValidate.isNotEmpty(getAccountName())) {
            title = getAccountName();
        } else {
            title = getPrUomDescr();
        }
        return title;
    }
}

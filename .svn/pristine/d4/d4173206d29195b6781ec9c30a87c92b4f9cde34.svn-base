package com.mapsengineering.workeffortext.birt.comparingVersions;

import java.sql.SQLException;
import java.sql.Timestamp;

import org.ofbiz.base.util.UtilValidate;

import com.mapsengineering.base.jdbc.ResultSetWrapper;

public class RelazioneComparingVersion extends ResultSetWrapper {

    private static final long serialVersionUID = 1L;

    public String getDescription() throws SQLException {
        return getRs().getString(E.DESCRIPTION.name());
    }

    public String getEtch() throws SQLException {
        return getRs().getString(E.ETCH.name());
    }

    public String getWorkEffortName() throws SQLException {
        return getRs().getString(E.WORK_EFFORT_NAME.name());
    }

    public String getComments() throws SQLException {
        return getRs().getString(E.COMMENTS.name());
    }

    public Double getAssocWeight() throws SQLException {
        return getRs().getDouble(E.ASSOC_WEIGHT.name());
    }

    public Integer getSequenceNum() throws SQLException {
        return getRs().getInt(E.SEQUENCE_NUM.name());
    }

    public Timestamp getFromDate() throws SQLException {
        return getRs().getTimestamp(E.FROM_DATE.name());
    }

    public Timestamp getThruDate() throws SQLException {
        return getRs().getTimestamp(E.THRU_DATE.name());
    }

    public String getTitle() throws SQLException {
        String title = "";
        if (UtilValidate.isNotEmpty(getEtch()))
            title = getEtch();

        if (UtilValidate.isNotEmpty(title))
            title += " - ";

        if (UtilValidate.isNotEmpty(getWorkEffortName()))
            title += getWorkEffortName();

        return title;
    }
}

package com.mapsengineering.workeffortext.birt.comparingVersions;

import java.sql.SQLException;
import java.sql.Timestamp;

import org.ofbiz.base.util.UtilValidate;

import com.mapsengineering.base.jdbc.ResultSetWrapper;

public class CardsComparingVersion extends ResultSetWrapper {

    private static final long serialVersionUID = 1L;

    public String getEtch() throws SQLException {
        return getRs().getString(E.ETCH.name());
    }

    public String getWorkEffortName() throws SQLException {
        return getRs().getString(E.WORK_EFFORT_NAME.name());
    }

    public String getDescription() throws SQLException {
        return getRs().getString(E.DESCRIPTION.name());
    }

    public String getPartyName() throws SQLException {
        return getRs().getString(E.PARTY_NAME.name());
    }

    public Timestamp getEstimatedStartDate() throws SQLException {
        return getRs().getTimestamp(E.ESTIMATED_START_DATE.name());
    }

    public Timestamp getEstimatedCompletionDate() throws SQLException {
        return getRs().getTimestamp(E.ESTIMATED_COMPLETION_DATE.name());
    }

    public String getWorkEffortId() throws SQLException {
        return getRs().getString(E.WORK_EFFORT_ID.name());
    }

    public String getWorkEffortTypeId() throws SQLException {
        return getRs().getString(E.WORK_EFFORT_TYPE_ID.name());
    }

    public String getSequenceNum() throws SQLException {
        return getRs().getString(E.SEQUENCE_NUM.name());
    }

    public String getSourceRefernceId() throws SQLException {
        return getRs().getString(E.SOURCE_REFERENCE_ID.name());
    }

    public String getTitle() throws SQLException {
        String title = "";
        if (UtilValidate.isNotEmpty(getEtch()))
            title = getEtch();

        if (UtilValidate.isNotEmpty(title) && !title.equals(""))
            title += " - ";

        if (UtilValidate.isNotEmpty(getWorkEffortName()))
            title += getWorkEffortName();

        return title;
    }
}

package com.mapsengineering.workeffortext.birt.comparingVersions;

import java.sql.SQLException;
import java.sql.Timestamp;

import com.mapsengineering.base.jdbc.ResultSetWrapper;

public class AllegatoComparingVersion extends ResultSetWrapper {

    private static final long serialVersionUID = 1L;

    public String getDataResourceName() throws SQLException {
        return getRs().getString(E.DATA_RESOURCE_NAME.name());
    }

    public String getWecDescription() throws SQLException {
        return getRs().getString(E.WEC_DESCRITPION.name());
    }

    public String getCDescription() throws SQLException {
        return getRs().getString(E.C_DESCRIPTION.name());
    }

    public Timestamp getFromDate() throws SQLException {
        return getRs().getTimestamp(E.FROM_DATE.name());
    }

    public Timestamp getThruDate() throws SQLException {
        return getRs().getTimestamp(E.THRU_DATE.name());
    }
}

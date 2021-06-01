package com.mapsengineering.workeffortext.birt.comparingVersions;

import java.sql.SQLException;
import java.sql.Timestamp;

import com.mapsengineering.base.jdbc.ResultSetWrapper;

public class AssegnazioneComparingVersion extends ResultSetWrapper {

    private static final long serialVersionUID = 1L;

    public String getPartyName() throws SQLException {
        return getRs().getString(E.PARTY_NAME.name());
    }

    public String getComments() throws SQLException {
        return getRs().getString(E.COMMENTS.name());
    }

    public Double getRoleTypeWeight() throws SQLException {
        return getRs().getDouble(E.ROLE_TYPE_WEIGHT.name());
    }

    public Timestamp getFromDate() throws SQLException {
        return getRs().getTimestamp(E.FROM_DATE.name());
    }

    public Timestamp getThruDate() throws SQLException {
        return getRs().getTimestamp(E.THRU_DATE.name());
    }
}

package com.mapsengineering.workeffortext.birt.comparingVersions;

import java.sql.SQLException;
import java.sql.Timestamp;

public class AssegnazioneUpdateComparingVersion extends AssegnazioneComparingVersion {

    private static final long serialVersionUID = 1L;

    public String getOldComments() throws SQLException {
        return getRs().getString(E.OLD_COMMENTS.name());
    }

    public Double getOldRoleTypeWeight() throws SQLException {
        return getRs().getDouble(E.OLD_ROLE_TYPE_WEIGHT.name());
    }

    public Timestamp getOldFromDate() throws SQLException {
        return getRs().getTimestamp(E.OLD_FROM_DATE.name());
    }

    public Timestamp getOldThruDate() throws SQLException {
        return getRs().getTimestamp(E.OLD_THRU_DATE.name());
    }
}

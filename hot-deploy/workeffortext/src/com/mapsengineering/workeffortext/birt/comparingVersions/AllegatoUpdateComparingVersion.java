package com.mapsengineering.workeffortext.birt.comparingVersions;

import java.sql.SQLException;
import java.sql.Timestamp;

public class AllegatoUpdateComparingVersion extends AllegatoComparingVersion {

    private static final long serialVersionUID = 1L;

    public String getOldWecDescription() throws SQLException {
        return getRs().getString(E.OLD_WEC_DESCRITPION.name());
    }

    public String getOldCDescription() throws SQLException {
        return getRs().getString(E.OLD_C_DESCRIPTION.name());
    }

    public Timestamp getOldFromDate() throws SQLException {
        return getRs().getTimestamp(E.OLD_FROM_DATE.name());
    }

    public Timestamp getOldThruDate() throws SQLException {
        return getRs().getTimestamp(E.OLD_THRU_DATE.name());
    }
}

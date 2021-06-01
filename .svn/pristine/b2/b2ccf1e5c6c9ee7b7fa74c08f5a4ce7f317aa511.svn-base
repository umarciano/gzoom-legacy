package com.mapsengineering.workeffortext.birt.comparingVersions;

import java.sql.SQLException;
import java.sql.Timestamp;

import com.mapsengineering.base.jdbc.ResultSetWrapper;

public class NoteComparingVersion extends ResultSetWrapper {

    private static final long serialVersionUID = 1L;

    public Timestamp getNoteDateTime() throws SQLException {
        return getRs().getTimestamp(E.NOTE_DATE_TIME.name());
    }

    public String getNoteName() throws SQLException {
        return getRs().getString(E.NOTE_NAME.name());
    }

    public String getNoteInfo() throws SQLException {
        return getRs().getString(E.NOTE_INFO.name());
    }

    public String getInternalNote() throws SQLException {
        return getRs().getString(E.INTERNAL_NOTE.name());
    }

    public String getIsHtml() throws SQLException {
        return getRs().getString(E.IS_HTML.name());
    }
}

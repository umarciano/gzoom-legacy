package com.mapsengineering.workeffortext.birt.comparingVersions;

import java.sql.SQLException;

public class NoteUpdateComparingVersion extends NoteComparingVersion {

    private static final long serialVersionUID = 1L;

    public String getOldNoteInfo() throws SQLException {
        return getRs().getString(E.OLD_NOTE_INFO.name());
    }

    public String getOldInternalNote() throws SQLException {
        return getRs().getString(E.OLD_INTERNAL_NOTE.name());
    }

    public String getOldIsHtml() throws SQLException {
        return getRs().getString(E.OLD_IS_HTML.name());
    }
}

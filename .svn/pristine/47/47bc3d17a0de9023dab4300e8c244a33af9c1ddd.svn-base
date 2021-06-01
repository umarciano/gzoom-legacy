package com.mapsengineering.base.reminder;

import java.util.Hashtable;
import java.util.Map;


public enum ReminderReportContentIdEnum {
    
    REMINDER_SCADENZA("REMINDER_SCADENZA", "sql/reminder/queryReminderScadObi.sql.ftl"),
    REMINDER_PERIODO("REMINDER_PERIODO", "sql/reminder/queryReminderPeriodo.sql.ftl"),
    REMINDER_STATO("REMINDER_STATO", "sql/reminder/queryReminderStato.sql.ftl"),
    REMINDER_PRS("REMINDER_PRS", "sql/reminder/queryReminderPRS.sql.ftl"),
    REMINDER_ASS_OBI("REMINDER_ASS_OBI", "sql/reminder/queryReminderAssObi.sql.ftl"),
    REMINDER_ASS_OBI_1("REMINDER_ASS_OBI_1", "sql/reminder/queryReminderAssObi.sql.ftl"),
    REMINDER_ASS_OBI_2("REMINDER_ASS_OBI_2", "sql/reminder/queryReminderAssObi.sql.ftl"),
    REMINDER_VAL_DIP("REMINDER_VAL_DIP", "sql/reminder/queryReminderValDip.sql.ftl"),
    REMINDER_VAL_DIP_1("REMINDER_VAL_DIP_1", "sql/reminder/queryReminderValDip.sql.ftl"),
    REMINDER_VAL_DIP_2("REMINDER_VAL_DIP_2", "sql/reminder/queryReminderValDip.sql.ftl");
    
    private final String code;
    
    private final String queryReminder;

    /**
     * Constructor
     * @param code
     * @param queryReminder
     */
    ReminderReportContentIdEnum(String code, String queryReminder) {
        this.code = code;
        this.queryReminder = queryReminder;
    }

    /**
     * Return code (es: REMINDER_SCADENZA)
     * @return code
     */
    public String code() {
        return code;
    }
    
    /**
     * Return queryReminder (es: sql/reminder/queryReminderScadObi.sql.ftl)
     * @return queryReminder
     */
    public String queryReminder() {
        return queryReminder;
    }
   
    private static final Map<String, String> REP_CONT_QUERY = new Hashtable<String, String>();

    static {
        for (ReminderReportContentIdEnum ss : values()) {
            REP_CONT_QUERY.put(ss.code, ss.queryReminder);
        }
    }
    
    /**
     * Return String
     * @param code
     * @return
     */
    public static String getQuery(String code) {
        return REP_CONT_QUERY.get(code);
    }
    
}
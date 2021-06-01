package com.mapsengineering.workeffortext.test;

import java.util.Locale;
import java.util.Map;

import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.service.ServiceUtil;

/**
 * Error perche per OBSA ci vuole una SPL, invece DSSL01 e' una SSL
 */
public class TestCreateWorkEffortRootErrorRole extends BaseTestCreateWorkEffortRoot {

    public static final String MODULE = TestCreateWorkEffortRootErrorRole.class.getName();

    protected void setUp() throws Exception {
        super.setUp();
        context = setServiceMap("18PMA3OBSA", "DSSL01", "SSL");
    }

    protected void assertResult(Map<String, Object> result) {
        assertTrue(ServiceUtil.isError(result));
        String warnMsg = UtilProperties.getMessage("WorkeffortExtErrorLabels", "OrgUnitRoleWorkEffortAndTypeNotMatch", context, Locale.ITALIAN);
        assertEquals(warnMsg, ServiceUtil.getErrorMessage(result));
    }

    protected String getTitle() {
        return "testWorkEffortRootRuoloOrganizzazioneNonCoincidono OBSA";
    }
}

package com.mapsengineering.base.standardimport;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;

/**
 * TakeOverService for WeSchedaInterface entity.
 * This class delegates all logic to WeSchedaTakeOverService since
 * WeSchedaInterface is just an interface table used for import staging.
 * The actual business logic resides in WeSchedaTakeOverService.
 */
public class WeSchedaInterfaceTakeOverService extends WeSchedaTakeOverService {
    
    public static final String MODULE = WeSchedaInterfaceTakeOverService.class.getName();

    /**
     * No-arg constructor required by TakeOverFactory
     */
    public WeSchedaInterfaceTakeOverService() {
        super();
        Debug.logInfo("WeSchedaInterfaceTakeOverService created (no-arg constructor)", MODULE);
    }

    @Override
    public void doImport() throws GeneralException {
        Debug.logInfo("=== WeSchedaInterfaceTakeOverService.doImport() START ===", MODULE);
        Debug.logInfo("  Delegating to WeSchedaTakeOverService.doImport()", MODULE);
        
        // Delegate all import logic to parent class
        super.doImport();
        
        Debug.logInfo("=== WeSchedaInterfaceTakeOverService.doImport() END ===", MODULE);
    }
}

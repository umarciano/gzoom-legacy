package com.mapsengineering.base.bl.crud;

import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javolution.util.FastMap;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.model.ModelEntity;
import org.ofbiz.entity.model.ModelReader;

import com.mapsengineering.base.util.MessageUtil;

/**
 * Base abstract class for steps into CRUD Service
 * This class does default parameters initialization and expose execution method
 * @author sandro
 *
 */
public abstract class AbstractCrudHandler {

    /**
     * Flag automatic primary key generation
     */
    public static final String AUTOMATIC_PK = "_AUTOMATIC_PK_";

    /**
     * Permitted operation
     * @author sandro
     *
     */
    public static enum Operation {
        CREATE, UPDATE, READ, DELETE;
    }

    protected Delegator delegator;
    protected Map<String, Object> context;
    protected String entityName;
    protected Operation operation;
    protected Map<String, Object> parameters;
    protected Locale locale;
    protected TimeZone timeZone;
    protected Map<String, Object> returnMap;
    protected ModelEntity modelEntity;

    /**
     * constructor
     */
    protected AbstractCrudHandler() {
        returnMap = FastMap.newInstance();
    }

    /**
     * Properties default initialization. To extends init process override doInit.
     * @return True init ok
     */
    private boolean init(Delegator delegator, String entityName, String operation, Locale locale, TimeZone timeZone, Map<String, Object> parameters,  Map<String, Object> context) {

        //Inizializzo variabili
        this.delegator = delegator;
        this.context = context;
        this.locale = locale;
        this.timeZone = timeZone;
        this.parameters = parameters;
        this.entityName = entityName;

        //Check if requested operation is correct
        try {
            this.operation = Operation.valueOf(operation);
        } catch (Exception e) {
            returnMap.putAll(MessageUtil.buildErrorMap("OperationNotSupported", e, locale, UtilMisc.toList(operation)));
            return false;
        }

        //Get entity metadata
        try {
            ModelReader modelReader = ModelReader.getModelReader(delegator.getDelegatorName());
            this.modelEntity = modelReader.getModelEntity(entityName);
        } catch (Exception e) {
            returnMap.putAll(MessageUtil.buildErrorMap("EntityMetadataReadError", e, locale));
            return false;
        }

        //if not initialized from user, create for it
        if (this.context == null) {
            this.context = FastMap.newInstance();
        }

        return doInit();
    }

    /**
     * Specific initialization. Override this method to extend initialization process.
     * This method will be called after init and before doExecution.
     * @return True if all Ok. Fase otherwise.
     */
    protected boolean doInit() {
        return true;
    }

    /**
     * Specific logic implementation. Override for every case.
     * @return True if all ok.
     */
    protected abstract boolean doExecution();

    /**
     * Public interface. Call init method, then specific implementation method.
     * @return true if init and execute ok. Error messages are in errorMap.
     */
    public boolean execute(Delegator delegator, String entityName, String operation, Locale locale, TimeZone timeZone, Map<String, Object> parameters,  Map<String, Object> context) {

        try {
            //Esegue inizializzazione.
            if (!init(delegator, entityName, operation, locale, timeZone, parameters, context)) {
                return false;
            }

            //esecuzione. Override implementazione
            return doExecution();

        } catch (Exception e) {
            returnMap.putAll(MessageUtil.buildErrorMap("GenericServiceError", e, locale, UtilMisc.toList(this.getClass().getName())));
            return false;
        }
    }

    /**
     * Gets error Map
     * @return errorMap
     */
    public Map<String, Object> getReturnMap() {
        return returnMap;
    }

}

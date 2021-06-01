package com.mapsengineering.base.standardimport.helper;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Map;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;

import com.mapsengineering.base.standardimport.common.E;
import com.mapsengineering.base.standardimport.common.TakeOverService;

/**
 * Manage EmplPositionType
 * @author dain
 *
 */
public class PersonEmplPositionTypeHelper {

    private TakeOverService takeOverService;

    /**
     * Constructor
     * @param takeOverService
     */
    public PersonEmplPositionTypeHelper(TakeOverService takeOverService) {
        this.takeOverService = takeOverService;
    }

    /**
     * Check if emplPositionTypeId or templateId is changed
     * @param emplPositionTypeId, incoming field
     * @param partyId
     * @param refDate
     * @return Map with parameters, in which templateId contains the oldTemplateId
     * @throws GenericEntityException
     */
    public Map<String, Object> getEmplPositionTypeIdAndSetEmplPositionTypeDate(String emplPositionTypeId, String  description, String comments, BigDecimal employmentAmount, String partyId, Timestamp refDate) throws GenericEntityException {
        //Se la categoria proveniente dall'importazione e' padre della categoria attualmente impostata per il dipendente allora nessuna modifica al campo emplPositionTypeId
        GenericValue person = takeOverService.getManager().getDelegator().findOne("Person", false, E.partyId.name(), partyId);
        

        /**
         * Check emplPositionTypeId is changed
         */
        Map<String, Object> result = checkEmploymentPositionTypeIdAndSetEmplPositionTypeDate(emplPositionTypeId, description, comments, employmentAmount, person, refDate);

        // check on the right value
        if(UtilValidate.isNotEmpty(result.get(E.emplPositionTypeId.name()))) {
            /**
             * Check templateId is changed
             */
            String currentEmploymentPositionTypeId = person.getString(E.emplPositionTypeId.name());
            String templateId = checkTemplate(currentEmploymentPositionTypeId, (String) result.get(E.emplPositionTypeId.name()));
            if (UtilValidate.isNotEmpty(templateId)) {
                result.put(E.templateId.name(), templateId);
            }
        }

        return result;
    }

    /**
     * Check if emplPositionTypeId, comments, description or employmentAmount are changed, in this case set emplPositionTypeDate = refDate <br>
     * if emplPositionTypeId = currentEmploymentPositionType.parentTypeId return currentEmploymentPositionTypeId <br>
     * @return map with: emplPositionTypeDate not null if there is a change for emplPositionTypeId, comments, description or employmentAmount, <br>
     *  emplPositionTypeId always not null
     * @throws GenericEntityException 
     */
    private Map<String, Object> checkEmploymentPositionTypeIdAndSetEmplPositionTypeDate(String newEmplPositionTypeId, String  description, String comments, BigDecimal newEmploymentAmount, GenericValue person, Timestamp refDate) throws GenericEntityException {
        // set employmentAmount from input or from person
        BigDecimal currentEmploymentAmount = UtilValidate.isEmpty(person.getBigDecimal(E.employmentAmount.name())) ? new BigDecimal(100) : person.getBigDecimal(E.employmentAmount.name());
        BigDecimal employmentAmount = UtilValidate.isNotEmpty(newEmploymentAmount) ? newEmploymentAmount : currentEmploymentAmount;
        
        // set emplPositionTypeId from input or from person
        String currentEmploymentPositionTypeId = UtilValidate.isEmpty(person.getString(E.emplPositionTypeId.name())) ? "" : person.getString(E.emplPositionTypeId.name());;
        Map<String, Object> result = checkEmplPositionTypeId(newEmplPositionTypeId, currentEmploymentPositionTypeId);
        String emplPositionTypeId = (String)result.get(E.emplPositionTypeId.name());
        
        /**
         * Valorizzo emplPositionTypeDate solo se cambio uno dei seguenti dati:
         * emplPositionTypeId
         * description - si trova dentro Party
         * comments
         * employmentAmount
         * 
         */
        GenericValue party = takeOverService.getManager().getDelegator().findOne(E.Party.name(), false, E.partyId.name(), person.getString(E.partyId.name()));
        
        String currentDescription = UtilValidate.isEmpty(party.getString(E.description.name())) ? "" : party.getString(E.description.name());
        String currentComments = UtilValidate.isEmpty(person.getString(E.comments.name())) ? "" : person.getString(E.comments.name());
        
        // check change only for not null vale
        if (UtilValidate.isNotEmpty(emplPositionTypeId) && !emplPositionTypeId.equals(currentEmploymentPositionTypeId)) {
            addLogInfoMessage(E.emplPositionTypeId.name(), currentEmploymentPositionTypeId, emplPositionTypeId, refDate);
            result.put(E.emplPositionTypeDate.name(), refDate);
        } if (UtilValidate.isNotEmpty(description) && !currentDescription.equals(description)) {
            addLogInfoMessage(E.description.name(), currentDescription, description, refDate);
            result.put(E.emplPositionTypeDate.name(), refDate);
        } if (UtilValidate.isNotEmpty(comments) && !currentComments.equals(comments)) {
            addLogInfoMessage(E.comments.name(), currentComments, comments, refDate);
            result.put(E.emplPositionTypeDate.name(), refDate);
        } if (UtilValidate.isNotEmpty(employmentAmount) && currentEmploymentAmount.compareTo(employmentAmount) != 0) {
            addLogInfoMessage(E.employmentAmount.name(), currentEmploymentAmount, employmentAmount, refDate);
            result.put(E.emplPositionTypeDate.name(), refDate);
        }
        return result;
    }
    
    private void addLogInfoMessage(String fieldName, Object currentValue, Object newValue, Timestamp refDate) {
        String msg = "There is a change for " + fieldName + " [ " + currentValue + " - > " + newValue + " ], so emplPositionTypeDate = " + refDate;
        takeOverService.addLogInfo(msg);
    }
    
    private Map<String, Object> checkEmplPositionTypeId(String newEmplPositionTypeId, String currentEmploymentPositionTypeId) throws GenericEntityException {
        String emplPositionTypeId = UtilValidate.isNotEmpty(newEmplPositionTypeId) ? newEmplPositionTypeId : currentEmploymentPositionTypeId;
        
        if (UtilValidate.isNotEmpty(currentEmploymentPositionTypeId)) {
            String msg = "Compare emplPositionType " + newEmplPositionTypeId + " with exixting value " + currentEmploymentPositionTypeId;
            takeOverService.addLogInfo(msg);
            
            GenericValue currentEmploymentPositionType = takeOverService.getManager().getDelegator().findOne(E.EmplPositionType.name(), UtilMisc.toMap(E.emplPositionTypeId.name(), currentEmploymentPositionTypeId), false);
            if (newEmplPositionTypeId.equals(currentEmploymentPositionType.get(E.parentTypeId.name()))) {
                emplPositionTypeId = currentEmploymentPositionTypeId;
            }
        }
        return UtilMisc.toMap(E.emplPositionTypeId.name(), (Object) emplPositionTypeId, E.emplPositionTypeDate.name(), null);
    }

    /**
     * Check if templateId is changed
     * @param currentEmploymentPositionTypeId, from person.emplPositionTypeId
     * @param emplPositionTypeId, incoming field
     * @return currentTemplateId (from currentEmploymentPositionTypeId) if <> templateId (from emplPositionTypeId)
     * @throws GenericEntityException 
     */
    private String checkTemplate(String currentEmploymentPositionTypeId, String emplPositionTypeId) throws GenericEntityException {
        GenericValue currentEmploymentPositionType = takeOverService.getManager().getDelegator().findOne(E.EmplPositionType.name(), UtilMisc.toMap(E.emplPositionTypeId.name(), currentEmploymentPositionTypeId), false);
        GenericValue employmentPositionType = takeOverService.getManager().getDelegator().findOne(E.EmplPositionType.name(), UtilMisc.toMap(E.emplPositionTypeId.name(), emplPositionTypeId), false);

        if (UtilValidate.isEmpty(currentEmploymentPositionType)) {
            return null;
        }
        String currentTemplateId = currentEmploymentPositionType.getString(E.templateId.name());
        String templateId = employmentPositionType.getString(E.templateId.name());

        if (UtilValidate.isNotEmpty(currentTemplateId) && !currentTemplateId.equals(templateId)) {
            return currentTemplateId;
        }
        return null;
    }
}

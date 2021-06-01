package com.mapsengineering.emplperf.test;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.service.ServiceUtil;

import com.mapsengineering.base.standardimport.common.PersonTypeEnum;
import com.mapsengineering.base.util.MessageUtil;
import com.mapsengineering.partyext.services.WorkEffortPartyServices;
import com.mapsengineering.workeffortext.services.E;
import com.mapsengineering.workeffortext.services.rootcopy.WorkEffortRootCopyService;

import javolution.util.FastList;

/** 
 * Integration Test for PersonInterface with file excel, snapshot, custom postPartyUpdateWorkEffort 
 *
 */
public class TestPartyUpdateWorkEffortWithInsertFromTemplate extends BaseTestInsertFromTemplate {
    public static final String MODULE = TestPartyUpdateWorkEffortWithInsertFromTemplate.class.getName();
    
    /** 30/06/2014 */
    protected static final Timestamp THRU_DATE_2014 = new Timestamp(UtilDateTime.toDate(6, 30, 2014, 0, 0, 0).getTime());
    /** 30/11/2014 */
    protected static final Timestamp THRU_DATE_NOV_2015 = new Timestamp(UtilDateTime.toDate(11, 30, 2014, 0, 0, 0).getTime());
    
    /** 01/07/2014 */
    protected static final Timestamp FROM_DATE_2014 = new Timestamp(UtilDateTime.toDate(7, 1, 2014, 0, 0, 0).getTime());
    /** 01/03/2014 */
    protected static final Timestamp NEW_UORG_DATE_2014 = new Timestamp(UtilDateTime.toDate(3, 1, 2014, 0, 0, 0).getTime());
    /** 28/02/2014 */
    protected static final Timestamp OLD_UORG_DATE_2014 = new Timestamp(UtilDateTime.toDate(2, 28, 2014, 0, 0, 0).getTime());
    /** 31/12/2014 */
    protected static final Timestamp COMPLETION_DATE_2014 = new Timestamp(UtilDateTime.toDate(12, 31, 2014, 0, 0, 0).getTime());
    
    private WorkEffortPartyServices workEffortPartyServices;

    private static final String SEP = " e ";
    
    protected static final String UORG_2 = "28"; // partyId="RCP10036";
    
    /**
     * Caso Wilma,
     * nel primo file excel crea soggetto, 
     * nel primo lancio crea scheda 
     * nessuna nuova informazione negli altri file
     * non viene creata nessuna scheda e quella esistente ha ancora la scadenza completionDate2014
     * anche dopo il terzo lancio
     */
    protected static final String WILMA = "109889";
    
    /**
     * TODO Ricontrollare
     * Caso Ombretta, nel secondo file ha di diverso solo la data cessazione valorizzata,
     * non viene creata nessuna scheda ma quella esistente ha la scadenza thruDate2014 e non completionDate2014
     */
    protected static final String OMBRETTA = "296";
    
    /**
     * TODO Ricontrollare
     * Caso Catia, cambia appartenenza il fromDate2014 da "RCP10022" a "RCP10036" 
     * nel secondo file ha di diverso l'employmentOrgCode,
     * quindi all'inizio non viene creata nessuna scheda ma quella esistente ha la scadenza thruDate2014 e non completionDate2014
     * Nel quarto lancio della creazione massiva schede individuali viene creata la nuova scheda dal fromDate2014 al completionDate2014
     */
    protected static final String CATIA = "857";
    
    /**
     * TODO Ricontrollare
     * Caso Stefano, cambia templateId il fromDate2014 da "ABCC" a "D" 
     * nel secondo file ha di diverso l'emplPositionTypeId,
     * quindi all'inizio non viene creata nessuna scheda perche la categoria e' diversa,
     * Nel terzo lancio della creazione massiva schede individuali viene creata la nuova scheda dal fromDate2014 al completionDate2014,
     */
    protected static final String STEFANO = "65";
    
    /**
     * Caso Claudio, 
     * nel secondo file ha di diverso l'allocationOrgCode,
     * la scheda resta sempre invariata con data fine completionDate2014,
     */
    protected static final String CLAUDIO = "010111M";
    
    /** TODO problema perche non e' chiaro cosa succede al caso di Roberto
     * TODO Ricontrollare
     * Caso Roberto, cambia assegnazione e valutatore il fromDate2014
     * ma nello standard non viene fatto niente quindi si ritrova ad avere due schede di valutazione, un aper la vecchia unita organizzativa e una per la nuova
     * nel dubbio no nviene fatto nessun controllo per adesso 
     * nel secondo file ha di diverso l'allocationOrgCode e il evaluatorCode,
     * Nel terzo lancio viene chiusa la scheda precedente con data thruDate, e viene creata una nuova scheda con data inizio fromDate,
     * I valutatori saranno D0005 per la scheda nel primo periodo e D0006 nella scheda per il secondo periodo
     */
    protected static final String ROBERTO = "010356I";
    
    
    /**
     * TODO Ricontrollare
     * Caso Raniero, non ha update nello standard, ma nel custom cambia valutatore e approvatore
     * nel secondo file ha di diverso approvatore e valutatore, ma non ha endYearElab valorizzato quindi non chiude le schede
     * poi nel secondo file ha di diverso approvatore e valutatore e ha endYearElab valorizzato, quindi le schede vengono chiuse nella parte custom
     * nel terzo file cambia di nuovo approvatore e valutatore e ha endYearElab valorizzato
     * nel quarto file cambia di nuovo approvatore e valutatore e ha endYearElab valorizzato
     * nel quinto file cambia di nuovo approvatore e valutatore ma non ha endYearElab valorizzato
     */
    protected static final String RANIERO = "029403M";
    
    /**
     * TODO Ricontrollare
     * Caso Francesco, non ha update nello standard, ma nel custom cambia valutatore e approvatore e ha thrudate
     * nei file successivi non viene piu gestito in quanto disabilitato
     */
    protected static final String FRANCESCO = "022345M";

    /**
     * TODO Ricontrollare
     * Caso Monica, non ha update nello standard, ma nel custom cambia l'employmentOrgCode al 01/07/2014
     * nel quarto file cambia l'employmentOrgCode e ha endYearElab valorizzato quindi
     * nel custom viene creata la relazione a partire dal 01/07/2014 ma poi viene cambiata l'unita' organizzativa sulla scheda esistente,
     * quindi un eventuale creazione massiva delle schede individuali cerca di creare una nuova scheda...
     * ATTENZIONE: nell' xxx lancio creazione massiva si creano le schede dal 01/01 al 01/03 sulla vecchia unita' organizzativa
     * perche' la scheda esistente dura per tutto l'anno ma il servizio non lo capisce
     */
    protected static final String MONICA = "023214L";
    
    /** 
     * Caso Giovanni e Giovanna, 
     * nel terzo file excel vengono creati i soggetti,
     * nel quinto lancio creazione massiva vengono schede create,
     * nel quarto file excel cambiano l' unita' organizzativa il 01/03
     * nel sesto lancio creazione massiva si creano le nuove schede dal 01/03
     * nel quinto file excel uno dei due, 01/07 (GIOVANNI), cambia template, l'altro no... 
     * e vengono chiuse a mano la scheda di Giovanni
     * nel settimo lancio creazione massiva si crea le nuova scheda dal 01/07 solo per GIOVANNI
     * ATTENZIONE: nell'ottavo lancio creazione massiva si creano le schede dal 01/01 al 01/03 sulla vecchia unita' organizzativa
     * perche' la scheda esistente dura per tutto l'anno ma il servizio non lo capisce
     */
    protected static final String GIOVANNI = "101010";
    protected static final String GIOVANNA = "101011";
    
    /**
     * TODO Ricontrollare
     */
    protected static final String ARMANDO = "10142";
    
    /**
     * Test with file excel, snapshot, custom postPartyUpdateWorkEffort
     */
    public void testPartyUpdateWorkEffortWithInsertFromTemplate() {
        try {
            EntityCondition extraConditionAfter = EntityCondition.makeCondition(E.templateId.name(), TestPartyUpdateWorkEffortWithInsertFromTemplate.TEMPLATE_ID_FOR_D);
            EntityCondition extraConditionAfterTEMP1 = EntityCondition.makeCondition(E.templateId.name(), TestPartyUpdateWorkEffortWithInsertFromTemplate.TEMPLATE_ID_FOR_ABCC);
            workEffortPartyServices = new WorkEffortPartyServices(dispatcher.getDispatchContext(), context);
            Debug.log(" - primo lancio creazione massiva schede per " + TestPartyUpdateWorkEffortWithInsertFromTemplate.UORG_1 + SEP + TestPartyUpdateWorkEffortWithInsertFromTemplate.EMPL_POSITION_TYPE_ID_D);
            // primo lancio creazione massiva schede
            setContextAndRunInsertFromTemplate(TestPartyUpdateWorkEffortWithInsertFromTemplate.UORG_1, TestPartyUpdateWorkEffortWithInsertFromTemplate.ROLE_TYPE_ID_UOSET, TestPartyUpdateWorkEffortWithInsertFromTemplate.EMPL_POSITION_TYPE_ID_D, TestPartyUpdateWorkEffortWithInsertFromTemplate.START_DATE_2014, TestPartyUpdateWorkEffortWithInsertFromTemplate.COMPLETION_DATE_2014, 0);
            workEffortRootSnapshot();
            
            Debug.log(" - primo lancio controllo PERSONA " + getPartyId(PERSONA) + " 1 scheda fino al 31/12");
            getWorkEffortEmplPerfForEvaluated(TestPartyUpdateWorkEffortWithInsertFromTemplate.PERSONA, TestPartyUpdateWorkEffortWithInsertFromTemplate.COMPLETION_DATE_2014, extraConditionAfter);

            // 10 persone vengono caricati, 
            // WILMA, Ombretta, Catia, Stefano, CLAUDIO, ROBERTO, tutti sulla ORG 14 e con valutatore 0005,
            // RANIERO, Francesco, MONICA, ANGELO hanno anche approverCode 0003
            Debug.log(" - caricamento primo file excel ");
            setContextAndRunPersonInterfaceUpdate("PersonInterface_FirstWorkEffortFromTemplate.xls", 1L, 10L);

            // secondo lancio creazione massiva schede, dovrebbe crearne 8
            Debug.log(" - secondo lancio creazione massiva schede per " + TestPartyUpdateWorkEffortWithInsertFromTemplate.UORG_1 + SEP + TestPartyUpdateWorkEffortWithInsertFromTemplate.EMPL_POSITION_TYPE_ID_D);
            setContextAndRunInsertFromTemplate(TestPartyUpdateWorkEffortWithInsertFromTemplate.UORG_1, TestPartyUpdateWorkEffortWithInsertFromTemplate.ROLE_TYPE_ID_UOSET, TestPartyUpdateWorkEffortWithInsertFromTemplate.EMPL_POSITION_TYPE_ID_D, TestPartyUpdateWorkEffortWithInsertFromTemplate.START_DATE_2014, TestPartyUpdateWorkEffortWithInsertFromTemplate.COMPLETION_DATE_2014, 0, 8);
            workEffortRootSnapshot();
            
            Debug.log(" - secondo lancio controllo WILMA " + getPartyId(WILMA) + " 1 scheda fino al 31/12");
            checkWorkEffortListSize(1, TestPartyUpdateWorkEffortWithInsertFromTemplate.WILMA, TestPartyUpdateWorkEffortWithInsertFromTemplate.COMPLETION_DATE_2014, extraConditionAfter);
            
            Debug.log(" - secondo lancio controllo OMBRETTA " + getPartyId(OMBRETTA) + " has thruDate valorizzato ma 1 scheda fino al 31/12");
            checkWorkEffortListSize(1, TestPartyUpdateWorkEffortWithInsertFromTemplate.OMBRETTA, TestPartyUpdateWorkEffortWithInsertFromTemplate.COMPLETION_DATE_2014, extraConditionAfter);
            
            Debug.log(" - secondo lancio controllo CATIA " + getPartyId(CATIA) + " cambia employment, ma 1 scheda fino al 31/12");
            checkWorkEffortListSize(1, TestPartyUpdateWorkEffortWithInsertFromTemplate.CATIA, TestPartyUpdateWorkEffortWithInsertFromTemplate.COMPLETION_DATE_2014, extraConditionAfter);
            
            Debug.log(" - secondo lancio controllo CATIA " + getPartyId(CATIA) + " ha WEM_EVAL_MANAGER 0005");
            checkEvaluatoApprover(6, TestPartyUpdateWorkEffortWithInsertFromTemplate.CATIA, PersonTypeEnum.WEM_EVAL_MANAGER.roleTypeId(), TestPartyUpdateWorkEffortWithInsertFromTemplate.VALUTATORE_1, TestPartyUpdateWorkEffortWithInsertFromTemplate.START_DATE_2014, TestPartyUpdateWorkEffortWithInsertFromTemplate.COMPLETION_DATE_2014);
            Debug.log(" - secondo lancio controllo CATIA " + getPartyId(CATIA) + "non ha WEM_EVAL_MANAGER 0006");
            checkEvaluatoApprover(0, TestPartyUpdateWorkEffortWithInsertFromTemplate.CATIA, PersonTypeEnum.WEM_EVAL_MANAGER.roleTypeId(), TestPartyUpdateWorkEffortWithInsertFromTemplate.VALUTATORE_2, TestPartyUpdateWorkEffortWithInsertFromTemplate.FROM_DATE_2014, TestPartyUpdateWorkEffortWithInsertFromTemplate.COMPLETION_DATE_2014);
            
            Debug.log(" - secondo lancio controllo STEFANO " + getPartyId(STEFANO) + " non ancora creato");
            checkWorkEffortListSize(0, TestPartyUpdateWorkEffortWithInsertFromTemplate.STEFANO, TestPartyUpdateWorkEffortWithInsertFromTemplate.COMPLETION_DATE_2014, extraConditionAfter);
            
            Debug.log(" - secondo lancio controllo CLAUDIO " + getPartyId(CLAUDIO) + " 1 scheda fino al 31/12");
            checkPartyRelationship(getPartyId(CLAUDIO), 1, getPartyId(UORG_1), null, 1, getPartyId(UORG_1), null, 1, getPartyId(VALUTATORE_1), null, 0, null, null);
            checkWorkEffortListSize(1, TestPartyUpdateWorkEffortWithInsertFromTemplate.CLAUDIO, TestPartyUpdateWorkEffortWithInsertFromTemplate.COMPLETION_DATE_2014, extraConditionAfter);
            
            Debug.log(" - secondo lancio controllo ROBERTO " + getPartyId(ROBERTO) + " 1 scheda fino al 31/12");
            checkWorkEffortListSize(1, TestPartyUpdateWorkEffortWithInsertFromTemplate.ROBERTO, TestPartyUpdateWorkEffortWithInsertFromTemplate.COMPLETION_DATE_2014, extraConditionAfter);
            
            Debug.log(" -  secondo lancio controllo RANIERO " + getPartyId(RANIERO) + " 1 scheda fino al 31/12");
            checkPartyRelationship(getPartyId(RANIERO), 1, getPartyId(UORG_1), null, 1, getPartyId(UORG_1), null, 1, getPartyId(VALUTATORE_1), null, 1, getPartyId(APPROVATORE_1), null);
            checkWorkEffortListSize(1, TestPartyUpdateWorkEffortWithInsertFromTemplate.RANIERO, TestPartyUpdateWorkEffortWithInsertFromTemplate.COMPLETION_DATE_2014, extraConditionAfter);
            
            Debug.log(" - secondo lancio controllo FRANCESCO " + getPartyId(FRANCESCO) + " 1 scheda fino al 31/12");
            checkWorkEffortListSize(1, TestPartyUpdateWorkEffortWithInsertFromTemplate.FRANCESCO, TestPartyUpdateWorkEffortWithInsertFromTemplate.COMPLETION_DATE_2014, extraConditionAfter);
            
            Debug.log(" - secondo lancio controllo MONICA " + getPartyId(MONICA) + " 1 scheda fino al 31/12");
            checkWorkEffortListSize(1, TestPartyUpdateWorkEffortWithInsertFromTemplate.MONICA, TestPartyUpdateWorkEffortWithInsertFromTemplate.COMPLETION_DATE_2014, extraConditionAfter);
            
            /*Vilma
            Ombretta
            Catia cambia employment
            Stefano
            CLAUDIO
            ROBERTO
            RANIERO
            Francesco*/

            // ROBERTO, RANIERO, Francesco cambiano valutatore
            // RANIERO, Francesco cmabiano approvatore 
            // Francesco ha pure thruDate valorizzato
            Debug.log(" - caricamento secondo file excel senza endYearElab");
            setContextAndRunPersonInterfaceUpdate("PersonInterface_SecondWorkEffortFromTemplate.xls", 0L, 8L);
            // come se le chiudessere a mano ? invocare per ogni party con le corrette condizioni, catia cambia appartenenza il fromDate2014 da "RCP10022" a "RCP10036"
            updateCatia();
            // come se le chiudessere a mano ? invocare per ogni party con le corrette condizioni, stefano cambia templateId il fromDate2014 da "ABCC" a "D"
            updateStefano();
            Debug.log(" - secondo lancio controllo CLAUDIO che ha cambiato assegnazione " + getPartyId(CLAUDIO) + " MA 1 scheda fino al 31/12");
            checkPartyRelationship(getPartyId(CLAUDIO), 1, getPartyId(UORG_1), null, 2, null, null, 1, getPartyId(VALUTATORE_1), null, 0, null, null);
            checkWorkEffortListSize(1, TestPartyUpdateWorkEffortWithInsertFromTemplate.CLAUDIO, TestPartyUpdateWorkEffortWithInsertFromTemplate.COMPLETION_DATE_2014, extraConditionAfter);
            
            
            // come se le chiudessere a mano ? invocare per ogni party con le corrette condizioni, roberto cambia assegnazione e valutatore il fromDate2014
            updateRoberto();
            
            Debug.log(" - caricamento secondo file controllo RANIERO " + getPartyId(RANIERO) + " ... ");
            checkPartyRelationship(getPartyId(RANIERO), 1, getPartyId(UORG_1), null, 1, getPartyId(UORG_1), null, 2, null, null, 2, null, null);
            Debug.log(" - caricamento secondo file controllo FRANCESCO " + getPartyId(FRANCESCO) + " ... ");
            checkPartyRelationship(getPartyId(FRANCESCO), 1, getPartyId(UORG_1), THRU_DATE_NOV_2015, 1, getPartyId(UORG_1), THRU_DATE_NOV_2015, 2, null, null, 2, null, null);
            
            // RANIERO 01/07 cambia valutatore 0005 -> 0017 e approvatore 0003 -> 0018 
            // Francesco 30/11 cessa e 01/07 cambia valutatore 0005 -> 0017 e approvatore 0003 -> 0018
            // MONICA 01/07 cambia employment 14 -> 28
            Debug.log(" - caricamento secondo file excel con endYearElab ");
            setContextAndRunPersonInterfaceUpdate("PersonInterface_SecondWorkEffortFromTemplate_EndYearElab.xls", 0L, 3L, true);
            // endYearElab era un parametro che, per il custom di Bologna, aggiornava le schede in caso di cambio approvatore e valutatore, ma adesso no nviene piu utilizato.
            Debug.log(" - caricamento secondo file excel con endYearElab controllo RANIERO " + getPartyId(RANIERO));
            checkPartyRelationship(getPartyId(RANIERO), 1, getPartyId(UORG_1), null, 1, getPartyId(UORG_1), null, 2, null, null, 2, null, null);
            Debug.log(" - caricamento secondo file excel con endYearElab controllo FRANCESCO " + getPartyId(FRANCESCO));
            checkPartyRelationship(getPartyId(FRANCESCO), 1, getPartyId(UORG_1), THRU_DATE_NOV_2015, 1, getPartyId(UORG_1), THRU_DATE_NOV_2015, 2, null, null, 2, null, null);
            Debug.log(" - caricamento secondo file excel con endYearElab controllo MONICA " + getPartyId(MONICA));
            checkPartyRelationship(getPartyId(MONICA), 2, null, null, 1, getPartyId(UORG_1), null, 1, getPartyId(VALUTATORE_1), null, 1, getPartyId(APPROVATORE_1), null);
            
            // invocare per ogni party con le corrette condizioni, monica cambia appartenenza
            updateMonica();
            
            // terzo lancio creazione massiva schede
            Debug.log(" - terzo lancio creazione massiva schede per " + TestPartyUpdateWorkEffortWithInsertFromTemplate.UORG_1 + SEP + TestPartyUpdateWorkEffortWithInsertFromTemplate.EMPL_POSITION_TYPE_ID_D);
            setContextAndRunInsertFromTemplate(TestPartyUpdateWorkEffortWithInsertFromTemplate.UORG_1, TestPartyUpdateWorkEffortWithInsertFromTemplate.ROLE_TYPE_ID_UOSET, TestPartyUpdateWorkEffortWithInsertFromTemplate.EMPL_POSITION_TYPE_ID_D, TestPartyUpdateWorkEffortWithInsertFromTemplate.START_DATE_2014, TestPartyUpdateWorkEffortWithInsertFromTemplate.COMPLETION_DATE_2014, 0);
            workEffortRootSnapshot();
            
            // non deve creare niente e ha ancora la scheda con scadenza completionDate2014
            Debug.log(" - terzo lancio controllo WILMA " + getPartyId(WILMA));
            checkWorkEffortListSize(1, TestPartyUpdateWorkEffortWithInsertFromTemplate.WILMA, TestPartyUpdateWorkEffortWithInsertFromTemplate.COMPLETION_DATE_2014, extraConditionAfter);
            
            // set person.thuDate
            // non deve creare niente e non ha e' piu la scheda con scadenza completionDate2014, ma con scadenza thruDate 30/06
            Debug.log(" - terzo lancio controllo OMBRETTA " + getPartyId(OMBRETTA));
            checkWorkEffortListSize(0, TestPartyUpdateWorkEffortWithInsertFromTemplate.OMBRETTA, TestPartyUpdateWorkEffortWithInsertFromTemplate.COMPLETION_DATE_2014, extraConditionAfter);
            checkWorkEffortListSize(1, TestPartyUpdateWorkEffortWithInsertFromTemplate.OMBRETTA, TestPartyUpdateWorkEffortWithInsertFromTemplate.THRU_DATE_2014, extraConditionAfter);
            
            // non deve creare niente e non ha e' piu la scheda con scadenza completionDate2014, ma con scadenza thruDate 30/06
            Debug.log(" - terzo lancio controllo CATIA " + getPartyId(CATIA));
            checkWorkEffortListSize(0, TestPartyUpdateWorkEffortWithInsertFromTemplate.CATIA, TestPartyUpdateWorkEffortWithInsertFromTemplate.COMPLETION_DATE_2014, extraConditionAfter);
            checkWorkEffortListSize(1, TestPartyUpdateWorkEffortWithInsertFromTemplate.CATIA, TestPartyUpdateWorkEffortWithInsertFromTemplate.THRU_DATE_2014, extraConditionAfter);
            
            // DEVE creare la scheda con la data di refDate, cioe' fromDate2014
            List<GenericValue> listaStefano = getWorkEffortEmplPerfForEvaluated(TestPartyUpdateWorkEffortWithInsertFromTemplate.STEFANO, TestPartyUpdateWorkEffortWithInsertFromTemplate.COMPLETION_DATE_2014, extraConditionAfter);
            assertTrue(UtilValidate.isNotEmpty(listaStefano));
            checkFromDateAndWorkEffortListSize(TestPartyUpdateWorkEffortWithInsertFromTemplate.FROM_DATE_2014, 1, TestPartyUpdateWorkEffortWithInsertFromTemplate.STEFANO, TestPartyUpdateWorkEffortWithInsertFromTemplate.COMPLETION_DATE_2014, extraConditionAfter);
            
            // non deve creare niente e ha ancora la scheda con scadenza completionDate2014
            Debug.log(" - terzo lancio controllo CLAUDIO " + getPartyId(CLAUDIO) + " 1 scheda fino al 31/12");
            checkWorkEffortListSize(1, TestPartyUpdateWorkEffortWithInsertFromTemplate.CLAUDIO, TestPartyUpdateWorkEffortWithInsertFromTemplate.COMPLETION_DATE_2014, extraConditionAfter);
            
            List<EntityCondition> condition = FastList.newInstance();
            condition.add(EntityCondition.makeCondition(E.partyId.name(), getPartyId(TestPartyUpdateWorkEffortWithInsertFromTemplate.ROBERTO)));
            
            condition.add(EntityCondition.makeCondition(E.roleRoleTypeId.name(), PersonTypeEnum.WEM_EVAL_MANAGER.roleTypeId()));
            condition.add(EntityCondition.makeCondition(E.workEffortRevisionId.name(), null));
            
            List<GenericValue> list = delegator.findList(E.WorkEffortPartyAssignRole.name(), EntityCondition.makeCondition(condition), null, null, null, false);
            Debug.log(" - terzo lancio controllo ROBERTO " + getPartyId(ROBERTO) + " WorkEffortPartyAssignRole ");
            Debug.log("Found " + list.size() + " WorkEffortPartyAssignRole with condition= " + condition);
            Debug.log("Found " + list);
            
            // Check there is evaluator = D0005 since startDate2014 to thruDate2014
            // TODO checkEvaluatoApprover(6, TestPartyUpdateWorkEffortWithInsertFromTemplate.ROBERTO, PersonTypeEnum.WEM_EVAL_MANAGER.roleTypeId(), TestPartyUpdateWorkEffortWithInsertFromTemplate.VALUTATORE_1, TestPartyUpdateWorkEffortWithInsertFromTemplate.START_DATE_2014, TestPartyUpdateWorkEffortWithInsertFromTemplate.THRU_DATE_2014);

            // deve cambiare il valutatore da 0005 a 0006
            // Check there is evaluator = D0006 since fromDate2014 to completionDate2014
            // TODO checkEvaluatoApprover(6, TestPartyUpdateWorkEffortWithInsertFromTemplate.ROBERTO, PersonTypeEnum.WEM_EVAL_MANAGER.roleTypeId(), TestPartyUpdateWorkEffortWithInsertFromTemplate.VALUTATORE_2, TestPartyUpdateWorkEffortWithInsertFromTemplate.FROM_DATE_2014, TestPartyUpdateWorkEffortWithInsertFromTemplate.COMPLETION_DATE_2014);

            // deve creare con scadenza completionDate2014, e inizio fromDate2014
            Debug.log(" - terzo lancio controllo CATIA " + getPartyId(CATIA) + " cambia date ");
            checkWorkEffortListSize(0, TestPartyUpdateWorkEffortWithInsertFromTemplate.CATIA, TestPartyUpdateWorkEffortWithInsertFromTemplate.COMPLETION_DATE_2014, extraConditionAfter);
            
            Debug.log(" - terzo lancio controllo MONICA " + getPartyId(MONICA));
            checkWorkEffortListSize(1, TestPartyUpdateWorkEffortWithInsertFromTemplate.MONICA, TestPartyUpdateWorkEffortWithInsertFromTemplate.COMPLETION_DATE_2014, extraConditionAfter);
            checkWorkEffortListSize(2, TestPartyUpdateWorkEffortWithInsertFromTemplate.MONICA, TestPartyUpdateWorkEffortWithInsertFromTemplate.THRU_DATE_2014, extraConditionAfter);
            
            // quarto lancio creazione massiva schede
            Debug.log(" - quarto lancio creazione massiva schede per " + TestPartyUpdateWorkEffortWithInsertFromTemplate.UORG_2 + SEP + TestPartyUpdateWorkEffortWithInsertFromTemplate.EMPL_POSITION_TYPE_ID_D);
            setContextAndRunInsertFromTemplate(TestPartyUpdateWorkEffortWithInsertFromTemplate.UORG_2, TestPartyUpdateWorkEffortWithInsertFromTemplate.ROLE_TYPE_ID_UOSET, TestPartyUpdateWorkEffortWithInsertFromTemplate.EMPL_POSITION_TYPE_ID_D, TestPartyUpdateWorkEffortWithInsertFromTemplate.START_DATE_2014, TestPartyUpdateWorkEffortWithInsertFromTemplate.COMPLETION_DATE_2014, 0);
            workEffortRootSnapshot();
            
            // deve creare con scadenza completionDate2014, e inizio fromDate2014
            Debug.log(" - terzo lancio controllo CATIA " + getPartyId(CATIA) + " cambia date ");
            checkFromDateAndWorkEffortListSize(TestPartyUpdateWorkEffortWithInsertFromTemplate.FROM_DATE_2014, 1, TestPartyUpdateWorkEffortWithInsertFromTemplate.CATIA, TestPartyUpdateWorkEffortWithInsertFromTemplate.COMPLETION_DATE_2014, extraConditionAfter);
            
            // non deve creare niente e ha ancora la scheda con scadenza completionDate2014
            Debug.log(" - terzo lancio controllo WILMA " + getPartyId(WILMA));
            checkWorkEffortListSize(1, TestPartyUpdateWorkEffortWithInsertFromTemplate.WILMA, TestPartyUpdateWorkEffortWithInsertFromTemplate.COMPLETION_DATE_2014, extraConditionAfter);
            
            // Raniero e Francesco non hanno update nello standard, ma nel custom cambiano valutatore e approvatore
            Debug.log(" - caricamento terzo file excel ");
            setContextAndRunPersonInterfaceUpdate("PersonInterface_ThirdWorkEffortFromTemplate.xls", 0L, 2L);
            setContextAndRunPersonInterfaceUpdate("PersonInterface_ThirdWorkEffortFromTemplate_EndYearElab.xls", 0L, 2L, true);
            
            // quinto lancio creazione massiva schede
            Debug.log(" - quinto lancio creazione massiva schede per " + TestPartyUpdateWorkEffortWithInsertFromTemplate.UORG_2 + SEP + TestPartyUpdateWorkEffortWithInsertFromTemplate.EMPL_POSITION_TYPE_ID_D);
            setContextAndRunInsertFromTemplate(TestPartyUpdateWorkEffortWithInsertFromTemplate.UORG_2, TestPartyUpdateWorkEffortWithInsertFromTemplate.ROLE_TYPE_ID_UOSET, TestPartyUpdateWorkEffortWithInsertFromTemplate.EMPL_POSITION_TYPE_ID_D, TestPartyUpdateWorkEffortWithInsertFromTemplate.START_DATE_2014, TestPartyUpdateWorkEffortWithInsertFromTemplate.COMPLETION_DATE_2014, 0, 2);
            Debug.log(" - quinto lancio controllo GIOVANNI " + getPartyId(GIOVANNI) + " 1 scheda che termina nel 2014 ");
            checkPartyRelationship(getPartyId(GIOVANNI), 1, getPartyId(UORG_2), null, 1, getPartyId(UORG_2), null, 1, getPartyId(VALUTATORE_1), null, 1, getPartyId(APPROVATORE_1), null);
            checkWorkEffortListSize(1, TestPartyUpdateWorkEffortWithInsertFromTemplate.GIOVANNI, TestPartyUpdateWorkEffortWithInsertFromTemplate.COMPLETION_DATE_2014, extraConditionAfter);
            Debug.log(" - quinto lancio controllo GIOVANNA " + getPartyId(GIOVANNA) + " 1 scheda che termina nel 2014 ");
            checkPartyRelationship(getPartyId(GIOVANNA), 1, getPartyId(UORG_2), null, 1, getPartyId(UORG_2), null, 1, getPartyId(VALUTATORE_1), null, 1, getPartyId(APPROVATORE_1), null);
            checkWorkEffortListSize(1, TestPartyUpdateWorkEffortWithInsertFromTemplate.GIOVANNA, TestPartyUpdateWorkEffortWithInsertFromTemplate.COMPLETION_DATE_2014, extraConditionAfter);
            
            
            // Raniero e Francesco non hanno update nello standard, ma nel custom cambiano valutatore e approvatore per la seconda volta
            Debug.log(" - caricamento quarto file excel ");
            setContextAndRunPersonInterfaceUpdate("PersonInterface_FourthWorkEffortFromTemplate.xls", 0L, 2L);
            setContextAndRunPersonInterfaceUpdate("PersonInterface_FourthWorkEffortFromTemplate_EndYearElab.xls", 0L, 2L, true);
            Debug.log(" - quarto file controllo GIOVANNI " + getPartyId(GIOVANNI) + " 1 scheda che termina nel 2014 ");
            checkPartyRelationship(getPartyId(GIOVANNI), 2, null, null, 2, null, null, 1, getPartyId(VALUTATORE_1), null, 1, getPartyId(APPROVATORE_1), null);
            checkWorkEffortListSize(1, TestPartyUpdateWorkEffortWithInsertFromTemplate.GIOVANNI, TestPartyUpdateWorkEffortWithInsertFromTemplate.COMPLETION_DATE_2014, extraConditionAfter);
            Debug.log(" - quarto file controllo GIOVANNA " + getPartyId(GIOVANNA) + " 1 scheda che termina nel 2014 ");
            checkPartyRelationship(getPartyId(GIOVANNA), 2, null, null, 2, null, null, 1, getPartyId(VALUTATORE_1), null, 1, getPartyId(APPROVATORE_1), null);
            checkWorkEffortListSize(1, TestPartyUpdateWorkEffortWithInsertFromTemplate.GIOVANNA, TestPartyUpdateWorkEffortWithInsertFromTemplate.COMPLETION_DATE_2014, extraConditionAfter);
            
            Debug.log(" - sesto lancio creazione massiva schede per " + TestPartyUpdateWorkEffortWithInsertFromTemplate.UORG_1 + SEP + TestPartyUpdateWorkEffortWithInsertFromTemplate.EMPL_POSITION_TYPE_ID_D);
            setContextAndRunInsertFromTemplate(TestPartyUpdateWorkEffortWithInsertFromTemplate.UORG_1, TestPartyUpdateWorkEffortWithInsertFromTemplate.ROLE_TYPE_ID_UOSET, TestPartyUpdateWorkEffortWithInsertFromTemplate.EMPL_POSITION_TYPE_ID_D, TestPartyUpdateWorkEffortWithInsertFromTemplate.START_DATE_2014, TestPartyUpdateWorkEffortWithInsertFromTemplate.COMPLETION_DATE_2014, 0, 4);
            
            Debug.log(" - sesto lancio controllo GIOVANNI " + getPartyId(GIOVANNI) + " 2 schede che terminano nel 2014 ");
            checkWorkEffortListSize(2, TestPartyUpdateWorkEffortWithInsertFromTemplate.GIOVANNI, TestPartyUpdateWorkEffortWithInsertFromTemplate.COMPLETION_DATE_2014, extraConditionAfter);
            Debug.log(" - sesto lancio controllo GIOVANNA " + getPartyId(GIOVANNA) + " 2 schede che terminano nel 2014 ");
            checkWorkEffortListSize(2, TestPartyUpdateWorkEffortWithInsertFromTemplate.GIOVANNA, TestPartyUpdateWorkEffortWithInsertFromTemplate.COMPLETION_DATE_2014, extraConditionAfter);
            
            // Raniero e Francesco non hanno update nello standard, ma nel custom cambiano valutatore e approvatore per la terza volta
            Debug.log(" - caricamento quinto file excel ");
            setContextAndRunPersonInterfaceUpdate("PersonInterface_FifthWorkEffortFromTemplate.xls", 0L, 5L);
            updateGiovanni();
            
            Debug.log(" - settimo lancio creazione massiva schede per " + TestPartyUpdateWorkEffortWithInsertFromTemplate.UORG_1 + SEP + TestPartyUpdateWorkEffortWithInsertFromTemplate.EMPL_POSITION_TYPE_ID_ABCC);
            setContextAndRunInsertFromTemplate(TestPartyUpdateWorkEffortWithInsertFromTemplate.UORG_1, TestPartyUpdateWorkEffortWithInsertFromTemplate.ROLE_TYPE_ID_UOSET, TestPartyUpdateWorkEffortWithInsertFromTemplate.EMPL_POSITION_TYPE_ID_ABCC, TestPartyUpdateWorkEffortWithInsertFromTemplate.START_DATE_2014, TestPartyUpdateWorkEffortWithInsertFromTemplate.COMPLETION_DATE_2014, 0, 2);
            // deve avere una nuova scheda che inizia il 1/3 per via del cambio di modello
            Debug.log(" - settimo lancio controllo GIOVANNI " + getPartyId(GIOVANNI) + " 2 schede che terminano dopo il 30/06 2014 D");
            checkWorkEffortListSize(2, TestPartyUpdateWorkEffortWithInsertFromTemplate.GIOVANNI, TestPartyUpdateWorkEffortWithInsertFromTemplate.THRU_DATE_2014, extraConditionAfter);
            Debug.log(" - settimo lancio controllo GIOVANNI " + getPartyId(GIOVANNI) + " 1 schede che terminano dopo il 31/12 2014 TEMP1");
            checkWorkEffortListSize(1, TestPartyUpdateWorkEffortWithInsertFromTemplate.GIOVANNI, TestPartyUpdateWorkEffortWithInsertFromTemplate.COMPLETION_DATE_2014, extraConditionAfterTEMP1);
            
            Debug.log(" - settimo lancio controllo GIOVANNA " + getPartyId(GIOVANNA) + " 2 schede che terminano nel 2014 ");
            checkWorkEffortListSize(2, TestPartyUpdateWorkEffortWithInsertFromTemplate.GIOVANNA, TestPartyUpdateWorkEffortWithInsertFromTemplate.COMPLETION_DATE_2014, extraConditionAfter);
            
            
            Debug.log(" - ottavo lancio creazione massiva schede per " + TestPartyUpdateWorkEffortWithInsertFromTemplate.UORG_2 + SEP + TestPartyUpdateWorkEffortWithInsertFromTemplate.EMPL_POSITION_TYPE_ID_D);
            setContextAndRunInsertFromTemplate(TestPartyUpdateWorkEffortWithInsertFromTemplate.UORG_2, TestPartyUpdateWorkEffortWithInsertFromTemplate.ROLE_TYPE_ID_UOSET, TestPartyUpdateWorkEffortWithInsertFromTemplate.EMPL_POSITION_TYPE_ID_D, TestPartyUpdateWorkEffortWithInsertFromTemplate.START_DATE_2014, TestPartyUpdateWorkEffortWithInsertFromTemplate.COMPLETION_DATE_2014, 0, 2);
            Debug.log(" - ottavo lancio controllo GIOVANNI " + getPartyId(GIOVANNI) + " 2 schede che terminano nel 2014 ");
            checkWorkEffortListSize(3, TestPartyUpdateWorkEffortWithInsertFromTemplate.GIOVANNI, TestPartyUpdateWorkEffortWithInsertFromTemplate.OLD_UORG_DATE_2014, extraConditionAfter);
            Debug.log(" - ottavo lancio controllo GIOVANNI " + getPartyId(GIOVANNI) + " 1 schede che terminano dopo il 31/12 2014 TEMP1");
            checkWorkEffortListSize(1, TestPartyUpdateWorkEffortWithInsertFromTemplate.GIOVANNI, TestPartyUpdateWorkEffortWithInsertFromTemplate.COMPLETION_DATE_2014, extraConditionAfterTEMP1);
            Debug.log(" - ottavo lancio controllo GIOVANNA " + getPartyId(GIOVANNA) + " 2 schede che terminano nel 2014 ");
            checkWorkEffortListSize(3, TestPartyUpdateWorkEffortWithInsertFromTemplate.GIOVANNA, TestPartyUpdateWorkEffortWithInsertFromTemplate.OLD_UORG_DATE_2014, extraConditionAfter);
            
            Debug.log(" - nono lancio creazione massiva schede per " + TestPartyUpdateWorkEffortWithInsertFromTemplate.UORG_1 + SEP + TestPartyUpdateWorkEffortWithInsertFromTemplate.EMPL_POSITION_TYPE_ID_D);
            setContextAndRunInsertFromTemplate(TestPartyUpdateWorkEffortWithInsertFromTemplate.UORG_1, TestPartyUpdateWorkEffortWithInsertFromTemplate.ROLE_TYPE_ID_UOSET, TestPartyUpdateWorkEffortWithInsertFromTemplate.EMPL_POSITION_TYPE_ID_D, TestPartyUpdateWorkEffortWithInsertFromTemplate.START_DATE_2014, TestPartyUpdateWorkEffortWithInsertFromTemplate.COMPLETION_DATE_2014, 0, 3);
        } catch (Exception e) {
            Debug.logError(e, MessageUtil.getExceptionMessage(e), MODULE);
            assertTrue(false);
        }

    }

    /**
     * Close All Individual Records STEFANO = "65"
     */
    protected void updateStefano() throws GeneralException {
        Map<String, String> conditionParamTemplateId = UtilMisc.toMap(E.templateId.name(), TestPartyUpdateWorkEffortWithInsertFromTemplate.TEMPLATE_ID_FOR_ABCC);
        List<Map<String, Object>> listconditionTemplateId = addCondition(conditionParamTemplateId);
        workEffortPartyServices.closeAllIndividualRecords(getPartyId(TestPartyUpdateWorkEffortWithInsertFromTemplate.STEFANO), TestPartyUpdateWorkEffortWithInsertFromTemplate.FROM_DATE_2014, TestPartyUpdateWorkEffortWithInsertFromTemplate.THRU_DATE_2014, listconditionTemplateId);
        
    }
    
    /**
     * Close All Individual Records ROBERTO = "D010356I"
     */
    protected void updateRoberto() throws GeneralException {
        Map<String, String> condition = UtilMisc.toMap("WEM_EVAL_MANAGER", getPartyId(TestPartyUpdateWorkEffortWithInsertFromTemplate.VALUTATORE_1));
        List<Map<String, Object>> listcondition = addCondition(condition);
        workEffortPartyServices.closeAllIndividualRecords(getPartyId(TestPartyUpdateWorkEffortWithInsertFromTemplate.ROBERTO), TestPartyUpdateWorkEffortWithInsertFromTemplate.FROM_DATE_2014, TestPartyUpdateWorkEffortWithInsertFromTemplate.THRU_DATE_2014, listcondition);
        
    }

    /**
     * Close All Individual Records CATIA = "D857"
     */
    protected void updateCatia() throws GeneralException {
        Map<String, String> conditionParam = UtilMisc.toMap(E.orgUnitId.name(), getPartyId(TestPartyUpdateWorkEffortWithInsertFromTemplate.UORG_1));
        List<Map<String, Object>> listcondition = addCondition(conditionParam);
        workEffortPartyServices.closeAllIndividualRecords(getPartyId(TestPartyUpdateWorkEffortWithInsertFromTemplate.CATIA), TestPartyUpdateWorkEffortWithInsertFromTemplate.FROM_DATE_2014, TestPartyUpdateWorkEffortWithInsertFromTemplate.THRU_DATE_2014, listcondition);
    }
    
    /**
     * Update orgUnitId UORG_2 with UORG_1
     * @throws GenericEntityException 
     */
    protected void updateMonica() throws GenericEntityException {
        Debug.log(" - updateMonica ");
        List<EntityCondition> condition = FastList.newInstance();
        condition.add(EntityCondition.makeCondition(E.parentTypeId.name(), E.CTX_EP.name()));
        condition.add(EntityCondition.makeCondition(E.isTemplate.name(), E.N.name()));
        condition.add(EntityCondition.makeCondition(E.workEffortRevisionId.name(), null));
        condition.add(EntityCondition.makeCondition(E.estimatedCompletionDate.name(), EntityOperator.GREATER_THAN_EQUAL_TO, COMPLETION_DATE_2014));
        condition.add(EntityCondition.makeCondition(E.partyId.name(), MONICA));
        condition.add(EntityCondition.makeCondition(E.roleTypeId.name(), E.WEM_EVAL_IN_CHARGE.name()));

        List<GenericValue> list = delegator.findList(E.WorkEffortAndWorkEffortPartyAssView.name(), EntityCondition.makeCondition(condition), null, null, null, false);
        for (GenericValue we : list) {
            
            GenericValue workEffort = delegator.findOne(E.WorkEffort.name(),UtilMisc.toMap(E.workEffortId.name(), we.getString(E.workEffortId.name())), false);
            if (workEffort.getString(E.orgUnitId.name()).equals(UORG_2)) {
                workEffort.set(E.orgUnitId.name(), UORG_1);
                workEffort.store();
            }
        }
    }

    /**
     * Close All Individual Records GIOVANNI = "D101010"
     * @throws GeneralException 
     */
    protected void updateGiovanni() throws GeneralException {
        Debug.log(" - updateGiovanni ");
        Map<String, String> conditionParam = UtilMisc.toMap(E.orgUnitId.name(), getPartyId(TestPartyUpdateWorkEffortWithInsertFromTemplate.UORG_1));
        List<Map<String, Object>> listcondition = addCondition(conditionParam);
        workEffortPartyServices.closeAllIndividualRecords(getPartyId(TestPartyUpdateWorkEffortWithInsertFromTemplate.GIOVANNI), TestPartyUpdateWorkEffortWithInsertFromTemplate.FROM_DATE_2014, TestPartyUpdateWorkEffortWithInsertFromTemplate.THRU_DATE_2014, listcondition);
    }

    private void workEffortRootSnapshot() {
        Debug.log(" - Snapshot ");
        context.put(WorkEffortRootCopyService.DELETE_OLD_ROOTS, E.N.name());
        context.put(WorkEffortRootCopyService.SNAPSHOT, E.Y.name());
        context.put(WorkEffortRootCopyService.GL_ACCOUNT_CREATION, E.Y.name());
        context.put(WorkEffortRootCopyService.CHECK_EXISTING, E.N.name());
        context.put(WorkEffortRootCopyService.ORGANIZATION_PARTY_ID, "Company");
        
        context.put(E.snapShotDescription.name(), "Automatica");

        context.put(E.workEffortTypeIdFrom.name(), "VD-12");

        context.put(E.workEffortRevisionId.name(), "RVD-12");

        context.put(E.estimatedStartDateFrom.name(), new Timestamp(UtilDateTime.toDate(1, 1, 2014, 0, 0, 0).getTime()));
        context.put(E.estimatedCompletionDateFrom.name(), new Timestamp(UtilDateTime.toDate(12, 31, 2014, 0, 0, 0).getTime()));

        context.put(E.workEffortTypeIdTo.name(), "VD-12");

        context.put(E.estimatedStartDateTo.name(), new Timestamp(UtilDateTime.toDate(1, 1, 2014, 0, 0, 0).getTime()));
        context.put(E.estimatedCompletionDateTo.name(), new Timestamp(UtilDateTime.toDate(12, 31, 2014, 0, 0, 0).getTime()));

        try {
            Map<String, Object> result = WorkEffortRootCopyService.workEffortRootCopyService(dispatcher.getDispatchContext(), context);
            Debug.log(" - result " + result);
            assertTrue(ServiceUtil.isSuccess(result));
        } catch (Exception e) {
            Debug.logError(e, MessageUtil.getExceptionMessage(e), MODULE);
            assertTrue(false);
        }
    }
}

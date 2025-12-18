/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.util.TreeSet;
import javolution.util.FastList;
import org.ofbiz.entity.GenericEntity;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityFieldValue;
import org.ofbiz.entity.condition.EntityFunction;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.ObjectType;
import org.ofbiz.entity.model.ModelEntity;
import org.ofbiz.entity.model.ModelField;
import org.ofbiz.entity.model.ModelFieldType;
import org.ofbiz.entity.util.EntityFindOptions;
if (UtilValidate.isEmpty(context.autocompleteOptions)) {

    fieldValue = parameters[fieldName];
    
    // CUSTOMIZZAZIONE GZOOM: Intercetta richieste per WorkEffortAndWorkEffortPartyAssView e applica filtri per Valutatori
    // Leggiamo le variabili dalla sessione per determinare se l'utente è un Valutatore
    def session = request.getSession();
    def isEmplValutatore = session.getAttribute("isEmplValutatore");
    def evaluatedPartyIds = session.getAttribute("evaluatedPartyIds");
    def userPartyId = session.getAttribute("userPartyId");
    def isEmplValutatoreAdmin = session.getAttribute("isEmplValutatoreAdmin");
    
    Debug.log("FINDAUTOCOMPLETE_DEBUG: isEmplValutatore=" + isEmplValutatore + ", evaluatedPartyIds=" + evaluatedPartyIds + ", isEmplValutatoreAdmin=" + isEmplValutatoreAdmin + ", entityName=" + context.entityName);

    //MAPS S.p.A. 02.07.2009, Nel caso in cui si arrivi da una look-up in tabella di tipo multi il fieldName avr�
    //il suffisso _0_$itemIndex. Questo comporter� un arrore nella ricerca,non esistendo un campo di questo tipo nell'entit�.
    //Una volta recuperato il valore dai parametri, eleboro il field name eliminando il suffisso
    //Debug.log("*************************************** FindAutocompleteOption.groovy --> fieldName prima= " + fieldName);
    //fieldName = StringUtil.removeRegex(fieldName, "^(_o_\\d)");
    if (fieldName.indexOf(UtilHttp.MULTI_ROW_DELIMITER) != -1) {
        fieldName = fieldName.substring(0, fieldName.indexOf(UtilHttp.MULTI_ROW_DELIMITER));
    }

    //Debug.log("*************************************** FindAutocompleteOption.groovy --> fieldName dopo= " + fieldName);

    //Maps spa: 05.05.09, if i have entityKeyField parameter, correct field name where to look for is that.
    //I have to change name only after retrived value, because into parameter list updated value is into original field
    //NOTE : ONLY FOR LOOKUP
    if (org.ofbiz.base.util.UtilValidate.isNotEmpty(parameters["entityKeyField"])) {
        fieldName = parameters["entityKeyField"];
    }

    //Debug.log("*************************************** FindAutocompleteOption.groovy --> context.entityName = " + context.entityName);
    //entityName pu� arrivare o nella forma [entityname,entityName,......] o nella sola stringa....testo la possibilit� di una lista
    //di entityname, altrimenti ne considero uno solo ed in ogni caso lo trasformo in lista (eventulamente di un solo elemento)
    entityNameList = null;
    if (fieldValue) {
        try {
            entityNameList = StringUtil.toList(context.entityName);
        } catch (Exception e) {
            entityNameList = [context.entityName];
        }
        Debug.log("*************************************** FindAutocompleteOption.groovy --> entityNameList = " + entityNameList);

        if (UtilValidate.isNotEmpty(entityNameList)) {
            searchFields = null;
            constraintFields = null;
            selectFields = null;
            sortByFields = null;
            distincts = null;
            displayFields = null;
            if (UtilValidate.isNotEmpty(context.searchFields)) {
                searchFields = StringUtil.toList(context.searchFields, "\\;\\s");
            } else {
                searchFields = [fieldName];
            }
            if (UtilValidate.isNotEmpty(context.distincts)) {
                distincts = StringUtil.toList(context.distincts, "\\;\\s");
            }
            if (UtilValidate.isNotEmpty(context.selectFields))
                selectFields = StringUtil.toList(context.selectFields, "\\;\\s");
            if (UtilValidate.isNotEmpty(context.sortByFields))
                sortByFields = StringUtil.toList(context.sortByFields, "\\;\\s");
            
            if (UtilValidate.isNotEmpty(parameters.constraintFields)) {
                constraintFields = StringUtil.toList(parameters.constraintFields, "\\;\\s");
            }
            
            // CUSTOMIZZAZIONE GZOOM: Se l'utente è un Valutatore, modifica entityName e constraint per filtrare
            // solo le schede dei Valutati assegnati + le proprie schede come Valutato
            // GESTIONE TRE CASI:
            // 1. Admin (isEmplValutatoreAdmin=true): NON applica filtri, mostra TUTTO
            // 2. Valutatore con valutati (evaluatedPartyIds non vuoto): applica filtro su evaluatedPartyIds + userPartyId
            // 3. Valutatore senza valutati (evaluatedPartyIds vuoto o solo userPartyId): applica filtro solo su userPartyId
            if (isEmplValutatore && entityNameList) {
                // CASO 1: Admin con permission ADMINISTRATOR_VIEW
                if (isEmplValutatoreAdmin) {
                    Debug.log("FINDAUTOCOMPLETE_DEBUG: Rilevato utente Valutatore ADMIN - NESSUN FILTRO applicato");
                    // NON fare nulla, lascia passare la query senza filtri partyId
                } else if (evaluatedPartyIds) {
                    // CASO 2 e 3: Valutatore NON admin (con o senza valutati)
                    Debug.log("FINDAUTOCOMPLETE_DEBUG: Rilevato utente Valutatore NON-ADMIN con evaluatedPartyIds=" + evaluatedPartyIds);
                    Debug.log("FINDAUTOCOMPLETE_DEBUG: entityNameList PRIMA = " + entityNameList);
                    Debug.log("FINDAUTOCOMPLETE_DEBUG: constraintFields PRIMA = " + constraintFields);
                    Debug.log("FINDAUTOCOMPLETE_DEBUG: selectFields PRIMA = " + selectFields);
                    
                    // Aggiungi l'utente loggato alla lista degli ID da cercare
                    // Questo permette al Valutatore di vedere anche le sue schede come Valutato
                    if (userPartyId && !evaluatedPartyIds.contains(userPartyId)) {
                        evaluatedPartyIds = evaluatedPartyIds + "," + userPartyId;
                        Debug.log("FINDAUTOCOMPLETE_DEBUG: Aggiunto userPartyId alla lista: evaluatedPartyIds = " + evaluatedPartyIds);
                    }
                
                // Modifica entityNameList per usare WorkEffortAndWorkEffortPartyAssView invece di WorkEffortView
                def modifiedEntityNames = [];
                entityNameList.each { entityName ->
                    if (entityName == "WorkEffortView" || entityName == "WorkEffortAndWorkEffortPartyAssView") {
                        modifiedEntityNames.add("WorkEffortAndWorkEffortPartyAssView");
                        Debug.log("FINDAUTOCOMPLETE_DEBUG: Cambiato entityName da " + entityName + " a WorkEffortAndWorkEffortPartyAssView");
                    } else {
                        modifiedEntityNames.add(entityName);
                    }
                }
                entityNameList = modifiedEntityNames;
                
                // Rimuovi workEffortRevisionDescr dai selectFields perché non esiste in WorkEffortAndWorkEffortPartyAssView
                if (UtilValidate.isNotEmpty(selectFields)) {
                    def modifiedSelectFields = [];
                    selectFields.each { selectFieldList ->
                        if (selectFieldList) {
                            def fieldListString = selectFieldList.toString();
                            // Rimuovi workEffortRevisionDescr dalla lista
                            fieldListString = fieldListString.replace(", workEffortRevisionDescr", "");
                            fieldListString = fieldListString.replace("workEffortRevisionDescr, ", "");
                            fieldListString = fieldListString.replace("workEffortRevisionDescr", "");
                            modifiedSelectFields.add(fieldListString);
                        } else {
                            modifiedSelectFields.add(selectFieldList);
                        }
                    }
                    selectFields = modifiedSelectFields;
                    Debug.log("FINDAUTOCOMPLETE_DEBUG: selectFields DOPO rimozione workEffortRevisionDescr = " + selectFields);
                }
                
                // Modifica i constraint esistenti per aggiungere il filtro sui Valutati
                if (UtilValidate.isNotEmpty(constraintFields)) {
                    def modifiedConstraints = [];
                    constraintFields.each { constraint ->
                        if (constraint && constraint.startsWith("[[") && constraint.endsWith("]]")) {
                            // Rimuovi le doppie parentesi
                            def innerConstraint = constraint.substring(2, constraint.length() - 2);
                            
                            // Sostituisci i nomi dei campi per WorkEffortAndWorkEffortPartyAssView
                            innerConstraint = innerConstraint.replace("weContextId", "parentTypeId");
                            innerConstraint = innerConstraint.replace("isTemplate", "weIsTemplate");
                            innerConstraint = innerConstraint.replace("isRoot", "weIsRoot");
                            
                            // Aggiungi i filtri per Valutatore: partyId IN (valutati + userPartyId), roleTypeId=WEM_EVAL_IN_CHARGE
                            // Nota: userPartyId è stato già aggiunto a evaluatedPartyIds sopra, quindi ora include anche le schede proprie
                            def newConstraint = "[[" + innerConstraint + "]! [partyId| in| " + evaluatedPartyIds + "]! [roleTypeId| equals| WEM_EVAL_IN_CHARGE]]";
                            modifiedConstraints.add(newConstraint);
                            Debug.log("FINDAUTOCOMPLETE_DEBUG: Constraint modificato da: " + constraint + " a: " + newConstraint);
                        } else {
                            modifiedConstraints.add(constraint);
                        }
                    }
                    constraintFields = modifiedConstraints;
                }
                
                Debug.log("FINDAUTOCOMPLETE_DEBUG: entityNameList DOPO = " + entityNameList);
                Debug.log("FINDAUTOCOMPLETE_DEBUG: constraintFields DOPO = " + constraintFields);
                } // Fine else if (evaluatedPartyIds)
            } // Fine if (isEmplValutatore && entityNameList)
            
			if (UtilValidate.isNotEmpty(parameters.displayFields)) {
				displayFields = StringUtil.toList(parameters.displayFields, "\\;\\s");
			}
            //Debug.log("*************************************** FindAutocompleteOption.groovy --> constraintFields = " + constraintFields);

            //Per ogni entity recupero le informazioni sul searchField, sortByField e constraint
            entityNameList.eachWithIndex { it,i ->
                orExprs = [];
                constraintExpr = [];

                entityName = it;
                ModelEntity modelEntity = delegator.getModelEntity(entityName);
                
				displayField = null;
				if (UtilValidate.isNotEmpty(displayFields) && displayFields.size() > i) {
					try {
						displayField = StringUtil.toList(displayFields[i]);
					} catch (Exception e) {
						displayField = [displayFields[i]];
					}
				}
				
                if (UtilValidate.isNotEmpty(searchFields) && searchFields.size() > i) {
                    try {
                        searchField = StringUtil.toList(searchFields[i]);
                    } catch (Exception e) {
                        searchField = [searchFields[i]];
                    }

                    //Debug.log("************************************** searchField='" + searchField + "'")

					ModelField modelField;
                    String modelFieldType;
                    String fieldJavaType;
					fieldValueList = [];
					
					//Le droplist hanno la possibilita di visualizzare piu descrizioni separate da - 
					if(UtilValidate.isNotEmpty(displayField) && displayField.size() > 1) {
						if(fieldValue.endsWith("-")) {
							delim = " -";
						}
						else {
							delim = " - ";
						}
						fieldValueList = StringUtil.split(fieldValue, delim);
					}
					else {
						fieldValueList[0] = fieldValue;
					}
					
                    for(fieldName in searchField) {
                    	if (UtilValidate.isNotEmpty(fieldName) && UtilValidate.isNotEmpty(modelEntity.getField(fieldName))) {
		                    modelFieldType  = modelEntity.getField(fieldName).getType();
	                    	fieldJavaType = delegator.getEntityFieldType(modelEntity, modelFieldType).getJavaType();
	                    	// Debug.log(" - fieldName " + fieldName + " modelFieldType" + modelFieldType + " - fieldJavaType " + fieldJavaType);
	                    	if ("String".equals(fieldJavaType)) {
								for(fieldValueItem in fieldValueList) {
		                        	orExprs.add(EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue(fieldName)),
		                                	EntityOperator.LIKE, "%" + fieldValueItem.toUpperCase() + "%"));
								}
	                        }
                        }
                    }
                }
                sortByField = null;
                if (UtilValidate.isNotEmpty(sortByFields) && sortByFields.size() > i) {
                    try {
                        sortByField = StringUtil.toList(sortByFields[i]);
                    } catch (Exception e) {
                        sortByField = [sortByFields[i]];
                    }
                }
                selectField = null;
                if (UtilValidate.isNotEmpty(selectFields) && selectFields.size() > i) {
                    try {
                        selectField = StringUtil.toList(selectFields[i]);
                    } catch (Exception e) {
                        selectField = [selectFields[i]];
                    }
                }
                distinct = false;
                if (UtilValidate.isNotEmpty(distincts) && distincts.size() > i) {
                    distinct = ("Y" == distincts[i]);
                }
				
                //Debug.log("************************************** entityName='" + entityName + "'")
                //Debug.log("************************************** selectField='" + selectField + "'")
                //Debug.log("************************************** sortByField='" + sortByField + "'")
				//Debug.log("************************************** distinct='" + distinct + "'")
                
                if (UtilValidate.isNotEmpty(constraintFields) && constraintFields.size() > i) {
                    try {
                        constraintField = StringUtil.toList(constraintFields[i], "\\!\\s");
                    } catch (Exception e) {
                        constraintField = [constraintFields[i]];
                    }
                    // Debug.log("*************************************** FindAutocompleteOption.groovy --> constraintField = " + constraintField);

                    //Loop over constraintList
                    for (constraint in constraintField) {
                        //Debug.log("************************************** constraint='" + constraint + "'")
                    	
//                    	Debug.log("************************************** constraint=" + constraint)
                        if (UtilValidate.isNotEmpty(constraint)) {
                            if (!constraint.startsWith('['))
                                constraint = '[' + constraint;
                            if (!constraint.endsWith(']'))
                                constraint += ']';
                            parts = StringUtil.toList(constraint,"\\|\\s");
                            // Debug.log("************************************** parts=" + parts)
                            if (parts.size() == 2) {
                            	if (parts[1].indexOf('inOrNull') >= 0) {
                            		constraintExpr.add(EntityCondition.makeCondition(EntityFieldValue.makeFieldValue(parts[0]),EntityOperator.EQUALS,
                                            GenericEntity.NULL_FIELD));
                            	} else {
                                constraintExpr.add(EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue(parts[0])),
                                        EntityOperator.lookup(parts[1]), null));
                            	}
                            } else if (parts.size() == 3) {
                                // Debug.log("************************************** parts[0]=" + parts[0])
                                // Debug.log("************************************** parts[1]=" + parts[1])
                                // Debug.log("************************************** parts[2]=" + parts[2])

                                // Debug.log("************************************** operator=" + EntityOperator.lookup(parts[1]))
								
                                def fieldDef = modelEntity.getField(parts[0]);
                                if (fieldDef == null) {
                                    Debug.logWarning("Field '" + parts[0] + "' not found in entity '" + modelEntity.getEntityName() + "'", "FindAutocompleteOptions");
                                    continue;
                                }
                                String model0FieldType  = fieldDef.getType();
                                String parts0JavaType = delegator.getEntityFieldType(modelEntity, model0FieldType).getJavaType();
                                if ("null".equals(parts[2]) || "[null-field]".equals(parts[2])) {
                                    // Debug.log("************************************** parts[2] null");
                                    // CUSTOMIZZAZIONE GZOOM: Non applicare UPPER() su campi Timestamp
                                    if ("java.sql.Timestamp".equals(parts0JavaType)) {
                                        constraintExpr.add(EntityCondition.makeCondition(EntityFieldValue.makeFieldValue(parts[0]),
                                                EntityOperator.lookup(parts[1]), GenericEntity.NULL_FIELD));
                                    } else {
                                        constraintExpr.add(EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue(parts[0])),
                                                EntityOperator.lookup(parts[1]), GenericEntity.NULL_FIELD));
                                    }
                                } else if (EntityOperator.BETWEEN.equals(EntityOperator.lookup(parts[1])) || EntityOperator.IN.equals(EntityOperator.lookup(parts[1])) || EntityOperator.NOT_IN.equals(EntityOperator.lookup(parts[1]))) {
                                    // Debug.log("************************************** parts[1] BETWEEN or IN or NOT_IN ");
                                    valueList = StringUtil.split(parts[2], ",");
                                    // Debug.log("************************************** valueList " + valueList);
                                    if (UtilValidate.isNotEmpty(valueList)) {
                                        if (!("String".equals(parts0JavaType))) {
                                            constraintExpr.add(EntityCondition.makeCondition(EntityFieldValue.makeFieldValue(parts[0]),
                                                    EntityOperator.lookup(parts[1]), valueList));
                                            
                                        } else {
                                            constraintExpr.add(EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue(parts[0])),
                                                    EntityOperator.lookup(parts[1]), valueList));
                                            
                                        }
                                    }
                                } else {
                                    if (!("String".equals(parts0JavaType))) {
                                        if ("java.sql.Timestamp".equals(parts0JavaType)) {
                                            valueDate = ObjectType.simpleTypeConvert(parts[2], "Timestamp", null, locale);
                                            constraintExpr.add(EntityCondition.makeCondition(EntityFieldValue.makeFieldValue(parts[0]),
                                                    EntityOperator.lookup(parts[1]), valueDate));
                                        } else {
                                            constraintExpr.add(EntityCondition.makeCondition(EntityFieldValue.makeFieldValue(parts[0]),
                                                    EntityOperator.lookup(parts[1]), parts[2]));
                                        }
                                    } else {
                                        //quando siamo nel caso equals non viene inserito UPPER
                                        if("equals".equals(parts[1])){
                                            constraintExpr.add(EntityCondition.makeCondition(EntityFieldValue.makeFieldValue(parts[0]),
                                                    EntityOperator.lookup(parts[1]), parts[2]));
                                        }else if ("inOrNull".equals(parts[1])) {
                                        	constraintInOrNull = [];
                                        	constraintInOrNull.add(EntityCondition.makeCondition(EntityFieldValue.makeFieldValue(parts[0]),EntityOperator.EQUALS,
                                                    GenericEntity.NULL_FIELD));                                       	
                                        	valueList = StringUtil.split(parts[2], ",");
                                            if (UtilValidate.isNotEmpty(valueList)) {
                                            	constraintInOrNull.add(EntityCondition.makeCondition(EntityFieldValue.makeFieldValue(parts[0]),
                                            			EntityOperator.IN, valueList));
                                            }
                                        	constraintExpr.add(EntityCondition.makeCondition(constraintInOrNull, EntityOperator.OR));                                    	
                                        }else{
                                            constraintExpr.add(EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue(parts[0])),
                                                EntityOperator.lookup(parts[1]), parts[2]));
                                        }
                                    }
                                }

                            } else if (parts.size() > 3) {
                                constraintExprOr = [];
                                for (int index=2; index<parts.size(); index++) {
                                    if ("null".equals(parts[index]) || "[null-field]".equals(parts[index])) {
                                        constraintExprOr.add(EntityCondition.makeCondition(EntityFieldValue.makeFieldValue(parts[0]),
                                                EntityOperator.lookup(parts[1]), GenericEntity.NULL_FIELD));
                                    } else {
                                        constraintExprOr.add(EntityCondition.makeCondition(EntityFieldValue.makeFieldValue(parts[0]),
                                                EntityOperator.lookup(parts[1]), parts[index]));
                                    }
                                }
                                constraintExpr.add(EntityCondition.makeCondition(constraintExprOr, EntityOperator.OR));                               
                            }
                        }
                    } //end loop
                }
                if (UtilValidate.isNotEmpty(orExprs)) {
                    
                    entityConditionList = EntityCondition.makeCondition(orExprs, EntityOperator.OR);

                    //Added constraint
                    if (constraintExpr) {
                        entityConditionList = EntityCondition.makeCondition(entityConditionList, EntityOperator.AND, EntityCondition.makeCondition(constraintExpr, EntityOperator.AND));
                    }
                    
                    findOpts = new EntityFindOptions();
                    findOpts.setDistinct(distinct);
                    Debug.log("************************************** entityConditionList=" + entityConditionList)
                    autocompleteOptions = delegator.findList(entityName, entityConditionList, selectField as Set, sortByField, findOpts, false);
                    
                    // DEBUG per Valutatori: log risultati query
                    if (isEmplValutatore && entityName == "WorkEffortAndWorkEffortPartyAssView") {
                        Debug.log("FINDAUTOCOMPLETE_DEBUG: Query eseguita su " + entityName);
                        Debug.log("FINDAUTOCOMPLETE_DEBUG: Numero risultati trovati: " + (autocompleteOptions ? autocompleteOptions.size() : 0));
                        if (autocompleteOptions && autocompleteOptions.size() > 0) {
                            autocompleteOptions.each { result ->
                                Debug.log("FINDAUTOCOMPLETE_DEBUG: Trovato workEffortId=" + result.workEffortId + ", workEffortName=" + result.workEffortName);
                            }
                        } else {
                            // Query di debug: verifichiamo TUTTI i roleTypeId per i Valutati per capire quale usare
                            def debugConditions = [];
                            debugConditions.add(EntityCondition.makeCondition("partyId", EntityOperator.IN, ["10224", "10225"]));
                            def debugEntityCondition = EntityCondition.makeCondition(debugConditions, EntityOperator.AND);
                            def debugResults = delegator.findList("WorkEffortAndWorkEffortPartyAssView", debugEntityCondition, null, null, null, false);
                            Debug.log("FINDAUTOCOMPLETE_DEBUG: Query DEBUG TUTTI i roleTypeId - Trovate " + (debugResults ? debugResults.size() : 0) + " schede totali per i Valutati 10224,10225");
                            if (debugResults && debugResults.size() > 0) {
                                def count = 0;
                                for (result in debugResults) {
                                    if (count >= 10) break; // Limita a 10 risultati per evitare troppi log
                                    Debug.log("FINDAUTOCOMPLETE_DEBUG: workEffortId=" + result.workEffortId + 
                                              ", workEffortName=" + result.workEffortName +
                                              ", partyId=" + result.partyId + 
                                              ", roleTypeId=" + result.roleTypeId + 
                                              ", thruDate=" + result.thruDate + 
                                              ", workEffortTypeId=" + result.workEffortTypeId + 
                                              ", parentTypeId=" + result.parentTypeId);
                                    count++;
                                }
                            }
                        }
                    }

                    if (UtilValidate.isEmpty(context.autocompleteOptions))
                        context.autocompleteOptions = autocompleteOptions;
                    else
                        context.autocompleteOptions.addAll(autocompleteOptions);
                }
            }
        }
    }
}


//import org.ofbiz.base.util.StringUtil;
//import org.ofbiz.entity.util.EntityFindOptions;
//import org.ofbiz.entity.condition.EntityCondition;
//import org.ofbiz.entity.condition.EntityConditionList;
//import org.ofbiz.entity.condition.EntityExpr;
//import org.ofbiz.entity.condition.EntityFieldValue;
//import org.ofbiz.entity.condition.EntityFunction;
//import org.ofbiz.entity.condition.EntityOperator;
//
//andExprs = [];
//entityName = context.entityName;
//searchFields = context.searchFields;
//displayFields = context.displayFields ?: searchFields;
//searchValueFieldName = parameters.searchValueField;
//if (searchValueFieldName) fieldValue = parameters.get(searchValueFieldName);
//searchType = context.searchType;
//
//if (searchFields && fieldValue) {
//	searchFieldsList = StringUtil.toList(searchFields);
//	displayFieldsSet = StringUtil.toSet(displayFields);
//	returnField = searchFieldsList[0]; //default to first element of searchFields
//	displayFieldsSet.add(returnField); //add it to select fields, in case it is missing
//	context.returnField = returnField;
//	context.displayFieldsSet = displayFieldsSet;
//	if ("STARTS_WITH".equals(searchType)) {
//		searchValue = fieldValue.toUpperCase() + "%";
//	} else if ("EQUALS".equals(searchType)) {
//		searchValue = fieldValue;
//	} else {//default is CONTAINS
//		searchValue = "%" + fieldValue.toUpperCase() + "%";
//	}
//	searchFieldsList.each { fieldName ->
//		if ("EQUALS".equals(searchType)) {
//			andExprs.add(EntityCondition.makeCondition(EntityFieldValue.makeFieldValue(returnField), EntityOperator.EQUALS, searchValue));    
//			return;//in case of EQUALS, we search only a match for the returned field
//		} else {
//			andExprs.add(EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue(fieldName)), EntityOperator.LIKE, searchValue));
//		}        
//	}
//}
//
//if (andExprs && entityName && displayFieldsSet) {
//	entityConditionList = EntityCondition.makeCondition(andExprs, EntityOperator.OR);
//	
//	//if there is an extra condition, add it to main condition
//	if (context.andCondition && context.andCondition  instanceof EntityCondition) {
//		entityConditionList = EntityCondition.makeCondition(context.andCondition, entityConditionList);
//	}
//	
//	Integer autocompleterViewSize = Integer.valueOf(context.autocompleterViewSize ?: 10);
//	EntityFindOptions findOptions = new EntityFindOptions();
//	findOptions.setMaxRows(autocompleterViewSize);
//	autocompleteOptions = delegator.findList(entityName, entityConditionList, displayFieldsSet, StringUtil.toList(displayFields), findOptions, false);
//	if (autocompleteOptions) {
//		context.autocompleteOptions = autocompleteOptions;
//	}
//}

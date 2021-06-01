package com.mapsengineering.base.standardimport.common;

import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.GeneralException;
import org.ofbiz.entity.GenericValue;


/**
 * Created with IntelliJ IDEA.
 * User: rime
 * Date: 11/25/13
 * Time: 4:52 PM
 */
public interface FieldConfig {

    /**
     * Recupera la mappa dei valori di default per il sottosistema dataSourceId ed interfaccia standardInterface
     * 
     * @param standardInterface
     * @param dataSource
     * @param interfaceSeq
     * @return
     */
    Map<String, Map<String, String>> getMapValue(String standardInterface, String dataSource, Long interfaceSeq);

    /**
     * Recupera la mappa di decodifica delle colonne per il sottosistema dataSourceId ed interfaccia standardInterface
     * 
     * @param standardInterface
     * @param dataSource
     * @param interfaceSeq
     * @return
     */
    Map<String, String> getMapField(String standardInterface, String dataSource, Long interfaceSeq);
    
    /**
     * Crea la chiave da utilizzare per recuperare i valori nelle mappe
     * 
     * @param dataSourceId
     * @param fieldName
     * @return
     */
    String getKey(String dataSourceId, String fieldName);
    
    /**
     * crea la chiave con dataSourceId, count e externalField
     * 
     * @param dataSourceId
     * @param count
     * @param externalFieldName
     * @return
     */
    String getKey(String dataSourceId, int count, String externalFieldName);

    /**
     * Recupera il record in base al sottosistema dataSourceId
     *
     * @param dataSourceId
     * @return
     * @throws GeneralException 
     */
    GenericValue getStfcDataSource(String dataSourceId) throws GeneralException;

    /**
     * Recupera la lista di record in base al sottosistema dataSourceId, con interfaceSeq = 1, senza considerare entityName
     *
     * @param dataSourceId
     * @return
     * @throws GeneralException 
     */
    List<GenericValue> getStfcDataSourceList(String dataSourceId) throws GeneralException;

    /**
     * Recupera il valore di default del dataSource, in base all' interfaccia standard, con il valore di default (dataSourceId = "DEFAULT") e interfaceSeq = 1
     *
     * @param entityName
     * @return
     * @throws GeneralException 
     */
    GenericValue getStfcDataSourceDefault(String entityName) throws GeneralException;

    /**
     * fa la query distinct su StandardImportFieldConfig, per la gestione del multitracciato
     * @param dataSource
     * @return standardInterface e interfaceSeq
     * @throws GeneralException
     */
    List<GenericValue> getStandardImportFieldConfigItems(String dataSourceId) throws GeneralException;

    /**
     * Recupera il dataSource
     *
     * @param entityName
     * @return
     * @throws GeneralException 
     */
    GenericValue getDataSource(String datasourceId) throws GeneralException;

}

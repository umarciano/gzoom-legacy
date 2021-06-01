package com.mapsengineering.base.client;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.GroovyUtil;
import org.ofbiz.base.util.ObjectType;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntity;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.model.ModelEntity;
import org.ofbiz.entity.model.ModelKeyMap;
import org.ofbiz.entity.model.ModelRelation;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.GenericDispatcher;

import com.mapsengineering.base.util.FindWorker;
import com.mapsengineering.base.util.ValidationUtil;

public class TreeWorker {

	/**
	 * Lists separator
	 */
	public static final String LIST_SEPARATOR = "|";
	/**
	 * Item separator
	 */
	public static final String ITEM_SEPARATOR = ";";

	/**
	 * Inner class for key fields
	 * @author sandro
	 */
	private class KeyField {
		private String singleField = null;
		private String fromField = null;
		private String toField = null;
		private boolean single = true;

		public KeyField(String initString) {
			if (initString.indexOf(ITEM_SEPARATOR) != -1) {
				this.single = false;
				List<String> fields = StringUtil.split(initString, ITEM_SEPARATOR);
				this.fromField = fields.get(0);
				this.toField = fields.get(1);
			} else {
				this.single = true;
				this.singleField = initString;
			}
		}

		public boolean isSingle() {
			return this.single;
		}

		public String getSingleField() {
			return singleField;
		}

		public String getFromField() {
			return fromField;
		}

		public String getToField() {
			return toField;
		}
	}

	/**
	 * Key field Id
	 */
	public static final String KEY_FIELDS = "keyFields";
	/**
	 * Entity parent field Id
	 */
	public static final String PARENT_REL_KEY_FIELDS = "parentRelKeyFields";
	/**
	 * Values got from entity
	 */
	public static final String VALUE_LIST = "treeViewValueList";
	/**
	 * Values for root positioning
	 */
	public static final String ROOT_VALUES = "rootValues";
	/**
	 * Order by field list
	 */
	public static final String ORDER_BY_FIELDS = "orderByFields";
	/**
	 * Entity name Treeview specific
	 */
	public static final String TREEVIEW_ENTITYNAME = "treeViewEntityName";
	/**
	 * Entity name Treeview specific
	 */
	public static final String GENERATE_NODE_FROM_PRIMARY = "generateNodeFromEntityName";
	public static final String PRIMARY_ENTITYNAME = "nodeEntityName";
	public static final String RELATION_TITLE = "relationTitle";
	/**
	 * Force to have unique root
	 */
	public static final String FORCE_UNIQUE_ROOT = "forceUniqueRoot";
	/**
	 * Elimino il click sulla radice unica
	 */
	public static final String UNIQUE_ROOT_NOT_MANAGED = "uniqueRootNotManaged";
	/**
	 * Try toset selected item
	 */
	public static final String SELECTED_ID = "selectedId";
	/**
	 * Script for custom node search
	 */
	public static final String CUSTOM_FIND_SCRIPT_LOCATION = "customFindScriptLocation";
	/**
	 * Set filter or not
	 */
	public static final String FILTER_BY_DATE = "filterByDate";
	
	/**
	 * Usate per filtrare l'albero con le date in ingresso
	 */
	public static final String DATE_FILTER = "dateFilter";
	
	/**
	 * Max leaf of each tree
	 */
	public static final int MAX_LEAFS = 20;
	/**
	 * Flag indicates if a node is leaf
	 */
	public static final String IS_LEAF = "IS_LEAF";

	public static final String ID_MAP = "idMap";

	/**
	 * contiene il nome del file .groovy che si vuole caricare
	 */
	public static final String LOAD_FILE_KEY_MAP = "loadFileKeyMap";

	//Bug 3506
	public static final String SELECTED_NODE = "selectedNode";
	public static final String ALL_NODE = "allNode";
	public static final String EXTRA_PARAMETERS = "extraParameters";

	private Map<String, Object> parameters;
	private Map<String, Object> treeviewCreateSelectedMap = FastMap.newInstance();
	private GenericDispatcher dispatcher;
	private Locale locale;
	//support list for retrive nodes
	private List<Map<String, Object>> treeList;
	private String entityName;
	private String primaryEntityName;
	private String relationTitle;
	private String generateNodeFromEntityName;
	private List<KeyField> keyFields;
	private List<String> orderByFields;
	private Map<String, Object> rootValues;
	private List<KeyField> parentRelKeyFields;
	private Map<String, Object> extraParameters;
	private boolean forceUniqueRoot;
	private boolean uniqueRootNotManaged;
	private String selectedId;
	private String customFindScriptLocation;
	private String filterByDate;
	private Map<String, Object> dateFilter;
	private int rootCounter = 0;
	private int childCounter = 0;
	private int recursionCounter = 0;

	private String loadFileKeyMap;
    private TimeZone timeZone;

	/**
	 * Execs perform find 
	 * @param inputFields
	 * @return result map
	 * @throws GeneralException 
	 */
	@SuppressWarnings("unchecked")
	private Map<String, Object> performFind(Map<String, Object> inputFields) throws GeneralException{

		Map<String, Object> result = FastMap.newInstance();

		List<GenericValue> foundList = null;
		if (UtilValidate.isNotEmpty(customFindScriptLocation)) {
			//Eseguo metodo custom
			Map<String, Object> context = FastMap.newInstance();
			context.put("parameters", parameters);
			context.put("locale", locale);
			context.put("timeZone", timeZone);
			context.put("dispatcher", dispatcher);
			context.put("delegator", dispatcher.getDelegator());
			context.put("inputFields", inputFields);
			context.put("orderByFields", orderByFields);
			//Attendo in output un parametro "foundList" - list di generic value
			GroovyUtil.runScriptAtLocation(customFindScriptLocation, context);

			if (context.containsKey("foundList")) {
				foundList = (List<GenericValue>)context.get("foundList");
			} else {
				Debug.log("Non trovati valori durante esecuzione script custom di ricerca");
				foundList = FastList.newInstance();
			}
		} else {
			//Perform find standard
			Delegator delegator = dispatcher.getDelegator();
			if (UtilValidate.isNotEmpty(orderByFields)) {
				foundList = delegator.findByAnd(entityName, inputFields, orderByFields);
			} else {
				foundList = delegator.findByAnd(entityName, inputFields);
			}
		}
		//filter if flag has been set
		if ("Y".equalsIgnoreCase(filterByDate)) {
			foundList = EntityUtil.filterByDate(foundList);
		}
		//Filtro per le date che sono state inserite
		if (UtilValidate.isNotEmpty(dateFilter)) {
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy"); 
			try {
				Timestamp fromDate = null;
				if(UtilValidate.isNotEmpty((String)dateFilter.get("fromDate"))){
					Date date = sdf.parse((String)dateFilter.get("fromDate"));
					fromDate = new Timestamp(date.getTime());
				}
				
				Timestamp thruDate = null;
				if(UtilValidate.isNotEmpty((String)dateFilter.get("thruDate"))){
					Date date;
					date = sdf.parse((String)dateFilter.get("thruDate"));
					thruDate = new Timestamp(date.getTime());					
				}
				
				foundList = FindWorker.filterByDate(foundList, fromDate, thruDate, null, null);
			
			} catch (ParseException e) {
				throw new GeneralException(e.getMessage());
			} 
		}
		
		result.put(TreeWorker.VALUE_LIST, foundList);


		return result;
	}

	/**
	 * Convert parameters in List
	 * @param parameter
	 * @return List
	 */
	private List<String> expandParameter(String parameter, String separator) {
		List<String> res = FastList.newInstance();

		if (UtilValidate.isNotEmpty(parameter)) {

			if (parameter.indexOf(separator) != -1) {
				res = StringUtil.split(parameter, separator);
			} else {
				res.add(parameter);
			}
		}
		return res;
	}
	
	/**
	 * mette in una mappa gli extraParameters per affinare ulteriormente la ricerca
	 * @return
	 */
	private Map<String, Object> getExtraParameters() {
		Map<String, Object> extraParametersMap = FastMap.newInstance();
		List<String> extraParameters = expandParameter((String)parameters.get(EXTRA_PARAMETERS), LIST_SEPARATOR);
		if (UtilValidate.isNotEmpty(extraParameters)) {
			for (String extraParameter : extraParameters) {
				List<String> extraParamList = StringUtil.split(extraParameter, ";");
				if (UtilValidate.isNotEmpty(extraParamList) && extraParamList.size() > 1) {
					extraParametersMap.put(extraParamList.get(0), extraParamList.get(1));
				}
			}
		}
		return extraParametersMap;
	}

	/**
	 * Constructor
	 * @param parameters
	 * @param dispatcher
	 * @param locale
	 */
	public TreeWorker(Map<String, Object> params, GenericDispatcher dispatcher, boolean createNewNode) {
		//Make a safe copy of parameters
		this.parameters = FastMap.newInstance();
		this.parameters.putAll(params);
		this.dispatcher = dispatcher;
		this.locale = UtilMisc.ensureLocale(params.get("locale"));
		getTimeZone(params);
		treeList = FastList.newInstance();
		orderByFields = FastList.newInstance();
		rootValues = FastMap.newInstance();
		extraParameters = getExtraParameters();

		//
		//Get parameters for all methods
		//
		//Entity key fields list 
		keyFields = FastList.newInstance();
		List<String> keyFieldPairs = expandParameter((String)parameters.get(KEY_FIELDS), LIST_SEPARATOR);
		for (String keyFieldPair : keyFieldPairs) {
			KeyField pair = new KeyField(keyFieldPair);
			keyFields.add(pair);
		}

		//Relation parent-child field list
		parentRelKeyFields = FastList.newInstance();
		List<String> relationPairs = expandParameter((String)parameters.get(PARENT_REL_KEY_FIELDS), LIST_SEPARATOR);
		for (String relationPair : relationPairs) {
			parentRelKeyFields.add(new KeyField(relationPair));
		}

		//Order By
		orderByFields = expandParameter((String)parameters.get(ORDER_BY_FIELDS), LIST_SEPARATOR);
		//starting key list
		List<String> rootValuesList = expandParameter((String)parameters.get(ROOT_VALUES), LIST_SEPARATOR);
		//Every item has to be a | separated list of pairs fieldname;value
		for (String item : rootValuesList) {
			List<String> itemList = getItemList(item);
			if (itemList.size() < 2) {
				continue;
			}
			if("[null-field]".equals(itemList.get(1))) {
				rootValues.put(itemList.get(0), GenericEntity.NULL_FIELD);
			}
			else {
				rootValues.put(itemList.get(0), itemList.get(1));
			}
		}

		//Get parameters for all methods
		//(using remove to in order to clean map from parameters that aren't useful for performFind)
		entityName = (String)parameters.get(TREEVIEW_ENTITYNAME);
		primaryEntityName = (String)parameters.get(PRIMARY_ENTITYNAME);
		relationTitle = (String)parameters.get(RELATION_TITLE);
		generateNodeFromEntityName = (String)parameters.get(GENERATE_NODE_FROM_PRIMARY);

		loadFileKeyMap = (String)parameters.get(LOAD_FILE_KEY_MAP);

		//Usa la root anche come primo child
		forceUniqueRoot = ("Y".equalsIgnoreCase((String)parameters.get(FORCE_UNIQUE_ROOT)));
		//Evita il click sulla radice
		uniqueRootNotManaged = ("Y".equalsIgnoreCase((String)parameters.get(UNIQUE_ROOT_NOT_MANAGED)));
		//selezione in base alle chiavi
		if (createNewNode) {
			Map<String, Object> idMap = UtilGenerics.checkMap(parameters.get(ID_MAP));

			Map<String, Object> contextMap = new HashMap<String, Object>();
			contextMap.putAll(parameters);
			if (UtilValidate.isNotEmpty(idMap)) {
				contextMap.putAll(idMap);
			}

			if ("true".equals(generateNodeFromEntityName)) {
				try {
					selectedId = generateNodeId(entityName, primaryEntityName, relationTitle, "child", idMap, loadFileKeyMap);
					//bug GN-117
					parameters.put(SELECTED_ID, selectedId);
				} catch (GeneralException e) {
					Debug.logError(e, null);
				}
			} else {
				String treeviewAlternateSelectedKey = UtilGenerics.cast(parameters.get("treeviewAlternateSelectedKey"));
				if(UtilValidate.isNotEmpty(treeviewAlternateSelectedKey)) {
					String altValue = (String)parameters.get(treeviewAlternateSelectedKey);
					if (altValue!=null) {
						treeviewCreateSelectedMap.clear();
						treeviewCreateSelectedMap.put(treeviewAlternateSelectedKey, altValue);
					}
				}
				//Genero mappa con chiave sulla base del treeViewEntityName
				else if (UtilValidate.isNotEmpty(entityName)) {
					ModelEntity modelEntity = dispatcher.getDelegator().getModelEntity(entityName);
					if (UtilValidate.isNotEmpty(modelEntity)) {
						List<String> pkFieldsName = modelEntity.getPkFieldNames();
						for (String pkFieldName : pkFieldsName) {
							if (idMap.containsKey(pkFieldName)) {
								treeviewCreateSelectedMap.put(pkFieldName, contextMap.get(pkFieldName));
							}
						}
					}
				}

			}
		} else {
			selectedId = (String)parameters.get(SELECTED_ID);
		}		
		
		

		//Script custom
		customFindScriptLocation = (String)parameters.get(CUSTOM_FIND_SCRIPT_LOCATION);
		
		//Flag filter or not
		filterByDate = (String)parameters.get(FILTER_BY_DATE);
		
		//Prendo se ci sono le date per filtrare l'albero
		dateFilter =  FastMap.newInstance();
		List<String> dateFilterList = expandParameter((String)parameters.get(DATE_FILTER), LIST_SEPARATOR);
		
		GregorianCalendar now = new GregorianCalendar();		
		Timestamp timestamp = new Timestamp(now.getTimeInMillis());
        Date date = new Date(timestamp.getTime());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String nowString = simpleDateFormat.format(date).toString();
        
		//Every item has to be a | separated list of pairs fieldname;value
		for (String item : dateFilterList) {
			List<String> itemList = StringUtil.split(item, ITEM_SEPARATOR);
			
			if(itemList.size() != 1 && itemList.size() != 2){
				continue;
			}
			
			if (itemList.size() == 1) {
				dateFilter.put(itemList.get(0), nowString);
				
			}else{
				if("[null-field]".equals(itemList.get(1))) {
					//prendo la data di oggi
					dateFilter.put(itemList.get(0), nowString);
				}
				else {
					dateFilter.put(itemList.get(0), itemList.get(1));
				}
			}
		}
		
		
		
	}
	
	private List<String> getItemList(String str) {
		if(UtilValidate.isEmpty(str)) {
			return new ArrayList<String>();
		}
		int i = str.indexOf(ITEM_SEPARATOR);
		List<String> itemList = new ArrayList<String>();
		if (i >= 0) {
			itemList.add(str.substring(0, i));
			itemList.add(str.substring(i+1, str.length()));
		} else {
			itemList.add(str);
		}
		return itemList;
	}

	private void getTimeZone(Map<String, Object> params) {
	    if(UtilValidate.isNotEmpty(params) && UtilValidate.isNotEmpty(params.get("timeZone"))) {
            this.timeZone = (TimeZone) params.get("timeZone");
        } else {
            this.timeZone = TimeZone.getDefault();
        }
    }

    /**
	 * Find entire tree
	 * @param parameters
	 * @param dispatcher
	 * @param locale
	 * @return return values (List<Map<String, Object>>)
	 * @throws GeneralException 
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> findTreeList() throws GeneralException {
		//Return map
		Map<String, Object> res = FastMap.newInstance();

		//Try to find all root
		res = findRoots();
		List<Map<String, Object>> rootList = (List<Map<String, Object>>)res.get(TreeWorker.VALUE_LIST);
		Map<String, Object> allNodeMap = UtilGenerics.checkMap(res.get(TreeWorker.ALL_NODE));

		//Loop over roots
		if (UtilValidate.isNotEmpty(rootList)) {
			for (Map<String, Object> root : rootList) {

				//first: add this root to return value list
				treeList.add(root);

				//last: get childs
				res = findChilds(root);

				Map<String, Object> childAllNodeMap = UtilGenerics.checkMap(res.get(TreeWorker.ALL_NODE));
				if (UtilValidate.isNotEmpty(childAllNodeMap)) {
					if (allNodeMap != null) {
						allNodeMap.putAll(childAllNodeMap);
					} else {
						allNodeMap = childAllNodeMap;
					}
				}

				// add childs to global list
				if (UtilValidate.isNotEmpty(res.get(TreeWorker.VALUE_LIST))) {
					treeList.addAll((List<GenericValue>)res.get(TreeWorker.VALUE_LIST));
				}
			}
		}

		//End - add global list to return 
		res.put(TreeWorker.VALUE_LIST, treeList);

		if (UtilValidate.isNotEmpty(allNodeMap)) {
			res.put(TreeWorker.ALL_NODE, allNodeMap);
		}

		return res;
	}

	/**
	 * Find TreeView Roots 
	 * @param parentFieldId
	 * @param parameters
	 * @param dispatcher
	 * @param locale
	 * @return values into key 'valueList' (type List<GenericValue>)  or error list
	 * @throws GeneralException 
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> findRoots() throws GeneralException {

		List<Map<String, Object>> nodeList = FastList.newInstance();
		Map<String, Object> inputFields = FastMap.newInstance();
		Map<String, Object> res = FastMap.newInstance();
		Map<String, Object> selectedNode = FastMap.newInstance();
		Map<String, Object> allNodeMap = FastMap.newInstance();

		//Controllo se devo generare una root unica
		if (forceUniqueRoot) {

			Map<String, Object> mainRoot = FastMap.newInstance();
			mainRoot.putAll(rootValues);
			//root id
			//            String nodeId = String.format("%s_root_%d", entityName, ++rootCounter);

			String nodeId = null;
			if ("true".equals(generateNodeFromEntityName)) {
				nodeId = generateNodeId(entityName, primaryEntityName, relationTitle, "root", rootValues, loadFileKeyMap);

				rootCounter++;
			} else {
				nodeId = String.format("%s_root_%d", entityName, ++rootCounter);
			}

			mainRoot.put("_NODE_ID_", nodeId);
			if (uniqueRootNotManaged) {
				mainRoot.put("_NOT_MANAGED_", "Y");
			}
			if (checkSelected(nodeId, mainRoot, (String)parameters.get(SELECTED_ID))) {
				selectedNode = mainRoot;
			}
			nodeList.add(mainRoot);
			allNodeMap.put(nodeId, mainRoot);

		} else {

			//Extract key values from parameters
			for (KeyField kf : keyFields) {
				String key = kf.isSingle() ? kf.getSingleField() : kf.getFromField();
				//search values into root values
				if (rootValues.containsKey(key)) {
					Object value = (Object)rootValues.get(key);
					inputFields.put(key, value);
				}
			}
			if (UtilValidate.isNotEmpty(extraParameters)) {
				inputFields.putAll(extraParameters);
			}

			res = performFind(inputFields);

			//Change GenericValue list into map list			
			List<GenericValue> foundList = (List<GenericValue>)res.get(TreeWorker.VALUE_LIST);
			if (UtilValidate.isNotEmpty(foundList)) {
				for (GenericValue node : foundList) {
					Map<String, Object> nodeMap = FastMap.newInstance();
					nodeMap.putAll(node.getAllFields());
					//root id
					//                    String nodeId = String.format("%s_root_%d", entityName, ++rootCounter);
					String nodeId = null;
					if ("true".equals(generateNodeFromEntityName)) {
						nodeId = generateNodeId(entityName, primaryEntityName, relationTitle, "root", rootValues, loadFileKeyMap);

						rootCounter++;
					} else {
						nodeId = String.format("%s_root_%d", entityName, ++rootCounter);
					}

					nodeMap.put("_NODE_ID_", nodeId);
					//check selected
					if (checkSelected(nodeId, nodeMap, (String)parameters.get(SELECTED_ID))) {
						selectedNode = nodeMap;
					}
					nodeList.add(nodeMap);
					allNodeMap.put(nodeId, nodeMap);
				}
			}

		}

		if(UtilValidate.isNotEmpty(selectedNode)) {
			res.put(TreeWorker.SELECTED_NODE, selectedNode);
		}
		if (UtilValidate.isNotEmpty(allNodeMap)) {
			res.put(ALL_NODE, allNodeMap);
		}
		res.put(TreeWorker.VALUE_LIST, nodeList);
		return res;
	}

	private String generateNodeId(String entityName, String primaryEntityName, String relationTitle, String suffix, Map<String, Object> valueMap, String loadFileKeyMap) throws GeneralException {
		StringBuilder res = new StringBuilder(entityName);

		if (UtilValidate.isNotEmpty(entityName)) {
			ModelEntity modelEntity = dispatcher.getDelegator().getModelEntity(entityName);

			Map<String, Object> keyMap = new HashMap<String, Object>();
			/**
			 * Controllo se nella variabile loadFileKeyMap ho il precorso del file che verra eseguito per caricare la keyMap
			 */
			if(UtilValidate.isEmpty(loadFileKeyMap)){
				if (UtilValidate.isNotEmpty(primaryEntityName)) {
					if (relationTitle == null)
						relationTitle = "";

					relationTitle += primaryEntityName;

					ModelRelation modelRelation = modelEntity.getRelation(relationTitle);
					if (UtilValidate.isNotEmpty(modelRelation)) {
						for (ModelKeyMap mkm : modelRelation.getKeyMapsClone()) {
							keyMap.put(mkm.getRelFieldName(), UtilValidate.isNotEmpty(valueMap.get(mkm.getFieldName())) ? valueMap.get(mkm.getFieldName()) : valueMap.get(mkm.getRelFieldName()));
						}
					}
				} else {
					for (String pkFieldName : modelEntity.getPkFieldNames()) {
						keyMap.put(pkFieldName, valueMap.get(pkFieldName));
					}
				}
			}else{
				//Eseguo metodo custom
				Map<String, Object> context = FastMap.newInstance();
				context.put("valueMap", valueMap);

				context.put("locale", locale);
				context.put("timeZone", timeZone);
                context.put("dispatcher", dispatcher);
				context.put("delegator", dispatcher.getDelegator());

				GroovyUtil.runScriptAtLocation(loadFileKeyMap, context);

				if (context.containsKey("keyMap")) {
					keyMap = UtilGenerics.checkMap(context.get("keyMap"));
				} else {
					Debug.log("Non trovati valori durante esecuzione script custom");
					keyMap = new HashMap<String, Object>();
				}
			}


			res.append("_").append(suffix);


			if (UtilValidate.isNotEmpty(keyMap)) {				
					for (Map.Entry<String, Object>  keyMapEntry : keyMap.entrySet()) {
					res.append("_").append(keyMapEntry.getValue());
				}
			}
		}

		return res.toString();
	}

	/**
	 * Find all root's child recursively
	 * @param root
	 * @return return Map
	 * @throws GeneralException 
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> findChilds(Map<String, Object> root) throws GeneralException {

		Map<String, Object> result = FastMap.newInstance();
		Map<String, Object> inputFields = FastMap.newInstance();
		List<Map<String, Object>> nodeList = FastList.newInstance();
		Map<String, Object> selectedNode = FastMap.newInstance();

		//inc and check recursion counter
		recursionCounter++;
		if (recursionCounter > MAX_LEAFS) {
			Debug.log(String.format("Superato max ricorsioni %d", MAX_LEAFS));
			recursionCounter--;
			result.put(TreeWorker.VALUE_LIST, nodeList);
			return result;
		}

		//get all children of this root
		//if I have two parent field, the first is From field, second is To field
		//In that case, id of current root (previous To field) is set into From field
		for (KeyField rkf : parentRelKeyFields) {
			if (rkf.isSingle()) {
				//Only one parent field
				inputFields.put(rkf.getSingleField(), root.get(rkf.getSingleField()));
			} else {
				inputFields.put(rkf.getFromField(), root.get(rkf.getToField()));
			}
		}
		if (UtilValidate.isNotEmpty(extraParameters)) {
			inputFields.putAll(extraParameters);
		}

		Map<String, Object> res = performFind(inputFields);

		List<Map<String, Object>> foundList = (List<Map<String, Object>>)res.get(TreeWorker.VALUE_LIST);
		Map<String, Object> allNodeMap = new HashMap<String, Object>();
		if (UtilValidate.isNotEmpty(foundList)) {
			root.put(IS_LEAF, "N");
			//Cycle over nodes
			for (Map<String, Object> node : foundList) {

				//before add node, translate it into map in order to add a parent reference 
				//for loadTreeView.ftl
				Map<String, Object> nodeMap = FastMap.newInstance();
				nodeMap.putAll(node);

				String nodeId = null;
				if ("true".equals(generateNodeFromEntityName)) {
					nodeId = generateNodeId(entityName, primaryEntityName, relationTitle, "child", nodeMap, loadFileKeyMap);

					childCounter++;
				} else {
					nodeId = String.format("%s_child_%d", entityName, ++childCounter);
				}

				nodeMap.put("_NODE_ID_", nodeId);
				//now add parent id 
				if (UtilValidate.isNotEmpty(root.get("_NODE_ID_"))) {
					nodeMap.put("_PARENT_NODE_ID_", root.get("_NODE_ID_"));
				}
				//check selected
				if (checkSelected(nodeId, nodeMap, (String)parameters.get(SELECTED_ID))) {
					selectedNode = nodeMap;
				}
				//Add to list
				nodeList.add(nodeMap);
				allNodeMap.put(nodeId, nodeMap);

				//find node's child
				Map<String, Object> childResult = findChilds(nodeMap);
				if (UtilValidate.isNotEmpty(childResult.get(TreeWorker.VALUE_LIST))) {
					nodeList.addAll((List<Map<String, Object>>)childResult.get(TreeWorker.VALUE_LIST));
				}
				if (UtilValidate.isNotEmpty(childResult.get(TreeWorker.ALL_NODE))) {
					allNodeMap.putAll((Map<String, Object>)childResult.get(TreeWorker.ALL_NODE));
				}
				if (UtilValidate.isNotEmpty(childResult.get(TreeWorker.SELECTED_NODE))) {
					selectedNode = UtilGenerics.checkMap(childResult.get(TreeWorker.SELECTED_NODE));
				}

			}
		} else {
			root.put(IS_LEAF, "Y");
		}

		result.put(TreeWorker.VALUE_LIST, nodeList);
		if (UtilValidate.isNotEmpty(allNodeMap)) {
			result.put(TreeWorker.ALL_NODE, allNodeMap);
		}
		if(UtilValidate.isNotEmpty(selectedNode)) {
			result.put(TreeWorker.SELECTED_NODE, selectedNode);
		}
		//dec recursion counter
		recursionCounter--;

		return result;
	}

	private boolean checkSelected(String nodeId, Map<String, Object> nodeMap, String selectedId) {
		boolean isSelected = false;
		// Controllo selected in creazione
		boolean isSameId = ValidationUtil.checkSameTreeNodeId(nodeId, selectedId);
		
		if (isSameId) {
			isSelected = true;
			nodeMap.put("_SELECTED_", "Y");
		}
		else {
			if (UtilValidate.isNotEmpty(treeviewCreateSelectedMap)) {
				int count = 0;
				for (KeyField kf : keyFields) {
					String key = kf.isSingle() ? kf.getSingleField() : kf.getFromField();
					Object value = treeviewCreateSelectedMap.get(key);
					if (value==null) continue; 
					Object nodeValue = nodeMap.get(key);
	
					Boolean res = ObjectType.doRealCompare(nodeValue, value, "equals", UtilValidate.isNotEmpty(nodeValue) ? nodeValue.getClass().getName() : "PlainString", UtilDateTime.getDateTimeFormat(locale), null, locale, dispatcher.getDispatchContext().getClassLoader(), false);
					if (res.booleanValue()) {
						count++;
					}
				}
				if (count != 0 && count == treeviewCreateSelectedMap.size()){
					nodeMap.put("_SELECTED_", "Y");
					isSelected= true;
				}
			} else {
				if (nodeId.equalsIgnoreCase(selectedId)) {
					nodeMap.put("_SELECTED_", "Y");
					isSelected = true;
				}    
			}
		}
		return isSelected;
	}
}

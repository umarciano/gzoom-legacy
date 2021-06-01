package com.mapsengineering.base.etl.extract;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.util.EntityFindOptions;

import com.mapsengineering.base.etl.EtlException;

/**
 * @author rime
 *
 */
public abstract class ExtractAbstract implements ExtractInterface {
	
	public static final String MODULE = ExtractAbstract.class.getName().toUpperCase();
	
	private String entityName;
	
	/**
	 * @param delegator
	 * @param parameters
	 * @return
	 * @throws GenericEntityException 
	 * @throws EtlException
	 */
	public List<GenericValue> execute(Delegator delegator,
			Map<String, Object> parameters) throws EtlException {
		EntityCondition entityCondition = buildEntityCondition(parameters);
		Set<String> fieldsToSelect = getFieldsToSelect();
		List<String> orderBy = getOrderBy();
		EntityFindOptions findOptions = getFindOptions();
		boolean useCache = false;
		try {
			return delegator.findList(getEntityName(), entityCondition, fieldsToSelect, orderBy, findOptions, useCache);
		} catch (GenericEntityException e) {
			throw new EtlException(e);
		}
	}
	
	/**
	 * @param parameters
	 * @return
	 */
	protected abstract EntityCondition buildEntityCondition(Map<String, Object> parameters);
	
	/**
	 * @return
	 */
	protected abstract EntityFindOptions getFindOptions();
	
	/**
	 * @return
	 */
	protected abstract List<String> getOrderBy();
	
	/**
	 * @return
	 */
	protected abstract Set<String> getFieldsToSelect();

	
	/**
	 * @param adoptionDate
	 * @return
	 */
	protected Date getFirstDayOfYear(Date adoptionDate) {
		SimpleDateFormat simpleDateformat = new SimpleDateFormat("yyyy");
        int year = Integer.valueOf(simpleDateformat.format(adoptionDate));
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.DAY_OF_YEAR, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.clear(Calendar.MINUTE);
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MILLISECOND);
		return cal.getTime();
	}

	/**
	 * @return
	 */
	public String getEntityName() {
		return entityName;
	}

	/**
	 * @param entityName
	 */
	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}

}

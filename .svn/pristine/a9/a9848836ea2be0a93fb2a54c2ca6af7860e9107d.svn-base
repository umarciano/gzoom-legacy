/**
 * 
 */
package com.mapsengineering.base.test;

import java.util.Locale;
import java.util.Map;

import javolution.util.FastMap;

import org.ofbiz.entity.model.ModelEntity;
import org.ofbiz.entity.model.ModelField;
import org.ofbiz.entity.model.ModelFieldType;
import org.ofbiz.entity.model.ModelReader;

import com.mapsengineering.base.bl.validation.FieldResolver;
import com.mapsengineering.base.bl.validation.ResolverFactory;


/**
 * This class tests classes performing formal validation against fields parameters
 * @author sandro
 *
 */
public class TestFieldsResolver extends GplusTestCase {

	private ModelFieldType mftString;
	private ModelFieldType mftNumeric;
	private ModelFieldType mftDateTime;
	private ModelFieldType mftDate;
	private ModelFieldType mftTime;
	private ModelFieldType mftCurrency;
	private ModelFieldType mftFixedPoint;
	private ModelFieldType mftFloatingPoint;
	
	private FieldResolver stringFieldResolver;
	private FieldResolver numericFieldResolver;
	private FieldResolver dateTimeFieldResolver;
	private FieldResolver dateFieldResolver;
	private FieldResolver timeFieldResolver;
	private FieldResolver currencyFieldResolver;
	private FieldResolver fixedPointFieldResolver;
	private FieldResolver floatingPointFieldResolver;
	
	/**
	 * Nome entità usata per i test
	 */
	public static final String TEST_ENTITY_NAME = "TestingGplus";
	
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		
		ModelReader modelReader = null;
		ModelEntity modelEntity = null;
		try { 
			modelReader = ModelReader.getModelReader(delegator.getDelegatorName());
			modelEntity = modelReader.getModelEntity(TEST_ENTITY_NAME);
		} catch (Exception e) {	}

		assertNotNull("Impossibile creare ModelReader tramite il delegator di test", modelReader);
		assertNotNull("Impossibile creare ModelEntity per la entity " + TEST_ENTITY_NAME, modelEntity);
		
		//Campo tipo id-ne
		ModelField testingId = modelEntity.getField("testingId");
		assertNotNull("Impossibile reperire descrizione campo testingId nella entity " + TEST_ENTITY_NAME, testingId);
		
		ModelField name = modelEntity.getField("name");
		assertNotNull("Impossibile reperire descrizione campo testingName nella entity " + TEST_ENTITY_NAME, name);

		ModelField numeric = modelEntity.getField("numeric");
		assertNotNull("Impossibile reperire descrizione campo testingName nella entity " + TEST_ENTITY_NAME, numeric);
		
		ModelField datetime = modelEntity.getField("datetime");
		assertNotNull("Impossibile reperire descrizione campo testingName nella entity " + TEST_ENTITY_NAME, datetime);
		
		ModelField date = modelEntity.getField("date");
		assertNotNull("Impossibile reperire descrizione campo testingName nella entity " + TEST_ENTITY_NAME, date);
		
		ModelField time = modelEntity.getField("time");
		assertNotNull("Impossibile reperire descrizione campo testingName nella entity " + TEST_ENTITY_NAME, time);

		ModelField currency = modelEntity.getField("currency");
		assertNotNull("Impossibile reperire descrizione campo testingSize nella entity " + TEST_ENTITY_NAME, currency);
		
		ModelField fixedpoint = modelEntity.getField("fixedpoint");
		assertNotNull("Impossibile reperire descrizione campo testingSize nella entity " + TEST_ENTITY_NAME, fixedpoint);
		
		ModelField floatingpoint = modelEntity.getField("floatingpoint");
		assertNotNull("Impossibile reperire descrizione campo testingDate nella entity " + TEST_ENTITY_NAME, floatingpoint);
		
		try {
			mftString = delegator.getEntityFieldType(modelEntity, name.getType());
			stringFieldResolver = ResolverFactory.getInstance(mftString);
			
			mftNumeric = delegator.getEntityFieldType(modelEntity, numeric.getType());
			numericFieldResolver = ResolverFactory.getInstance(mftNumeric);
			
			mftDateTime = delegator.getEntityFieldType(modelEntity, datetime.getType());
			dateTimeFieldResolver = ResolverFactory.getInstance(mftDateTime);
			
			mftDate = delegator.getEntityFieldType(modelEntity, date.getType());
			dateFieldResolver = ResolverFactory.getInstance(mftDate);

			mftTime = delegator.getEntityFieldType(modelEntity, time.getType());
			timeFieldResolver = ResolverFactory.getInstance(mftTime);

			mftCurrency = delegator.getEntityFieldType(modelEntity, currency.getType());
			currencyFieldResolver = ResolverFactory.getInstance(mftCurrency);

			mftFixedPoint = delegator.getEntityFieldType(modelEntity, fixedpoint.getType());
			fixedPointFieldResolver = ResolverFactory.getInstance(mftFixedPoint);

			mftFloatingPoint= delegator.getEntityFieldType(modelEntity, floatingpoint.getType());
			floatingPointFieldResolver = ResolverFactory.getInstance(mftFloatingPoint);
			
		} catch (Exception e) { }
		
		assertNotNull(stringFieldResolver);
		assertNotNull(numericFieldResolver);
		assertNotNull(dateTimeFieldResolver);
		assertNotNull(dateFieldResolver);
		assertNotNull(timeFieldResolver);
		assertNotNull(currencyFieldResolver);
		assertNotNull(fixedPointFieldResolver);
		assertNotNull(floatingPointFieldResolver);
	}
	
	private String getErrorMessage(Object obj) {
		return "Errore class: " + obj.getClass().getName();
	}
	
	/**
	 * Tests all field conversions
	 */
	public void testFieldsResolver() {

		//Test string field
		Map<String, Object> errorMap = FastMap.newInstance();
		
		char[] buf = new char[512];
		String fieldTooLong = new String(buf);
		
		assertNotNull(getErrorMessage(stringFieldResolver), stringFieldResolver.resolve("name", "test", mftString, errorMap, Locale.ITALIAN));
		assertNull(getErrorMessage(stringFieldResolver), stringFieldResolver.resolve("name", fieldTooLong, mftString, errorMap, Locale.ITALIAN));
		assertTrue(errorMap.containsKey("errorMessage"));

		errorMap = FastMap.newInstance();
		
		assertNotNull(getErrorMessage(numericFieldResolver), numericFieldResolver.resolve("numeric", "123.456.789", mftNumeric, errorMap, Locale.ITALIAN));
		assertNotNull(getErrorMessage(numericFieldResolver), numericFieldResolver.resolve("numeric", "123,456,789", mftNumeric, errorMap, Locale.ENGLISH));
		assertNotNull(getErrorMessage(numericFieldResolver), numericFieldResolver.resolve("numeric", "-123456789", mftNumeric, errorMap, Locale.ITALIAN));
		assertNull(getErrorMessage(numericFieldResolver), numericFieldResolver.resolve("numeric", "123456789-", mftNumeric, errorMap, Locale.ITALIAN));
		assertNull(getErrorMessage(numericFieldResolver), numericFieldResolver.resolve("numeric", "123xyz", mftNumeric, errorMap, Locale.ITALIAN));
		assertTrue(errorMap.containsKey("errorMessage"));

		
		errorMap = FastMap.newInstance();
		
		assertNotNull(getErrorMessage(floatingPointFieldResolver), floatingPointFieldResolver.resolve("floatingpoint", "123.456,78", mftFloatingPoint, errorMap, Locale.ITALIAN));
		assertNotNull(getErrorMessage(floatingPointFieldResolver), floatingPointFieldResolver.resolve("floatingpoint", "123,456.79", mftFloatingPoint, errorMap, Locale.ENGLISH));
		assertNotNull(getErrorMessage(floatingPointFieldResolver), floatingPointFieldResolver.resolve("floatingpoint", "-1234567,89", mftFloatingPoint, errorMap, Locale.ITALIAN));
		assertNull(getErrorMessage(floatingPointFieldResolver), floatingPointFieldResolver.resolve("floatingpoint", "1234567,89-", mftFloatingPoint, errorMap, Locale.ITALIAN));
		assertNull(getErrorMessage(floatingPointFieldResolver), floatingPointFieldResolver.resolve("floatingpoint", "123xyz", mftFloatingPoint, errorMap, Locale.ITALIAN));
		assertTrue(errorMap.containsKey("errorMessage"));
		
		errorMap = FastMap.newInstance();
		
		assertNotNull(getErrorMessage(dateFieldResolver), dateFieldResolver.resolve("date", new java.util.Date(), mftDate, errorMap, Locale.ITALIAN));
		assertNotNull(getErrorMessage(dateFieldResolver), dateFieldResolver.resolve("date", new java.sql.Date(new java.util.Date().getTime()), mftDate, errorMap, Locale.ITALIAN));
		assertNotNull(getErrorMessage(dateFieldResolver), dateFieldResolver.resolve("date", "02-23-2009", mftDate, errorMap, Locale.US));
		assertNotNull(getErrorMessage(dateFieldResolver), dateFieldResolver.resolve("date", "23/02/2009", mftDate, errorMap, Locale.ITALIAN));
		assertNull(getErrorMessage(dateFieldResolver), dateFieldResolver.resolve("date", "23022009", mftDate, errorMap, Locale.ITALIAN));
		assertTrue(errorMap.containsKey("errorMessage"));

		errorMap = FastMap.newInstance();
		
		assertNotNull(getErrorMessage(dateTimeFieldResolver), dateTimeFieldResolver.resolve("datetime", new java.util.Date(), mftDateTime, errorMap, Locale.ITALIAN));
		assertNotNull(getErrorMessage(dateTimeFieldResolver), dateTimeFieldResolver.resolve("datetime", new java.sql.Date(
						new java.util.Date().getTime()), mftDateTime, errorMap,	Locale.ITALIAN));
		assertNotNull(getErrorMessage(dateTimeFieldResolver), dateTimeFieldResolver.resolve("datetime", "02-23-2009 12:23:54.678", mftDateTime, errorMap, Locale.US));
		assertNotNull(getErrorMessage(dateTimeFieldResolver), dateTimeFieldResolver.resolve("datetime", "23/02/2009 12:23:54", mftDateTime, errorMap, Locale.ITALIAN));
		assertNull(getErrorMessage(dateTimeFieldResolver), dateTimeFieldResolver.resolve("datetime", "23022009", mftDateTime, errorMap, Locale.ITALIAN));
		assertTrue(errorMap.containsKey("errorMessage"));
		
		errorMap = FastMap.newInstance();
		
		assertNotNull(getErrorMessage(timeFieldResolver), timeFieldResolver.resolve("time", "12:23:54.678", mftTime, errorMap, Locale.US));
		assertNotNull(getErrorMessage(timeFieldResolver), timeFieldResolver.resolve("time", "12:23:54", mftTime, errorMap, Locale.ITALIAN));
		assertNull(getErrorMessage(timeFieldResolver), timeFieldResolver.resolve("time", "122359", mftTime, errorMap, Locale.ITALIAN));
		assertTrue(errorMap.containsKey("errorMessage"));
		
	}

}

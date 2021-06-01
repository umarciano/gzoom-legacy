package com.mapsengineering.base.bl.validation;

import org.ofbiz.base.util.cache.UtilCache;
import org.ofbiz.entity.model.ModelFieldType;
import org.ofbiz.service.GenericServiceException;

public class ResolverFactory {
	
	/**
	 * Enum gestione creazione Resolver. In caso di creazione di nuovi Resolver aggiungere 
	 * qui il processo di creazione usando come chiave (maiuscolo) la classe gestita relativa.
	 * @author sandro
	 *
	 */
	private enum ResolverType {
		STRING 		{ FieldResolver create() { return new StringResolver();} },
		DATE 		{ FieldResolver create() { return new DateResolver();} },
		TIMESTAMP 	{ FieldResolver create() { return new DatetimeResolver();} },
		TIME	 	{ FieldResolver create() { return new TimeResolver();} },
		BIGDECIMAL 	{ FieldResolver create() { return new BigdecimalResolver();} },
		LONG	 	{ FieldResolver create() { return new LongResolver();} },
		DOUBLE 		{ FieldResolver create() { return new DoubleResolver();} };
		
		abstract FieldResolver create();
	}
	
	private ResolverFactory() {}
	
	/**
	 * Resolver cache
	 */
	private static UtilCache<String, FieldResolver> cache = UtilCache.createUtilCache("gplus.FieldResolver", 0, 0);
	
	/**
	 * Get Resolver instance related to expectedField
	 * @param expectedField
	 * @return Resolver instance
	 */
	public static synchronized FieldResolver getInstance(ModelFieldType expectedField) throws GenericServiceException {
		
		String clazz = expectedField.getJavaType();
		if (clazz.indexOf(".")>-1) {
			clazz = clazz.substring(clazz.lastIndexOf(".")+1);
		}
		clazz = clazz.toUpperCase();
		
		if (cache.containsKey(clazz)) {
			return cache.get(clazz);
		}
		
		ResolverType rt;
		rt = ResolverType.valueOf(clazz);
		
		FieldResolver fr = rt.create();
		cache.put(clazz, fr);
		return fr;
	}
	
}

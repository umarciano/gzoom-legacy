package com.mapsengineering.base.util.entity;

import java.io.Serializable;
import java.util.Locale;

import org.ofbiz.entity.GenericValue;

/**
 * Base class to wrap an OFBiz GenericValue
 * @author sivi
 *
 */
public class GenericValueBaseWrapper implements Serializable {

    private static final long serialVersionUID = 1L;

    private String entityName;
    private GenericValue genericValue;
    private Locale locale;

    /**
     * Default constructor
     */
    public GenericValueBaseWrapper() {
    }

    /**
     * Create a new wrapper to a GenericValue
     * @param genericValue
     */
    public GenericValueBaseWrapper(GenericValue genericValue) {
        setGenericValue(genericValue);
    }

    /**
     * Create a new wrapper to a GenericValue
     * @param genericValue
     * @param locale if set, field values are localized 
     */
    public GenericValueBaseWrapper(GenericValue genericValue, Locale locale) {
        setGenericValue(genericValue);
        setLocale(locale);
    }

    /**
     * Get the current GenericValue
     * @return
     */
    public GenericValue getGenericValue() {
        return genericValue;
    }

    /**
     * Set the current GenericValue
     * @param genericValue
     */
    public void setGenericValue(GenericValue genericValue) {
        this.genericValue = genericValue;
    }

    /**
     * Get current locale
     * @return if not null, field values are localized
     */
    public Locale getLocale() {
        return locale;
    }

    /**
     * Set current locale
     * @param locale if not null, field values are localized
     */
    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    /**
     * Get the reference entity name, may differ from GenericValue
     * @return
     */
    public String getEntityName() {
        return entityName;
    }

    /**
     * Set the reference entity name, may differ from GenericValue
     * @param entityName
     */
    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    /**
     * Get a field value
     * @param name
     * @return if a locale is set, the value is localized
     */
    public Object get(String name) {
        return locale != null ? genericValue.get(name, locale) : genericValue.get(name);
    }
}

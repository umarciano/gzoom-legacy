package com.mapsengineering.workeffortext.pub.data;

public class PubGlAccountData {

    private String id;
    private String type;
    private String name;
    private String uomType;
    private Object targetValue;
    private Object actualValue;
    private String imageUrl;

    public PubGlAccountData() {
        this(null, null, null, null, null, null, null);
    }

    public PubGlAccountData(String id, String type, String name, String uomType, Object actualValue, Object targetValue, String imageUrl) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.uomType = uomType;
        this.actualValue = actualValue;
        this.targetValue = targetValue;
        this.imageUrl = imageUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUomType() {
        return uomType;
    }

    public void setUomType(String uomType) {
        this.uomType = uomType;
    }

    public Object getTargetValue() {
        return targetValue;
    }

    public void setTargetValue(Object targetValue) {
        this.targetValue = targetValue;
    }

    public Object getActualValue() {
        return actualValue;
    }

    public void setActualValue(Object actualValue) {
        this.actualValue = actualValue;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}

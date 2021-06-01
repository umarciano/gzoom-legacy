<#assign delegatorName = generator.delegatorName>
<#assign delegatorInfo = Static["org.ofbiz.entity.config.EntityConfigUtil"].getDelegatorInfo(delegatorName)>
<#assign modelGroupReader = Static["org.ofbiz.entity.model.ModelGroupReader"].getModelGroupReader(delegatorName)>
<#assign groupName = modelGroupReader.getEntityGroupName(modelEntity.entityName, delegatorName)>
<#assign helperName = generator.getEntityGroupHelperName(delegatorInfo, groupName)>
<#assign modelFieldTypeReader = Static["org.ofbiz.entity.model.ModelFieldTypeReader"].getModelFieldTypeReader(helperName)>
package ${packageName};

public class ${className} extends com.mapsengineering.base.util.entity.GenericValueBaseWrapper {

    public static final String ENTITY_NAME = "${modelEntity.entityName}";

    private static final long serialVersionUID = 1L;

    public ${className}() {
        this(null, null);
    }

    public ${className}(org.ofbiz.entity.GenericValue genericValue) {
        this(genericValue, null);
    }

    public ${className}(org.ofbiz.entity.GenericValue genericValue, java.util.Locale locale) {
        super(genericValue, locale);
        setEntityName(ENTITY_NAME);
    }
    <#list modelEntity.fieldsIterator as field>
    <#assign fieldType = modelFieldTypeReader.getModelFieldType(field.type).javaType>
    <#assign fieldName = field.name>
    <#assign fieldNameCapitalized = Static["org.apache.commons.lang.StringUtils"].capitalize(fieldName)>

    public ${fieldType} get${fieldNameCapitalized}() {
        return (${fieldType})get("${fieldName}");
    }

    public void set${fieldNameCapitalized}(${fieldType} value) {
        getGenericValue().set("${fieldName}", value);
    }
    </#list>
    <#if generator.generateRelations>
    <#list modelEntity.relationsIterator as relation>
    <#assign relModelEntity = generator.modelReader.getModelEntity(relation.relEntityName)>
    <#assign relClassName = generator.getFullClassName(relModelEntity)>
    <#if relation.type == "one" || relation.type == "one-nofk">

    public ${relClassName} getRelated${relation.combinedName}() {
        return new ${relClassName}(genericValue.getRelatedOne("${relation.combinedName}"), locale);
    }
    <#elseif relation.type == "many">
    </#if>
    </#list>
    </#if>
}

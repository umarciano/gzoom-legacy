
  
  SELECT 
  PP.PARENT_ROLE_CODE, PA.PARTY_NAME, PA.PARTY_NAME_LANG, 
  PR.PARTY_ID, PA.EXTERNAL_ID

FROM  <@table "PARTY_ROLE"/>  PR 
INNER JOIN  <@table "PARTY"/>  PA ON PA.PARTY_ID = PR.PARTY_ID 
INNER JOIN  <@table "PARTY_PARENT_ROLE"/>  PP ON PP.PARTY_ID = PR.PARTY_ID 
INNER JOIN  <@table "USER_LOGIN"/>  UL ON UL.USER_LOGIN_ID = '${userLogin.userLoginId}'

<#if fromSearch?has_content && fromSearch?if_exists == "Y">
  INNER JOIN <@table "WORK_EFFORT"/> WE ON WE.ORG_UNIT_ID = PR.PARTY_ID
  INNER JOIN <@table "WORK_EFFORT_TYPE"/> WET ON WET.WORK_EFFORT_TYPE_ID = WE.WORK_EFFORT_TYPE_ID
  INNER JOIN <@table "STATUS_ITEM"/> ST ON ST.STATUS_ID = WE.CURRENT_STATUS_ID
</#if>

LEFT OUTER JOIN  <@table "PARTY_RELATIONSHIP"/>  E ON PR.ROLE_TYPE_ID = E.ROLE_TYPE_ID_FROM
        AND PR.PARTY_ID = E.PARTY_ID_FROM
        AND (E.PARTY_RELATIONSHIP_TYPE_ID = 'ORG_RESPONSIBLE' OR 
			(E.PARTY_RELATIONSHIP_TYPE_ID = 'ORG_DELEGATE' AND 
				(E.CTX_ENABLED IS NULL 
					<#if weContextId?has_content>
						OR E.CTX_ENABLED LIKE '%${weContextId?if_exists}%'
					</#if>
				)
			)
		)
        
        AND E.THRU_DATE IS NULL
        AND E.PARTY_ID_TO = UL.PARTY_ID
LEFT OUTER JOIN  <@table "PARTY_RELATIONSHIP"/>  Z ON PR.ROLE_TYPE_ID = Z.ROLE_TYPE_ID_TO
        AND PR.PARTY_ID = Z.PARTY_ID_TO
        AND Z.PARTY_RELATIONSHIP_TYPE_ID = 'GROUP_ROLLUP'
        AND Z.THRU_DATE IS NULL
LEFT OUTER JOIN  <@table "PARTY_RELATIONSHIP"/>  Y ON Z.ROLE_TYPE_ID_FROM = Y.ROLE_TYPE_ID_FROM
        AND Z.PARTY_ID_FROM = Y.PARTY_ID_FROM
        AND (Y.PARTY_RELATIONSHIP_TYPE_ID = 'ORG_RESPONSIBLE' OR 
			(Y.PARTY_RELATIONSHIP_TYPE_ID = 'ORG_DELEGATE' AND 
				(Y.CTX_ENABLED IS NULL 
					<#if weContextId?has_content>
						OR Y.CTX_ENABLED LIKE '%${weContextId?if_exists}%'
					</#if>
				)
			)
		)
		AND Y.THRU_DATE IS NULL 
        AND Y.PARTY_ID_TO = UL.PARTY_ID
LEFT OUTER  JOIN <@table "PARTY_RELATIONSHIP"/>  Z2 ON Z.ROLE_TYPE_ID_FROM = Z2.ROLE_TYPE_ID_TO
        AND Z.PARTY_ID_FROM = Z2.PARTY_ID_TO
        AND Z2.PARTY_RELATIONSHIP_TYPE_ID = 'GROUP_ROLLUP'
        AND Z2.THRU_DATE IS NULL 
LEFT OUTER JOIN <@table "PARTY_RELATIONSHIP"/>  Y2 ON Z2.ROLE_TYPE_ID_FROM = Y2.ROLE_TYPE_ID_FROM
        AND Z2.PARTY_ID_FROM = Y2.PARTY_ID_FROM
        AND (Y2.PARTY_RELATIONSHIP_TYPE_ID = 'ORG_RESPONSIBLE' OR 
			(Y2.PARTY_RELATIONSHIP_TYPE_ID = 'ORG_DELEGATE' AND 
				(Y2.CTX_ENABLED IS NULL 
					<#if weContextId?has_content>
						OR Y2.CTX_ENABLED LIKE '%${weContextId?if_exists}%'
					</#if>
				)
			)
		)
		AND Y2.THRU_DATE IS NULL 
        AND Y2.PARTY_ID_TO = UL.PARTY_ID
WHERE PR.PARENT_ROLE_TYPE_ID = 'ORGANIZATION_UNIT'
  
  <#if statusId?has_content>
  AND PA.STATUS_ID = <@param statusId />
  </#if>
  
  <#if roleTypeIdList?if_exists?has_content>
    AND PR.ROLE_TYPE_ID IN ( 
         <#list roleTypeIdList as roleTypeId>
             <@param roleTypeId />,
         </#list>
         'NULL'
    )
  </#if>

  <#if organizationId?has_content>
  AND PP.ORGANIZATION_ID = <@param organizationId />
  </#if>

  AND (1 = 0
  	   <#if isOrgMgr?default(false) == true>
          OR E.PARTY_ID_TO IS NOT NULL
       </#if>
       <#if isSup?default(false) == true>
    	  OR  Y.PARTY_ID_TO IS NOT NULL
       </#if>
       <#if isTop?default(false) == true>
          OR  Y2.PARTY_ID_TO IS NOT NULL
       </#if>       
  )
  
  <#if fromSearch?has_content && fromSearch?if_exists == "Y">
      <#if weContextId?has_content>
        AND WET.PARENT_TYPE_ID = '${weContextId?if_exists}'
      </#if>
      <#if snapshot?has_content && snapshot?if_exists == "Y">
        AND WE.WORK_EFFORT_REVISION_ID IS NOT NULL
      <#else>
        AND WE.WORK_EFFORT_REVISION_ID IS NULL
      </#if>
      <#if !rootInqyTree?has_content || rootInqyTree?if_exists != "Y">
        AND ST.ACT_ST_ENUM_ID <> 'ACTSTATUS_CLOSED'
        <#if currentStatusContains?has_content>
          AND ST.STATUS_ID LIKE '%${currentStatusContains?if_exists}%'
        </#if>
        <#if gpMenuEnumId?has_content>
          AND WET.GP_MENU_ENUM_ID = '${gpMenuEnumId?if_exists}'
        </#if>
      </#if>
  </#if>

GROUP BY
  PP.PARENT_ROLE_CODE, PA.PARTY_NAME, PA.PARTY_NAME_LANG, 
  PR.PARTY_ID, PA.EXTERNAL_ID
<#if queryOrderBy?has_content>
  ORDER BY ${queryOrderBy?if_exists}
<#else>
  ORDER BY PP.PARENT_ROLE_CODE
</#if>
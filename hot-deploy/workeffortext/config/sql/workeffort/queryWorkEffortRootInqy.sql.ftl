SELECT A.WORK_EFFORT_ID, A.WORK_EFFORT_TYPE_ID, A.CURRENT_STATUS_ID, A.WORK_EFFORT_PARENT_ID, A.ORGANIZATION_ID, A.ORG_UNIT_ROLE_TYPE_ID, A.ORG_UNIT_ID, 
  A.SOURCE_REFERENCE_ID, A.WORK_EFFORT_NAME, A.ESTIMATED_START_DATE, A.ESTIMATED_COMPLETION_DATE, A.WORK_EFFORT_SNAPSHOT_ID, A.ETCH AS WE_ETCH, 
  U.ULVR_USER_LOGIN_ID AS UV_USER_LOGIN_ID, B.PARENT_TYPE_ID AS WE_CONTEXT_ID, B.IS_ROOT AS WE_IS_ROOT, B.IS_TEMPLATE AS WE_IS_TEMPLATE, B.DESCRIPTION AS WE_TYPE_DESCRIPTION, 
  B.ETCH, B.HIERARCHY_ASSOC_TYPE_ID AS WE_HIERARCHY_TYPE_ID, C.DESCRIPTION AS WE_STATUS_DESCR, C.ACT_ST_ENUM_ID AS WE_ACTIVATION, C.SEQUENCE_ID AS WE_STATUS_SEQ_ID, C.STATUS_TYPE_ID AS WE_STATUS_TYPE_ID, 
  D.GL_FISCAL_TYPE_ID, D.NEXT_STATUS_ID, D.MANAGEMENT_ROLE_TYPE_ID, D.MANAG_WE_STATUS_ENUM_ID, 
  <#if isOrgMgr?default(false) == true>
      E.PARTY_ID_TO, 
  <#else>
    NULL AS PARTY_ID_TO,
  </#if>
  <#if isRole?default(false) == true>
    F.PARTY_ID,
  <#else>
    G.PARTY_ID,
  </#if>
  A.WORK_EFFORT_NAME_LANG, G.PARTY_NAME, 
  B.DESCRIPTION_LANG AS WE_TYPE_DESCRIPTION_LANG, C.DESCRIPTION_LANG AS WE_STATUS_DESCR_LANG, G.PARTY_NAME_LANG, B.ETCH_LANG, PPR.PARENT_ROLE_CODE,
  <#if withProcess?if_exists == 'Y'>
   PR.WORK_EFFORT_NAME AS WE_FROM_NAME, PR.WORK_EFFORT_NAME_LANG AS WE_FROM_NAME_LANG
  <#else>
   NULL AS WE_FROM_NAME, NULL AS WE_FROM_NAME_LANG
  </#if> 
FROM <@table "WORK_EFFORT"/> A 
INNER JOIN (SELECT UL.PARTY_ID AS UL_PARTY_ID, ULVR.USER_LOGIN_ID AS ULVR_USER_LOGIN_ID, ULVR.PARTY_ID AS ULVR_PARTY_ID, ULVR.ROLE_TYPE_ID AS ULVR_ROLE_TYPE_ID, ULVR.COMMENTS AS ULVR_COMMENTS 
  FROM <@table "USER_LOGIN_VALID_PARTY_ROLE"/> ULVR 
  INNER JOIN <@table "USER_LOGIN"/> UL ON ULVR.USER_LOGIN_ID = UL.USER_LOGIN_ID) U 
ON (A.ORGANIZATION_ID = U.ULVR_PARTY_ID AND U.ULVR_ROLE_TYPE_ID = 'INTERNAL_ORGANIZATIO')
INNER JOIN <@table "WORK_EFFORT_TYPE"/> B ON (A.WORK_EFFORT_TYPE_ID = B.WORK_EFFORT_TYPE_ID)
INNER JOIN <@table "STATUS_ITEM"/> C ON (A.CURRENT_STATUS_ID = C.STATUS_ID)
LEFT OUTER JOIN <@table "WORK_EFFORT_TYPE_STATUS"/> D ON (A.WORK_EFFORT_TYPE_ID = D.WORK_EFFORT_TYPE_ROOT_ID AND A.CURRENT_STATUS_ID = D.CURRENT_STATUS_ID)
LEFT OUTER JOIN <@table "STATUS_ITEM"/> ST ON ST.STATUS_ID = D.CURRENT_STATUS_ID

<#if isOrgMgr?default(false) == true>
LEFT OUTER JOIN <@table "PARTY_RELATIONSHIP"/> E ON (A.ORG_UNIT_ROLE_TYPE_ID = E.ROLE_TYPE_ID_FROM AND A.ORG_UNIT_ID = E.PARTY_ID_FROM 
  AND E.PARTY_RELATIONSHIP_TYPE_ID IN ('ORG_RESPONSIBLE', 'ORG_DELEGATE') AND E.FROM_DATE <= A.ESTIMATED_COMPLETION_DATE AND (E.THRU_DATE IS NULL OR E.THRU_DATE >= A.ESTIMATED_COMPLETION_DATE)
  AND E.PARTY_ID_TO = U.UL_PARTY_ID)
</#if>

<#if isSup?default(false) == true>
LEFT OUTER JOIN <@table "PARTY_RELATIONSHIP"/> Z ON (A.ORG_UNIT_ROLE_TYPE_ID = Z.ROLE_TYPE_ID_TO AND A.ORG_UNIT_ID = Z.PARTY_ID_TO
  AND Z.PARTY_RELATIONSHIP_TYPE_ID = 'GROUP_ROLLUP' AND Z.FROM_DATE <= A.ESTIMATED_COMPLETION_DATE
  AND (Z.THRU_DATE IS NULL OR Z.THRU_DATE >= A.ESTIMATED_COMPLETION_DATE))
LEFT OUTER JOIN <@table "PARTY_RELATIONSHIP"/> Y ON (Z.ROLE_TYPE_ID_FROM = Y.ROLE_TYPE_ID_FROM AND Z.PARTY_ID_FROM = Y.PARTY_ID_FROM
  AND Y.PARTY_RELATIONSHIP_TYPE_ID IN ('ORG_RESPONSIBLE', 'ORG_DELEGATE') AND Y.FROM_DATE <= A.ESTIMATED_COMPLETION_DATE
  AND (Y.THRU_DATE IS NULL OR Y.THRU_DATE >= A.ESTIMATED_COMPLETION_DATE)
  AND Y.PARTY_ID_TO = U.UL_PARTY_ID)
</#if>

<#if isTop?default(false) == true>
LEFT OUTER JOIN <@table "PARTY_RELATIONSHIP"/> Z1 ON A.ORG_UNIT_ROLE_TYPE_ID = Z1.ROLE_TYPE_ID_TO AND A.ORG_UNIT_ID = Z1.PARTY_ID_TO
        AND Z1.PARTY_RELATIONSHIP_TYPE_ID = 'GROUP_ROLLUP' AND Z1.FROM_DATE <= A.ESTIMATED_COMPLETION_DATE
        AND (Z1.THRU_DATE IS NULL OR Z1.THRU_DATE >= A.ESTIMATED_COMPLETION_DATE)
LEFT OUTER JOIN <@table "PARTY_RELATIONSHIP"/> Z2 ON Z1.ROLE_TYPE_ID_FROM = Z2.ROLE_TYPE_ID_TO AND Z1.PARTY_ID_FROM = Z2.PARTY_ID_TO
  AND Z2.PARTY_RELATIONSHIP_TYPE_ID = 'GROUP_ROLLUP' AND Z2.FROM_DATE <= A.ESTIMATED_COMPLETION_DATE
  AND (Z2.THRU_DATE IS NULL OR Z2.THRU_DATE >= A.ESTIMATED_COMPLETION_DATE)
LEFT OUTER JOIN <@table "PARTY_RELATIONSHIP"/> Y2 ON Z2.ROLE_TYPE_ID_FROM = Y2.ROLE_TYPE_ID_FROM AND Z2.PARTY_ID_FROM = Y2.PARTY_ID_FROM
  AND Y2.PARTY_RELATIONSHIP_TYPE_ID IN ('ORG_RESPONSIBLE', 'ORG_DELEGATE') AND Y2.FROM_DATE <= A.ESTIMATED_COMPLETION_DATE
  AND (Y2.THRU_DATE IS NULL OR Y2.THRU_DATE >= A.ESTIMATED_COMPLETION_DATE)
  AND Y2.PARTY_ID_TO = U.UL_PARTY_ID
</#if>

<#if isRole?default(false) == true>
LEFT OUTER JOIN <@table "WORK_EFFORT_PARTY_ASSIGNMENT"/> F ON (A.WORK_EFFORT_ID = F.WORK_EFFORT_ID AND A.ESTIMATED_COMPLETION_DATE = F.THRU_DATE 
  AND F.ROLE_TYPE_ID LIKE 'WEM%' AND F.PARTY_ID = U.UL_PARTY_ID)
</#if>

LEFT OUTER JOIN <@table "WORK_EFFORT_PARTY_ASSIGNMENT"/> PA ON PA.WORK_EFFORT_ID = A.WORK_EFFORT_ID AND PA.ROLE_TYPE_ID = 'WEM_EVAL_IN_CHARGE'     

<#if (isOrgMgr?default(false) == true) || (isSup?default(false) == true) || (isRole?default(false) == true) || (isTop?default(false) == true) >
	INNER JOIN <@table "PARTY"/> G ON (A.ORG_UNIT_ID = G.PARTY_ID) 
	INNER JOIN <@table "PARTY_PARENT_ROLE"/> PPR ON (G.PARTY_ID = PPR.PARTY_ID 
 	AND PPR.ROLE_TYPE_ID = 'ORGANIZATION_UNIT') 
<#else>
	LEFT OUTER JOIN <@table "PARTY"/> G ON (A.ORG_UNIT_ID = G.PARTY_ID) 
	LEFT OUTER JOIN <@table "PARTY_PARENT_ROLE"/> PPR ON (G.PARTY_ID = PPR.PARTY_ID 
  	AND PPR.ROLE_TYPE_ID = 'ORGANIZATION_UNIT')
</#if>
<#if withProcess?if_exists == 'Y'>
 LEFT OUTER JOIN <@table "WORK_EFFORT_TYPE_ASSOC"/> FAT ON FAT.WORK_EFFORT_TYPE_ID = A.WORK_EFFORT_TYPE_ID
   AND FAT.IS_PARENT_REL IS NOT NULL AND FAT.IS_PARENT_REL = 'Y'
 LEFT OUTER JOIN <@table "WORK_EFFORT_ASSOC"/> PRFA ON PRFA.WORK_EFFORT_ASSOC_TYPE_ID IS NOT NULL AND PRFA.WORK_EFFORT_ID_TO = A.WORK_EFFORT_ID
  AND PRFA.WORK_EFFORT_ASSOC_TYPE_ID = FAT.WORK_EFFORT_ASSOC_TYPE_ID AND PRFA.WORK_EFFORT_REVISION_ID IS NULL
  AND PRFA.WORK_EFFORT_ID_FROM IS NOT NULL 
  AND PRFA.THRU_DATE = A.ESTIMATED_COMPLETION_DATE
 LEFT OUTER JOIN <@table "WORK_EFFORT"/> PR ON PR.WORK_EFFORT_ID = PRFA.WORK_EFFORT_ID_FROM
</#if>
WHERE B.IS_ROOT = 'Y' AND B.PARENT_TYPE_ID LIKE 'CTX%' 
  AND ( 
  	   <#if isOrgMgr?default(false) == true || isSup?default(false) == true || isRole?default(false) == true || isTop?default(false) == true >
  			1 = 0
  	   <#else>
  			0 = 0
  	   </#if>
  	   <#if isOrgMgr?default(false) == true>
           OR (E.PARTY_ID_TO IS NOT NULL
          AND (PA.PARTY_ID IS NULL OR PA.PARTY_ID <> '${userLogin.partyId?if_exists}')) 
       </#if>
       <#if isSup?default(false) == true>
    	   OR (Y.PARTY_ID_TO IS NOT NULL 
          AND (PA.PARTY_ID IS NULL OR PA.PARTY_ID <> '${userLogin.partyId?if_exists}')) 
       </#if>
       <#if isRole?default(false) == true>
           OR (F.PARTY_ID IS NOT NULL 
          AND (F.ROLE_TYPE_ID <> 'WEM_EVAL_IN_CHARGE' 
           OR (D.MANAGEMENT_ROLE_TYPE_ID = 'WEM_EVAL_IN_CHARGE' OR ST.ACT_ST_ENUM_ID = 'ACTSTATUS_CLOSED')))          
       </#if>
       <#if isTop?default(false) == true>
    	   OR (Y2.PARTY_ID_TO IS NOT NULL
          AND (PA.PARTY_ID IS NULL OR PA.PARTY_ID <> '${userLogin.partyId?if_exists}')) 
       </#if>       
  ) AND B.IS_ROOT = 'Y' AND B.PARENT_TYPE_ID LIKE 'CTX%' 
  <#if userLogin?has_content && userLogin.userLoginId?has_content>
      AND U.ULVR_USER_LOGIN_ID = '${userLogin.userLoginId}'
  </#if>   
  <#if organizationId?has_content>
  	  AND A.ORGANIZATION_ID = <@param organizationId />
  </#if>  
  <#if orgUnitId?has_content>
  	  <#if childStruct?if_exists == 'Y'>
	  	  	<#if searchDate?has_content>
		  	  	AND EXISTS (SELECT 1 FROM PARTY_ROLLUP_VIEW PRV
				WHERE PRV.PARTY_ID_ROOT = <@param orgUnitId />
				  AND PRV.FROM_DATE <= <@param searchDate jdbcType.TIMESTAMP />
				  AND (PRV.THRU_DATE IS NULL 
				   OR PRV.THRU_DATE >= <@param searchDate jdbcType.TIMESTAMP />)
				  AND PRV.PARTY_ID_TO = A.ORG_UNIT_ID)
		  	<#else>
		   		AND EXISTS (SELECT 1 FROM PARTY_ROLLUP_VIEW PRV
				WHERE PRV.PARTY_ID_ROOT = <@param orgUnitId />
				  AND PRV.THRU_DATE IS NULL 
				  AND PRV.PARTY_ID_TO = A.ORG_UNIT_ID)
		  	</#if>
	  <#else>
	   		AND A.ORG_UNIT_ID = <@param orgUnitId />
	  </#if>
  </#if>
  <#if weStatusDescr?has_content>
      AND C.DESCRIPTION = <@param weStatusDescr />
  </#if>
  <#if weStatusDescrLang?has_content>
      AND C.DESCRIPTION_LANG = <@param weStatusDescrLang />
  </#if>  
  <#if workEffortTypeId?has_content>
  	  AND A.WORK_EFFORT_TYPE_ID = <@param workEffortTypeId />
  </#if>
  <#if sourceReferenceId?has_content>
  	  AND UPPER(A.SOURCE_REFERENCE_ID) LIKE '%${sourceReferenceId?upper_case?if_exists}%'
  </#if>
  <#if workEffortName?has_content>
      AND UPPER(A.WORK_EFFORT_NAME) LIKE '%${workEffortName?upper_case?if_exists}%'
  </#if>
  <#if workEffortNameLang?has_content>
      AND UPPER(A.WORK_EFFORT_NAME_LANG) LIKE '%${workEffortNameLang?upper_case?if_exists}%'
  </#if>
  <#if weActivation?has_content>
  	  AND C.ACT_ST_ENUM_ID = <@param weActivation />
  </#if>
  <#if weIsTemplate?has_content>
  	  AND B.IS_TEMPLATE = <@param weIsTemplate />
  </#if>
  <#if weEtch?has_content>
  	  AND UPPER(A.ETCH) LIKE '%${weEtch?upper_case?if_exists}%'
  </#if>             
  <#if weContextId?has_content>
  	  AND B.PARENT_TYPE_ID = <@param weContextId />
  </#if>  
  <#if isRootActive?has_content>
  	  AND B.IS_ROOT_ACTIVE = <@param isRootActive />
  </#if>
  <#if workEffortId?has_content>
  	  AND A.WORK_EFFORT_ID = <@param workEffortId />
  </#if>  
  <#if workEffortRevisionId?has_content>
      <#if workEffortRevisionId?if_exists == "[null-field]">
          AND A.WORK_EFFORT_REVISION_ID IS NULL
      <#else>
  	      AND A.WORK_EFFORT_REVISION_ID = <@param workEffortRevisionId />
  	  </#if>
  </#if>  
  
<#if queryOrderBy?has_content>
    ORDER BY ${queryOrderBy}
<#else>
	<#if weContextId?if_exists == 'CTX_EP' || weContextId?if_exists == 'CTX_DI'>
    	ORDER BY A.WORK_EFFORT_NAME ASC, B.DESCRIPTION ASC, C.DESCRIPTION ASC, A.WORK_EFFORT_ID ASC
	<#else>
		ORDER BY B.SEQ_ESP ASC, A.SOURCE_REFERENCE_ID ASC, A.WORK_EFFORT_NAME ASC, B.DESCRIPTION ASC, C.DESCRIPTION ASC, A.WORK_EFFORT_ID ASC
	</#if>   
</#if>


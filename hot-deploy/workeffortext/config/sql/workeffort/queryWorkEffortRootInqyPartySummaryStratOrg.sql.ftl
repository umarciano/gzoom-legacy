SELECT
   A.WORK_EFFORT_ID,
   A.CURRENT_STATUS_ID,
   A.WORK_EFFORT_PARENT_ID,
   A.ORGANIZATION_ID,
   A.ORG_UNIT_ROLE_TYPE_ID,
   A.ORG_UNIT_ID,
   M.PARTY_NAME,
   M.PARTY_NAME_LANG,
   A.SOURCE_REFERENCE_ID,
   A.WORK_EFFORT_NAME,
   A.WORK_EFFORT_NAME_LANG,
   A.ESTIMATED_START_DATE,
   A.ESTIMATED_COMPLETION_DATE,
   A.WORK_EFFORT_SNAPSHOT_ID,
   A.ETCH AS WE_ETCH,
   UL.USER_LOGIN_ID AS UV_USER_LOGIN_ID,
   B.WORK_EFFORT_TYPE_ID AS WORK_EFFORT_ROOT_TYPE_ID,
   B.PARENT_TYPE_ID AS WE_CONTEXT_ID,
   B.IS_ROOT AS WE_IS_ROOT,
   B.IS_TEMPLATE AS WE_IS_TEMPLATE,
   B.DESCRIPTION AS WE_TYPE_DESCRIPTION,
   B.DESCRIPTION_LANG AS WE_TYPE_DESCRIPTION_LANG,
   B.ETCH,
   B.ETCH_LANG,
   B.HIERARCHY_ASSOC_TYPE_ID AS WE_HIERARCHY_TYPE_ID,
   C.DESCRIPTION AS WE_STATUS_DESCR,
   C.ACT_ST_ENUM_ID AS WE_ACTIVATION,
   C.STATUS_TYPE_ID AS WE_STATUS_TYPE_ID,
   C.DESCRIPTION_LANG AS WE_STATUS_DESCR_LANG,
   D.GL_FISCAL_TYPE_ID,
   D.NEXT_STATUS_ID,
   D.MANAGEMENT_ROLE_TYPE_ID,
   D.MANAG_WE_STATUS_ENUM_ID,
   MIN(L.SEQUENCE_ID) AS MIN_SEQUENCE_ID 
FROM
   (
(((((<@table "WORK_EFFORT"/> A 
      INNER JOIN
         <@table "USER_LOGIN_VALID_PARTY_ROLE"/> UL 
         ON A.ORGANIZATION_ID = UL.PARTY_ID 
         AND 
         (
            UL.ROLE_TYPE_ID = 'INTERNAL_ORGANIZATIO' 
         )
) 
      INNER JOIN
         <@table "WORK_EFFORT_TYPE"/> B 
         ON A.WORK_EFFORT_TYPE_ID = B.WORK_EFFORT_TYPE_ID) 
      INNER JOIN
         <@table "STATUS_ITEM"/> C 
         ON A.CURRENT_STATUS_ID = C.STATUS_ID) 
      LEFT OUTER JOIN
         <@table "WORK_EFFORT_TYPE_STATUS"/> D 
         ON A.WORK_EFFORT_TYPE_ID = D.WORK_EFFORT_TYPE_ROOT_ID 
         AND A.CURRENT_STATUS_ID = D.CURRENT_STATUS_ID) 
      LEFT OUTER JOIN
         <@table "STATUS_VALID_CHANGE"/> I 
         ON A.CURRENT_STATUS_ID = I.STATUS_ID) 
      LEFT OUTER JOIN
         <@table "STATUS_ITEM"/> L 
         ON I.STATUS_ID_TO = L.STATUS_ID 
   )
   LEFT OUTER JOIN
      <@table "PARTY"/> M 
      ON A.ORG_UNIT_ID = M.PARTY_ID 
   INNER JOIN
      (
         SELECT
            UL.PARTY_ID AS UL_PARTY_ID,
            ULVR.USER_LOGIN_ID AS ULVR_USER_LOGIN_ID,
            ULVR.PARTY_ID AS ULVR_PARTY_ID,
            ULVR.ROLE_TYPE_ID AS ULVR_ROLE_TYPE_ID,
            ULVR.COMMENTS AS ULVR_COMMENTS 
         FROM
            <@table "USER_LOGIN_VALID_PARTY_ROLE"/> ULVR 
            INNER JOIN
               <@table "USER_LOGIN"/> UL 
               ON ULVR.USER_LOGIN_ID = UL.USER_LOGIN_ID 
      )
      U 
      ON A.ORGANIZATION_ID = U.ULVR_PARTY_ID 
      AND 
      (
         U.ULVR_ROLE_TYPE_ID = 'INTERNAL_ORGANIZATIO' 
      )
   LEFT OUTER JOIN
      <@table "PARTY_RELATIONSHIP"/> E 
      ON A.ORG_UNIT_ROLE_TYPE_ID = E.ROLE_TYPE_ID_FROM 
      AND A.ORG_UNIT_ID = E.PARTY_ID_FROM 
	  AND (E.PARTY_RELATIONSHIP_TYPE_ID = 'ORG_RESPONSIBLE' OR 
		  (E.PARTY_RELATIONSHIP_TYPE_ID = 'ORG_DELEGATE' AND 
			  (E.CTX_ENABLED IS NULL 
				  <#if weContextId?has_content>
					  OR E.CTX_ENABLED LIKE '%${weContextId?if_exists}%'
				  </#if>
			  )
		  )
	  )
      AND 
         (
            E.FROM_DATE <= A.ESTIMATED_COMPLETION_DATE 
            AND 
            (
               E.THRU_DATE IS NULL 
               OR E.THRU_DATE >= A.ESTIMATED_COMPLETION_DATE 
            )
         )
         AND E.PARTY_ID_TO = U.UL_PARTY_ID
      
   LEFT OUTER JOIN
      <@table "WORK_EFFORT_PARTY_ASSIGNMENT"/> F 
      ON A.WORK_EFFORT_ID = F.WORK_EFFORT_ID 
      AND A.ESTIMATED_COMPLETION_DATE = F.THRU_DATE 
      AND 
      (
         F.PARTY_ID = U.UL_PARTY_ID 
      )
   LEFT OUTER JOIN
      <@table "PARTY_RELATIONSHIP"/> Z 
      ON A.ORG_UNIT_ROLE_TYPE_ID = Z.ROLE_TYPE_ID_TO 
      AND A.ORG_UNIT_ID = Z.PARTY_ID_TO 
      AND Z.PARTY_RELATIONSHIP_TYPE_ID = 'GROUP_ROLLUP' 
      AND Z.FROM_DATE <= A.ESTIMATED_COMPLETION_DATE 
      AND 
      (
         Z.THRU_DATE IS NULL 
         OR Z.THRU_DATE >= A.ESTIMATED_COMPLETION_DATE 
      )
   LEFT OUTER JOIN
      <@table "PARTY_RELATIONSHIP"/> Y 
      ON Z.ROLE_TYPE_ID_FROM = Y.ROLE_TYPE_ID_FROM 
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
      AND Y.FROM_DATE <= A.ESTIMATED_COMPLETION_DATE 
      AND 
      (
         Y.THRU_DATE IS NULL 
         OR Y.THRU_DATE >= A.ESTIMATED_COMPLETION_DATE 
      )
      AND Y.PARTY_ID_TO = U.UL_PARTY_ID 
   LEFT OUTER JOIN
      <@table "PARTY_RELATIONSHIP"/> Z2 
      ON Z.ROLE_TYPE_ID_FROM = Z2.ROLE_TYPE_ID_TO 
      AND Z.PARTY_ID_FROM = Z2.PARTY_ID_TO 
      AND Z2.PARTY_RELATIONSHIP_TYPE_ID = 'GROUP_ROLLUP' 
      AND Z2.FROM_DATE <= A.ESTIMATED_COMPLETION_DATE 
      AND 
      (
         Z2.THRU_DATE IS NULL 
         OR Z2.THRU_DATE >= A.ESTIMATED_COMPLETION_DATE
      )
   LEFT OUTER JOIN
      <@table "PARTY_RELATIONSHIP"/> Y2 
      ON Z2.ROLE_TYPE_ID_FROM = Y2.ROLE_TYPE_ID_FROM 
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
	  AND Y2.FROM_DATE <= A.ESTIMATED_COMPLETION_DATE 
      AND 
      (
         Y2.THRU_DATE IS NULL 
         OR Y2.THRU_DATE >= A.ESTIMATED_COMPLETION_DATE
      )
      AND Y2.PARTY_ID_TO = U.UL_PARTY_ID 
   WHERE  ( 1 = 1         
        <#if userLogin?has_content && userLogin.userLoginId?has_content>
            AND UL.USER_LOGIN_ID = '${userLogin.userLoginId}'
            AND U.ULVR_USER_LOGIN_ID = '${userLogin.userLoginId}'
        </#if>   
        <#if weContextId?has_content>
  	        AND B.PARENT_TYPE_ID = <@param weContextId />
        </#if>
        <#if orgUnitId?has_content>
  	        AND A.ORG_UNIT_ID = <@param orgUnitId />
        </#if> ) 
   AND 
   (
(B.IS_ROOT = 'Y' 
      AND B.IS_TEMPLATE = 'N' 
      AND B.PARENT_TYPE_ID LIKE 'CTX%'
      <#if organizationId?has_content>
  	      AND A.ORGANIZATION_ID = <@param organizationId />
      </#if> ) 
   )
   AND 
   (
((B.IS_ROOT = 'Y' 
      AND B.IS_TEMPLATE = 'N' 
      AND B.PARENT_TYPE_ID LIKE 'CTX%')) 
   )
   AND (1 = 0
  	  <#if isOrgMgr?default(false) == true>
           OR E.PARTY_ID_TO IS NOT NULL
      </#if>
      <#if isSup?default(false) == true>
    	   OR Y.PARTY_ID_TO IS NOT NULL
      </#if>
      <#if isRole?default(false) == true>
          OR F.PARTY_ID IS NOT NULL
      </#if>
      <#if isTop?default(false) == true>
          OR Y2.PARTY_ID_TO IS NOT NULL
      </#if> )
GROUP BY
   A.WORK_EFFORT_ID,
   A.CURRENT_STATUS_ID,
   A.WORK_EFFORT_PARENT_ID,
   A.ORGANIZATION_ID,
   A.ORG_UNIT_ROLE_TYPE_ID,
   A.ORG_UNIT_ID,
   M.PARTY_NAME,
   M.PARTY_NAME_LANG,
   A.SOURCE_REFERENCE_ID,
   A.WORK_EFFORT_NAME,
   A.WORK_EFFORT_NAME_LANG,
   A.ESTIMATED_START_DATE,
   A.ESTIMATED_COMPLETION_DATE,
   A.WORK_EFFORT_SNAPSHOT_ID,
   A.ETCH,
   UL.USER_LOGIN_ID,
   B.WORK_EFFORT_TYPE_ID,
   B.PARENT_TYPE_ID,
   B.IS_ROOT,
   B.IS_TEMPLATE,
   B.DESCRIPTION,
   B.DESCRIPTION_LANG,
   B.ETCH,
   B.ETCH_LANG,
   B.HIERARCHY_ASSOC_TYPE_ID,
   C.DESCRIPTION,
   C.ACT_ST_ENUM_ID,
   C.STATUS_TYPE_ID,
   C.DESCRIPTION_LANG,
   D.GL_FISCAL_TYPE_ID,
   D.NEXT_STATUS_ID,
   D.MANAGEMENT_ROLE_TYPE_ID,
   D.MANAG_WE_STATUS_ENUM_ID 
<#if queryOrderBy?has_content>
    ORDER BY ${queryOrderBy}
<#else>
    ORDER BY A.SOURCE_REFERENCE_ID ASC  
</#if>
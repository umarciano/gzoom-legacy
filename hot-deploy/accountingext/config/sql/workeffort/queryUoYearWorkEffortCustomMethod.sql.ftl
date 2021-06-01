  SELECT                        
    AC.GL_ACCOUNT_ID                        
    , WE.WORK_EFFORT_ID                        
    , WM.WORK_EFFORT_MEASURE_ID AS VOUCHER_REF                            
  <#if calcType?if_exists?contains('MAX')>
    , MAX(TE.AMOUNT) AS AMOUNT
  <#elseif calcType?if_exists?contains('MIN')> 
    , MIN(TE.AMOUNT) AS AMOUNT
  <#elseif calcType?if_exists?contains('COUNT')> 
    , COUNT(TE.AMOUNT) AS AMOUNT
  <#elseif calcType?if_exists?contains('MEDIA')> 
    , ROUND(SUM(TE.AMOUNT)/COUNT(TE.AMOUNT), UM.DECIMAL_SCALE) AS AMOUNT         
  <#else>
    , SUM(TE.AMOUNT) AS AMOUNT
  </#if>    
  FROM <@table "GL_ACCOUNT"/> AC     
  INNER JOIN <@table "GL_ACCOUNT_INPUT_CALC"/> IC ON IC.GL_ACCOUNT_ID = AC.GL_ACCOUNT_ID     
  INNER JOIN <@table "UOM"/> UM ON UM.UOM_ID = AC.DEFAULT_UOM_ID     
  INNER JOIN <@table "CUSTOM_TIME_PERIOD"/> PD ON PD.PERIOD_TYPE_ID = AC.PERIOD_TYPE_ID     
  INNER JOIN <@table "WORK_EFFORT_MEASURE"/> WM ON WM.GL_ACCOUNT_ID = AC.GL_ACCOUNT_ID AND WM.WORK_EFFORT_ID IS NOT NULL                     
    AND WM.FROM_DATE <= PD.THRU_DATE AND WM.THRU_DATE >= PD.FROM_DATE                    
  INNER JOIN <@table "WORK_EFFORT_MEASURE"/> WM2 ON WM2.GL_ACCOUNT_ID = IC.GL_ACCOUNT_ID_REF AND WM2.WORK_EFFORT_ID = WM.WORK_EFFORT_ID     
  INNER JOIN <@table "ACCTG_TRANS_ENTRY"/> TE ON TE.VOUCHER_REF = WM2.WORK_EFFORT_MEASURE_ID AND TE.AMOUNT IS NOT NULL     
  INNER JOIN <@table "ACCTG_TRANS"/> AT ON AT.ACCTG_TRANS_ID = TE.ACCTG_TRANS_ID    
  INNER JOIN <@table "WORK_EFFORT"/> WE ON WE.WORK_EFFORT_ID = WM.WORK_EFFORT_ID AND WE.WORK_EFFORT_SNAPSHOT_ID IS NULL 
  WHERE AC.GL_ACCOUNT_ID = <@param glAccountId />
    AND ((IC.GL_FISCAL_TYPE_ID IS NOT NULL AND AT.GL_FISCAL_TYPE_ID = IC.GL_FISCAL_TYPE_ID) OR (IC.GL_FISCAL_TYPE_ID IS NULL AND AT.GL_FISCAL_TYPE_ID = <@param glFiscalTypeId />))
    AND AT.TRANSACTION_DATE <= <@param refDate jdbcType.TIMESTAMP />
    AND AT.TRANSACTION_DATE >= <@param transDate jdbcType.TIMESTAMP />
    <#if workEffortId?if_exists?has_content>
		AND WE.WORK_EFFORT_ID = <@param workEffortId />
	</#if>    
  GROUP BY AC.GL_ACCOUNT_ID, WE.WORK_EFFORT_ID, WM.WORK_EFFORT_MEASURE_ID  
  
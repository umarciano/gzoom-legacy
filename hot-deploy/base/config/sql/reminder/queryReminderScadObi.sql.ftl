<#include "classpath://sql/FtlQuery-vendor-lib.ftl" />
<#include "classpath://sql/reminder/reminderCommon.sql.ftl" />

<@selectReminder />
       
<@fromReminder />


<#if filterInnerJoin?has_content>        
${filterInnerJoin} 
</#if>

<#if filterLeftJoin?has_content>        
${filterLeftJoin} 
</#if>

WHERE RTT.WORK_EFFORT_TYPE_ID_ROOT = <@param workEffortTypeId /> 
  AND FIL_W.SCHEDULED_START_DATE IS NULL

  -- GESTIONE AVVIO SOLLECITO
   AND (WTS.FREQ_SOLL IS NULL OR FIL_W.ESTIMATED_COMPLETION_DATE <= <@dateAddMonitoringDate "WTS.START_SOLL", "day" />)
   
  -- GESTIONE FREQUENZA SOLLECITO
  -- IL PRIMO PARTE SEMPRE, DAL SECONDO DIPENDE DALLA FREQUENZA E SE 0 NON PARTE PIU
   AND (FIL_W.DATA_SOLL IS NULL OR WTS.FREQ_SOLL IS NULL OR <@param monitoringDate jdbcType.TIMESTAMP /> > <@dateAdd "FIL_W.DATA_SOLL", "WTS.FREQ_SOLL", "day" />)
  
  <#if filterWhere?has_content>        
  ${filterWhere} 
  </#if>

<@groupByReminder />
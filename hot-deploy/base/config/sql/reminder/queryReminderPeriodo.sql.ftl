<#include "classpath://sql/FtlQuery-vendor-lib.ftl" />
<#include "classpath://sql/reminder/reminderCommon.sql.ftl" />


<#if retrieveWorkEffortReminder>
<@selectWorkEffortReminder />
<#else>
<@selectReminder />
</#if>
       
<@fromReminder />


INNER JOIN WORK_EFFORT_TYPE_PERIOD WTP ON WTP.WORK_EFFORT_TYPE_ID = FIL_W.WORK_EFFORT_TYPE_ID
	   AND WTP.STATUS_ENUM_ID IN ('OPEN', 'REOPEN')
	   AND WTP.ORGANIZATION_ID = FIL_W.ORGANIZATION_ID



<#if filterInnerJoin?has_content>        
${filterInnerJoin} 
</#if>

<#if filterLeftJoin?has_content>        
${filterLeftJoin} 
</#if>

WHERE RTT.WORK_EFFORT_TYPE_ID_ROOT = <@param workEffortTypeId /> 
  AND WTP.PER_LAV_FROM < <@param monitoringDate jdbcType.TIMESTAMP />
   
  -- GESTIONE FREQUENZA SOLLECITO
  -- IL PRIMO PARTE SEMPRE, DAL SECONDO DIPENDE DALLA FREQUENZA E SE 0 NON PARTE PIU
   AND (FIL_W.DATA_SOLL IS NULL OR RTS.FREQ_SOLL IS NULL OR <@monitoringDateSenzaOra/> > <@dateAddWithoutTime "FIL_W.DATA_SOLL", "RTS.FREQ_SOLL", "day" />)
  
  <#if filterWhere?has_content>        
  ${filterWhere} 
  </#if>

<#if retrieveWorkEffortReminder>
<@groupByWorkEffortReminder />
<#else>
<@groupByReminder />
</#if>
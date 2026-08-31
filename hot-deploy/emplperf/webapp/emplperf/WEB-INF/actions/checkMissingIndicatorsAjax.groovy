import org.ofbiz.base.util.*
import org.ofbiz.entity.condition.*

// Recupera workEffortId
def workEffortId = parameters.workEffortId

println "[checkMissingIndicatorsAjax] Controllo indicatori per workEffortId=${workEffortId}"

def hasAllIndicators = true
def missingCount = 0
def missingNames = []

try {
    if (workEffortId) {
        // Query tutti gli indicatori della scheda
        def measureConditions = [
            EntityCondition.makeCondition("workEffortId", EntityOperator.EQUALS, workEffortId)
        ]
        def measureCondition = EntityCondition.makeCondition(measureConditions, EntityOperator.AND)
        def measures = delegator.findList("WorkEffortMeasure", measureCondition, null, null, null, false)
        
        println "[checkMissingIndicatorsAjax] Trovati ${measures?.size() ?: 0} indicatori"
        
        if (measures != null && measures.size() > 0) {
            measures.each { measure ->
                def measureId = measure.getString("workEffortMeasureId")
                def glAccountId = measure.getString("glAccountId")
                def measureName = measure.getString("uomDescr") ?: "Indicatore " + measureId
                
                // Controlla se ha valore in AcctgTransAndEntries
                if (glAccountId != null) {
                    def valueConditions = [
                        EntityCondition.makeCondition("glAccountId", EntityOperator.EQUALS, glAccountId),
                        EntityCondition.makeCondition("workEffortId", EntityOperator.EQUALS, workEffortId)
                    ]
                    def valueCondition = EntityCondition.makeCondition(valueConditions, EntityOperator.AND)
                    def transEntries = delegator.findList("AcctgTransAndEntries", valueCondition, null, ["-transactionDate"], null, false)
                    
                    if (transEntries == null || transEntries.isEmpty()) {
                        hasAllIndicators = false
                        missingCount++
                        missingNames.add(measureName)
                        println "[checkMissingIndicatorsAjax] MANCANTE: ${measureName}"
                    }
                }
            }
        }
    }
    
    // Costruisci JSON response manualmente
    def jsonResponse = '{"success":true,"hasAllIndicators":' + hasAllIndicators + ',"missingCount":' + missingCount + ',"missingNames":[]}'
    
    println "[checkMissingIndicatorsAjax] Response: ${jsonResponse}"
    
    // Scrivi nella response
    response.setContentType("application/json")
    response.setCharacterEncoding("UTF-8")
    def writer = response.getWriter()
    writer.write(jsonResponse)
    writer.flush()
    
    return "success"
    
} catch (Exception e) {
    println "[checkMissingIndicatorsAjax] ERRORE: ${e.message}"
    e.printStackTrace()
    
    def jsonError = '{"success":false,"error":"' + e.message.replace('"', '\\"') + '"}'
    
    response.setContentType("application/json")
    response.setCharacterEncoding("UTF-8")
    def writer = response.getWriter()
    writer.write(jsonError)
    writer.flush()
    
    return "error"
}

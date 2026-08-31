import org.ofbiz.base.util.*
import org.ofbiz.entity.condition.*

// Recupera parametri
def glAccountId = parameters.glAccountId
def workEffortId = parameters.workEffortId
def workEffortMeasureId = parameters.workEffortMeasureId

println "[getIndicatorValueAjax] Recupero valore per glAccountId=${glAccountId}, workEffortId=${workEffortId}, workEffortMeasureId=${workEffortMeasureId}"

def indicatorValue = ""

try {
    // Query per recuperare l'ultimo valore salvato usando API vecchia OFBiz
    def conditions = [
        EntityCondition.makeCondition("glAccountId", EntityOperator.EQUALS, glAccountId),
        EntityCondition.makeCondition("workEffortId", EntityOperator.EQUALS, workEffortId)
    ]
    def conditionList = EntityCondition.makeCondition(conditions, EntityOperator.AND)
    
    def transactions = delegator.findList(
        "AcctgTransAndEntries",
        conditionList,
        null,
        ["-transactionDate"],
        null,
        false
    )
    
    if (transactions && transactions.size() > 0) {
        def firstTrans = transactions.get(0)
        def amount = firstTrans.amount
        
        if (amount != null) {
            // Formatta come intero senza decimali
            indicatorValue = String.valueOf(amount.intValue())
            println "[getIndicatorValueAjax] Valore trovato: ${indicatorValue}"
        }
    } else {
        println "[getIndicatorValueAjax] Nessuna transazione trovata"
    }
    
    // Costruisci JSON manualmente e scrivi direttamente nella response
    def jsonResponse = "{\"success\":true,\"indicatorValue\":\"${indicatorValue}\",\"workEffortMeasureId\":\"${workEffortMeasureId}\"}"
    
    // Scrivi direttamente nella response HTTP
    response.setContentType("application/json")
    response.setCharacterEncoding("UTF-8")
    def writer = response.getWriter()
    writer.write(jsonResponse)
    writer.flush()
    
    return "success"
    
} catch (Exception e) {
    println "[getIndicatorValueAjax] ERRORE: ${e.message}"
    e.printStackTrace()
    
    def jsonError = "{\"success\":false,\"error\":\"${e.message}\"}"
    
    response.setContentType("application/json")
    response.setCharacterEncoding("UTF-8")
    def writer = response.getWriter()
    writer.write(jsonError)
    writer.flush()
    
    return "error"
}

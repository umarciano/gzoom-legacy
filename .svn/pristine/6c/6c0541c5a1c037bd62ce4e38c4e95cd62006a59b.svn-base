
/** -------------
 * Nota: per funzionare correttamente TreeviewMgr ha bisgno che sia caricata sia jQuery che resolve.conflict.js
 */

RegisterTreeViewResponder = {
    load : function() {
        UpdateAreaResponder.Responders.register( {
            onLoad : function(newContent) {
                if (typeof TreeviewMgr != 'undefined') {
                  	TreeviewMgr.loadTree(newContent);
                }
            }
        }, 'register-treeview-responder');

    }

}

document.observe("dom:loaded", RegisterTreeViewResponder.load);
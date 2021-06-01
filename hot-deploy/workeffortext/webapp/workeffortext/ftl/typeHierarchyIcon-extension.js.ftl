TypeHierarchyIcon = {

    load: function() {
        var icon = $$("i.typeHierarchyIcon")[0];
        if (Object.isElement(icon)) {
            var form = icon.up("form");
            var workEffortId = form.down("input[name='workEffortId']");
            icon.observe("click", TypeHierarchyIcon.openPopup.curry(workEffortId));   
        }
    },
    
    openPopup: function(workEffortId, event) {
        new Ajax.Request("<@ofbizUrl>workEffortTypeHierarchyPopup</@ofbizUrl>", {
            parameters: {
                workEffortId: workEffortId.getValue()
            },
            onSuccess: function(transport) {
                Utils.showModalBox(transport.responseText);
            }
        });
        
    }
}
<#if loadTypeHierarchyIcon?if_exists == "typeHierarchyIconLoading">
	<#assign loadTypeHierarchyIcon="typeHierarchyIconLoaded"/>
	TypeHierarchyIcon.load();
</#if>

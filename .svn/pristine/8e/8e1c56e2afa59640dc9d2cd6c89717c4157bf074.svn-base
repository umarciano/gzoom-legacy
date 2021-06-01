WorkEffortTypePeriodSearch = {
    load: function() {
        var form = $$("form[name='WorkEffortTypePeriodSearchForm']")[0];
        if(!Object.isElement(form)) {
            form = $$("form[name='WorkEffortTypePeriodViewSearchForm']")[0];
        }
        if(Object.isElement(form)) {
            var select = form.down("input[value='CLOSE']");
            if(Object.isElement(select) && "statusEnumId" == select.readAttribute("name")) {
                select.removeAttribute("checked");
            }
        }
    }
}

WorkEffortTypePeriodSearch.load();
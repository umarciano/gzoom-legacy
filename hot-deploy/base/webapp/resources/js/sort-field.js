SortField = {
    load : function(newContentToExplore, withoutResponder) {
        tableList = !Object.isElement(newContentToExplore) ? $(document.body).select('table.list-table') : (newContentToExplore.hasClassName('list-table') ? [newContentToExplore] : newContentToExplore.select('table.list-table'));
        tableList.each(function(table) {
            table.select('a').each(function(element) {
                Event.observe(element, 'click', function(e) {
                    elm = Event.element(e);
                    var jar = new CookieJar({path : "/"});
                    var order = jar.get("sortField_" + table.identify());
                    var sortField = elm.up('th');
                    if (sortField) {
                        sortField = sortField.identify().substring(sortField.identify().indexOf('.') + 1); 
                        sortField = element.hasClassName('sort-order-desc') ? '-' + sortField :'' + sortField;
                        if (order) {
                            if(order !== sortField) {
                                jar.remove("sortField_" + table.identify());
                                jar.put("sortField_" + table.identify(), sortField);
                            }
                        }
                        else {
                            jar.put("sortField_" + table.identify(), sortField);
                        }
                    }
                });
            });        
        });
        
        if (!withoutResponder) {
            UpdateAreaResponder.Responders.register( {
                onLoad : function(newContent) {
                    if (newContent) {
                        SortField.load(newContent, true);
                    }
                }
            }, 'register-sort-field');
        }
    }
}   
        
document.observe("dom:loaded", SortField.load);
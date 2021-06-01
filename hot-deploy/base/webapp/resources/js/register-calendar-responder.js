RegisterCalendarResponder = {
    load : function() {
        if (typeof CalendarDateSelect != 'undefined') {
            UpdateAreaResponder.Responders.register( {
                onLoad : function(newContent) {
                    if (newContent) {
                        CalendarDateSelect.reloadCalendar(newContent);
                    }
                }
            }, 'register-calendar-responder');
        }
    }

}

document.observe("dom:loaded", RegisterCalendarResponder.load);
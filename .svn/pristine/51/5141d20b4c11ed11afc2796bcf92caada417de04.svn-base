$j('div.mblContainer').qtip({
    position: {
        target: 'mouse'
    },
    style: {
        width: 550,
        color: 'black',
        name: 'light',
        'padding-top': 8,
        'padding-left': 8,
        'padding-right': 8,
        'padding-bottom': 0
    },
    show: {
        solo: true,
    },
    content : {
        data : {},
        url: '<@ofbizUrl>${tooltipRequest}</@ofbizUrl>',
        method: 'post'
    },
    api: {
        beforeContentLoad: function() {
            var parameters = $(this.elements.target).children("div.mblParameter");
            var data = this.options.content.data;
            $j(parameters).each( function() {
                var inp = document.createElement("input");
                data[$j(this).attr("name")] = $j(this).attr("value");
            });
            
            return this;
        }
    }
});
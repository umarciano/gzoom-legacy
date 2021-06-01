$j('img.speedometer_enlarge').qtip({
    position: {
        target: 'mouse',
 		adjust: { screen: true }        
    },
	hide: {	
		when: 'mouseout',
		fixed: true 
	},      
    style: {
        color: 'black',
        name: 'cream',
        width: {
            value: 100,
            min: 150,
            max: 650
        },
        textAlign: 'center'
    },
    show: {
        solo: true,
    },
    api: {
        beforeContentUpdate: function(content) {
            var target = $j(this.elements.target).attr("target");
            if ("" != target) {
                return "<div class='speedometer_show_speed' style='width: 3.0em !important;' >" + $j(this.elements.target).attr("speed") + "/" + target + "</div>";
            } else {
                return "<div class='speedometer_show_speed'>" + $j(this.elements.target).attr("speed") + "</div>";
            }
        }
    }
});
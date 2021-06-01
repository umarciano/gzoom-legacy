//
// Sandro: ho aggiunto come parametri di creazione adjust e hide per evitare che si modifichi la dimensione schermo all'apertura tooltip e 
// anche il flicker dell'immagine che si generava (parametro hide)

populateTooltip = function(selectors) {
    if (selectors) {
        $A(selectors).each(function(selector) {
            $j(selector).qtip({
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
                        value: 450,
                        min: 150,
                        max: 650
                    }
                },
                api: {
                    beforeContentUpdate: function(content) {
                        return ($(this.elements.target).attr("description") != undefined) ? $(this.elements.target).attr("description") : false;
                    } 
                }
            });
        });
    }
}

populateTooltipValue = function(selectors) {
    if (selectors) {
        $A(selectors).each(function(selector) {
            $j(selector).qtip({
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
                        value: 80
                    }
                },
                api: {
                    beforeContentUpdate: function(content) {
                        return ($(this.elements.target).attr("value") != undefined) ? $(this.elements.target).attr("value") : false;
                    } 
                }
            });
        });
    }
}
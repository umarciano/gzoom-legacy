// Nota sandro 04-11 - da una ricerca sembra che questo js non sia utilizzato da nessuna parte
$j('div.mblShowDescription').qtip({
    position: {
        target: 'mouse'
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
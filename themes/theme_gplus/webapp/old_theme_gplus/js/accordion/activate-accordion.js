ActivateAccorsion = {
    load: function() {
        if ($('vertical_container')) {
            var bottomAccordion = new accordion('vertical_container');

            var nestedVerticalAccordion = new accordion('vertical_nested_container', {
              classNames : {
                    toggle : 'vertical_accordion_toggle',
                    toggleActive : 'vertical_accordion_toggle_active',
                    content : 'vertical_accordion_content'
                }
            });

            // Open first one
            bottomAccordion.activate($$('#vertical_container .accordion_toggle')[0]);

            // Open second one
            //topAccordion.activate($$('#horizontal_container .horizontal_accordion_toggle')[2]);
        }
    }
};

document.observe("dom:loaded", ActivateAccorsion.load);
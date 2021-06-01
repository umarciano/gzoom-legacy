<#if defaultStyleList?has_content>

//Per originale vedi formatblock.js

(function($) {
elRTE.prototype.ui.prototype.buttons.customformatlist = function(rte, name) {
    this.constructor.prototype.constructor.call(this, rte, name);

    var cmd = this.rte.browser.msie 
        ? function(v) { self.val = v; self.constructor.prototype.command.call(self); }
        : function(v) { self.ieCommand(v); } 
    var self = this;
    var opts = {
        labelTpl : '%label',
        tpls     : {'' : '%label'},
        select   : function(v) { self.customformatlist(v); },
        src      : {
            'span'    : this.rte.i18n('Format'),
            <#if defaultStyleList?has_content && !style1?has_content && !style2?has_content && !style3?has_content>
                <#list defaultStyleList as defaultStyle>
                    '${defaultStyle.contentName}'      : this.rte.i18n('${defaultStyle.description}'),
                </#list>
            </#if>
            <#if style1?has_content>
            '${style1.contentName}'      : this.rte.i18n('${style1.description}'),
            </#if>
            <#if style2?has_content>
            '${style2.contentName}'      : this.rte.i18n('${style2.description}'),
            </#if>
            <#if style3?has_content>
            '${style3.contentName}'      : this.rte.i18n('${style3.description}'),
            </#if>
        }
    }

    this.select = this.domElem.elSelect(opts);
    
    this.command = function() {

    }
    
    this.customformatlist = function(v) {

        function format(n, tag) {
            
            function replaceChilds(p) {
                $(p).find('h1,h2,h3,h4,h5,h6,p,address,pre').each(function() {
                    $(this).replaceWith($(this).html());
                });
                return p;
            }
            
            if (/^(LI|DT|DD|TD|TH|CAPTION)$/.test(n.nodeName)) {
                !self.rte.dom.isEmpty(n) && self.rte.dom.wrapContents(replaceChilds(n), tag);
            } else if (/^(UL|OL|DL|TABLE)$/.test(n.nodeName)) {
                self.rte.dom.wrap(n, tag);
            } else {
                !self.rte.dom.isEmpty(n) && $(replaceChilds(n)).replaceWith( $(self.rte.dom.create(tag)).html($(n).html()));
            }
            
        }
        this.rte.history.add();

        var tag = v.toUpperCase(),
            i, n, $n,
            c = this.rte.selection.collapsed(),
            bm = this.rte.selection.getBookmark(),
            nodes = this.rte.selection.selected({
                collapsed : true,
                blocks    : true,
                filter    : 'textContainsNodes',
                wrap      : 'inline',
                tag       : 'span'
            })
            l = nodes.length,
            s = $(nodes[0]).prev(),
            e = $(nodes[nodes.length-1]).next();

        while (l--) {
            n = nodes[l];
            $n = $(n);
            if (tag == 'DIV' || tag == 'SPAN') {
                if (/^(H[1-6]|P|ADDRESS|PRE)$/.test(n.nodeName)) {
                    $n.replaceWith($(this.rte.dom.create('div')).html($n.html()||''));
                }
            } else {
                if (/^(THEAD|TBODY|TFOOT|TR)$/.test(n.nodeName)) {
                    $n.find('td,th').each(function() { format(this, tag); });
                } else if (n.nodeName != tag) {
                    format(n, tag);
                }
            }
        }

        this.rte.selection.moveToBookmark(bm);

        this.rte.ui.update(true);
    }
    
    this.update = function() {
        this.domElem.removeClass('disabled');
        var n = this.rte.dom.selfOrParent(this.rte.selection.getNode(), /^(H[1-6]|P|ADDRESS|PRE)$/);
        this.select.val(n ? n.nodeName.toLowerCase() : 'span');
    }
}
})(jQuery);
</#if>
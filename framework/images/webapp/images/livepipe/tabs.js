/**
* @author Ryan Johnson <http://syntacticx.com/>
* @copyright 2008 PersonalGrid Corporation <http://personalgrid.com/>
* @package LivePipe UI
* @license MIT
* @url http://livepipe.net/control/tabs
* @require prototype.js, livepipe.js
*/

if(typeof(Prototype) == "undefined")
  throw "Control.Tabs requires Prototype to be loaded.";
if(typeof(Object.Event) == "undefined")
  throw "Control.Tabs requires Object.Event to be loaded.";

Control.Tabs = Class.create({
  initialize: function(tab_list_container,options){
    if(!$(tab_list_container))
      throw "Control.Tabs could not find the element: " + tab_list_container;

    this.options = {
      beforeChange: Prototype.emptyFunction,
      afterChange: Prototype.emptyFunction,
      hover: false,
      linkSelector: 'li a',
      setClassOnContainer: false,
      activeClassName: 'active',
      defaultTab: 'first',
      autoLinkExternal: true,
      targetRegExp: /#(.+)$/,
      showFunction: Element.show,
      hideFunction: Element.hide
    };

    Object.extend(this.options,options || {});
    this.loadTab(tab_list_container, true);
  },
  loadTab : function(tab_list_container, init) {
      this.activeContainer = false;
      this.activeLink = false;
      this.containers = $H({});
      this.links = [];
      this.id=tab_list_container.identify();

      if (Control.Tabs.instances.indexOf(this) == -1)
        Control.Tabs.instances.push(this);
      (typeof(this.options.linkSelector == 'string')
        ? $(tab_list_container).select(this.options.linkSelector)
        : this.options.linkSelector($(tab_list_container))
      ).findAll(function(link){
          return (/^#/).exec((Prototype.Browser.WebKit ? decodeURIComponent(link.href) : link.href).replace(window.location.href.split('#')[0],''));
      }).each(function(link){
          this.addTab(link);
      }.bind(this));
      this.containers.values().each(Element.hide);
      
      // if this.options.defaultTab does not exists, set first tab
      if (this.options.defaultTab != 'first' && this.options.defaultTab != 'last') {
    	  tabTmp = this.links.find(function(link){
    		  if(typeof(this.options.defaultTab) == 'string') {
    			  return link.key == this.options.defaultTab;
    		  }
              return link == this.options.defaultTab;
          }.bind(this));
    	  if(! Object.isElement(tabTmp)) {
    		  this.options.defaultTab = 'first';
    	  }
      }
      if (this.options.defaultTab == 'first'){
          this.setActiveTab(this.links.first(), init);
      }
      else if (this.options.defaultTab == 'last')
          this.setActiveTab(this.links.last(), init);
      else {
          this.setActiveTab(this.options.defaultTab, init);
      }

      var targets = this.options.targetRegExp.exec(window.location);
      if(targets && targets[1]){
          targets[1].split(',').each(function(target){
              this.setActiveTab(this.links.find(function(link){
                  return link.key == target;
              }));
          }.bind(this));
      }
      if(this.options.autoLinkExternal){
          $A(document.getElementsByTagName('a')).each(function(a){
              if(!this.links.include(a)){
                  var clean_href = a.href.replace(window.location.href.split('#')[0],'');
                  if(clean_href.substring(0,1) == '#'){
                      if(this.containers.keys().include(clean_href.substring(1))){
                          $(a).observe('click',function(event,clean_href){

                              this.setActiveTab(clean_href.substring(1));
                          }.bindAsEventListener(this,clean_href));
                      }
                  }
              }
          }.bind(this));
      }

      return this;
  },
  addTab: function(link){
    this.links.push(link);
    link.key = link.getAttribute('href').replace(window.location.href.split('#')[0],'').split('/').last().replace(/#/,'');
    link.key = link.key.split('?')[0];
    var container = $(link.key);
    if(!container)
      throw "Control.Tabs: #" + link.key + " was not found on the page."
    this.containers.set(link.key,container);
    link[this.options.hover ? 'onmouseover' : 'onclick'] = function(link){
      if(window.event)
        Event.stop(window.event);
      this.setActiveTab(link);

      return false;
    }.bind(this,link);
  },
  // setActiveTab recursive method
  setActiveTab: function(link, init){
      if(!link && typeof(link) == 'undefined')
      return;
    if(typeof(link) == 'string'){
      this.setActiveTab(this.links.find(function(_link){
        return _link.key == link;
      }), init);
    }else if(typeof(link) == 'number'){
      this.setActiveTab(this.links[link], init);
    }else{
      if(this.notify('beforeChange',this.activeContainer,this.containers.get(link.key), init) == false)
        return;

      if(this.activeContainer)
        this.options.hideFunction(this.activeContainer);
      this.links.each(function(item){
        (this.options.setClassOnContainer ? $(item.parentNode) : item).removeClassName(this.options.activeClassName);
      }.bind(this));
      (this.options.setClassOnContainer ? $(link.parentNode) : link).addClassName(this.options.activeClassName);
      this.activeContainer = this.containers.get(link.key);
      this.activeLink = link;
      this.options.showFunction(this.containers.get(link.key));
      this.notify('afterChange',this.containers.get(link.key), init);
    }
  },
  next: function(){
    this.links.each(function(link,i){
      if(this.activeLink == link && this.links[i + 1]){
        this.setActiveTab(this.links[i + 1]);
        throw $break;
      }
    }.bind(this));
  },
  previous: function(){
    this.links.each(function(link,i){
      if(this.activeLink == link && this.links[i - 1]){
        this.setActiveTab(this.links[i - 1]);
        throw $break;
      }
    }.bind(this));
  },
  first: function(){
    this.setActiveTab(this.links.first());
  },
  last: function(){
    this.setActiveTab(this.links.last());
  }
});
Object.extend(Control.Tabs,{
  instances: [],
  findByTabId: function(id, tabId){
    return Control.Tabs.instances.find(function(tab){
      var found = tab.links.find(function(link){
        if (!Object.isString(link))
            return link.key == id;
        else
            return link == id;
      });
      if (!found && tab.id == tabId) {
      	found = tab;
      }
      return found;
    });
  }
});
Object.Event.extend(Control.Tabs);
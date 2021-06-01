// CalendarDateSelect version 1.15 - a prototype based date picker
// Questions, comments, bugs? - see the project page: http://code.google.com/p/calendardateselect
if (typeof Prototype == 'undefined') alert("CalendarDateSelect Error: Prototype could not be found. Please make sure that your application's layout includes prototype.js (.g. <%= javascript_include_tag :defaults %>) *before* it includes calendar_date_select.js (.g. <%= calendar_date_select_includes %>).");
if (Prototype.Version < "1.6") alert("Prototype 1.6.0 is required.  If using earlier version of prototype, please use calendar_date_select version 1.8.3");

Element.addMethods({
  purgeChildren: function(element) { $A(element.childNodes).each(function(e){$(e).remove();}); },
  build: function(element, type, options, style) {
    var newElement = Element.buildAndAppend(type, options, style);
    element.appendChild(newElement);
    return newElement;
  }
});

Element.buildAndAppend = function(type, options, style)
{
  var e = $(document.createElement(type));
  $H(options).each(function(pair) { e[pair.key] = pair.value });
  if (style) e.setStyle(style);
  return e;
};
nil = null;

Date.one_day = 24*60*60*1000;
Date.weekdays = $w("S M T W T F S");
Date.first_day_of_week = 0;
Date.months = $w("January February March April May June July August September October November December" );
Date.prototype.getPaddedDays = function() { return Date.padded2(this.getDate()); }
Date.prototype.getPaddedMonth = function() { return Date.padded2(this.getMonth()+1); }
Date.padded2 = function(hour) { var padded2 = parseInt(hour, 10); if (hour < 10) padded2 = "0" + padded2; return padded2; }
Date.prototype.getPaddedMinutes = function() { return Date.padded2(this.getMinutes()); }
Date.prototype.getAMPMHour = function() { var hour = this.getHours(); return (hour == 0) ? 12 : (hour > 12 ? hour - 12 : hour ) }
Date.prototype.getAMPM = function() { return (this.getHours() < 12) ? "AM" : "PM"; }
Date.prototype.stripTime = function() { return new Date(this.getFullYear(), this.getMonth(), this.getDate());};
Date.prototype.daysDistance = function(compare_date) { return Math.round((compare_date - this) / Date.one_day); };
Date.prototype.toFormattedString = function(include_time){
  var hour, str;
  str = Date.months[this.getMonth()] + " " + this.getDate() + ", " + this.getFullYear();

  if (include_time) { hour = this.getHours(); str += " " + this.getAMPMHour() + ":" + this.getPaddedMinutes() + " " + this.getAMPM() }
  return str;
}
/* mod for OFBiz Date Field */
  Date.prototype.toFormattedString_ofbiz_default = function(include_time){
  var hour, str;
  str = this.getFullYear() + "-" + Date.padded2(this.getMonth() + 1) + "-" + Date.padded2(this.getDate());

  if (include_time) { hour=this.getHours(); str += " " + this.getHours() + ":" + this.getPaddedMinutes() + ":" + this.getSeconds() + "." + this.getMilliseconds() }
  return str;
}
/* mod end*/
Date.parseFormattedString = function(string) { return new Date(string);}
Math.floor_to_interval = function(n, i) { return Math.floor(n/i) * i;}
window.f_height = function() { return( [window.innerHeight ? window.innerHeight : null, document.documentElement ? document.documentElement.clientHeight : null, document.body ? document.body.clientHeight : null].select(function(x){return x>0}).first()||0); }
window.f_width = function() { return( [window.innerWidth ? window.innerWidth : null, document.documentElement ? document.documentElement.clientWidth : null, document.body ? document.body.clientWidth : null].select(function(x){return x>0}).first()||0); }
window.f_scrollTop = function() { return ([window.pageYOffset ? window.pageYOffset : null, document.documentElement ? document.documentElement.scrollTop : null, document.body ? document.body.scrollTop : null].select(function(x){return x>0}).first()||0 ); }
window.f_scrollLeft = function() { return ([window.pageXOffset ? window.pageXOffset : null, document.documentElement ? document.documentElement.scrollLeft : null, document.body ? document.body.scrollLeft : null].select(function(x){return x>0}).first()||0 ); }

_translations = {
  "OK": "OK",
  "Now": "Now",
  "Today": "Today",
  "Clear": "Clear"
}
SelectBox = Class.create();
SelectBox.prototype = {
  initialize: function(parent_element, values, html_options, style_options) {
    this.element = $(parent_element).build("select", html_options, style_options);
    this.populate(values);
  },
  populate: function(values) {
    this.element.purgeChildren();
    var that = this; $A(values).each(function(pair) { if (typeof(pair)!="object") {pair = [pair, pair]}; that.element.build("option", { value: pair[1], innerHTML: pair[0]}) });
  },
  setValue: function(value) {
    var e = this.element;
    var matched = false;
    $R(0, e.options.length - 1 ).each(function(i) { if(e.options[i].value==value.toString()) {e.selectedIndex = i; matched = true;}; } );
    return matched;
  },
  getValue: function() { return $F(this.element)}
}
CalendarDateSelect = Class.create();
CalendarDateSelect.prototype = {
  initialize: function(target_element, options) {
    this.target_element = $(target_element); // make sure it's an element, not a string
    if (!this.target_element) { alert("Target element " + target_element + " not found!"); return false;}
    if (this.target_element.tagName != "INPUT") this.target_element = this.target_element.down("INPUT")

    this.target_element.calendar_date_select = this;
    this.last_click_at = 0;
    // initialize the date control
    this.options = $H({
      embedded: false,
      popup: nil,
      time: false,
      buttons: true,
      clear_button: true,
      year_range: 25,
      close_on_click: nil,
      minute_interval: 5,
      popup_by: this.target_element,
      month_year: "dropdowns",
      onchange: this.target_element.onchange,
      valid_date_check: nil,
      parent: false
    }).merge(options || {});
    this.use_time = this.options.get("time");
    this.parseDate();
    this.callback("before_show")
    this.initCalendarDiv();
    if(!this.options.get("embedded")) {
      this.positionCalendarDiv()
      // set the click handler to check if a user has clicked away from the document
      Event.observe(document, "mousedown", this.closeIfClickedOut_handler = this.closeIfClickedOut.bindAsEventListener(this));
      Event.observe(document, "keypress", this.keyPress_handler = this.keyPress.bindAsEventListener(this));
    }
    this.callback("after_show")
  },
  positionCalendarDiv: function() {
    var above = false;
    var anchorToRight = false;
    var c_pos = this.calendar_div.cumulativeOffset(), c_left = c_pos[0], c_top = c_pos[1], c_dim = this.calendar_div.getDimensions(), c_height = c_dim.height, c_width = c_dim.width;
    var w_top = window.f_scrollTop(), w_height = window.f_height(), w_left = window.f_scrollLeft(), w_width = window.f_width();
    var e_dim = $(this.options.get("popup_by")).cumulativeOffset(), e_top = e_dim[1], e_left = e_dim[0], e_width = $(this.options.get("popup_by")).getDimensions().width, e_height = $(this.options.get("popup_by")).getDimensions().height, e_bottom = e_top + e_height, e_right = e_left + e_width;
    
    var parent_left = 0, parent_top = 0;
    if (this.options.get('parent')) {
    	var parent = $(this.options.get('parent'));
    	if (Object.isElement(parent)) {
    		var parent_dim = parent.cumulativeOffset();
    		parent_left = parent_dim[0];
    		parent_top = parent_dim[1];
    	}
    }

    if ( (( e_bottom + c_height ) > (w_top + w_height)) && ( e_bottom - c_height > w_top )) above = true;
    if ( (e_left + c_width) > (w_left + w_width)) anchorToRight = true;
    /* mod for OFBiz layered lookups */
    var left_px = (anchorToRight ? (e_right - c_width) : ((e_left-parent_left).abs())).toString() + "px", top_px = (above ? (e_top - c_height ) : ( (e_bottom - parent_top).abs())).toString() + "px";
    /*var top_px = (above ? -(e_height + c_height) : "0").toString() + "px";*/
    /* end mod*/

    /* mod for OFBiz layered lookups */
    this.calendar_div.style.left = left_px;  this.calendar_div.style.top = top_px;
    /*this.calendar_div.style.left = "0px";  this.calendar_div.style.top = top_px;*/
    /* end mod*/

    this.calendar_div.setStyle({visibility:""});
    /* mod for OFBiz layered lookups*/
    /*this.target_element.up().style.height = e_height.toString() + "px";*/
    /* end mod*/

    // draw an iframe behind the calendar -- ugly hack to make IE 6 happy
    if(navigator.appName=="Microsoft Internet Explorer") this.iframe = $(document.body).build("iframe", {src: "javascript:false", className: "ie6_blocker"}, { left: left_px, top: top_px, height: c_height.toString()+"px", width: c_width.toString()+"px", border: "0px"})
  },
  initCalendarDiv: function() {
    if (this.options.get("embedded")) {
      var parent = this.target_element.parentNode;
      var style = {}
    } else {
    	var parent = document.body
    	if (this.options.get("parent")) {
    		var optionParent = $(this.options.get("parent"));
    		if (Object.isElement(optionParent)) {
    			parent = $(optionParent);
    		}
    	}
    	
    	if(document.body.scrollTop > 0)
    	{
    	    document.body.scrollTop = 0;
    	}
    	
      /* mod for OFBiz layered lookups */
      var style = { position:"absolute", visibility: "hidden", left:0, top:0 }
      /*var style = { position:"relative", visibility: "hidden", left:0, top:0 }*/
      /* end mod */
    }
    this.calendar_div = $(parent).build('div', {className: "calendar_date_select"}, style);

    var that = this;
    // create the divs
    $w("top header body buttons footer bottom").each(function(name) {
      eval("var " + name + "_div = that." + name + "_div = that.calendar_div.build('div', { className: 'cds_"+name+"' }, { clear: 'left'} ); ");
    });

    this.initHeaderDiv();
    this.initButtonsDiv();
    this.initCalendarGrid();
    this.updateFooter("&#160;");

    this.refresh();
    this.setUseTime(this.use_time);
  },
  initHeaderDiv: function() {
    var header_div = this.header_div;
    // mod for ofbiz 
    //this.close_button = header_div.build("a", { innerHTML: "x", href:"#", onclick:function () { this.close(); return false; }.bindAsEventListener(this), className: "close" });
    // end mod
    this.next_month_button = header_div.build("a", { innerHTML: "&gt;", href:"#", onclick:function () { this.navMonth(this.date.getMonth() + 1 ); return false; }.bindAsEventListener(this), className: "next" });
    this.prev_month_button = header_div.build("a", { innerHTML: "&lt;", href:"#", onclick:function () { this.navMonth(this.date.getMonth() - 1 ); return false; }.bindAsEventListener(this), className: "prev" });

    if (this.options.get("month_year")=="dropdowns") {
      this.month_select = new SelectBox(header_div, $R(0,11).map(function(m){return [Date.months[m], m]}), {className: "month", onchange: function () { this.navMonth(this.month_select.getValue()) }.bindAsEventListener(this)});
      this.year_select = new SelectBox(header_div, [], {className: "year", onchange: function () { this.navYear(this.year_select.getValue()) }.bindAsEventListener(this)});
      this.populateYearRange();
    } else {
      this.month_year_label = header_div.build("span")
    }
  },
  initCalendarGrid: function() {
    var body_div = this.body_div;
    this.calendar_day_grid = [];
    var days_table = body_div.build("table", { cellPadding: "0px", cellSpacing: "0px", width: "100%" })
    // make the weekdays!
    var weekdays_row = days_table.build("thead").build("tr");
    Date.weekdays.each( function(weekday) {
      weekdays_row.build("th", {innerHTML: weekday});
    });

    var days_tbody = days_table.build("tbody")
    // Make the days!
    var row_number = 0, weekday;
    for(var cell_index = 0; cell_index<42; cell_index++)
    {
      weekday = (cell_index+Date.first_day_of_week ) % 7;
      if ( cell_index % 7==0 ) days_row = days_tbody.build("tr", {className: 'row_'+row_number++});
      (this.calendar_day_grid[cell_index] = days_row.build("td", {
          calendar_date_select: this,
          onmouseover: function () { this.calendar_date_select.dayHover(this); },
          onmouseout: function () { this.calendar_date_select.dayHoverOut(this) },
          onclick: function() { this.calendar_date_select.updateSelectedDate(this, true); },
          className: (weekday==0) || (weekday==6) ? " weekend" : "" //clear the class
        },
        { cursor: "pointer" }
      )).build("div");
      this.calendar_day_grid[cell_index];
    }
  },
  initButtonsDiv: function()
  {
    var buttons_div = this.buttons_div;
    if (this.options.get("time"))
    {
      var blank_time = $A(this.options.get("time")=="mixed" ? [[" - ", ""]] : []);
      buttons_div.build("span", {innerHTML:"@", className: "at_sign"});

      var t = new Date();
      this.hour_select = new SelectBox(buttons_div,
        blank_time.concat($R(0,23).map(function(x) {t.setHours(x); return $A([t.getAMPMHour()+ " " + t.getAMPM(),x])} )),
        {
          calendar_date_select: this,
          onchange: function() { this.calendar_date_select.updateSelectedDate( { hour: this.value });},
          className: "hour"
        }
      );
      buttons_div.build("span", {innerHTML:":", className: "seperator"});
      var that = this;
      this.minute_select = new SelectBox(buttons_div,
        blank_time.concat($R(0,59).select(function(x){return (x % that.options.get('minute_interval')==0)}).map(function(x){ return $A([ Date.padded2(x), x]); } ) ),
        {
          calendar_date_select: this,
          onchange: function() { this.calendar_date_select.updateSelectedDate( {minute: this.value }) },
          className: "minute"
        }
      );

    } else if (! this.options.get("buttons")) buttons_div.remove();

    if (this.options.get("buttons")) {
      buttons_div.build("span", {innerHTML: "&#160;"});
      if (this.options.get("time")=="mixed" || !this.options.get("time")) b = buttons_div.build("a", {
          innerHTML: _translations["Today"],
          href: "#",
          onclick: function() {this.today(false); return false;}.bindAsEventListener(this)
        });

      if (this.options.get("time")=="mixed") buttons_div.build("span", {innerHTML: "&#160;|&#160;", className:"button_seperator"})

      if (this.options.get("time")) b = buttons_div.build("a", {
        innerHTML: _translations["Now"],
        href: "#",
        onclick: function() {this.today(true); return false}.bindAsEventListener(this)
      });

      if (!this.options.get("embedded") && !this.closeOnClick())
      {
        buttons_div.build("span", {innerHTML: "&#160;|&#160;", className:"button_seperator"})
        buttons_div.build("a", { innerHTML: _translations["OK"], href: "#", onclick: function() {this.close(); return false;}.bindAsEventListener(this) });
      }
      if (this.options.get('clear_button')) {
        buttons_div.build("span", {innerHTML: "&#160;|&#160;", className:"button_seperator"})
        buttons_div.build("a", { innerHTML: _translations["Clear"], href: "#", onclick: function() {this.clearDate(); if (!this.options.get("embedded")) this.close(); return false;}.bindAsEventListener(this) });
      }
    }
  },
  refresh: function ()
  {
    this.refreshMonthYear();
    this.refreshCalendarGrid();

    this.setSelectedClass();
    this.updateFooter();
  },
  refreshCalendarGrid: function () {
    this.beginning_date = new Date(this.date).stripTime();
    this.beginning_date.setDate(1);
    this.beginning_date.setHours(12); // Prevent daylight savings time boundaries from showing a duplicate day
    var pre_days = this.beginning_date.getDay() // draw some days before the fact
    if (pre_days < 3) pre_days += 7;
    this.beginning_date.setDate(1 - pre_days + Date.first_day_of_week);

    var iterator = new Date(this.beginning_date);

    var today = new Date().stripTime();
    var this_month = this.date.getMonth();
    vdc = this.options.get("valid_date_check");
    for (var cell_index = 0;cell_index<42; cell_index++)
    {
      day = iterator.getDate(); month = iterator.getMonth();
      cell = this.calendar_day_grid[cell_index];
      Element.remove(cell.childNodes[0]); div = cell.build("div", {innerHTML:day});
      if (month!=this_month) div.className = "other";
      cell.day = day; cell.month = month; cell.year = iterator.getFullYear();
      if (vdc) { if (vdc(iterator.stripTime())) cell.removeClassName("disabled"); else cell.addClassName("disabled") };
      iterator.setDate( day + 1);
    }

    if (this.today_cell) this.today_cell.removeClassName("today");

    if ( $R( 0, 41 ).include(days_until = this.beginning_date.stripTime().daysDistance(today)) ) {
      this.today_cell = this.calendar_day_grid[days_until];
      this.today_cell.addClassName("today");
    }
  },
  refreshMonthYear: function() {
    var m = this.date.getMonth();
    var y = this.date.getFullYear();
    // set the month
    if (this.options.get("month_year") == "dropdowns")
    {
      this.month_select.setValue(m, false);

      var e = this.year_select.element;
      if (this.flexibleYearRange() && (!(this.year_select.setValue(y, false)) || e.selectedIndex <= 1 || e.selectedIndex >= e.options.length - 2 )) this.populateYearRange();

      this.year_select.setValue(y);

    } else {
      this.month_year_label.update( Date.months[m] + " " + y.toString()  );
    }
  },
  populateYearRange: function() {
    this.year_select.populate(this.yearRange().toArray());
  },
  yearRange: function() {
    if (!this.flexibleYearRange())
      return $R(this.options.get("year_range")[0], this.options.get("year_range")[1]);

    var y = this.date.getFullYear();
    return $R(y - this.options.get("year_range"), y + this.options.get("year_range"));
  },
  flexibleYearRange: function() { return (typeof(this.options.get("year_range")) == "number"); },
  validYear: function(year) { if (this.flexibleYearRange()) { return true;} else { return this.yearRange().include(year);}  },
  dayHover: function(element) {
    var hover_date = new Date(this.selected_date);
    hover_date.setYear(element.year); hover_date.setMonth(element.month); hover_date.setDate(element.day);
    this.updateFooter(hover_date.toFormattedString(this.use_time));
  },
  dayHoverOut: function(element) { this.updateFooter(); },
  clearSelectedClass: function() {if (this.selected_cell) this.selected_cell.removeClassName("selected");},
  setSelectedClass: function() {
    if (!this.selection_made) return;
    this.clearSelectedClass()
    if ($R(0,42).include( days_until = this.beginning_date.stripTime().daysDistance(this.selected_date.stripTime()) )) {
      this.selected_cell = this.calendar_day_grid[days_until];
      this.selected_cell.addClassName("selected");
    }
  },
  reparse: function() { this.parseDate(); this.refresh(); },
  dateString: function(formatted) {
      //mod for ofbiz Date input field, the return value will not be localized.
      //return (this.selection_made) ? this.selected_date.toFormattedString(this.use_time) : "&#160;";
      return (this.selection_made) ? (formatted ? this.selected_date.toFormattedString(this.use_time) : this.selected_date.toFormattedString_ofbiz_default(this.use_time)) : "&#160;";
  },
  parseDate: function()
  {
    var value = $F(this.target_element).strip()
    this.selection_made = (value != "");
    this.date = value=="" ? NaN : Date.parseFormattedString(this.options.get("date") || value);
    if (isNaN(this.date)) this.date = new Date();
    if (!this.validYear(this.date.getFullYear())) this.date.setYear( (this.date.getFullYear() < this.yearRange().start) ? this.yearRange().start : this.yearRange().end);
    this.selected_date = new Date(this.date);
    this.use_time = /[0-9]:[0-9]{2}/.exec(value) ? true : false;
    this.date.setDate(1);
  },
  updateFooter:function(text) { if (!text) text = this.dateString(true); this.footer_div.purgeChildren(); this.footer_div.build("span", {innerHTML: text }); },
  clearDate:function() {
    if ((this.target_element.disabled || this.target_element.readOnly) && this.options.get("popup") != "force") return false;
    var last_value = this.target_element.value;
    this.target_element.value = "";
    this.clearSelectedClass();
    this.updateFooter('&#160;');
    if (last_value!=this.target_element.value) this.callback("onchange");
  },
  updateSelectedDate:function(partsOrElement, via_click) {
    var parts = $H(partsOrElement);
    if ((this.target_element.disabled || this.target_element.readOnly) && this.options.get("popup") != "force") return false;
    if (parts.get("day")) {
      var t_selected_date = this.selected_date, vdc = this.options.get("valid_date_check");
      for (var x = 0; x<=3; x++) t_selected_date.setDate(parts.get("day"));
      t_selected_date.setYear(parts.get("year"));
      t_selected_date.setMonth(parts.get("month"));

      if (vdc && ! vdc(t_selected_date.stripTime())) { return false; }
      this.selected_date = t_selected_date;
      this.selection_made = true;
    }

    if (!isNaN(parts.get("hour"))) this.selected_date.setHours(parts.get("hour"));
    if (!isNaN(parts.get("minute"))) this.selected_date.setMinutes( Math.floor_to_interval(parts.get("minute"), this.options.get("minute_interval")) );
    if (parts.get("hour") === "" || parts.get("minute") === "")
      this.setUseTime(false);
    else if (!isNaN(parts.get("hour")) || !isNaN(parts.get("minute")))
      this.setUseTime(true);

    this.updateFooter();
    this.setSelectedClass();

    if (this.selection_made) this.updateValue();
    if (this.closeOnClick()) { this.close(); }
    if (via_click && !this.options.get("embedded")) {
      if ((new Date() - this.last_click_at) < 333) this.close();
      this.last_click_at = new Date();
    }
  },
  closeOnClick: function() {
    if (this.options.get("embedded")) return false;
    if (this.options.get("close_on_click")===nil )
      return (this.options.get("time")) ? false : true
    else
      return (this.options.get("close_on_click"))
  },
  navMonth: function(month) { (target_date = new Date(this.date)).setMonth(month); return (this.navTo(target_date)); },
  navYear: function(year) { (target_date = new Date(this.date)).setYear(year); return (this.navTo(target_date)); },
  navTo: function(date) {
    if (!this.validYear(date.getFullYear())) return false;
    this.date = date;
    this.date.setDate(1);
    this.refresh();
    this.callback("after_navigate", this.date);
    return true;
  },
  setUseTime: function(turn_on) {
    this.use_time = this.options.get("time") && (this.options.get("time")=="mixed" ? turn_on : true) // force use_time to true if time==true && time!="mixed"
    if (this.use_time && this.selected_date) { // only set hour/minute if a date is already selected
      var minute = Math.floor_to_interval(this.selected_date.getMinutes(), this.options.get("minute_interval"));
      var hour = this.selected_date.getHours();

      this.hour_select.setValue(hour);
      this.minute_select.setValue(minute)
    } else if (this.options.get("time")=="mixed") {
      this.hour_select.setValue(""); this.minute_select.setValue("");
    }
  },
  updateValue: function() {
    var last_value = this.target_element.value;
    this.target_element.value = this.dateString(true);
    if (last_value!=this.target_element.value) this.callback("onchange");
  },
  today: function(now) {
    var d = new Date(); this.date = new Date();
    var o = $H({ day: d.getDate(), month: d.getMonth(), year: d.getFullYear(), hour: d.getHours(), minute: d.getMinutes()});
    if ( ! now ) o = o.merge({hour: "", minute:""});
    this.updateSelectedDate(o, true);
    this.refresh();
  },
  close: function() {
    if (this.closed) return false;
    this.callback("before_close");
    this.target_element.calendar_date_select = nil;
    Event.stopObserving(document, "mousedown", this.closeIfClickedOut_handler);
    Event.stopObserving(document, "keypress", this.keyPress_handler);
    this.calendar_div.remove(); this.closed = true;
    if (this.iframe) this.iframe.remove();
    if (this.target_element.type != "hidden" && ! this.target_element.disabled) this.target_element.focus();
    this.callback("after_close");
  },
  closeIfClickedOut: function(e) {
    if (! $(Event.element(e)).descendantOf(this.calendar_div) ) this.close();
  },
  keyPress: function(e) {
    if (e.keyCode==Event.KEY_ESC) this.close();
  },
  callback: function(name, param) { if (this.options.get(name)) { this.options.get(name).bind(this.target_element)(param); } }
}


//OFBiz addition: functions to call the calendar
function call_cal(target, datetime, options) {
    new CalendarDateSelect(target, options || {time:true,year_range:10} );
}

function call_cal_notime(target, datetime, options) {
    new CalendarDateSelect(target, options || {time:false,year_range:10} );
}

Calendar = Class.create({
    initialize: function(calendar_panel, options) {
        this.options = $H(options || {});
        this.calendar_panel = $(calendar_panel);

        /*dhtmlLoadScript('/images/component-extensions/calendar/date.js', {conditionForLoad : '!((\'dateExtension\' in window) && dateExtension)'});

        var synchronousOptions = {onComplete: this.start.bind(this), conditionForLoad : '!((\'localization\' in window) && localization === \'' + this.options.get("locale") + '\')'};
        if (this.options.get("locale"))
            dhtmlLoadScript('/images/component-extensions/calendar/i18n/LocalizedDate_'+ this.options.get("locale") + '.js', synchronousOptions);*/
        
        this.start();
    },
    start: function() {
        this.initParameters();
        this.populateCalendar();
    },
    populateCalendar: function() {
        this.id = this.calendar_panel.identify().substring(0, this.calendar_panel.identify().indexOf('_datePanel'));       
        var date = new Date(); 
        var idDate = this.id +"_"+ date.getTime()
        if ('false'==this.options.get("time")) {        	
            calendarInputElement = new Element('input', {type: 'text',
                                                         name : this.id,
                                                         id : idDate,  
                                                         title : this.options.get("localizedInputTitle") || '',
                                                         value : ((this.options.get("localizedValue") && this.options.get("localizedValue") != 'null') ? this.options.get("localizedValue") : ((this.options.get("localizedDefaultFormatToShow") && this.options.get("localizedDefaultFormatToShow") != 'null') ? this.options.get("localizedDefaultFormatToShow") : '')),
                                                         size : this.options.get("size") || '',
                                                         maxlength : this.options.get("maxlength") || ''});
            
            calendarInputElement.setValue = calendarInputElement.setValue.wrap(
            	function(proceed, value) {
            		if (value !== null && !Object.isUndefined(value) && value !== '') {
            			var originalValue = value;
            			try {
            				value = Date.parseFormattedString(value);
	            			value = value.toFormattedString();
            			} catch(e) {
            				value = new Date(Date.getDateFromFormat(originalValue,"yyyy-MM-dd HH:mm:ss.S"));
            				value = value.toFormattedString();
            			}
            		}
            		proceed(value);
            	}
            );
            
            calendarInputElement.getFormattedValue = function() {
            	var value = this.getValue();
            	if (value !== null && !Object.isUndefined(value) && value !== '') {
        			try {
        				value = Date.parseFormattedString(value);
        			} catch(e) {
        				value = new Date(Date.getDateFromFormat(value,"yyyy-MM-dd HH:mm:ss.S"));
        			}
        		}
            	return value;
            }

            /*calendarImageElement = new Element('img', {src : this.options.get("imagesrc") || '',
                                                         width: '16',
                                                         height: '16',
                                                         border: '0',
                                                         alt : this.options.get("localizedIconTitle") || ''});*/

            var onclickStr = '';
            if ('true'==this.options.get("shortDateInput"))
            	onclickStr += 'call_cal_notime(';
            else
            	onclickStr += 'call_cal(';
            onclickStr += '\'' + idDate + '\'';
            onclickStr += ', \'' + ((this.options.get("dateTimeValue") && this.options.get("dateTimeValue") != 'null') ? this.options.get("dateTimeValue") : '') + '\'';

            var optionParameter='{close_on_click : true';
            if (this.options.get("yearRange") && this.options.get("yearRange") != 'null') {
                optionParameter += ', year_range : '+this.options.get("yearRange");
            }
            if (this.options.get("parent") && this.options.get("parent") != 'null') {
            	optionParameter += ', parent : \''+this.options.get("parent") + '\'';
            }
            optionParameter += '}';

            onclickStr += ', ' + optionParameter;
            onclickStr +=')';
            calendarAnchorElement = new Element('span', {'class' : 'calendar-anchor'}).insert(new Element('a', {'href' : '#', 'onclick' : onclickStr}).insert(new Element('i', {'class' : 'far fa-calendar', 'style': 'font-size: 1.5em;'})));

            var classNames = this.options.get("classNames");
            $w(classNames).each(function(className) {
                calendarInputElement.addClassName(className);
                if ('readonly' === className) {
                    calendarInputElement.writeAttribute('readonly', 'readonly');
                    calendarAnchorElement.hide();
                }
            });

            this.removeOriginalField();

            if (!this.calendar_panel.hasClassName("calendarSingleForm")) {
                divInputElement = new Element('div', {className: "calendar_input_field"}).insert(calendarInputElement);
                divIconElement = new Element('div', {className: "calendar_icon"}).insert(calendarAnchorElement);

                this.calendar_panel.insert({ Top: divInputElement });
                this.calendar_panel.insert(divIconElement);
            }
            else {
                this.calendar_panel.insert({ Top: calendarInputElement });
                this.calendar_panel.insert(calendarAnchorElement );
            }



            //this.calendar_panel.insert({ Top: calendarInputElement });
        }
    },
    initParameters: function() {
        /*Date.parseFormattedString = function (string) {
            return $D(string);
        };

        Date.prototype.toFormattedString = function(include_time) {
            if (include_time)
                return this.strftime(Date.DATE_TIME_FORMAT);
            else
                return this.strftime(Date.DATE_FORMAT);
        };

        Date.weekdays = $A(Date.ABBR_DAYNAMES);
        Date.months = $A(Date.MONTHNAMES);*/
        Date.useExternalLibrary = true;
        Date.include_time = true;
    },
    removeOriginalField : function() {
        var dateElements = this.calendar_panel.select('.date');
        if (dateElements) {
            dateElements.each(function(element) {
                switch (element.type.toLowerCase()) {
                    case 'select-one':
                        element.update();
                        element.replace();
                        break;
                    default:
                        element.remove();
                }
            });

            var timeElements = this.calendar_panel.select('input.time');
            if (timeElements && timeElements.size() > 0) {
                timeElements.each(function(element) {
                    element.remove();
                });
                if ($('time-separator'))
                    $('time-separator').remove();
            }
        }

        var compositeHiddenField = this.calendar_panel.descendants().find(function(element) {
            return (element.readAttribute('name') && element.readAttribute('name').include('compositeType'));
        });
        if (compositeHiddenField) {
            var timeDropdownElements = this.calendar_panel.select('select.time');


            var value = $F(compositeHiddenField);

            if (value && (value=='Date' || ((!timeDropdownElements || (timeDropdownElements && timeDropdownElements.size()==0)) && (value=='Time' || value=='Timestamp')))) {
                compositeHiddenField.remove();
            }
        }
    }
});

Object.extend(CalendarDateSelect, {
    load : function() {
        /*if (!$$('link[href="/images/component-extensions/calendar/calendar_date_select.css"]')[0]) {
            var head = $$('head')[0];
            var cssNode = new Element('link', {'type' : 'text/css', 'rel' : 'stylesheet', 'href' : '/images/component-extensions/calendar/calendar_date_select.css', 'media' : 'screen'});
            head.insert(cssNode);
        }*/

        CalendarDateSelect.populateCalendarPanel();
        createEventPanel(CalendarDateSelect.populateCalendarPanel)
    },
    populateCalendarPanel : function(newContent, putInNewContent) {
        $A((!Object.isElement(newContent) ? $$('.datePanel') : $(newContent).select('.datePanel'))).each(function(datePanel) {
            var parameters = datePanel.select('.dateParams');
            if (parameters) {
                var paramName = datePanel.identify().substring(0, datePanel.identify().indexOf('_datePanel'));
                if (paramName) {
                    options=Form.serializeElements(parameters,true);
                    parameters.each(function(element) {
                        element.remove();
                    });
                    
                    if (Object.isElement(newContent) && putInNewContent) {
                    	options.parent = newContent.identify();
                    }

                    new Calendar(datePanel, options);
                }
            }
        });
    },
    reloadCalendar : function(newContent, putInNewContent) {
        CalendarDateSelect.populateCalendarPanel(newContent, putInNewContent);
    }
});

document.observe("dom:loaded", CalendarDateSelect.load);
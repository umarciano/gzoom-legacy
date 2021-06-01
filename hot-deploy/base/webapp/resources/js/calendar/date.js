/**
 * Date instance methods
 *
 * @author Ken Snyder (ken d snyder at gmail dot com)
 * @date 2008-09-10
 * @version 2.0.2 (http://kendsnyder.com/sandbox/date/)
 * @license Creative Commons Attribution License 3.0 (http://creativecommons.org/licenses/by/3.0/)
 */
// begin by creating a scope for utility variables
(function() {
  //
  // pre-calculate the number of milliseconds in a day
  //
  var day = 24 * 60 * 60 * 1000;
    //
    // function to add leading zeros
    //
    var zeroPad = function(number, digits) {
        number = String(number);
        while (number.length < digits) number = '0' + number;
        return number;
    };
  //
  // set up integers and functions for adding to a date or subtracting two dates
  //
  var multipliers = {
    millisecond: 1,
    second: 1000,
    minute: 60 * 1000,
    hour: 60 * 60 * 1000,
    day: day,
    week: 7 * day,
    month: {
      // add a number of months
      add: function(d, number) {
        // add any years needed (increments of 12)
        multipliers.year.add(d, Math[number > 0 ? 'floor' : 'ceil'](number / 12));
        // ensure that we properly wrap betwen December and January
        var prevMonth = d.getMonth() + (number % 12);
        if (prevMonth == 12) {
          prevMonth = 0;
          d.setYear(d.getFullYear() + 1);
        } else if (prevMonth == -1) {
          prevMonth = 11;
          d.setYear(d.getFullYear() - 1);
        }
        d.setMonth(prevMonth);
      },
      // get the number of months between two Date objects (decimal to the nearest day)
      diff: function(d1, d2) {
        // get the number of years
        var diffYears = d1.getFullYear() - d2.getFullYear();
        // get the number of remaining months
        var diffMonths = d1.getMonth() - d2.getMonth() + (diffYears * 12);
        // get the number of remaining days
        var diffDays = d1.getDate() - d2.getDate();
        // return the month difference with the days difference as a decimal
        return diffMonths + (diffDays / 30);
      }
    },
    year: {
      // add a number of years
      add: function(d, number) {
        d.setYear(d.getFullYear() + Math[number > 0 ? 'floor' : 'ceil'](number));
      },
      // get the number of years between two Date objects (decimal to the nearest day)
      diff: function(d1, d2) {
        return multipliers.month.diff(d1, d2) / 12;
      }
    }
  };
  //
  // alias each multiplier with an 's' to allow 'year' and 'years' for example
  //
  for (var unit in multipliers) {
        if (unit.substring(unit.length - 1) != 's') { // IE will iterate newly added properties :|
        multipliers[unit + 's'] = multipliers[unit];
        }
  }
    //
    // take a date instance and a format code and return the formatted value
    //
    var format = function(d, code) {
        if (Date.prototype.strftime.formatShortcuts[code]) {
            // process any shortcuts recursively
            return d.strftime(Date.prototype.strftime.formatShortcuts[code]);
        } else {
            // get the format code function and toPaddedString() argument
            var getter = (Date.prototype.strftime.formatCodes[code] || '').split('.');
            var nbr = d['get' + getter[0]] ? d['get' + getter[0]]() : '';
            // run toPaddedString() if specified
            if (getter[1]) nbr = zeroPad(nbr, getter[1]);
            // prepend the leading character
            return nbr;
        }
    };
  //
  // Add methods to Date instances
  //
  var instanceMethods = {
    //
    // Return a date one day ahead (or any other unit)
    //
    // @param string unit
    // units: year | month | day | week | hour | minute | second | millisecond
    // @return object Date
    //
    succ: function(unit) {
      return this.clone().add(1, unit);
    },
    //
    // Add an arbitrary amount to the currently stored date
    //
    // @param integer/float number
    // @param string unit
    // @return object Date (chainable)
    //
    add: function(number, unit) {
      var factor = multipliers[unit] || multipliers.day;
      if (typeof factor == 'number') {
        this.setTime(this.getTime() + (factor * number));
      } else {
        factor.add(this, number);
      }
      return this;
    },
    //
    // Find the difference between the current and another date
    //
    // @param string/object dateObj
    // @param string unit
    // @param boolean allowDecimal
    // @return integer/float
    //
    diff: function(dateObj, unit, allowDecimal) {
      // ensure we have a Date object
      dateObj = Date.create(dateObj);
      if (dateObj === null) return null;
      // get the multiplying factor integer or factor function
      var factor = multipliers[unit] || multipliers.day;
      if (typeof factor == 'number') {
        // multiply
        var unitDiff = (this.getTime() - dateObj.getTime()) / factor;
      } else {
        // run function
        var unitDiff = factor.diff(this, dateObj);
      }
      // if decimals are not allowed, round toward zero
      return (allowDecimal ? unitDiff : Math[unitDiff > 0 ? 'floor' : 'ceil'](unitDiff));
    },
    //
    // Convert a date to a string using traditional strftime format codes
    //
    // @param string formatStr
    // @return string
    //
    strftime: function(formatStr) {
      // default the format string to year-month-day
      var source = formatStr || '%Y-%m-%d', result = '', match;
      // replace each format code
            while (source.length > 0) {
                if (match = source.match(Date.prototype.strftime.formatCodes.matcher)) {
            result += source.slice(0, match.index);
            result += (match[1] || '') + format(this, match[2]);
            source = source.slice(match.index + match[0].length);
          } else {
            result += source, source = '';
          }
        }
            return result;
        },
    //
    // Return a proper two-digit year integer
    //
    // @return integer
    //
    getShortYear: function() {
      return this.getYear() % 100;
    },
    //
    // Get the number of the current month, 1-12
    //
    // @return integer
    //
    getMonthNumber: function() {
      return this.getMonth() + 1;
    },
    //
    // Get the name of the current month
    //
    // @return string
    //
    getMonthName: function() {
      return Date.MONTHNAMES[this.getMonth()];
    },
    //
    // Get the abbreviated name of the current month
    //
    // @return string
    //
    getAbbrMonthName: function() {
      return Date.ABBR_MONTHNAMES[this.getMonth()];
    },
    //
    // Get the name of the current week day
    //
    // @return string
    //
    getDayName: function() {
      return Date.DAYNAMES[this.getDay()];
    },
    //
    // Get the abbreviated name of the current week day
    //
    // @return string
    //
    getAbbrDayName: function() {
      return Date.ABBR_DAYNAMES[this.getDay()];
    },
    //
    // Get the ordinal string associated with the day of the month (i.e. st, nd, rd, th)
    //
    // @return string
    //
    getDayOrdinal: function() {
      return Date.ORDINALNAMES[this.getDate() % 10];
    },
    //
    // Get the current hour on a 12-hour scheme
    //
    // @return integer
    //
    getHours12: function() {
      var hours = this.getHours();
      return hours > 12 ? hours - 12 : (hours == 0 ? 12 : hours);
    },
    //
    // Get the AM or PM for the current time
    //
    // @return string
    //
    getAmPm: function() {
      return this.getHours() >= 12 ? 'PM' : 'AM';
    },
    //
    // Get the current date as a Unix timestamp
    //
    // @return integer
    //
    getUnix: function() {
      return Math.round(this.getTime() / 1000, 0);
    },
    //
    // Get the GMT offset in hours and minutes (e.g. +06:30)
    //
    // @return string
    //
    getGmtOffset: function() {
      // divide the minutes offset by 60
      var hours = this.getTimezoneOffset() / 60;
      // decide if we are ahead of or behind GMT
      var prefix = hours < 0 ? '+' : '-';
      // remove the negative sign if any
      hours = Math.abs(hours);
      // add the +/- to the padded number of hours to : to the padded minutes
      return prefix + zeroPad(Math.floor(hours), 2) + ':' + zeroPad((hours % 1) * 60, 2);
    },
    //
    // Get the browser-reported name for the current timezone (e.g. MDT, Mountain Daylight Time)
    //
    // @return string
    //
    getTimezoneName: function() {
      var match = /(?:\((.+)\)$| ([A-Z]{3}) )/.exec(this.toString());
            return match[1] || match[2] || 'GMT' + this.getGmtOffset();
    },
    //
    // Convert the current date to an 8-digit integer (%Y%m%d)
    //
    // @return int
    //
    toYmdInt: function() {
      return (this.getFullYear() * 10000) + (this.getMonthNumber() * 100) + this.getDate();
    },
    //
    // Create a copy of a date object
    //
    // @return object
    //
        clone: function() {
            return new Date(this.getTime());
        }
  };
    for (var name in instanceMethods) Date.prototype[name] = instanceMethods[name];
  //
  // Add static methods to the date object
  //
  var staticMethods = {
    //
    // The heart of the date functionality: returns a date object if given a convertable value
    //
    // @param string/object/integer date
    // @return object Date
    //
    create: function(date) {
      // If the passed value is already a date object, return it
      if (date instanceof Date) return date;
      // If the passed value is an integer, interpret it as a unix timestamp
      if (typeof date == 'number') return new Date(date * 1000);
      // If the passed value is a string, attempt to parse it using Date.parse()
            var parsable = String(date).replace(/^\s*(.+)\s*$/, '$1'), i = 0, length = Date.create.patterns.length, pattern;
            var current = null;
            while (i < length) {
                if (current) {
                    if (pattern[2])
                        ms = Date.getDateFromFormat(current, pattern[2])
                    else
                        ms = Date.parse(current);
                    //if (ms == 0) continue;
                    if (!isNaN(ms) && ms > 0) return new Date(ms);
                }
                pattern = Date.create.patterns[i];
                if (typeof pattern == 'function') {
                    obj = pattern(current);
                    if (obj instanceof Date) return obj;
                } else {
                    current = parsable.replace(pattern[0], pattern[1]);
                }
                i++;
            }
            return NaN;
    },
    getDateFromFormat : function(val,format) {
        val=val+"";
        format=format+"";
        var i_val=0;
        var i_format=0;
        var c="";
        var token="";
        var token2="";
        var x,y;
        var now=new Date();
        var year=now.getYear();
        var month=now.getMonth()+1;
        var date=1;
        var hh=now.getHours();
        var mm=now.getMinutes();
        var ss=now.getSeconds();
        var S=now.getMilliseconds();
        var ampm="";

        while (i_format < format.length) {
            // Get next token from format string
            c=format.charAt(i_format);
            token="";
            while ((format.charAt(i_format)==c) && (i_format < format.length)) {
                token += format.charAt(i_format++);
                }
            // Extract contents of value based on format token
            if (token=="yyyy" || token=="yy" || token=="y") {
                if (token=="yyyy") { x=4;y=4; }
                if (token=="yy")   { x=2;y=2; }
                if (token=="y")    { x=2;y=4; }
                year=Date._getInt(val,i_val,x,y);
                if (year==null) { return 0; }
                i_val += year.length;
                if (year.length==2) {
                    if (year > 70) { year=1900+(year-0); }
                    else { year=2000+(year-0); }
                    }
                }
            else if (token=="MMM"||token=="NNN"){
                month=0;
                for (var i=0; i<Date.MONTHNAMES.length; i++) {
                    var month_name=Date.MONTHNAMES[i];
                    if (val.substring(i_val,i_val+month_name.length).toLowerCase()==month_name.toLowerCase()) {
                        if (token=="MMM"||(token=="NNN"&&i>11)) {
                            month=i+1;
                            if (month>12) { month -= 12; }
                            i_val += month_name.length;
                            break;
                            }
                        }
                    }
                if ((month < 1)||(month>12)){return 0;}
                }
            else if (token=="EE"||token=="E"){
                for (var i=0; i<Date.DAYNAMES.length; i++) {
                    var day_name=Date.DAYNAMES[i];
                    if (val.substring(i_val,i_val+day_name.length).toLowerCase()==day_name.toLowerCase()) {
                        i_val += day_name.length;
                        break;
                        }
                    }
                }
            else if (token=="MM"||token=="M") {
                month=Date._getInt(val,i_val,token.length,2);
                if(month==null||(month<1)||(month>12)){return 0;}
                i_val+=month.length;}
            else if (token=="dd"||token=="d") {
                date=Date._getInt(val,i_val,token.length,2);
                if(date==null||(date<1)||(date>31)){return 0;}
                i_val+=date.length;}
            else if (token=="hh"||token=="h") {
                hh=Date._getInt(val,i_val,token.length,2);
                if(hh==null||(hh<1)||(hh>12)){return 0;}
                i_val+=hh.length;}
            else if (token=="HH"||token=="H") {
                hh=Date._getInt(val,i_val,token.length,2);
                if(hh==null||(hh<0)||(hh>23)){return 0;}
                i_val+=hh.length;}
            else if (token=="KK"||token=="K") {
                hh=Date._getInt(val,i_val,token.length,2);
                if(hh==null||(hh<0)||(hh>11)){return 0;}
                i_val+=hh.length;}
            else if (token=="kk"||token=="k") {
                hh=Date._getInt(val,i_val,token.length,2);
                if(hh==null||(hh<1)||(hh>24)){return 0;}
                i_val+=hh.length;hh--;}
            else if (token=="mm"||token=="m") {
                mm=Date._getInt(val,i_val,token.length,2);
                if(mm==null||(mm<0)||(mm>59)){return 0;}
                i_val+=mm.length;}
            else if (token=="ss"||token=="s") {
                ss=Date._getInt(val,i_val,token.length,2);
                if(ss==null||(ss<0)||(ss>59)){return 0;}
                i_val+=ss.length;}
            else if (token=="a") {
                if (val.substring(i_val,i_val+2).toLowerCase()=="am") {ampm="AM";}
                else if (val.substring(i_val,i_val+2).toLowerCase()=="pm") {ampm="PM";}
                else {return 0;}
                i_val+=2;}
            else if (token=="S") {
                S=Date._getInt(val,i_val,token.length,3);
                if(S==null||(S<0)||(S>999)){return 0;}
                i_val+=S.length;}
            else {
                if (val.substring(i_val,i_val+token.length)!=token) {return 0;}
                else { i_val+=token.length; }
                }
            }
        // If there are any trailing characters left in the value, it doesn't match
        if (i_val != val.length) { return 0; }
        // Is date valid for month?
        if (month==2) {
            // Check for leap year
            if ( ( (year%4==0)&&(year%100 != 0) ) || (year%400==0) ) { // leap year
                if (date > 29){ return 0; }
                }
            else { if (date > 28) { return 0; } }
            }
        if ((month==4)||(month==6)||(month==9)||(month==11)) {
            if (date > 30) { return 0; }
            }
        // Correct hours value
        if (hh<12 && ampm=="PM") { hh=hh-0+12; }
        else if (hh>11 && ampm=="AM") { hh-=12; }
        var newdate=new Date(year,month-1,date,hh,mm,ss,S);
        return newdate.getTime();
    },
    // ------------------------------------------------------------------
    // Utility functions for parsing in getDateFromFormat()
    // ------------------------------------------------------------------
    _isInteger : function(val) {
        var digits="1234567890";
        for (var i=0; i < val.length; i++) {
            if (digits.indexOf(val.charAt(i))==-1) { return false; }
            }
        return true;
    },
    _getInt : function (str,i,minlength,maxlength) {
        for (var x=maxlength; x>=minlength; x--) {
            var token=str.substring(i,i+x);
            if (token.length < minlength) { return null; }
            if (Date._isInteger(token)) { return token; }
            }
        return null;
    },

    //
    // constants representing month names, day names, and ordinal names
    // (same names as Ruby Date constants)
    //
    MONTHNAMES      : 'January February March April May June July August September October November December'.split(' '),
    ABBR_MONTHNAMES : 'Jan Feb Mar Apr May Jun Jul Aug Sep Oct Nov Dec'.split(' '),
    DAYNAMES        : 'Sunday Monday Tuesday Wednesday Thursday Friday Saturday'.split(' '),
    ABBR_DAYNAMES   : 'Sun Mon Tue Wed Thu Fri Sat'.split(' '),
    ORDINALNAMES    : 'th st nd rd th th th th th th'.split(' '),
    //
    // Shortcut for full ISO-8601 date conversion
    //
    ISO: '%Y-%m-%dT%H:%M:%S.%N%G',
    //
    // Shortcut for SQL-type formatting
    //
    SQL: '%Y-%m-%d %H:%M:%S',
    //
    // Setter method for month, day, and ordinal names for i18n
    //
    // @param object newNames
    //
      daysInMonth: function(year, month) {
          if (month == 2)
              return new Date(year, 1, 29).getDate() == 29 ? 29 : 28;
          return [undefined,31,undefined,31,30,31,30,31,31,30,31,30,31][month];
      }
  };
    for (var name in staticMethods) Date[name] = staticMethods[name];
     //
  // format codes for strftime
  //
  // each code must be an array where the first member is the name of a Date.prototype function
  // and optionally a second member indicating the number to pass to Number#toPaddedString()
  //
    Date.prototype.strftime.formatCodes = {
    //
    // 2-part regex matcher for format codes
    //
    // first match must be the character before the code (to account for escaping)
    // second match must be the format code character(s)
    //
    matcher: /()%(#?(%|[a-z]))/i,
    // year
    Y: 'FullYear',
    y: 'ShortYear.2',
    // month
    m: 'MonthNumber.2',
 '#m': 'MonthNumber',
    B: 'MonthName',
    b: 'AbbrMonthName',
    // day
    d: 'Date.2',
 '#d': 'Date',
    e: 'Date',
    A: 'DayName',
    a: 'AbbrDayName',
    w: 'Day',
    o: 'DayOrdinal',
    // hours
    H: 'Hours.2',
 '#H': 'Hours',
    I: 'Hours12.2',
 '#I': 'Hours12',
    p: 'AmPm',
    // minutes
    M: 'Minutes.2',
 '#M': 'Minutes',
    // seconds
    S: 'Seconds.2',
 '#S': 'Seconds',
    s: 'Unix',
    // milliseconds
    N: 'Milliseconds.3',
 '#N': 'Milliseconds',
    // timezone
    O: 'TimezoneOffset',
    Z: 'TimezoneName',
    G: 'GmtOffset'
  };
  //
  // shortcuts that will be translated into their longer version
  //
  // be sure that format shortcuts do not refer to themselves: this will cause an infinite loop
  //
  Date.prototype.strftime.formatShortcuts = {
    // date
    F: '%Y-%m-%d',
    // time
    T: '%H:%M:%S',
    X: '%H:%M:%S',
    // local format date
    x: '%m/%d/%y',
    D: '%m/%d/%y',
    // local format extended
 '#c': '%a %b %e %H:%M:%S %Y',
    // local format short
    v: '%e-%b-%Y',
    R: '%H:%M',
    r: '%I:%M:%S %p',
    // tab and newline
    t: '\t',
    n: '\n',
  '%': '%'
  };
  //
  // A list of conversion patterns (array arguments sent directly to gsub)
  // Add, remove or splice a patterns to customize date parsing ability
  //
    Date.create.patterns = [
    [/-/g, '/'], // US-style time with dashes => Parsable US-style time
        [/st|nd|rd|th/g, ''], // remove st, nd, rd and th
    [/(3[01]|[0-2]\d)\s*\.\s*(1[0-2]|0\d)\s*\.\s*([1-9]\d{3})/, '$2/$1/$3'], // World time => Parsable US-style time
    [/([1-9]\d{3})\s*-\s*(1[0-2]|0\d)\s*-\s*(3[01]|[0-2]\d)/, '$2/$3/$1'], // ISO-style time => Parsable US-style time
        function(str) { // 12-hour time
            var match = str.match(/^(?:(.+)\s+)?([1-9]|1[012])(?:\s*\:\s*(\d\d))?(?:\s*\:\s*(\d\d))?\s*(am|pm)\s*$/i);
            //                      ^opt. date  ^hour         ^opt. minute       ^opt. second          ^am or pm
            if (match) {
                if (match[1]) {
                    var d = Date.create(match[1]);
                    if (isNaN(d)) return;
                } else {
                    var d = new Date();
                    d.setMilliseconds(0);
                }
                var hour = parseFloat(match[2]);
                hour = match[5].toLowerCase() == 'am' ? (hour == 12 ? 0 : hour) : (hour == 12 ? 12 : hour + 12);
                d.setHours(hour, parseFloat(match[3] || 0), parseFloat(match[4] || 0));
                return d;
            }
        }
  ];
})();
//
// Create a convenience method for creating dates from strings
//
$D = Date.create;

dateExtension = true;
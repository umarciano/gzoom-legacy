/**
 * FlogUriParser is freely distributable under the terms of an MIT-style license.
 * 
 * Copyright (c) 2006 Adam Burmister, Original author Poly9.com
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 *
 * ------------------------------------------------------------------------------------
 *
 * @author Adam Burmister, adam.burmister@gmail.com, www.flog.co.nz
 * @version 0.1
 * @namespace Flog
 *
 * Based on the Poly9.com url parser, modified to Prototype class and javascript conventions
 * 
 * Usage: var p = new Flog.UriParser('http://user:password@flog.co.nz/pathname?arguments=1#fragment');
 * p.host == 'flog.co.nz';
 * p.protocol == 'http';
 * p.pathname == '/pathname';
 * p.querystring == 'arguements=1';
 * p.querystring[arguements] | p.querystring.arguements == 1
 * p.fragment == 'fragment';
 * p.user == 'user';
 * p.password == 'password';
 *
 */

/* Fake a Flog.* namespace */
if(typeof(Flog) == 'undefined') var Flog = {};

Flog.UriParser = Class.create();

/* A prototype modification of poly9.com's original parser */
Flog.UriParser.prototype = {
	_regExp : /^((\w+):\/\/)?((\w+):?(\w+)?@)?([^\/\?:]+):?(\d+)?(\/?[^\?#]+)?\??([^#]+)?#?(\w*)/,
	username : null,
	password : null,
	port : null,
	protocol : null,
	host : null,
	pathname : null,
	url : null,
	querystring : {},
	fragment : null,
	
	initialize: function(uri) {
		if(uri) this.parse(uri);	
	},
	
	_getVal : function(r, i) {
		if(!r) return null;
		return (typeof(r[i]) == 'undefined' ? null : r[i]);
	},
	
	parse: function(uri) {
		var r = this._regExp.exec(uri);
		if (!r) throw "FlogUriParser::parse -> Invalid URI"
		this.url		= this._getVal(r,0);
		this.protocol	= this._getVal(r,2);
		this.username	= this._getVal(r,4);
		this.password	= this._getVal(r,5);
		this.host		= this._getVal(r,6);
		this.port		= this._getVal(r,7);
		this.pathname	= this._getVal(r,8);
		this.querystring= new Flog.UriParser.QueryString(this._getVal(r,9));
		this.fragment	= this._getVal(r,10);
		return r;
	}
};

/* Querystring sub class */
Flog.UriParser.QueryString = Class.create();
Flog.UriParser.QueryString.prototype = {
	rawQueryString : '',
	length : 0,
	initialize : function(qs) {
		if(!qs) { 
			this.rawQueryString = '';
			this.length = 0;
			return;
		}
		this.rawQueryString = qs;
		var args = qs.split('&');
		this.length = args.length;
		for (var i=0;i<args.length;i++) {
			var pair = args[i].split('=');
			this[unescape(pair[0])] = ((pair.length == 2) ? unescape(pair[1]) : pair[0]);
		}
	},
	toString : function() {
		return this.rawQueryString;	
	}
};

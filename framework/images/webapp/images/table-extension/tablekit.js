/*
*
* Copyright (c) 2007 Andrew Tetlaw & Millstream Web Software
* http://www.millstream.com.au/view/code/tablekit/
* Version: 1.3b 2008-03-23
*
* Permission is hereby granted, free of charge, to any person
* obtaining a copy of this software and associated documentation
* files (the "Software"), to deal in the Software without
* restriction, including without limitation the rights to use, copy,
* modify, merge, publish, distribute, sublicense, and/or sell copies
* of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
*
* The above copyright notice and this permission notice shall be
* included in all copies or substantial portions of the Software.
*
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
* EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
* MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
* NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
* BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
* ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
* CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
* SOFTWARE.
* *
*/

/* TODO con IE vs MOZILLA:
 * se nascondo una colonna, il parametro oldIndex passato alla funzione moveColumn
 * ï¿½ diverso.
 * Inoltre con IE
 * durante lo spostamento delle colonne la funzione TableKit.getCellIndex() ritorna -1 quando rilascio il mouse su una riga del corpo della tabella */

if(typeof(Prototype) == "undefined")
    throw "Tablekit requires Prototype to be loaded.";
if(typeof(Control.SelectMultiple) == "undefined")
    throw "Tablekit requires Control.SelectMultiple to be loaded.";

// Use the TableKit class constructure if you'd prefer to init your tables as JS objects
var TableKit = Class.create({
    initialize : function(elm, options) {
        var table = $(elm);
        if(table.tagName !== "TABLE") {
            return;
        }
        TableKit.register(table,Object.extend(TableKit.options,options || {}));
        this.id = table.id;
        var op = TableKit.option('sortable resizable editable draggable toggleable hoverbar selectable headerFixable', this.id);
        if(op.sortable) {
            TableKit.Sortable.init(table);
        }
        if(op.resizable) {
            TableKit.Resizable.init(table);
        }
        if(op.editable) {
            TableKit.Editable.init(table);
        }
        if(op.draggable) {
            TableKit.Draggable.init(table);
        }
        if(op.toggleable) {
            TableKit.Toggleable.init(table);
        }
        if(op.headerFixable) {
            TableKit.HeaderFixable.init(table);
        }
        if(op.hoverbar) {
            TableKit.HoverBar.init(table);
        }
        if(op.selectable) {
            TableKit.Selectable.init(table);
        }
    },
    sort : function(column, order) {
        TableKit.Sortable.sort(this.id, column, order);
    },
    resizeColumn : function(column, w) {
        TableKit.Resizable.resize(this.id, column, w);
    },
    editCell : function(row, column) {
        TableKit.Editable.editCell(this.id, row, column);
    },
    selectRow : function(row, column) {
        TableKit.Selectable.selectRow(row, this.id);
    }
});

Object.extend(TableKit, {
    getBodyRows : function(table) {
        table = $(table);
        var id = table.id;
        if(!TableKit.tables[id].dom.rows) {
            // TableKit.tables[id].dom.rows = (table.tHead && table.tHead.rows.length > 0) ? $A(table.tBodies[0].rows) : $A(table.rows).without(table.rows[0]);
            TableKit.tables[id].dom.rows = (table.tHead && table.tHead.rows.length > 0) ? (table.tBodies[0] ? ($A(table.tBodies[0].rows)) : null) : $A(table.rows).without(table.rows[0]);
        }
        return TableKit.tables[id].dom.rows;
    },
    getRow : function(table, index) {
        var bodyRows = TableKit.getBodyRows(table);
        if (bodyRows && index < bodyRows.size())
            return TableKit.getBodyRows(table)[index];
        return null;
    },
    refreshBodyRows : function(table) {
        table = $(table);
        var id = table.id;

        if(TableKit.tables[id].dom.rows) {
            TableKit.tables[id].dom.rows = null;
        }
        TableKit.getBodyRows(table);
    },
    getHeaderCells : function(table, cell) {
        if(!table) { table = $(cell).up('table'); }
        var id = table.id;
        if(!TableKit.tables[id].dom.head) {
            if(table.tHead && table.tHead.rows.length > 0) {
                var header = new Array();
                for(var x = 0; x < table.tHead.rows.length; x += 1) {
                    var header2 = $A(table.tHead.rows[x].cells);
                    header = header.concat(header2);
                }
                TableKit.tables[id].dom.head = header;
            }else{
                TableKit.tables[id].dom.head = $A(table.rows[0].cells);
            }
        }
        return TableKit.tables[id].dom.head;
    },
    refreshHeaderCells : function(table) {
        table = $(table);
        var id = table.id;

        if(TableKit.tables[id].dom.head) {
            TableKit.tables[id].dom.head = null;
        }
        TableKit.getHeaderCells(table);
    },
    getHeaderRows : function(table) {
        table = $(table);
        var id = table.id;
        if(!TableKit.tables[id].dom.head.rows) {
            TableKit.tables[id].dom.head.rows = (table.tHead && table.tHead.rows.length > 0) ? $A(table.tHead.rows) : $A(table.rows[0]);
        }
        return TableKit.tables[id].dom.head.rows;
    },
    getLastHeaderRow : function(table) {
        return TableKit.getHeaderRows(table) ? $A($(TableKit.getHeaderRows(table).last()).select('th,td')) : $A([]);
    },
    getFirstHeaderRow : function(table) {
        return TableKit.getHeaderRows(table) && TableKit.getHeaderRows(table).size() > 1 ? $A($(TableKit.getHeaderRows(table).first()).select('th,td')) : $A([]);
    },
    getHeaderGroups : function(table, cellIndex) {
        return TableKit.getHeaderRows(table).collect(function(headerRow) {
            return $A(headerRow.cells).inject([], function(array, cell, index) {
                cell = $(cell);
                if (cellIndex < index + (cell.colspan ? cell.colspan : 1)) {
                    array.push(cell);
                }
                return array;
            })
        });
    },
    getClassFieldGroupId : function(table, index) {
        /* return the element with the fieldGroupId if exists */
        var lastHeaderRow = TableKit.getLastHeaderRow(table);
        var cell = lastHeaderRow[index];
        var classFieldGroupId = $w(cell.readAttribute('class')).find(function(element){
            return element.indexOf(TableKit.FIELD_GROUP_CLASS_ID) > -1;
        });
        if(classFieldGroupId) {
            classFieldGroupId = classFieldGroupId.substring(classFieldGroupId.indexOf(TableKit.FIELD_GROUP_CLASS_ID)+TableKit.FIELD_GROUP_CLASS_ID.length);
        }
        return $(classFieldGroupId);
    },
    getClassFieldGroup : function(table, index) {
        /* return the class name of fieldGroupId if exists: "TableKit.FIELD_GROUP_CLASS_ID<fieldGroupId>" */
        var lastHeaderRow = TableKit.getLastHeaderRow(table);
        var cell = lastHeaderRow[index];
        return $w(cell.readAttribute('class')).find(function(element){
            return element.indexOf(TableKit.FIELD_GROUP_CLASS_ID) > -1;
        });
    },
    getPositionOnFirstHeaderRow : function(table, index) {
       /* for an index in the second Header row return the position of the correspondent element in the first header row */
       var position = TableKit.getFirstHeaderRow(table).inject(-1,function(acc, cell, actualIndex) {
           if(actualIndex >= index) {
               throw $break;
           }
           acc = acc + cell.colSpan;
           return actualIndex
       });
       return position < 0 ? 0: position;
    },
    isFirst : function(table, index) {
       /* test if it is the first element of a fieldGroup */
       var classFieldGroup = TableKit.getClassFieldGroup(table, index);
       var firstCellOfFieldGroup = TableKit.getLastHeaderRow(table).find(function(cell) {
           return cell.hasClassName(classFieldGroup) && !cell.hasClassName('hidden');
       });
       return index <= TableKit.getCellIndex(firstCellOfFieldGroup);
    },
    isLast : function(table, index) {
       /* test if it is the last element of a fieldGroup */
       var classFieldGroup = TableKit.getClassFieldGroup(table, index);
       var FieldGroupChild = TableKit.getLastHeaderRow(table).findAll(function(cell) {
           return cell.hasClassName(classFieldGroup) /*&& !cell.hasClassName('hidden')*/;
       });
       return index >= TableKit.getCellIndex(FieldGroupChild.last());
    },
    FirstIndexOfFieldGroup : function(table, index) {
       /* return the index of the first cell of the FieldGroup */
       var classFieldGroup = TableKit.getClassFieldGroup(table, index);
       var firstCellOfFieldGroup = TableKit.getLastHeaderRow(table).find(function(cell) {
           return cell.hasClassName(classFieldGroup);
       });
       return TableKit.getCellIndex(firstCellOfFieldGroup);
    },
    LastIndexOfFieldGroup : function(table, index) {
       /* return the index of the last cell of the FieldGroup */
       var classFieldGroup = TableKit.getClassFieldGroup(table, index);
       var FieldGroupChild = TableKit.getLastHeaderRow(table).findAll(function(cell) {
           return cell.hasClassName(classFieldGroup);
       });
       return TableKit.getCellIndex(FieldGroupChild.last());
    },
    refreshHeaderRows : function(table) {
        table = $(table);
        var id = table.id;

        if(TableKit.tables[id].dom.head.rows) {
            TableKit.tables[id].dom.head.rows = null;
        }
        TableKit.getHeaderRows(table);
    },
    getCellIndex : function(cell) {
        if(cell.parentNode.cells) {
           return $A(cell.parentNode.cells).indexOf(cell);
        }
        else {
            if(cell.up('td')) {
                var td = cell.up('td');
                return $A(td.parentNode.cells).indexOf(td);
            }
            else if(cell.up('th')){
                var th = cell.up('th');
                return $A(th.parentNode.cells).indexOf(th);
            }
        }
    },
    getRowIndex : function(row) {
        return $A(row.parentNode.rows).indexOf(row);
    },
    getCellText : function(cell, refresh) {
        if(!cell) { return ""; }
        var data = TableKit.getCellData(cell);
        if(refresh || data.refresh || !data.textContent) {
            data.textContent = cell.textContent ? cell.textContent : cell.innerText;
            data.refresh = false;
        }
        return data.textContent;
    },
    getCellData : function(cell) {
      var t = null;
        if(!cell.id) {
            t = $(cell).up('table');
            cell.id = t.id + "-cell-" + TableKit._getc();
        }
        var tblid = t ? t.id : cell.id.match(/(.*)-cell.*/)[1];
        if(!TableKit.tables[tblid].dom.cells[cell.id]) {
            TableKit.tables[tblid].dom.cells[cell.id] = {textContent : '', htmlContent : '', active : false};
        }
        return TableKit.tables[tblid].dom.cells[cell.id];
    },
    removeColumn : function(table, index) {
        table = $(table);
        var id = table.id;

        var headRow = TableKit.getLastHeaderRow(table);
        if (index > 0 && index < headRow.size()) {
            headRow[index].remove();
        }
        TableKit.getBodyRows(table).each(function(row) {
            if (index > 0 && index < row.size()) {
                row[index].remove();
            }
        });
    },
    moveColumn : function(table, oldIndex, newIndex) {
        table = $(table);
        var id = table.id;

        var headRow = TableKit.getLastHeaderRow(table);
        var cell = null;
        if (oldIndex >= 0 && oldIndex < headRow.size()) {
            cell = headRow[oldIndex];
        }
        var oldClassFieldGroupId = TableKit.getClassFieldGroupId(table, oldIndex);
        var newClassFieldGroupId = TableKit.getClassFieldGroupId(table, newIndex);
         /* the movement is possible if:
          * 1. src e dest elements are not contain in FG
          * 2. src e dest elements are contain in same FG
          * 3. dest element is contains in FG and the newIndex is minor than oldIndex
          *    -> the element is insert after FG
          *       the newIndex is the position after FG
          * 4. dest element is contains in FG and the newIndex is major than oldIndex
          *    -> the element is insert before FG
          *       the newIndex is the position before FG */

        if(!oldClassFieldGroupId && newClassFieldGroupId && newIndex < oldIndex) {
           newIndex = TableKit.FirstIndexOfFieldGroup(table, newIndex);
        } else if(!oldClassFieldGroupId && newClassFieldGroupId && newIndex > oldIndex) {
           newIndex = TableKit.LastIndexOfFieldGroup(table, newIndex);
        }

        /* return the position of the correspondent element in the first header row */
        var oldPosition = TableKit.getPositionOnFirstHeaderRow(table, oldIndex);
        var newPosition = TableKit.getPositionOnFirstHeaderRow(table, newIndex);

        if ((!oldClassFieldGroupId && !newClassFieldGroupId) ||
            (!oldClassFieldGroupId && TableKit.isFirst(table, newIndex) && newIndex < oldIndex) ||
            (!oldClassFieldGroupId && TableKit.isLast(table, newIndex) && newIndex > oldIndex) ||
            ($(oldClassFieldGroupId) === $(newClassFieldGroupId))) {
            if (cell) {
                if (newIndex >= 0) {
                    if (newIndex > headRow.size()) {
                        cell.up('tr')[0].insert({'bottom' : cell});
                    } else if (newIndex > oldIndex){
                        $(headRow[newIndex]).insert({'after' : cell});
                    } else if (newIndex < oldIndex){
                        $(headRow[newIndex]).insert({'before' : cell});
                    }
               }
            }
            if(TableKit.getBodyRows(table)) {
                TableKit.getBodyRows(table).each(function(row) {
                    var cell = null;
                    var cellList = $(row).select('td');
                    if (oldIndex >= 0 && oldIndex < cellList.size()) {
                        cell = cellList[oldIndex];
                    }
                    if (cell) {
                        if (newIndex >= 0) {
                            if (newIndex > cellList.size()) {
                                row.insert({'bottom' : cell});
                            } else if (newIndex > oldIndex){
                                $(cellList[newIndex]).insert({'after' : cell});
                            } else if (newIndex < oldIndex){
                                $(cellList[newIndex]).insert({'before' : cell});
                           }
                       }
                   }
                });
            }
            var headRow = TableKit.getFirstHeaderRow(table);
            var cell = null;
            if (oldPosition >= 0 && oldPosition < headRow.size()) {
               /* if not FG */
               if(!oldClassFieldGroupId) {
                   cell = headRow[oldPosition];
               }
               else if($(oldClassFieldGroupId) === $(newClassFieldGroupId)) {
                   /* DO NOTHING */
               }
            }
            if (cell) {
                if (newPosition >= 0) {
                    if (newPosition > headRow.size()) {
                        cell.up('tr')[0].insert({'bottom' : cell});
                    } else if (newPosition > oldPosition){
                        $(headRow[newPosition]).insert({'after' : cell});
                    } else if (newPosition < oldPosition){
                        $(headRow[newPosition]).insert({'before' : cell});
                    }
                }
            }

        }
        TableKit.refreshHeaderRows(table);
        TableKit.refreshHeaderCells(table);
        TableKit.refreshBodyRows(table);

    },
    register : function(table, options) {
        if(!table.id) {
            table.id = "tablekit-table-" + TableKit._getc();
        }
        var id = table.id;
        TableKit.tables[id] = TableKit.tables[id] ?
                                Object.extend(TableKit.tables[id], options || {}) :
                                Object.extend(
                                  {dom : {head:null,rows:null,cells:{}},sortable:false,resizable:false,editable:false,draggable:false,selectable:false,hoverbar:false,toggleable:false,headerFixable:false},
                                  options || {}
                                );
    },
    registerObserver : function(table, observer, name, f) {
    	if(table) {
	        if (Object.isFunction(f) && name) {
	            if(!table.id) {
	                table.id = "tablekit-table-" + TableKit._getc();
	            }
	            var observers = TableKit.option('observers', table.identify())[0];
	            var eventObservers = null;
	            if (observer in observers) {
	                eventObservers = $H(observers[observer] || {'K' : Prototype.K});
	            }
	            
	            if (eventObservers) {
	            	eventObservers.set(name, f);
	            } else {
	            	eventObservers = $H({name : f});
	            }
	            
	            observers[observer] = eventObservers.toObject();
	
	            TableKit.register(table, {
	                'observers' : observers
	            });
	        }
        }
    },
    unregisterObserver : function(table, observer, name) {
        if (name) {
            if(!table.id) {
                table.id = "tablekit-table-" + TableKit._getc();
            }
            var observers = TableKit.option('observers', table.identify())[0];
            // lista di observers per quella table
            var eventObservers = null;
            if (observer in observers) {
                eventObservers = $H(observers[observer]);
                if (eventObservers.get(name)) {
                    eventObservers.unset(name);
                    if (eventObservers.values().length == 0) {
                        eventObservers = $H({'K' : Prototype.K});
                    }
                }
                observers[observer] = eventObservers.toObject();
            }

            TableKit.register(table, {
                'observers' : observers
            });
        }
    },
    isRegistered : function(table, observer, functionName){
    	var observers = TableKit.option('observers', table.identify())[0];
    	var eventObservers = null;
    	var f = null;
    	if (observer in observers) {
    		eventObservers = $H(observers[observer]);
    		f = eventObservers.get(functionName);
    	}
    	
    	return Object.isFunction(f) && f != Prototype.K;
    	 
    }, 
    _registeredExtension : null,
    registerExtension : function(loadNamespace, _options) {
        if (!TableKit._registeredExtension)
            TableKit._registeredExtension = new Array();
        TableKit._registeredExtension.push(loadNamespace);
        TableKit.setup(_options);
    },
    notify : function(eventName, table, event) {
        if(TableKit.tables[table.id] &&  TableKit.tables[table.id].observers && TableKit.tables[table.id].observers[eventName]) {
            $H(TableKit.tables[table.id].observers[eventName]).each(function(pair) {
            	var eventObserver = pair.value;
                if (Object.isFunction(eventObserver))
                    eventObserver(table, event);
            });

        }
    },
    isSortable : function(table) {
        return TableKit.tables[table.id] ? TableKit.tables[table.id].sortable : TableKit.hasClassName(table, TableKit.options.sortableSelector);
    },
    isResizable : function(table) {
        return TableKit.tables[table.id] ? TableKit.tables[table.id].resizable : TableKit.hasClassName(table, TableKit.options.resizableSelector);
    },
    isEditable : function(table) {
        return TableKit.tables[table.id] ? TableKit.tables[table.id].editable : TableKit.hasClassName(table, TableKit.options.editableSelector);
    },
    isDraggable : function(table) {
        return TableKit.tables[table.id] ? TableKit.tables[table.id].draggable : TableKit.hasClassName(table, TableKit.options.draggableSelector);
    },
    isToggleable : function(table) {
        return TableKit.tables[table.id] ? TableKit.tables[table.id].toggleable : TableKit.hasClassName(table, TableKit.options.toggleableSelector);
    },
    isHeaderFixable : function(table) {
        return TableKit.tables[table.id] ? TableKit.tables[table.id].headerFixable : TableKit.hasClassName(table, TableKit.options.headerFixableSelector);
    },
    isHoverBar : function(table) {
        return TableKit.tables[table.id] ? TableKit.tables[table.id].hoverbar : TableKit.hasClassName(table, TableKit.options.hoverbarSelector);
    },
    isSelectable : function(table) {
        return TableKit.tables[table.id] ? TableKit.tables[table.id].selectable : TableKit.hasClassName(table, TableKit.options.selectableSelector);
    },
    hasClassName : function(table, selectors) {
        var res = false;
        if (Object.isString(selectors)) {
            selectors = $w(selectors);
        }

        if (Object.isArray(selectors) && selectors.size() > 0) {
            return $A(selectors).inject(true, function(a, c) {
                if (c.indexOf('.') != -1) {
                    c = c.substring(c.indexOf('.')+1);
                }
                return a && table.hasClassName(c);
            });
        }

        return false;
    },
    setup : function(o) {
        Object.extend(TableKit.options, o || {} );
    },
    option : function(s, id, o1, o2) {
        o1 = o1 || TableKit.options;
        o2 = o2 || (id ? (TableKit.tables[id] ? TableKit.tables[id] : {}) : {});
        var key = id + s;
        if(!TableKit._opcache[key]){
            TableKit._opcache[key] = $A($w(s)).inject([],function(a,v){
                //Maps spa - bug fix
                //a.push(a[v] = o2[v] || o1[v]);
                a.push(a[v] = (o2[v] != null && o2[v] != undefined) ? ((typeof o2[v] == 'boolean' || Object.isString(o2[v])) ? o2[v] : Object.clone(o2[v], true)) : ((typeof o1[v] == 'boolean' || Object.isString(o1[v])) ? o1[v] : Object.clone(o1[v], true)));
                return a;
            });
        }
        return TableKit._opcache[key];
    },
    e : function(event) {
        return event || window.event;
    },
    tables : {},
    _opcache : {},
    FIELD_GROUP_CLASS_ID : 'fieldGroupId_',
    options : {
        autoLoad : true,
        stripe : true,
        sortable : true,
        resizable : true,
        editable : true,
        draggable: true,
        toggleable: true,
        headerFixable: true,
        hoverbar: true,
        selectable: true,
        toggleable: true,
        rowEvenClass : 'roweven',
        rowOddClass : 'rowodd',
        headerFixableSelector : ['table.headerFixable'],
        selectableSelector : ['table.selectable'],
        hoverbarSelector : ['table.hover-bar'],
        toggleableSelector : ['table.toggleable'],
        draggableSelector : ['table.draggable'],
        draggingClass : 'dragging',
        ghostTdClass : 'ghostTd',
        hoveringClass : 'hovering',
        sortableSelector : ['table.sortable'],
        columnClass : 'sortcol',
        descendingClass : 'sortdesc',
        ascendingClass : 'sortasc',
        defaultSortDirection : 1,
        noSortClass : 'nosort',
        sortFirstAscendingClass : 'sortfirstasc',
        sortFirstDecendingClass : 'sortfirstdesc',
        resizableSelector : ['table.resizable'],
        minWidth : 10,
        showHandle : true,
        resizeOnHandleClass : 'resize-handle-active',
        editableSelector : ['table.editable'],
        formClassName : 'editable-cell-form',
        noEditClass : 'noedit',
        editAjaxURI : '/',
        editAjaxOptions : {},
        observers : {
            'onSortStart' 	        : {'K' : Prototype.K},
            'onSort' 		        : {'K' : Prototype.K},
            'onSortEnd' 	        : {'K' : Prototype.K},
            'onResizeStart'         : {'K' : Prototype.K},
            'onResize' 		        : {'K' : Prototype.K},
            'onResizeEnd' 	        : {'K' : Prototype.K},
            'onEditStart' 	        : {'K' : Prototype.K},
            'onEdit' 		        : {'K' : Prototype.K},
            'onEditEnd' 	        : {'K' : Prototype.K},
            'onDragStart' 	        : {'K' : Prototype.K},
            'onDrag' 		        : {'K' : Prototype.K},
            'onDragEnd' 	        : {'K' : Prototype.K},
            'onToggleStart'	        : {'K' : Prototype.K},
            'onToggle' 		        : {'K' : Prototype.K},
            'onToggleEnd' 	        : {'K' : Prototype.K},
            'onSelectStart'	        : {'K' : Prototype.K},
            'onSelect' 		        : {'K' : Prototype.K},
            'onSelectEnd' 	        : {'K' : Prototype.K},
            'onDblClickSelectStart'	: {'K' : Prototype.K},
            'onDblClickSelect' 		: {'K' : Prototype.K},
            'onDblClickSelectEnd' 	: {'K' : Prototype.K}
        }
    },
    _c : 0,
    _getc : function() {return TableKit._c += 1;},
    unloadTable : function(table){
      table = $(table);
      if(!TableKit.tables[table.id]) {return;} //if not an existing registered table return
        var headerCells = TableKit.getHeaderCells(table);
        var op = TableKit.option('sortable resizable editable draggable hoverbar noSortClass descendingClass ascendingClass columnClass sortFirstAscendingClass sortFirstDecendingClass selectable', table.id);
         //unregister all the sorting and resizing events
        headerCells.each(function(c){
            c = $(c);
            if(op.sortable) {
              if(!c.hasClassName(op.noSortClass)) {
                  Event.stopObserving(c, 'mousedown', TableKit.Sortable._sort);
                  c.removeClassName(op.columnClass);
                  c.removeClassName(op.sortFirstAscendingClass);
                  c.removeClassName(op.sortFirstDecendingClass);
                  //ensure that if table reloaded current sort is remembered via sort first class name
                  if(c.hasClassName(op.ascendingClass)) {
                    c.removeClassName(op.ascendingClass);
                    c.addClassName(op.sortFirstAscendingClass)
                  } else if (c.hasClassName(op.descendingClass)) {
                    c.removeClassName(op.descendingClass);
                    c.addClassName(op.sortFirstDecendingClass)
                  }
              }
          }
          if(op.resizable) {
              Event.stopObserving(c, 'mouseover', TableKit.Resizable.initDetect);
              Event.stopObserving(c, 'mouseout', TableKit.Resizable.killDetect);
          }
          if(op.draggable) {
              Event.stopObserving(c, 'mousedown', TableKit.Draggable.drag);
              Event.stopObserving(c, 'mouseover', TableKit.Draggable.dragEffect);
              Event.stopObserving(c, 'mouseout', TableKit.Draggable.dragEffect);
              Event.stopObserving(c, 'mouseup', TableKit.Draggable.dragEffect);
          }
        });
        //unregister the editing events and cancel any open editors
        if(op.editable || op.draggable) {
          if (op.editable) {
              Event.stopObserving(table.tBodies[0], 'click', TableKit.Editable._editCell);
          }
          var rows = $A(TableKit.getBodyRows(table));
          rows.each(function(row) {
              $A(row.select('td')).each(function(c) {
                  var cell = $(c);

                  if (op.editable) {
                      var editor = TableKit.Editable.getCellEditor(cell);
                      editor.cancel(cell);
                  }
                  if (op.draggable) {
                      Event.stopObserving(c, 'mouseover', TableKit.Draggable.dragEffect);
                      Event.stopObserving(c, 'mouseout', TableKit.Draggable.dragEffect);
                      Event.stopObserving(c, 'mouseup', TableKit.Draggable.dragEffect);
                  }
                  if (op.selectable) {
                      Event.stopObserving(c, 'mousedown', TableKit.Selectable.selectRowEvent);
                      Event.stopObserving(c, 'dom:mousedown', TableKit.Selectable.selectRowEvent);
                      Event.stopObserving(c, 'dblclick', TableKit.Selectable.dblclickSelection);
                      Event.stopObserving(c, 'dom:dblclick', TableKit.Selectable.dblclickSelection);
                  }
                  if(op.hoverbar && Prototype.Browser.IE) {
                      Event.stopObserving(c, 'mouseover', TableKit.HoverBar.hoverEffect);
                      Event.stopObserving(c, 'mouseout', TableKit.HoverBar.hoverEffect);
                  }
              });
          });
        }
        //delete the cache
        //TableKit.tables[table.id].dom = {head:null,rows:null,cells:{}}; // TODO: watch this for mem leaks
        var tempMap = $H(TableKit.tables);
        tempMap.unset(table.id);
        TableKit.tables = tempMap.toObject();
    },
    reloadTable : function(table, contentToExplore){
      table = $(table);
      TableKit.unloadTable(table);

      if(TableKit.tables[table.id]) {
          //Maps spa - bug fix
          // var op = TableKit.option('sortable resizable editable draggable toggleable hoverbar', table.id);
          var op = TableKit.option('sortable resizable editable draggable hoverbar toggleable selectable headerFixable', table.id);
          if(op.sortable) {TableKit.Sortable.init(table);}
          if(op.resizable) {TableKit.Resizable.init(table);}
          if(op.editable) {TableKit.Editable.init(table);}
          if(op.draggable) {TableKit.Draggable.init(table);}
          if(op.toggleable) {TableKit.Toggleable.init(table);}
          if(op.hoverbar) {TableKit.HoverBar.init(table);}
          if(op.selectable) {TableKit.Selectable.init(table);}
          if(op.headerFixable) {TableKit.HeaderFixable.init(table);}
          if (TableKit._registeredExtension) {
              TableKit._registeredExtension.each(function(namespace) {
              if ('reloadTable' in namespace)
                  namespace.reloadTable(table, contentToExplore);
              })
          }
      } else {
          TableKit.load(table, true);
      }
      
      return TableKit.tables[table.id];
    },
    unload : function(contentToExplore) {
        var tmpMap = $H(TableKit.tables);

        if (tmpMap.keys().size() > 0) {
            tmpMap.keys().each(function(k) {
                if (contentToExplore && (contentToExplore.down('table#' + k) || contentToExplore === $(k)))
                    TableKit.unloadTable(k);
            });
        }
    },
    reload : function(contentToExplore) {
      var tmpMap = $H(TableKit.tables);

      if (tmpMap.keys().size() > 0) {
          tableToRemove = new Array();
          tmpMap.keys().each(function(k) {
            if (!$(k)) {
                tableToRemove.push(k);
            }
            if (contentToExplore && (contentToExplore.down('table#' + k) || contentToExplore === $(k))) {
                var table = TableKit.reloadTable(k, contentToExplore);
                if (table) {
                	tmpMap.set(k, table);
                }
            }
          });

          $A(tableToRemove).each(function(k) {
              tmpMap.unset(k);
          });
          TableKit.tables = tmpMap.toObject();

          $A((contentToExplore.tagName === 'TABLE' ? contentToExplore.up() : contentToExplore).select('table')).select(function(table) {
              return !TableKit.tables[table.identify()];
          }).each(function(table) {
              TableKit.reloadTable(table, contentToExplore);
          });
      } else {
          TableKit.load(contentToExplore, true)
      }
    },
    load : function(contentToExplore, withoutCss) {
        if (!withoutCss) {
            var head = $$('head')[0];
            var cssNode = new Element('link', {'type' : 'text/css', 'rel' : 'stylesheet', 'href' : '/images/table-extension/tablekit.css', 'media' : 'screen'});
            head.insert(cssNode);
        }

        if(TableKit.options.autoLoad) {
            if(TableKit.options.sortable) {
                $A(TableKit.options.sortableSelector).each(function(s){
                    (!Object.isElement(contentToExplore) ? $$(s) : (contentToExplore.tagName === 'TABLE' ? (contentToExplore.hasClassName(s.indexOf('.') != -1 ? s.substring(s.indexOf('.')+1) : s) ? [contentToExplore] : [])  : contentToExplore.select(s))).each(function(t) {
                        TableKit.Sortable.init(t);
                    });
                });
            }
            if(TableKit.options.resizable) {
                $A(TableKit.options.resizableSelector).each(function(s){
                    (!Object.isElement(contentToExplore) ? $$(s) : (contentToExplore.tagName === 'TABLE' ? (contentToExplore.hasClassName(s.indexOf('.') != -1 ? s.substring(s.indexOf('.')+1) : s) ? [contentToExplore] : [])  : contentToExplore.select(s))).each(function(t) {
                        TableKit.Resizable.init(t);
                    });
                });
            }
            if(TableKit.options.editable) {
                $A(TableKit.options.editableSelector).each(function(s){
                    (!Object.isElement(contentToExplore) ? $$(s) : (contentToExplore.tagName === 'TABLE' ? (contentToExplore.hasClassName(s.indexOf('.') != -1 ? s.substring(s.indexOf('.')+1) : s) ? [contentToExplore] : [])  : contentToExplore.select(s))).each(function(t) {
                        TableKit.Editable.init(t);
                    });
                });
            }
            if(TableKit.options.draggable) {
                $A(TableKit.options.draggableSelector).each(function(s){
                    (!Object.isElement(contentToExplore) ? $$(s) : (contentToExplore.tagName === 'TABLE' ? (contentToExplore.hasClassName(s.indexOf('.') != -1 ? s.substring(s.indexOf('.')+1) : s) ? [contentToExplore] : [])  : contentToExplore.select(s))).each(function(t) {
                        TableKit.Draggable.init(t);
                    });
                });
            }
            if(TableKit.options.toggleable) {
                $A(TableKit.options.toggleableSelector).each(function(s){
                    (!Object.isElement(contentToExplore) ? $$(s) : (contentToExplore.tagName === 'TABLE' ? (contentToExplore.hasClassName(s.indexOf('.') != -1 ? s.substring(s.indexOf('.')+1) : s) ? [contentToExplore] : [])  : contentToExplore.select(s))).each(function(t) {
                        TableKit.Toggleable.init(t);
                    });
                });
            }
            if(TableKit.options.headerFixable) {
                $A(TableKit.options.headerFixableSelector).each(function(s){
                    (!Object.isElement(contentToExplore) ? $$(s) : (contentToExplore.tagName === 'TABLE' ? (contentToExplore.hasClassName(s.indexOf('.') != -1 ? s.substring(s.indexOf('.')+1) : s) ? [contentToExplore] : [])  : contentToExplore.select(s))).each(function(t) {
                        TableKit.HeaderFixable.init(t);
                    });
                });
            }
            if(TableKit.options.hoverbar) {
                $A(TableKit.options.hoverbarSelector).each(function(s){
                    (!Object.isElement(contentToExplore) ? $$(s) : (contentToExplore.tagName === 'TABLE' ? (contentToExplore.hasClassName(s.indexOf('.') != -1 ? s.substring(s.indexOf('.')+1) : s) ? [contentToExplore] : [])  : contentToExplore.select(s))).each(function(t) {
                        TableKit.HoverBar.init(t);
                    });
                });
            }
            if(TableKit.options.selectable) {
                $A(TableKit.options.selectableSelector).each(function(s){
                    (!Object.isElement(contentToExplore) ? $$(s) : (contentToExplore.tagName === 'TABLE' ? (contentToExplore.hasClassName(s.indexOf('.') != -1 ? s.substring(s.indexOf('.')+1) : s) ? [contentToExplore] : [])  : contentToExplore.select(s))).each(function(t) {
                        TableKit.Selectable.init(t);
                    });
                });
            }

            if (TableKit._registeredExtension) {
                TableKit._registeredExtension.each(function(namespace) {
                    if ('load' in namespace)
                        namespace.load(contentToExplore);
                })
            }
        }
    }
});

TableKit.Rows = {
    stripe : function(table) {
        var rows = TableKit.getBodyRows(table);
        if(rows) {
            rows.each(function(r,i) {
                TableKit.Rows.addStripeClass(table,r,i);
            });
        }
    },
    addStripeClass : function(t,r,i) {
        t = t || r.up('table');
        var op = TableKit.option('rowEvenClass rowOddClass', t.id);
        var css = ((i+1)%2 === 0 ? op[0] : op[1]);
        // using prototype's assClassName/RemoveClassName was not efficient for large tables, hence:
        var cn = r.className.split(/\s+/);
        var newCn = [];
        for(var x = 0, l = cn.length; x < l; x += 1) {
            if(cn[x] !== op[0] && cn[x] !== op[1]) { newCn.push(cn[x]); }
        }
        newCn.push(css);
        r.className = newCn.join(" ");
    }
};

TableKit.Sortable = {
    init : function(elm, options){
        var table = $(elm);
        if(table.tagName !== "TABLE") {
            return;
        }
        TableKit.register(table,Object.extend(options || {},{sortable:true}));
        var sortFirst;
        var cells = TableKit.getHeaderCells(table);
        var op = TableKit.option('noSortClass columnClass sortFirstAscendingClass sortFirstDecendingClass', table.id);
        cells.each(function(c){
            c = $(c);
            if(!c.hasClassName(op.noSortClass)) {
                Event.observe(c, 'mousedown', TableKit.Sortable._sort);
                c.addClassName(op.columnClass);
                if(c.hasClassName(op.sortFirstAscendingClass) || c.hasClassName(op.sortFirstDecendingClass)) {
                    sortFirst = c;
                }
            }
        });

        if(sortFirst) {
            if(sortFirst.hasClassName(op.sortFirstAscendingClass)) {
                TableKit.Sortable.sort(table, sortFirst, 1);
            } else {
                TableKit.Sortable.sort(table, sortFirst, -1);
            }
        } else { // just add row stripe classes
            TableKit.Rows.stripe(table);
        }
    },
    reload : function(table) {
        table = $(table);
        var cells = TableKit.getHeaderCells(table);
        var op = TableKit.option('noSortClass columnClass', table.id);
        cells.each(function(c){
            c = $(c);
            if(!c.hasClassName(op.noSortClass)) {
                Event.stopObserving(c, 'mousedown', TableKit.Sortable._sort);
                c.removeClassName(op.columnClass);
            }
        });
        TableKit.Sortable.init(table);
    },
    _sort : function(e) {
        if(TableKit.Resizable._onHandle) {return;}
        e = TableKit.e(e);
        Event.stop(e);
        var cell = Event.element(e);
        while(!(cell.tagName && cell.tagName.match(/td|th/gi))) {
            cell = cell.parentNode;
        }
        TableKit.Sortable.sort(null, cell);
    },
    sort : function(table, index, order) {
        var cell;
        if(typeof index === 'number') {
            if(!table || (table.tagName && table.tagName !== "TABLE")) {
                return;
            }
            table = $(table);
            index = Math.min(table.rows[0].cells.length, index);
            index = Math.max(1, index);
            index -= 1;
            cell = (table.tHead && table.tHead.rows.length > 0) ? $(table.tHead.rows[table.tHead.rows.length-1].cells[index]) : $(table.rows[0].cells[index]);
        } else {
            cell = $(index);
            table = table ? $(table) : cell.up('table');
            index = TableKit.getCellIndex(cell);
        }
        var op = TableKit.option('noSortClass descendingClass ascendingClass defaultSortDirection', table.id);

        if(cell.hasClassName(op.noSortClass)) {return;}
        //TableKit.notify('onSortStart', table);
        order = order ? order : op.defaultSortDirection;
        var rows = TableKit.getBodyRows(table);

        if(cell.hasClassName(op.ascendingClass) || cell.hasClassName(op.descendingClass)) {
            rows.reverse(); // if it was already sorted we just need to reverse it.
            order = cell.hasClassName(op.descendingClass) ? 1 : -1;
        } else {
            var datatype = TableKit.Sortable.getDataType(cell,index,table);
            var tkst = TableKit.Sortable.types;
            rows.sort(function(a,b) {
                return order * tkst[datatype].compare(TableKit.getCellText(a.cells[index]),TableKit.getCellText(b.cells[index]));
            });
        }
        var tb = table.tBodies[0];
        var tkr = TableKit.Rows;
        rows.each(function(r,i) {
            tb.appendChild(r);
            tkr.addStripeClass(table,r,i);
        });
        var hcells = TableKit.getHeaderCells(null, cell);
        $A(hcells).each(function(c,i){
            c = $(c);
            c.removeClassName(op.ascendingClass);
            c.removeClassName(op.descendingClass);
            if(index === i) {
                if(order === 1) {
                    c.addClassName(op.ascendingClass);
                } else {
                    c.addClassName(op.descendingClass);
                }
            }
        });
    },
    types : {},
    detectors : [],
    addSortType : function() {
        $A(arguments).each(function(o){
            TableKit.Sortable.types[o.name] = o;
        });
    },
    getDataType : function(cell,index,table) {
        cell = $(cell);
        index = (index || index === 0) ? index : TableKit.getCellIndex(cell);

        var colcache = TableKit.Sortable._coltypecache;
        var cache = colcache[table.id] ? colcache[table.id] : (colcache[table.id] = {});

        if(!cache[index]) {
            var t = false;
            // first look for a data type id on the heading row cell
            if(cell.id && TableKit.Sortable.types[cell.id]) {
                t = cell.id
            }
            if(!t) {
              t = $w(cell.className).detect(function(n){ // then look for a data type classname on the heading row cell
                  return (TableKit.Sortable.types[n]) ? true : false;
              });
            }
            if(!t) {
                var rows = TableKit.getBodyRows(table);
                cell = rows[0].cells[index]; // grab same index cell from body row to try and match data type
                t = TableKit.Sortable.detectors.detect(
                        function(d){
                            return TableKit.Sortable.types[d].detect(TableKit.getCellText(cell));
                        });
            }
            cache[index] = t;
        }
        return cache[index];
    },
    _coltypecache : {}
};

TableKit.Sortable.detectors = $A($w('date-iso date date-eu date-au time currency datasize number casesensitivetext text')); // setting it here because Safari complained when I did it above...

TableKit.Sortable.Type = Class.create();
TableKit.Sortable.Type.prototype = {
    initialize : function(name, options){
        this.name = name;
        options = Object.extend({
            normal : function(v){
                return v;
            },
            pattern : /.*/
        }, options || {});
        this.normal = options.normal;
        this.pattern = options.pattern;
        if(options.compare) {
            this.compare = options.compare;
        }
        if(options.detect) {
            this.detect = options.detect;
        }
    },
    compare : function(a,b){
        return TableKit.Sortable.Type.compare(this.normal(a), this.normal(b));
    },
    detect : function(v){
        return this.pattern.test(v);
    }
};

TableKit.Sortable.Type.compare = function(a,b) {
    return a < b ? -1 : a === b ? 0 : 1;
};

TableKit.Sortable.addSortType(
    new TableKit.Sortable.Type('number', {
        pattern : /^[-+]?[\d]*\.?[\d]+(?:[eE][-+]?[\d]+)?/,
        normal : function(v) {
            // This will grab the first thing that looks like a number from a string, so you can use it to order a column of various srings containing numbers.
            v = parseFloat(v.replace(/^.*?([-+]?[\d]*\.?[\d]+(?:[eE][-+]?[\d]+)?).*$/,"$1"));
            return isNaN(v) ? 0 : v;
        }}),
    new TableKit.Sortable.Type('text',{
        normal : function(v) {
            return v ? v.toLowerCase() : '';
        }}),
    new TableKit.Sortable.Type('casesensitivetext',{pattern : /^[A-Z]+$/}),
    new TableKit.Sortable.Type('datasize',{
        pattern : /^[-+]?[\d]*\.?[\d]+(?:[eE][-+]?[\d]+)?\s?[k|m|g|t]b$/i,
        normal : function(v) {
            var r = v.match(/^([-+]?[\d]*\.?[\d]+([eE][-+]?[\d]+)?)\s?([k|m|g|t]?b)?/i);
            var b = r[1] ? Number(r[1]).valueOf() : 0;
            var m = r[3] ? r[3].substr(0,1).toLowerCase() : '';
            var result = b;
            switch(m) {
                case  'k':
                    result = b * 1024;
                    break;
                case  'm':
                    result = b * 1024 * 1024;
                    break;
                case  'g':
                    result = b * 1024 * 1024 * 1024;
                    break;
                case  't':
                    result = b * 1024 * 1024 * 1024 * 1024;
                    break;
            }
            return result;
        }}),
    new TableKit.Sortable.Type('date-au',{
        pattern : /^\d{2}\/\d{2}\/\d{4}\s?(?:\d{1,2}\:\d{2}(?:\:\d{2})?\s?[a|p]?m?)?/i,
        normal : function(v) {
            if(!this.pattern.test(v)) {return 0;}
            var r = v.match(/^(\d{2})\/(\d{2})\/(\d{4})\s?(?:(\d{1,2})\:(\d{2})(?:\:(\d{2}))?\s?([a|p]?m?))?/i);
            var yr_num = r[3];
            var mo_num = parseInt(r[2],10)-1;
            var day_num = r[1];
            var hr_num = r[4] ? r[4] : 0;
            if(r[7]) {
                var chr = parseInt(r[4],10);
                if(r[7].toLowerCase().indexOf('p') !== -1) {
                    hr_num = chr < 12 ? chr + 12 : chr;
                } else if(r[7].toLowerCase().indexOf('a') !== -1) {
                    hr_num = chr < 12 ? chr : 0;
                }
            }
            var min_num = r[5] ? r[5] : 0;
            var sec_num = r[6] ? r[6] : 0;
            return new Date(yr_num, mo_num, day_num, hr_num, min_num, sec_num, 0).valueOf();
        }}),
    new TableKit.Sortable.Type('date-us',{
        pattern : /^\d{2}\/\d{2}\/\d{4}\s?(?:\d{1,2}\:\d{2}(?:\:\d{2})?\s?[a|p]?m?)?/i,
        normal : function(v) {
            if(!this.pattern.test(v)) {return 0;}
            var r = v.match(/^(\d{2})\/(\d{2})\/(\d{4})\s?(?:(\d{1,2})\:(\d{2})(?:\:(\d{2}))?\s?([a|p]?m?))?/i);
            var yr_num = r[3];
            var mo_num = parseInt(r[1],10)-1;
            var day_num = r[2];
            var hr_num = r[4] ? r[4] : 0;
            if(r[7]) {
                var chr = parseInt(r[4],10);
                if(r[7].toLowerCase().indexOf('p') !== -1) {
                    hr_num = chr < 12 ? chr + 12 : chr;
                } else if(r[7].toLowerCase().indexOf('a') !== -1) {
                    hr_num = chr < 12 ? chr : 0;
                }
            }
            var min_num = r[5] ? r[5] : 0;
            var sec_num = r[6] ? r[6] : 0;
            return new Date(yr_num, mo_num, day_num, hr_num, min_num, sec_num, 0).valueOf();
        }}),
    new TableKit.Sortable.Type('date-eu',{
        pattern : /^\d{2}-\d{2}-\d{4}/i,
        normal : function(v) {
            if(!this.pattern.test(v)) {return 0;}
            var r = v.match(/^(\d{2})-(\d{2})-(\d{4})/);
            var yr_num = r[3];
            var mo_num = parseInt(r[2],10)-1;
            var day_num = r[1];
            return new Date(yr_num, mo_num, day_num).valueOf();
        }}),
    new TableKit.Sortable.Type('date-iso',{
        pattern : /[\d]{4}-[\d]{2}-[\d]{2}(?:T[\d]{2}\:[\d]{2}(?:\:[\d]{2}(?:\.[\d]+)?)?(Z|([-+][\d]{2}:[\d]{2})?)?)?/, // 2005-03-26T19:51:34Z
        normal : function(v) {
            if(!this.pattern.test(v)) {return 0;}
            var d = v.match(/([\d]{4})(-([\d]{2})(-([\d]{2})(T([\d]{2}):([\d]{2})(:([\d]{2})(\.([\d]+))?)?(Z|(([-+])([\d]{2}):([\d]{2})))?)?)?)?/);
            var offset = 0;
            var date = new Date(d[1], 0, 1);
            if (d[3]) { date.setMonth(d[3] - 1) ;}
            if (d[5]) { date.setDate(d[5]); }
            if (d[7]) { date.setHours(d[7]); }
            if (d[8]) { date.setMinutes(d[8]); }
            if (d[10]) { date.setSeconds(d[10]); }
            if (d[12]) { date.setMilliseconds(Number("0." + d[12]) * 1000); }
            if (d[14]) {
                offset = (Number(d[16]) * 60) + Number(d[17]);
                offset *= ((d[15] === '-') ? 1 : -1);
            }
            offset -= date.getTimezoneOffset();
            if(offset !== 0) {
                var time = (Number(date) + (offset * 60 * 1000));
                date.setTime(Number(time));
            }
            return date.valueOf();
        }}),
    new TableKit.Sortable.Type('date',{
        pattern: /^(?:sun|mon|tue|wed|thu|fri|sat)\,\s\d{1,2}\s(?:jan|feb|mar|apr|may|jun|jul|aug|sep|oct|nov|dec)\s\d{4}(?:\s\d{2}\:\d{2}(?:\:\d{2})?(?:\sGMT(?:[+-]\d{4})?)?)?/i, //Mon, 18 Dec 1995 17:28:35 GMT
        compare : function(a,b) { // must be standard javascript date format
            if(a && b) {
                return TableKit.Sortable.Type.compare(new Date(a),new Date(b));
            } else {
                return TableKit.Sortable.Type.compare(a ? 1 : 0, b ? 1 : 0);
            }
        }}),
    new TableKit.Sortable.Type('time',{
        pattern : /^\d{1,2}\:\d{2}(?:\:\d{2})?(?:\s[a|p]m)?$/i,
        compare : function(a,b) {
            var d = new Date();
            var ds = d.getMonth() + "/" + d.getDate() + "/" + d.getFullYear() + " ";
            return TableKit.Sortable.Type.compare(new Date(ds + a),new Date(ds + b));
        }}),
    new TableKit.Sortable.Type('currency',{
        pattern : /^[$ï¿½ï¿½ï¿½ï¿½]/, // dollar,pound,yen,euro,generic currency symbol
        normal : function(v) {
            return v ? parseFloat(v.replace(/[^-\d\.]/g,'')) : 0;
        }})
);

TableKit.Resizable = {
    init : function(elm, options){
        var table = $(elm);
        if(table.tagName !== "TABLE") {return;}
        TableKit.register(table,Object.extend(options || {},{resizable:true}));
        var cells = TableKit.getHeaderCells(table);
        cells.each(function(c){
            c = $(c);
            Event.observe(c, 'selectstart', function(e) {
                if (TableKit.Resizable._handle || TableKit.Draggable.getDraggedCell(table)) {
                    Event.stop(e);
                }
            });
            Event.observe(c, 'mouseover', TableKit.Resizable.initDetect);
            Event.observe(c, 'mouseout', TableKit.Resizable.killDetect);
        });
    },
    resize : function(table, index, w) {
        var cell;
        if(typeof index === 'number') {
            if(!table || (table.tagName && table.tagName !== "TABLE")) {return;}
            table = $(table);
            index = Math.min(table.rows[0].cells.length, index);
            index = Math.max(1, index);
            index -= 1;
            cell = (table.tHead && table.tHead.rows.length > 0) ? $(table.tHead.rows[table.tHead.rows.length-1].cells[index]) : $(table.rows[0].cells[index]);
        } else {
            cell = $(index);
            table = table ? $(table) : cell.up('table');
            index = TableKit.getCellIndex(cell);
        }
        var pad = parseInt(cell.getStyle('paddingLeft'),10) + parseInt(cell.getStyle('paddingRight'),10);
        w = Math.max(w-pad, TableKit.option('minWidth', table.id)[0]);

        cell.setStyle({'width' : w + 'px'});
    },
    hideColumn : function(e) {
        e = TableKit.e(e);
        var cell = Event.element(e);
        cell.addClassName('hideColumn');
        index = TableKit.getCellIndex(cell);
        var table = cell.up('table');
        for( i = 0; i < table.tBodies[0].rows.length; i++) {
            table.tBodies[0].rows[i].cells[index].addClassName('hideColumn');
        }
        var position = 0;
        for( i = 0; i < table.tHead.rows[0].cells.length; i++,position++) {
            var th = table.tHead.rows[0].cells[i];
            var colSpan = th.colSpan;
            for( j = 1; j < colSpan; j++) {
                position++;
            }
            if(position >= index) {
                if (colSpan == 1) {
                    th.addClassName('hideColumn');
                    break;
                }
                else {
                    th.colSpan = colSpan-1;
                    break;
                }
            }
        }
        Event.stop(e);
    },
    initDetect : function(e) {
        e = TableKit.e(e);
        var cell = Event.element(e);
        Event.observe(cell, 'mousemove', TableKit.Resizable.detectHandle);
        Event.observe(cell, 'mousedown', TableKit.Resizable.startResize);
    },
    detectHandle : function(e) {
        e = TableKit.e(e);
        var cell = Event.element(e);
        var test = TableKit.Resizable.pointerPos(cell,Event.pointerX(e),Event.pointerY(e));
        if(TableKit.Resizable.pointerPos(cell,Event.pointerX(e),Event.pointerY(e))){
            cell.addClassName(TableKit.option('resizeOnHandleClass', cell.up('table').id)[0]);
            TableKit.Resizable._onHandle = true;
        } else {
            cell.removeClassName(TableKit.option('resizeOnHandleClass', cell.up('table').id)[0]);
            TableKit.Resizable._onHandle = false;
        }
    },
    killDetect : function(e) {
        e = TableKit.e(e);
        TableKit.Resizable._onHandle = false;
        var cell = Event.element(e);
        Event.stopObserving(cell, 'mousemove', TableKit.Resizable.detectHandle);
        Event.stopObserving(cell, 'mousedown', TableKit.Resizable.startResize);
        cell.removeClassName(TableKit.option('resizeOnHandleClass', cell.up('table').id)[0]);
    },
    startResize : function(e) {
        e = TableKit.e(e);
        if(!TableKit.Resizable._onHandle) {return;}
        var cell = Event.element(e);
        Event.stopObserving(cell, 'mousemove', TableKit.Resizable.detectHandle);
        Event.stopObserving(cell, 'mousedown', TableKit.Resizable.startResize);
        Event.stopObserving(cell, 'mouseout', TableKit.Resizable.killDetect);
        TableKit.Resizable._cell = cell;
        var table = cell.up('table');
        TableKit.Resizable._tbl = table;
        if(TableKit.option('showHandle', table.id)[0]) {
            TableKit.Resizable._handle = $(document.createElement('div')).addClassName('resize-handle').setStyle({
                'top' : cell.cumulativeOffset()[1] + 'px',
                'left' : Event.pointerX(e) + 'px',
                'height' : table.getDimensions().height + 'px'
            });
            document.body.appendChild(TableKit.Resizable._handle);
        }
        Event.observe(document, 'mousemove', TableKit.Resizable.drag);
        Event.observe(document, 'mouseup', TableKit.Resizable.endResize);
        Event.stop(e);
    },
    endResize : function(e) {
        e = TableKit.e(e);
        var cell = TableKit.Resizable._cell;
        TableKit.Resizable.resize(null, cell, (Event.pointerX(e) - cell.cumulativeOffset()[0]));
        Event.stopObserving(document, 'mousemove', TableKit.Resizable.drag);
        Event.stopObserving(document, 'mouseup', TableKit.Resizable.endResize);
        if(TableKit.option('showHandle', TableKit.Resizable._tbl.id)[0]) {
            $$('div.resize-handle').each(function(elm){
                document.body.removeChild(elm);
            });
        }
        Event.observe(cell, 'mouseout', TableKit.Resizable.killDetect);
        TableKit.Resizable._tbl = TableKit.Resizable._handle = TableKit.Resizable._cell = null;
        Event.stop(e);
    },
    drag : function(e) {
        e = TableKit.e(e);
        if(TableKit.Resizable._handle === null) {
            try {
                TableKit.Resizable.resize(TableKit.Resizable._tbl, TableKit.Resizable._cell, (Event.pointerX(e) - TableKit.Resizable._cell.cumulativeOffset()[0]));
            } catch(e) {}
        } else {
            TableKit.Resizable._handle.setStyle({'left' : Event.pointerX(e) + 'px'});
        }
        return false;
    },
    pointerPos : function(element, x, y) {
        var offset = $(element).cumulativeOffset();
        var dim = element.getDimensions();
        var pixel = 5;
        if(Prototype.Browser.IE)
            pixel = 20;
        return (y >= offset[1] &&
                y <  offset[1] + dim.height &&
                x >= offset[0] + dim.width - pixel &&
                x <  offset[0] + dim.width);
      },
    _onHandle : false,
    _cell : null,
    _tbl : null,
    _handle : null
};


TableKit.Editable = {
    init : function(elm, options){
        var table = $(elm);
        if(table.tagName !== "TABLE") {return;}
        TableKit.register(table,Object.extend(options || {},{editable:true}));
        Event.observe(table.tBodies[0], 'click', TableKit.Editable._editCell);
    },
    _editCell : function(e) {
        e = TableKit.e(e);
        var cell = Event.findElement(e,'td');
        if(cell) {
            TableKit.Editable.editCell(null, cell, null, e);
        } else {
            return false;
        }
    },
    editCell : function(table, index, cindex, event) {
        var cell, row;
        if(typeof index === 'number') {
            if(!table || (table.tagName && table.tagName !== "TABLE")) {return;}
            table = $(table);
            index = Math.min(table.tBodies[0].rows.length, index);
            index = Math.max(1, index);
            index -= 1;
            cindex = Math.min(table.rows[0].cells.length, cindex);
            cindex = Math.max(1, cindex);
            cindex -= 1;
            row = $(table.tBodies[0].rows[index]);
            cell = $(row.cells[cindex]);
        } else {
            cell = $(event ? Event.findElement(event, 'td') : index);
            table = (table && table.tagName && table.tagName !== "TABLE") ? $(table) : cell.up('table');
            row = cell.up('tr');
        }
        var op = TableKit.option('noEditClass', table.id);
        if(cell.hasClassName(op.noEditClass)) {return;}

        var head = $(TableKit.getHeaderCells(table, cell)[TableKit.getCellIndex(cell)]);
        if(head.hasClassName(op.noEditClass)) {return;}

        var data = TableKit.getCellData(cell);
        if(data.active) {return;}
        data.htmlContent = cell.innerHTML;
        var ftype = TableKit.Editable.getCellEditor(null,null,head);
        ftype.edit(cell, event);
        data.active = true;
    },
    getCellEditor : function(cell, table, head) {
      var head = head ? head : $(TableKit.getHeaderCells(table, cell)[TableKit.getCellIndex(cell)]);
      var ftype = TableKit.Editable.types['text-input'];
        if(head.id && TableKit.Editable.types[head.id]) {
            ftype = TableKit.Editable.types[head.id];
        } else {
            var n = $w(head.className).detect(function(n){
                    return (TableKit.Editable.types[n]) ? true : false;
            });
            ftype = n ? TableKit.Editable.types[n] : ftype;
        }
        return ftype;
    },
    types : {},
    addCellEditor : function(o) {
        if(o && o.name) { TableKit.Editable.types[o.name] = o; }
    }
};

TableKit.Editable.CellEditor = Class.create();
TableKit.Editable.CellEditor.prototype = {
    initialize : function(name, options){
        this.name = name;
        this.options = Object.extend({
            element : 'input',
            attributes : {name : 'value', type : 'text'},
            selectOptions : [],
            showSubmit : true,
            submitText : 'OK',
            showCancel : true,
            cancelText : 'Cancel',
            ajaxURI : null,
            ajaxOptions : null
        }, options || {});
    },
    edit : function(cell) {
        cell = $(cell);
        var op = this.options;
        var table = cell.up('table');

        var form = $(document.createElement("form"));
        form.id = cell.id + '-form';
        form.addClassName(TableKit.option('formClassName', table.id)[0]);
        form.onsubmit = this._submit.bindAsEventListener(this);

        var field = document.createElement(op.element);
            $H(op.attributes).each(function(v){
                field[v.key] = v.value;
            });
            switch(op.element) {
                case 'input':
                case 'textarea':
                field.value = TableKit.getCellText(cell);
                break;

                case 'select':
                var txt = TableKit.getCellText(cell);
                $A(op.selectOptions).each(function(v){
                    field.options[field.options.length] = new Option(v[0], v[1]);
                    if(txt === v[1]) {
                        field.options[field.options.length-1].selected = 'selected';
                    }
                });
                break;
            }
            form.appendChild(field);
            if(op.element === 'textarea') {
                form.appendChild(document.createElement("br"));
            }
            if(op.showSubmit) {
                var okButton = document.createElement("input");
                okButton.type = "submit";
                okButton.value = op.submitText;
                okButton.className = 'editor_ok_button';
                form.appendChild(okButton);
            }
            if(op.showCancel) {
                var cancelLink = document.createElement("a");
                cancelLink.href = "#";
                cancelLink.appendChild(document.createTextNode(op.cancelText));
                cancelLink.onclick = this._cancel.bindAsEventListener(this);
                cancelLink.className = 'editor_cancel';
                form.appendChild(cancelLink);
            }
            cell.innerHTML = '';
            cell.appendChild(form);
    },
    _submit : function(e) {
        var cell = Event.findElement(e,'td');
        var form = Event.findElement(e,'form');
        Event.stop(e);
        this.submit(cell,form);
    },
    submit : function(cell, form) {
        var op = this.options;
        form = form ? form : cell.down('form');
        var head = $(TableKit.getHeaderCells(null, cell)[TableKit.getCellIndex(cell)]);
        var row = cell.up('tr');
        var table = cell.up('table');
        var s = '&row=' + (TableKit.getRowIndex(row)+1) + '&cell=' + (TableKit.getCellIndex(cell)+1) + '&id=' + row.id + '&field=' + head.id + '&' + Form.serialize(form);
        this.ajax = new Ajax.Updater(cell, op.ajaxURI || TableKit.option('editAjaxURI', table.id)[0], Object.extend(op.ajaxOptions || TableKit.option('editAjaxOptions', table.id)[0], {
            postBody : s,
            onComplete : function() {
                var data = TableKit.getCellData(cell);
                data.active = false;
                data.refresh = true; // mark cell cache for refreshing, in case cell contents has changed and sorting is applied
            }
        }));
    },
    _cancel : function(e) {
        var cell = Event.findElement(e,'td');
        Event.stop(e);
        this.cancel(cell);
    },
    cancel : function(cell) {
        this.ajax = null;
        var data = TableKit.getCellData(cell);
        cell.innerHTML = data.htmlContent;
        data.htmlContent = '';
        data.active = false;
    },
    ajax : null
};

TableKit.Editable.textInput = function(n,attributes) {
    TableKit.Editable.addCellEditor(new TableKit.Editable.CellEditor(n, {
        element : 'input',
        attributes : Object.extend({name : 'value', type : 'text'}, attributes||{})
    }));
};
TableKit.Editable.textInput('text-input');

TableKit.Editable.multiLineInput = function(n,attributes) {
    TableKit.Editable.addCellEditor(new TableKit.Editable.CellEditor(n, {
        element : 'textarea',
        attributes : Object.extend({name : 'value', rows : '5', cols : '20'}, attributes||{})
    }));
};
TableKit.Editable.multiLineInput('multi-line-input');

TableKit.Editable.selectInput = function(n,attributes,selectOptions) {
    TableKit.Editable.addCellEditor(new TableKit.Editable.CellEditor(n, {
        element : 'select',
        attributes : Object.extend({name : 'value'}, attributes||{}),
        'selectOptions' : selectOptions
    }));
};

TableKit.Draggable = {
    init : function(elm, options){
        var table = $(elm);
        if(table.tagName !== "TABLE") {return;}
        TableKit.register(table,Object.extend(options || {},{draggable:true}));
        var headerCells = TableKit.getLastHeaderRow(table);
        headerCells.each(function(c){
            c = $(c);
            Event.observe(c, 'selectstart', function(e) {
                e = Event.extend(TableKit.e(e));
                var cell = Event.element(e);
                var table = cell.up('table');

                if (TableKit.Resizable._handle || TableKit.Draggable.getDraggedCell(table)) {
                    Event.stop(e);
                }
            });
            Event.observe(c, 'mousedown', function(e) {
                e = Event.extend(TableKit.e(e));
                if (!TableKit.Resizable._onHandle && Event.isLeftClick(e) && "TH" === Event.element(e).tagName)
                    TableKit.Draggable.drag(e);
                Event.stop(e);
            });
            Event.observe(c, 'mouseover', TableKit.Draggable.dragEffect);
            Event.observe(c, 'mouseout', TableKit.Draggable.dragEffect);
            Event.observe(c, 'mouseup', TableKit.Draggable.dragEffect);
        });

        var rows = TableKit.getBodyRows(table);
        if(rows) {
            rows.each(function(row) {
                $(row).select('td').each(function(c) {
                    c = $(c);
                    Event.observe(c, 'selectstart', function(e) {
                        e = Event.extend(TableKit.e(e));
                        var cell = Event.element(e);
                        var table = cell.up('table');

                        if (TableKit.Resizable._handle || TableKit.Draggable.getDraggedCell(table)) {
                            Event.stop(e);
                        }
                    });
                    Event.observe(c, 'mouseover', TableKit.Draggable.dragEffect);
                    Event.observe(c, 'mouseout', TableKit.Draggable.dragEffect);
                    Event.observe(c, 'mouseup', TableKit.Draggable.dragEffect);
                });
            });
        }
    },
    dragEffect : function(e) {
        e = Event.extend(TableKit.e(e));
        var cell = Event.element(e);
        var table = cell.up('table');

        var cellIndex = TableKit.getCellIndex(cell);
        var draggedCellIndex = TableKit.Draggable.getDraggedCellIndex(table);
        // return if the cell being hovered is the same as the one being dragged
        if (cellIndex != draggedCellIndex) {
            // if there is a cell being dragged
            if (draggedCellIndex != -1) {
                // change class to give a visual effect
                var headerRows = $A(TableKit.getHeaderRows(table));
                if (headerRows && headerRows.size() > 0) {
                    var lastHeaderRow = $(headerRows.last());

                    var head = $(lastHeaderRow.select('th')[cellIndex]);
                    var hoveringClass = TableKit.option('hoveringClass', table.id)[0];
                    if (e.type == "mouseover") {
                        head.addClassName(hoveringClass);
                    } else {
                        head.removeClassName(hoveringClass);
                    }
                }
            }
        }
    },
    drag : function(e) {
        e = Event.extend(TableKit.e(e));
        var cell = Event.element(e);
        var table = cell.up('table');

        // reference of the cell that is being dragged
        Object.extend(cell, {draggedCell : true});

        // change class for visual effect
        cell.addClassName(TableKit.option('draggingClass', table.id)[0]);

        // create the ghost td
        TableKit.Draggable.createGhostTd(e)
        // start the engine
        TableKit.Draggable.dragEngine(true)
    },
    getDraggedCell : function(table) {
        return table ? $(table).select('th.'+TableKit.option('draggingClass', table.id)[0]).find(function(element) {
            element = $(element);
            return ('draggedCell' in element) && (element.draggedCell);
        }) : null;
    },
    getDraggedCellIndex : function(table) { /* TODO */
        /* return TableKit.Draggable.getDraggedCell(table) ? TableKit.getCellIndex(TableKit.Draggable.getDraggedCell(table)) : -1; */
        return TableKit.Draggable.getDraggedCell(table) ? TableKit.Draggable.getDraggedCell(table).cellIndex : -1;
    },
    getGhostElement : function(table) {
        return table ? $$('div.' + TableKit.option('ghostTdClass', table.id)[0]).find(function(element) {
            element = $(element);
            return ('table' in element) && (element.table === table);
        }) : null;
    },
    createGhostTd : function(e) {
        e = Event.extend(TableKit.e(e));
        var cell = Event.element(e);
        var table = cell.up('table');

        // if ghost exists return
        if (TableKit.Draggable.getGhostElement(table)) return
        // horizontal position
        x = Event.pointerX(e);
        // vertical position
        y = Event.pointerY(e);

        // create the ghost td (visual effect)
        ghostTd = new Element("div", {'className' : TableKit.option('ghostTdClass', table.id)[0],
                                           'style' : 'top: ' + (y + 5) + 'px; left: ' + (x + 10) + 'px'});
        Object.extend(ghostTd, {'table' : table});
        // ghost td receives the content of the dragged cell
        ghostTd.innerHTML = $(TableKit.getLastHeaderRow(table)[TableKit.getCellIndex(cell)]).innerHTML;
        $$("body")[0].insert(ghostTd);
    },
    dragEngine : function(bool) {
        // fire the drop function
        function mouseUpFunction(e) {
            TableKit.Draggable.drop(e)
        }
        function mouseMoveFunction(e) {
            TableKit.Draggable.getCoords(e)
        }

        if (bool) {
            document.documentElement.onmouseup = mouseUpFunction;
            document.documentElement.onmousemove = mouseMoveFunction;

        } else {
            document.documentElement.onmouseup = null;
            document.documentElement.onmousemove = null;
        }
    },
    getCoords : function(e) {
        e = Event.extend(TableKit.e(e));
        var cell = Event.element(e);
        var table = cell.up('table');

        // horizontal position
        x = Event.pointerX(e);
        // vertical position
        y = Event.pointerY(e);

        ghostElement = TableKit.Draggable.getGhostElement(table);

        if (ghostElement) {
            // make the ghostTd follow the mouse
            ghostElement.style.top = y + 5 + "px"
            ghostElement.style.left = x + 10 + "px"
        }
    },
    drop : function(e) {
        // assign event to variable e
        e = Event.extend(TableKit.e(e));
        var cell = Event.element(e);
        var table = cell.up('table');

        // end the engine
        this.dragEngine(false)

        // remove the ghostTd
        ghostElement = TableKit.Draggable.getGhostElement(table);
        if (ghostElement)
            ghostElement.remove();

        var newcellindex = TableKit.getCellIndex(cell);

        var oldcellindex =  TableKit.Draggable.getDraggedCellIndex(table);

        var draggedCell = TableKit.Draggable.getDraggedCell(table);

        // start the function to sort the column
        this.sortColumn(table, oldcellindex,newcellindex)

        // remove visual effect from column being dragged
        if (draggedCell) {
            draggedCell.removeClassName(TableKit.option('draggingClass', table.id)[0]);
        }
    },
    sortColumn : function(table, o,d) {
        // returns if destionation dont have a valid index
        if (d == null) return
        // returns if origin is equals to the destination
        if (o == d) return

        // loop through every row
        TableKit.moveColumn(table, o, d);
    }
};

TableKit.Toggleable = {
    init : function(elm, options){
        var table = $(elm);
        if(table.tagName !== "TABLE") {return;}
        TableKit.register(table,Object.extend(options || {},{toggleable:true}));
        TableKit.Toggleable.loadContainer();

        var headerCells = TableKit.getLastHeaderRow(table);
        headerCells.each(function(c){
            c = $(c);

            Event.observe(c, (Prototype.Browser.Opera ? 'click' : 'contextmenu'), TableKit.Toggleable.openMenu);
        });
    },
    loadContainer: function(){
        if(TableKit.Toggleable.loaded)
            return;
        TableKit.Toggleable.loaded = true;
        container = new Element('div', {'id' : 'control_contextmenu'});
        container.setStyle({
            position : 'absolute',
            zIndex : 99999
        })
        container.hide();
        Object.extend(container, {table : null});
        $$("body")[0].insert(container);
        Event.observe(document.body,'click',function(e){
            e = Event.extend(TableKit.e(e));
            var element = $(Event.element(e));

            var contextmenu = $('control_contextmenu');
            if(contextmenu) {
                var container = element.up('#control_contextmenu');
                if(!container || (container && container.identify() != contextmenu.identify())) {
                    TableKit.Toggleable.closeMenu();
                }
            }
            return true;
        });
    },
    closeMenu: function(e){
        if (e) {
            e = Event.extend(TableKit.e(e));
            Event.stop(e);
        }

        var contextmenu = $('control_contextmenu');
        if (contextmenu) {
            contextmenu.hide();
            contextmenu.update();
            contextmenu.table = null;
        }
    },
    openMenu: function(e){
        e = Event.extend(TableKit.e(e));
        var cell = Event.element(e);
        var currentTable = cell.up('table');

        var contextmenu = $('control_contextmenu');

        if (contextmenu) {
            var contextmenuTable = contextmenu.table;
            if(contextmenuTable)
                TableKit.Toggleable.closeMenu(e);

        }

        /* build the dropdown element and the table with checkbox*/
        TableKit.Toggleable.buildMenu(e);
        TableKit.Toggleable.positionContainer(contextmenu, Event.pointerY(e), Event.pointerX(e));

        Event.stop(e);
        contextmenu.show();

        if ('table' in contextmenu)
            contextmenu.table = currentTable;
    },
    positionContainer: function(contextmenu, top, left){
        contextmenu = $(contextmenu);
        var dimensions = contextmenu.getDimensions();
        var bottom = dimensions.height + top;
        var right = dimensions.width + left;
        var viewport_dimensions = document.viewport.getDimensions();
        var viewport_scroll_offsets = document.viewport.getScrollOffsets();
        if(bottom > viewport_dimensions.height + viewport_scroll_offsets.top)
            top -= bottom - ((viewport_dimensions.height  + viewport_scroll_offsets.top) - TableKit.Toggleable._OFFSET);
        if(right > viewport_dimensions.width + viewport_scroll_offsets.left)
            left -= right - ((viewport_dimensions.width + viewport_scroll_offsets.left) - TableKit.Toggleable._OFFSET);
        contextmenu.setStyle({
            top: top + 'px',
            left: left + 'px'
        });
    },
    buildMenu: function(e){
        e = Event.extend(TableKit.e(e));

        var contextmenu = $('control_contextmenu');
        contextmenu.show();

        var cell = Event.element(e);
        var table = cell.up('table');

        /* creates dropdown  */
        var select_column = new Element('select', {'id' : 'select_column'});
        select_column.hide();
        contextmenu.insert(select_column);

        /* creates list of fieldName */
        var table_checkbox = new Element('table', {'id' : 'table_checkbox', 'class' : 'select_multiple_table'});
        contextmenu.insert(table_checkbox);
        var tbody = new Element('tbody');
        table_checkbox.insert(tbody);

        var headerCells = TableKit.getLastHeaderRow(table);
        var headersSecondRowId='';
        headerCells.each(function(c) {
            c = $(c);
            var text = $(c.firstChild);
            if(typeof(text) != "undefined" && text!=null) {
                name = text.nodeName;
                if(name != "#text") {
                    text = text.childNodes[0];
                }
                var tr = new Element('tr');
                tbody.appendChild(tr);
                var td1 = new Element('td', {'class' : 'select_multiple_name'});
                td1.update(text.nodeValue);
                tr.insert(td1);
                var td2 = new Element('td', {'class' : 'select_multiple_checkbox'});
                tr.insert(td2);
                var input = new Element('input', {'type' : 'checkbox', 'value' : c.readAttribute('id')});
                td2.insert(input);
                var option = new Element('option', {'value' : c.readAttribute('id')});
                option.update(text.nodeValue);
                select_column.insert(option);
                option.text = text.nodeValue;
                if(!c.hasClassName('hidden')) {
                    input.writeAttribute('checked', 'checked');
                    headersSecondRowId = headersSecondRowId.concat(c.readAttribute('id')).concat(TableKit.Toggleable._VALUE_SEPARATOR);
                }
            }
        });
        var option = new Element('option', {'value' : headersSecondRowId, 'selected' : 'selected'});
        select_column.insert(option);

        /* creates button for submit change and close contextMenu*/
        var select_multiple_submit = new Element('div', {'class' : 'select_multiple_submit'});
        var input = new Element('input', {'type' : 'button', 'value' : 'Ok', 'id' : 'select_close'});
        select_multiple_submit.insert(input);
        contextmenu.insert(select_multiple_submit);

        var table_checkbox = new Control.SelectMultiple('select_column','table_checkbox',{
               checkboxSelector: 'input[type=checkbox]',
               valueSeparator: TableKit.Toggleable._VALUE_SEPARATOR,
               nameSelector: '.select_multiple_name'
        });
        table_checkbox.setValue(headersSecondRowId);

        Event.observe(select_multiple_submit, 'click', function(e){
            e = Event.extend(TableKit.e(e));
            Event.stop(e);

            var contextmenu = $('control_contextmenu');
            contextmenu.hide();

            var select_column = contextmenu.down('select#select_column');
            var value_string = select_column.options[select_column.options.selectedIndex].value;
            var value_collection = $A(value_string.split ? value_string.split(TableKit.Toggleable._VALUE_SEPARATOR) : value_string)

            var table = contextmenu.table;
            var headers = TableKit.getLastHeaderRow(table);
            headers.each(function(item) {
                item = $(item);
                if(value_collection.indexOf(item.readAttribute('id')) < 0) {
                    if(!item.hasClassName('hidden')) {
                        TableKit.Toggleable.hideColumn(table, item);
                    }
                }
                else {
                    if(item.hasClassName('hidden')) {
                        TableKit.Toggleable.showColumn(table, item);
                    }
                }
            });
            TableKit.Toggleable.closeMenu(e);
        });
    },
    hideColumn:function(table, headerCell){
        var index = TableKit.getCellIndex(headerCell);
        var positionThRow1 = -1; /* position of the th in the first row of header */
                                                 /* the colspan increment position */
        /* hide title element in the second Header Row */
        headerCell.addClassName('hidden');

        /* hide body element */
        TableKit.getBodyRows(table).each(function(row) {
            row = $(row);
            row.down('td', index).addClassName('hidden');
        });

        /* select fieldGroupId if exists */
        var classFieldGroupId = TableKit.getClassFieldGroupId(table, TableKit.getCellIndex(headerCell))
        if(classFieldGroupId) {
            if(classFieldGroupId.readAttribute('colspan') == 1) {
                classFieldGroupId.addClassName('hidden');
            }
            else{
                classFieldGroupId.writeAttribute('colspan',parseInt(classFieldGroupId.readAttribute('colspan'))-1);
            }
        }
        /* else select the call in the first Header Row */
        else {
            var lastHeaderCells = TableKit.getLastHeaderRow(table);
            var firstHeaderCells = TableKit.getFirstHeaderRow(table);
            if(firstHeaderCells) {
                index = lastHeaderCells.inject(0,function(acc, actualCell) {
                    if($(actualCell) === $(headerCell)) {
                        throw $break;
                    }
                    return acc + ($(actualCell).hasClassName('hidden') ? 0 : 1);
                });
                var showedCell = firstHeaderCells.findAll(function(element) {
                    return !$(element).hasClassName('hidden');
                });

                showedCell.each(function(th) {
                    positionThRow1 += th.colSpan;
                    if(positionThRow1 >= index) {
                        if (parseInt(th.colSpan) == 1) {
                            th.addClassName('hidden');
                            throw $break;
                        }
                        else {
                            th.writeAttribute('colspan',parseInt(th.colSpan)-1);
                            throw $break;
                        }
                    }
                });
            }
        }
     },
     showColumn:function(table, headerCell){
        var index = TableKit.getCellIndex(headerCell);
        var positionThRow1 = -1; /* position of the th in the first row of header */
                                                 /* the colspan increment position */
        /* show title element in the second Header Row */
        headerCell.removeClassName('hidden');

        /* show body element */
        TableKit.getBodyRows(table).each(function(row) {
            row = $(row);
            row.down('td', index).removeClassName('hidden');
        });

        /* select fieldGroupId if exists */
        var classFieldGroupId = TableKit.getClassFieldGroupId(table, TableKit.getCellIndex(headerCell))
        if(classFieldGroupId) {
            if(classFieldGroupId.hasClassName('hidden')) {
                classFieldGroupId.removeClassName('hidden');
            } else {
                classFieldGroupId.writeAttribute('colspan',parseInt(classFieldGroupId.readAttribute('colspan'))+1);
            }
        } else {
            /* else select the call in the first Header Row */
            var lastHeaderCells = TableKit.getLastHeaderRow(table);
            var firstHeaderCells = TableKit.getFirstHeaderRow(table);
            if(firstHeaderCells) {
                index = lastHeaderCells.inject(0,function(acc, actualCell) {
                    if($(actualCell) === $(headerCell)) {
                        throw $break;
                    }
                    return acc + ($(actualCell).hasClassName('hidden') && $(actualCell).readAttribute('class').indexOf(TableKit.FIELD_GROUP_CLASS_ID) == -1)? 1 : 0;
                });

                var showedCell = firstHeaderCells.findAll(function(element) {
                    return $(element).hasClassName('hidden') && !$(element).hasClassName("fieldgroup");
                });

                showedCell.each(function(th) {
                    positionThRow1 += th.colSpan;
                    if(positionThRow1 >= index) {
                        if(th.hasClassName('hidden')) {
                            th.removeClassName('hidden');
                            throw $break;
                        } else {
                            th.writeAttribute('colspan',parseInt(th.colSpan)+1);
                            throw $break;
                        }
                    }
                });
            }
        }
    },
    _OFFSET : 4,
    _VALUE_SEPARATOR : ','
};


TableKit.HeaderFixable = {
    init : function(elm, options){
        /* add submit for save user setting */
        var table = $(elm);
        if(table.tagName !== "TABLE") {return;}
        TableKit.register(table,Object.extend(options || {},{headerFixable:true}));
        if(TableKit.getBodyRows(table)) {
            /* se l'altezza del body contente tutte le righe della tabella è maggione di quella impostata nel foglio di stile
               viene inserito un div tableContainer con scroll-bar e header fisso */
            /* l'altezza della riga è calcolata senza tableContainer e con height: auto per il tbody*/
            var divTableContainer = table.up('div.tableContainer');
            if(divTableContainer) {
                divTableContainer.insert({'before' : table});
                divTableContainer.remove();
            }
            var tbodyHeight = 'auto';
            table.down('tbody').setStyle({height : tbodyHeight});
           // var rowHeight = TableKit.getBodyRows(table).first().getDimensions().height;

            /* l'altezza del tbody è calcolata con il div tableContainer e senza height: auto per il tbody*/
            if(!table.up('div.tableContainer')) {
                divTableContainer = new Element('div', {'class' : 'tableContainer'});
                table.insert({after : divTableContainer});
                divTableContainer.insert(table);
                divTableContainer.setStyle({height : "auto"});
            }
          /*  table.down('tbody').setStyle({height : ''});
            var h = divTableContainer.down('tbody').getHeight();

            var tableSize = TableKit.getBodyRows(table).size();
            if(rowHeight * tableSize < h) {
                table.down('tbody').setStyle({height : tbodyHeight});
                var rowHeight = TableKit.getBodyRows(table).first().getDimensions().height;
                divTableContainer.insert({'before' : table});
                divTableContainer.remove();
            } */
        }
    }

};
TableKit.HoverBar = {
    init : function(elm, options){
        var table = $(elm);
        if(table.tagName !== "TABLE") {return;}
        TableKit.register(table,Object.extend(options || {},{hoverbar:true}));

        if (Prototype.Browser.IE) {
            var rows = TableKit.getBodyRows(table);
            rows.each(function(row){
                row = $(row);
                $A(row.select('td')).each(function(c) {
                    Event.observe(c, 'mouseover', TableKit.HoverBar.hoverEffect);
                    Event.observe(c, 'mouseout', TableKit.HoverBar.hoverEffect);
                });
            });
        }
    },
    hoverEffect : function(e) {
        e = Event.extend(TableKit.e(e));
        var cell = Event.element(e);

        TableKit.HoverBar.toggleBar(e, cell);

        $A(cell.siblings()).each(function(c) {
            TableKit.HoverBar.toggleBar(e, c);
        });
    },
    toggleBar : function(e, cell) {
        if (e.type == 'mouseover' && !cell.hasClassName('hover-bar-cell')) {
            cell.addClassName('hover-bar-cell');
        } else if (e.type == 'mouseout' && cell.hasClassName('hover-bar-cell')){
            cell.removeClassName('hover-bar-cell')
        }
    }
};

TableKit.Selectable = {
    init : function(elm, options){
        var table = $(elm);
        if(table.tagName !== "TABLE") {return;}
        TableKit.register(table,Object.extend(options || {},{selectable:true}));

        var rows = TableKit.getBodyRows(table);
        if(rows) {
            rows.each(function(row){
                row = $(row);
                $A(row.select('td')).each(function(c) {
                    Event.observe(c, 'mousedown', TableKit.Selectable.selectRowEvent);
                    Event.observe(c, 'dom:mousedown', TableKit.Selectable.selectRowEvent);
                    Event.observe(c, 'dblclick', TableKit.Selectable.dblclickSelection);
                    Event.observe(c, 'dom:dblclick', TableKit.Selectable.dblclickSelection);
                });
            });
        }
     },
    dblclickSelection : function(e) {
        e = Event.extend(TableKit.e(e));
        var cell = Event.element(e);
        var table = cell.up('table');

        TableKit.notify('onDblClickSelectStart', table, e);

        TableKit.Selectable.selectRowEvent(e);

        //TODO Gestire ulteriore evento
        TableKit.notify('onDblClickSelectEnd', table, e);
    },
    selectFirstRow : function(table) {
        var rowToSelect = TableKit.getRow(table, 0);
        TableKit.Selectable._selectRow(rowToSelect);
        return rowToSelect;
    },
    selectRow : function(row, table) {
        TableKit.Selectable._toggleSelection(row, table);
    },
    selectRowEvent : function(e) {
        e = Event.extend(TableKit.e(e));
        var cell = Event.element(e);
        var table = cell.up('table');

        if (TableKit.Resizable._handle || TableKit.Draggable.getDraggedCell(table)) {
            Event.stop(e);
            return;
        }
        var row = cell.up('tr');
        if (!TableKit.Selectable._lastSelectedRow || (TableKit.Selectable._lastSelectedRow && TableKit.Selectable._lastSelectedRow !== row)) {
            TableKit.notify('onSelectStart', table, e);

            if (e.ctrlKey) {
                TableKit.Selectable._selectRow(row);
            } else if (e.shiftKey) {
                if (TableKit.Selectable._lastSelectedRow) {
                    TableKit.Selectable._addRangeSelection(TableKit.Selectable._lastSelectedRow,row);
                } else {
                    TableKit.Selectable._toggleSelection(row);
                }
            } else {
                TableKit.Selectable._toggleSelection(row);
            }
            TableKit.Selectable._lastSelectedRow = row;

            TableKit.notify('onSelectEnd', table, e);
        }
    },
    getSelectedRows : function(table, row) {
        if (!table) table = row.up('table');
        var rows = TableKit.getBodyRows(table) || $A([]);
        return rows.select(function(row) {
            row = $(row);
            return ('selected' in row) && row.selected;
        });
    },
    setTbodyHeight : function(tbody, height) {   	
    	tbody.setStyle({height: height + "px"});
    	tbody.down("tr").each(function(row) {
            if(row) {
            	row.setStyle({height: "auto"});
            }
        });
    },    
    _toggleSelection : function(newRow, table) {
        var currentSelectedRow = TableKit.Selectable.getSelectedRows(table, newRow);
        if (currentSelectedRow) {
            currentSelectedRow.each(function(row) {
                TableKit.Selectable._deselectRow(row);
            });
        }
        TableKit.Selectable._selectRow(newRow);
    },
    _addRangeSelection : function(oldRow, newRow) {
        if (newRow) {
            if (!oldRow) {
                TableKit.Selectable._toggleSelection(newRow);
            } else {
                oldRow = $(oldRow);
                newRow = $(newRow);
                if ($A(oldRow.siblings()).include(newRow)) {
                    var oldRowIndex = TableKit.getRowIndex(oldRow);
                    var newRowIndex = TableKit.getRowIndex(newRow);

                    $A(TableKit.Selectable.getSelectedRows(null, newRow)).each(function(row) {
                        if (row != oldRow) {
                            TableKit.Selectable._deselectRow(row);
                        }
                    });

                    $R((oldRowIndex >= newRowIndex ? newRowIndex : oldRowIndex ), (oldRowIndex <= newRowIndex ? newRowIndex : oldRowIndex)).each(function(index) {
                        TableKit.Selectable._selectRow(TableKit.getRow(oldRow.up('table'), index));
                    });
                }
            }
        }
    },
    _selectRow : function(row) {
        if (row) {
            row = $(row);
            if (!('selected' in row))
                Object.extend(row, {selected : true});
            else if (!row.selected){
                row.selected = true;
            }
            if (Prototype.Browser.IE) {
               $A(row.select('td')).each(function(c) {
                   c = $(c);
                   if (!c.hasClassName('selected-cell'))
                       c.addClassName('selected-cell');
                });
            } else {
                if (!row.hasClassName('selected-row'))
                    row.addClassName('selected-row');
            }
        }
    },
    _deselectRow : function(row) {
        if (row) {
            row = $(row);
            if (('selected' in row) && row.selected){
                row.selected = false;
            }
            if (Prototype.Browser.IE) {
               $A(row.select('td')).each(function(c) {
                   c = $(c);
                   if (c.hasClassName('selected-cell'))
                       c.removeClassName('selected-cell');
                });
            } else {
                if (row.hasClassName('selected-row'))
                    row.removeClassName('selected-row');
            }
        }
    },
    _lastSelectedRow : null
};

document.observe("dom:loaded", TableKit.load);
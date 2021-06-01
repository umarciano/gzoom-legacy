CellSelection = {
    load : function(newContentToExplore, withoutResponder, contentToUpdate) {
        var tableSelectable = !Object.isElement(newContentToExplore) ? $$('table.selectable') : ((newContentToExplore.tagName==='TABLE' && newContentToExplore.hasClassName('selectable')) ? [newContentToExplore] : newContentToExplore.select('table.selectable'));
        tableSelectable.each(function(table) {
            if (TableKit.isSelectable(table) && !table.hasClassName('no-jar-selectable')) {
                TableKit.registerObserver(table, 'onSelectEnd', 'cell-selection-cookie-management', function(table, e) {
                    var selectRow = TableKit.Selectable.getSelectedRows(table)[0];
                    var rowIndex = TableKit.getRowIndex(selectRow);

                    var jar = new CookieJar({path : "/"});

                    if (!selectRow) {
                        jar.remove("selectedRow" + table.identify());
                    } else {
                        var row = jar.get("selectedRow" + table.identify());
                        if (row) {
                            if(row !== rowIndex.toString()) {
                                jar.remove("selectedRow" + table.identify());
                                jar.put("selectedRow" + table.identify(), rowIndex);
                            }
                        }
                        else
                            jar.put("selectedRow" + table.identify(), rowIndex);
                    }
                } );
            }
        });

        CellSelection._registerFocusEvent(newContentToExplore);
        CellSelection._registerTabEvent(newContentToExplore, contentToUpdate);
        CellSelection._selectRow(newContentToExplore);

        if (!withoutResponder) {
//            CellSelection._selectRow(newContentToExplore);

            UpdateAreaResponder.Responders.register( {
                onLoad : function(newContent, options) {
                    if (newContent) {
                        CellSelection.load(newContent, true, options.contentToUpdate);
                    }
                },
                onAfterLoad : function(newContent) {
                    if (newContent) {
                        CellSelection._selectRow(newContent);
                    }
                }
            }, 'cell-selection');
        }
    },

    _registerTabEvent : function(newContentToExplore, contentToUpdate) {
        if(!Object.isElement($(newContentToExplore))) {
            newContentToExplore = $(document.body);
        }
        
        /*var forceRegistration = false;
        if (contentToUpdate) {
        	var currentContentToUpdate = $(contentToUpdate);
        	if (Object.isElement(currentContentToUpdate)) {
        		var tabs = Control.Tabs.findByTabId(currentContentToUpdate.identify());
                if (tabs) {
                	forceRegistration = true;
                }
        	}
        }*/
        
        var forceRegistration = false;
        if (contentToUpdate) {
        	var currentContentToUpdate = $(contentToUpdate);
        	if (Object.isElement(currentContentToUpdate)) {
        		var tabs = Control.Tabs.findByTabId(currentContentToUpdate.identify());
                if (tabs) {
                	var activeContainer = tabs.getActiveContainer();
                	forceRegistration = Object.isElement(activeContainer) && activeContainer.identify() === currentContentToUpdate.identify();
                }
        	}
        }
        
        if(typeof Control.Tabs !== 'undefined') {
            Control.Tabs.massiveRegisterEvent(newContentToExplore, Prototype.K, CellSelection._selectRow, "cellselection", null, forceRegistration, true);
        }
    },

    _registerFocusEvent : function(newContentToExplore) {
        var tableList = [];
        var multiEditableTableList = !Object.isElement(newContentToExplore) ? $(document.body).select('table.multi-editable') : ((newContentToExplore.tagName==='TABLE' && newContentToExplore.hasClassName('multi-editable')) ? [newContentToExplore] : newContentToExplore.select('table.multi-editable'));

        tableList.concat(multiEditableTableList);
        multiEditableTableList.each(function(table) {
            if (TableKit.isSelectable(table)) {
                TableKit.registerObserver(table, 'onSelectEnd', 'focus-cell', CellSelection._focusCell);
            }

            table.select('input, select').select(function(element) {
                return (element.tagName === 'INPUT' && element.readAttribute('type') !== 'hidden' && element.readAttribute('type') !== 'submit') ||  element.tagName === 'SELECT';
            }).each(function(element) {
                Event.observe(element, 'focus', function(e) {
                    elm = Event.element(e);

                    CellSelection._selectCell(elm);
                });
            });
        });

        var singleEditableTableList = !Object.isElement(newContentToExplore) ? $(document.body).select('table.single-editable', '.login-screenlet table.basic-table') : newContentToExplore.select('table.single-editable', '.login-screenlet table.basic-table');

        tableList.concat(singleEditableTableList);
        singleEditableTableList.each(function(table) {
            table.select('input, select').each(function(element) {
                if (element.type !== 'hidden' && element.type !== 'submit') {
                    Event.observe(element, 'focus', function(e) {
                        elm = Event.element(e);

                        if (!elm.readAttribute('readonly'))
                            elm.addClassName('selected-cell-input')
                    });

                    Event.observe(element, 'blur', function(e) {
                        elm = Event.element(e);

                        if (!elm.readAttribute('readonly'))
                            elm.removeClassName('selected-cell-input')
                    });
                }
            });
        });

        return tableList;
    },

    _selectRow : function(newContent) {
        var selectedRowId;
        var selectedRow;
        var tableCount = 0;
        var singleEditableFound = false;

        do {
            if (!Object.isElement($(newContent))) {
                newContent = $(document.body);
            }

            var table = newContent.down('table.single-editable');
            if (!table) {
                table = newContent.down('.login-screenlet table.basic-table');
            }
            if (!table) {
                table = newContent.down('table.selectable', tableCount);
                if (!table) return;

                tableCount++;
                var jar = new CookieJar({path : "/"});
                selectedRowId = jar.get("selectedRow" + table.identify());
            }
            else {
                singleEditableFound = true;
            }

            if(selectedRowId) {
                selectedRow = TableKit.getRow(table, selectedRowId);
            }
            else {
                if (TableKit.isSelectable(table)) {
                    var rows = TableKit.getBodyRows(table);
                    if (rows)
                        selectedRow = rows.first();
                }
            }

            if (selectedRow && TableKit.isSelectable(table) && !TableKit.Selectable.getSelectedRows(table).include(selectedRow) ) {
                //TableKit.Selectable._toggleSelection(selectedRow, table);
                selectedRow.down('td').fire('dom:mousedown');
            }
            else {
                if (table) {
                	elements = CellSelection._getElementsOfRow(table);
                    focusedElement = document.activeElement;
                    if(elements.indexOf(focusedElement) == -1) {
	                	elementToFocus = CellSelection._findFirstElement(table, ['input']);
	                    if (elementToFocus) {
	                    	elementToFocus.focus();
	                    }
                    }
                }
            }
        }
        while(!singleEditableFound);
    },

    _getElementsOfRow: function(container) {
    	var elements = null;
     	var selectedRows = new Array();
        if (TableKit.isSelectable($(container)))
            selectedRows = TableKit.Selectable.getSelectedRows($(container));
        if(selectedRows.size() > 0) {
            var selectedRow = selectedRows.first();
            elements = selectedRow.select('input', 'select', 'textarea').findAll(function(element) {
                return 'hidden' != element.type && 'submit' != element.type && !element.disabled && element.readAttribute('readonly') !== 'readonly';
            });
        } else {
            elements = $(container).descendants().findAll(function(element) {
                return 'hidden' != element.type && 'submit' != element.type && !element.disabled && element.readAttribute('readonly') !== 'readonly';
            });
        }
        
    	return elements;
    },
    
    _findFirstElement: function(container, typeElement) {
        typeElement = typeElement || ['input', 'select', 'textarea'];
        var elements = CellSelection._getElementsOfRow(container);
        firstByIndex = elements.findAll(function(element) {
            return element.hasAttribute('tabIndex') && element.tabIndex >= 0;
        }).sortBy(function(element) { return element.tabIndex }).first();

        return firstByIndex ? firstByIndex : elements.find(function(element) {
            return typeElement.include(element.tagName.toLowerCase());
        });
    },

    _selectCell : function(elementFocused) {
        if (elementFocused) {
            var tdSelected = elementFocused;
            if (tdSelected.tagName !== 'TD')
                tdSelected = elementFocused.up('td');

            if (tdSelected) {

                var currentPosition = -1;
                var currentRow = tdSelected.up('tr');
                if (currentRow) {
                    currentPosition = $A(currentRow.select('td')).inject(0, function(acc, cell) {
                        if (cell !== tdSelected)
                            return acc+1;
                        else
                            throw $break;

                    });
                }

                var selectedCellList = tdSelected.up('table').select('td.selected-cell-input');
                selectedCellList.invoke('removeClassName', 'selected-cell-input');

                var selectedCellTopList = tdSelected.up('table').select('td.selected-cell-top');
                selectedCellTopList.invoke('removeClassName', 'selected-cell-top');

                var selectedCellRightList = tdSelected.up('table').select('td.selected-cell-right');
                selectedCellRightList.invoke('removeClassName', 'selected-cell-right');

                var selectedCellBottomList = tdSelected.up('table').select('td.selected-cell-bottom');
                selectedCellBottomList.invoke('removeClassName', 'selected-cell-bottom');

                var rightElement = tdSelected.next('td',0);
                if (rightElement) {
                    if (!rightElement.hasClassName('selected-cell-right'))
                        rightElement.addClassName('selected-cell-right');
                }

                if (currentPosition != -1) {
                    var upRow = currentRow.previous('tr', 0);
                    if (upRow) {
                        var upElement = upRow.select('td')[currentPosition];
                        if (upElement) {
                            if (!upElement.hasClassName('selected-cell-top'))
                                upElement.addClassName('selected-cell-top');
                        }

                    }
                    var bottomRow = currentRow.next('tr', 0);
                    if (bottomRow) {
                        var bottomElement = bottomRow.select('td')[currentPosition];
                        if (bottomElement) {
                            if (!bottomElement.hasClassName('selected-cell-bottom'))
                                bottomElement.addClassName('selected-cell-bottom');
                        }

                    }
                }

                if (!tdSelected.hasClassName('selected-cell-input'))
                    tdSelected.addClassName('selected-cell-input');

                var selectedRows = TableKit.Selectable.getSelectedRows(null, currentRow);
                if (!selectedRows.include(currentRow)) {
//                    TableKit.Selectable._toggleSelection(currentRow);
                    tdSelected.fire('dom:mousedown');
                }



            }
        }
    },
    _focusCell : function(table, e) {
        var cell = Event.element(e);
        if (cell.tagName !== 'TD')
            cell = cell.up('td');

        if (cell.tagName === 'TD') {
            Event.stop(e);

            var row = cell.up('tr');
            var editableWidget = $A(cell.descendants()).select(function(element) {
               return (element.tagName === 'INPUT' && element.readAttribute('type') !== 'hidden' && element.readAttribute('type') !== 'submit') ||  element.tagName === 'SELECT';
            });
            if (!editableWidget || (editableWidget && editableWidget.length == 0)) {
                cell = cell.next('td', 0);
                if (!cell) {
                    var cells = $A(row.select('td'));

                    cell = cells.find(function(element) {
                        var editableWidget = $A(element.descendants()).select(function(element) {
                           return (element.tagName === 'INPUT' && element.readAttribute('type') !== 'hidden' && element.readAttribute('type') !== 'submit') ||  element.tagName === 'SELECT';
                        });
                        if (editableWidget && editableWidget.length > 0) {
                            return element;
                        }
                    });
                }
            }

            if (cell) {
                var editableWidget = $A(cell.descendants()).select(function(element) {
                   return (element.tagName === 'INPUT' && element.readAttribute('type') !== 'hidden' && element.readAttribute('type') !== 'submit') ||  element.tagName === 'SELECT';
                });
                if (editableWidget && editableWidget.length > 0) {
                    $(editableWidget.first()).focus();
                }
            }
        }
    }
}

document.observe("dom:loaded", CellSelection.load);
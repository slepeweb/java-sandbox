/**
 ***********************************************************************
 ***********************************************************************
Copyright (C) 1996 - 2011 Alterian Technology Ltd. All rights reserved.

Alterian Technology Ltd
Alterian plc
The Spectrum Building
Bond Street
Bristol BS1 3LG, UK
+44 (0) 117 970 3200 +44 (0) 117 970 3201

http://www.alterian.com
info@alterian.com
 ***********************************************************************
File Information
================

%PID% %PRT% %PO%
%PM% - %PD%

 ***********************************************************************
 ***********************************************************************
 */

var PSArea = Class.create(PSPageElement, {
	initialize: function($super, params) {
		$super(params);
		this.loadPage();
	},
	
	createElement: function($super, parent) {
		this.elementName = "area";
		$super(parent);
		this.element.addClassName(PageStudio.CLASS.BODY);
		this.createToolbar();
		if(PageStudio.contributor == "MWC") {
			this.element.fadeEle = true;
			//stop the MWC from fading out the PS area
			PageStudio.preventContributorFade(this.element);
		}
	},
	
	createListeners: function($super) {
		$super({
			onCellAdded: this.onCellAdded.bindAsEventListener(this),
			onCellItemAdded: this.onCellItemAdded.bindAsEventListener(this),
			onCellSelected: this.onCellSelected.bindAsEventListener(this),
			onCellItemSelected: this.onCellItemSelected.bindAsEventListener(this),
			onDeleteCellClicked: this.onDeleteCellClicked.bindAsEventListener(this),
			onDeleteCellItemClicked: this.onDeleteCellItemClicked.bindAsEventListener(this),
			onComponentSelected: this.onComponentSelected.bindAsEventListener(this),
			onComponentDropped: this.onComponentDropped.bind(this),
			onSelectedCellRemoved: this.onSelectedCellRemoved.bindAsEventListener(this),
			onSelectedItemRemoved: this.onSelectedItemRemoved.bindAsEventListener(this),
			onLayoutChanged: this.onLayoutChanged.bindAsEventListener(this),
			onPageStudioLoaded: this.onPageStudioLoaded.bindAsEventListener(this),
			onMarkupComponentSelected: this.onMarkupComponentSelected.bindAsEventListener(this)
		});
	},
	
	attachEvents: function($super) {
		$super();		
		this.element.observe(PageStudio.EVENT.CELL_SELECTED, this.listeners.onCellSelected);
		this.element.observe(PageStudio.EVENT.CELL_ITEM_SELECTED, this.listeners.onCellItemSelected);
		this.element.observe(PageStudio.EVENT.DELETE_CELL_CLICKED, this.listeners.onDeleteCellClicked);
		this.element.observe(PageStudio.EVENT.DELETE_CELL_ITEM_CLICKED, this.listeners.onDeleteCellItemClicked);
		this.element.observe(PageStudio.EVENT.LAYOUT_CHANGED, this.listeners.onLayoutChanged);
		
		document.observe(PageStudio.EVENT.ADD_COMPONENT_CLICKED, this.listeners.onComponentSelected);
		document.observe(PSInsertMarkupComponentTool.EVENT.COMPONENT_SUBMITTED, this.listeners.onMarkupComponentSelected);
		document.observe(PageStudio.EVENT.LOADED, this.listeners.onPageStudioLoaded);
	},
	
	createToolbar: function() {
		this.toolbar = new PSMainTab({ "target": this.element, "transition": "appear" });
	},
	
	addComponent: function(component) {
		new PSAddComponent({ "body": this.element, "cell": this.selectedCell, "component": component, "undoable": true });
	},
	
	copyItem: function(item) {
		return new PSAddComponent({ "body": this.element, "cell": this.selectedCell, "item": item, "undoable": true });
	},
	
	addMarkupComponent: function(markup, element) {
		if(this.selectedItem) {
			if(element && this.selectedItem.replaceElement) {
				this.selectedItem.replaceElement(element, markup);
			}
			else if(this.selectedItem.insertIntoCurrentSelection) {
				this.selectedItem.insertIntoCurrentSelection(markup);
			}
			//Call focus on the wrappper so that the Smart Client can detect that there has been a change.
			if(this.selectedItem.wrapper) this.selectedItem.wrapper.focus();
		}
	},
	
	setSelectedItem: function(item) {
		if(this.selectedItem != null) {
			this.selectedItem.element.stopObserving(PageStudio.EVENT.REMOVED, this.listeners.onSelectedItemRemoved);
			this.selectedItem = null;
		}
		if (item) {
			this.selectedItem = item;
			this.selectedItem.element.observe(PageStudio.EVENT.REMOVED, this.listeners.onSelectedItemRemoved);
			PSEdit.notifyCopy(true);
		}
		else {
			PSEdit.notifyCopy(false);
		}
	},
	
	setSelectedCell: function(cell) {
		if (cell) {
			this.selectedCell = cell;
			this.selectedCell.element.observe(PageStudio.EVENT.REMOVED, this.listeners.onSelectedCellRemoved);
		}
		else if(this.selectedCell != null) {
			this.selectedCell.element.stopObserving(PageStudio.EVENT.REMOVED, this.listeners.onSelectedCellRemoved);
			this.selectedCell = null;
		}
	},
	
	loadPage: function() {
		//temporarily disable the cell and item added events while the page is loading
		//or they will be fired many times unnecessarily.
		this.element.stopObserving(PageStudio.EVENT.CELL_ADDED, this.listeners.onCellAdded);
		this.element.stopObserving(PageStudio.EVENT.CELL_ITEM_ADDED, this.listeners.onCellItemAdded);
		this.element.observe(PageStudio.EVENT.PAGE_LOAD_COMPLETED, this.onLoadCompleted.bindAsEventListener(this));
		new PSLoadPage(this.element);
	},
	
	makeElementsSortable: function() {
		this.element.select("."+PageStudio.CLASS.CELL).pluck("PSObj").invoke("makeSortable");
		Sortable.create(this.element, { "tag": "div", "only": PageStudio.CLASS.CELL, "handle": PageStudio.CLASS.CELL_HANDLE, "containment": $$("."+PageStudio.CLASS.BODY), "dropOnEmpty": true, "zindex": 99999, "scroll": window, "overlap": "null", "constraint": null, "greedy": true, "onUpdate": this.onSorted });
		PSDroppableReceiver.add(this.element, null, { "greedy": true, "accept": PageStudio.CLASS.DRAG_WRAPPER, "onReceive": this.listeners.onComponentDropped });
	},
	
	getCells: function()
	{
		return this.element.select("." + PageStudio.CLASS.CELL);
	},	
	
	setLayout: function(layout) {
		var oldCells = this.element.select("." + PageStudio.CLASS.CELL);
		
		//add new layout
		this.element.insert(decodeURIComponent(layout.html));

		//filters existing cells
		var filter = function(cell) {
			return oldCells.member(cell);
		};
		
		//remove existing cell items from the old layout.
		var cells = this.getCells();
		var newCells = cells.reject(filter.bind(this));
		
		var success = false;
		
		if(newCells.length > 0) {
			//move the cell items in to the new layout.
			for(var c = 0; c < oldCells.length; c++) {
				var cell = oldCells[c];
				var items = cell.select("." + PageStudio.CLASS.CELL_ITEM);
				for(var i = 0; i < items.length; i++) {
					var item = items[i];
					if (c < newCells.length) {
						newCells[c].insert(item);
					}
					else {
						newCells[newCells.length-1].insert(item);
					}
				}
				cell.PSObj.remove();
			}
			newCells[0].focus();
			success = true;
		}
		else if(confirm(PageStudio.locale.get("confirm_blank_layout"))){
			this.clear(cells);
			success = true;
		}
		
		if(success) {
	   	//reload the page
		  this.element.fire(PageStudio.EVENT.LAYOUT_CHANGED);
		  PageStudio.setDirty(true);
		}
		
		return success;
	},
	
	clear: function(cells) {
		if(!cells) cells = this.getCells(); 
		cells.pluck("PSObj").invoke("remove");
	},
	
	hide: function() {
		this.element.addClassName(PageStudio.CLASS.HIDE);
	},
	
	show: function() {
		this.element.removeClassName(PageStudio.CLASS.HIDE);
	},
	
	select: function($super) {
		$super();
		this.element.fire(PSArea.EVENT.PAGE_AREA_SELECTED, { "area": this });
		this.toolbar.show();
	},
	
	deselect: function($super) {
		$super();
		this.toolbar.hide();
	},
	
	onLoadCompleted: function(e) {
		this.element.stopObserving(PageStudio.EVENT.PAGE_LOAD_COMPLETED);
		
		this.element.observe(PageStudio.EVENT.CELL_ADDED, this.listeners.onCellAdded);
		this.element.observe(PageStudio.EVENT.CELL_ITEM_ADDED, this.listeners.onCellItemAdded);
		this.loaded = true;
		if(PageStudio.loaded) this.makeElementsSortable();
		document.fire(PSArea.EVENT.PAGE_AREA_LOADED);
	},
	
	onPageStudioLoaded: function() {
		//TODO: Find out why stopping this observer can cause a problem in webkit.
		document.stopObserving(PageStudio.EVENT.LOADED, this.listeners.onPageStudioLoaded);
		this.makeElementsSortable();
	},
	
	onLayoutChanged: function(e) {
		this.loadPage();
	},
	
	onCellAdded: function(e) {
		var cell = e.memo.object;
		cell.select();
		this.makeElementsSortable();
		PageStudio.setDirty(true);
	},
	
	onCellItemAdded: function(e) {
		var item = e.memo.item;
		item.select();
		this.makeElementsSortable();
		PageStudio.setDirty(true);
	},
	
	onSelectedCellRemoved: function(e) {
		this.setSelectedCell(null);
	},
	
	onSelectedItemRemoved: function(e) {
		this.setSelectedItem(null);
	},
	
	onCellSelected: function(e) {
		this.setSelectedCell(e.memo.object);
	},
	
	onCellItemSelected: function(e) {
		this.setSelectedItem(e.memo.object);
	},
	
	onComponentSelected: function(e) {
		if(this.isSelected) {
			this.addComponent(e.memo.component);
		}
	},
	
	onMarkupComponentSelected: function(e) {
		if(this.isSelected) {
			this.addMarkupComponent(e.memo.markup, e.memo.element);
		}
	},
	
	onDeleteCellClicked: function(e) {
		var cell = e.memo.cell;
		if (cell == this.selectedCell) {
			this.setSelectedCell(null);
		}
		cell.remove(true);
		PageStudio.setDirty(true);
	},
	
	onDeleteCellItemClicked: function(e) {
		var item = e.memo.item;
		if (item == this.selectedItem) {
			this.setSelectedItem(null);
		}
		item.remove(true);
		PageStudio.setDirty(true);
	},
	
	onComponentDropped: function(e) {
		new PSAddComponent({ "body": e.memo.element.droppable, "cell": null, "component": e.memo.object.draggable, "event": e, "undoable": true });
	},
	
	onSorted: function(ele) {
		PageStudio.setDirty(true);
	},
	
	onClick: function($super, e) {
		$super(e);
		if(e.element() == this.element && this.selectedCell) {
			this.selectedCell.deselect();
			this.setSelectedCell(null);
		}
	}
});

PSArea.EVENT = {
	PAGE_AREA_LOADED: "pagestudio:pageAreaLoaded",
	PAGE_AREA_SELECTED: "pagestudio:pageAreaSelected"
};

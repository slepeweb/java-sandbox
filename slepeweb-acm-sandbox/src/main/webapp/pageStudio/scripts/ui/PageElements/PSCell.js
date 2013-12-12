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

var PSCell = Class.create(PSPageElement, {
	initialize: function($super, params) {
		$super(params);
		if (!params.element && params.event) {
			this.calculatePosition(params.event);
			this.calculateWidth(params.event);
		}
	},
	
	afterInitialize: function($super) {
		this.classes = PageStudio.stripPageStudioClasses(this.element.className);
		$super();
	},
	
	createElement: function($super, parent) {
		this.elementName = "cell";
		$super(parent);
		this.element.addClassName(PageStudio.CLASS.CELL);
		this.createWrapper();
		this.createToolbar();
	},
	
	createToolbar: function() {
		this.toolbar = new PSCellTab({ "target": this.element, "transition": "appear", "cell": this });
		this.toolbar.show();
	},
	
	createListeners: function($super) {
		$super({
			onComponentDropped: this.onComponentDropped.bindAsEventListener(this),
			onSortablityStarted: this.onSortablityStarted.bindAsEventListener(this),
			onCellItemAdded: this.onCellItemAdded.bindAsEventListener(this),
			onResizeStart: this.onResizeStart.bindAsEventListener(this),
			onResizeEnd: this.onResizeEnd.bindAsEventListener(this),
			onPreviewCellStylesChanged: this.onPreviewCellStylesChanged.bindAsEventListener(this)
		});
	},
	
	attachEvents: function($super) {
		if(PageStudio.loaded) {
			$super();
			document.observe(PageStudio.EVENT.SORTABILITY_STARTED, this.listeners.onSortablityStarted);
			document.observe("pagestudio:previewCellStylesChanged", this.listeners.onPreviewCellStylesChanged);
			this.element.observe(PageStudio.EVENT.CELL_ITEM_ADDED, this.listeners.onCellItemAdded);
			this.element.observe(PageStudio.EVENT.RESIZE_START, this.listeners.onResizeStart);
			this.element.observe(PageStudio.EVENT.RESIZE_END, this.listeners.onResizeEnd);
			new PSResizableHelper(this.element, { handle: "right", minWidth: "0", minHeight: "0" });
		}
		else {
			document.observe(PageStudio.EVENT.LOADED, this.attachEvents.bind(this));
		}
	},
	
	calculatePosition: function(event) {
		//get the mouse event out of the drop event
		var e = null;
		if (event) {
			e = event.memo.object.event;
		}
		
		var cells = this.element.up().select("." + PageStudio.CLASS.CELL);
		var previous = null;
		var scrollOffset = document.viewport.getScrollOffsets();
		cells.each(function(cell) {
			var offset = cell.viewportOffset();
			if (offset.top + scrollOffset.top <= e.pointerY() && offset.left + scrollOffset.left <= e.pointerX()) {
				previous = cell;
			}
		});
		
		if (previous) previous.insert({ "after": this.element });
	},
	
	//calculate a helpful width for the new cell
	calculateWidth: function(event) {
		//get the mouse event out of the drop event
		var e = null;
		if (event) {
			e = event.memo.object.event;
		}
		
		var scrollOffset = document.viewport.getScrollOffsets();
		
		//loop through previous cells looking for any that extend beyond the point where the cell was dropped
		var previous = this.element.previous("."+PageStudio.CLASS.CELL);
		while(previous != null) {
			var previousViewOffset = previous.viewportOffset();
			var previousDimensions = previous.getDimensions();
			//if new cell was dropped below this cell...
			if (!(e && e.pointerY() - scrollOffset.top > previousViewOffset.top + previousDimensions.height)) {
				var previousOffset = previous.positionedOffset();
				var bodyWidth = this.element.up().getWidth();
				//...calculate the width of the gap as a percentage of the page area.
				var width = 99 - (((previousOffset.left + previousDimensions.width) / bodyWidth) * 100);
				if (width >= 1) {
					this.element.setStyle({"width": width + "%" });
					this.element.fire(PageStudio.EVENT.RESIZE);
				}
				break;
			}
			previous = previous.previous("."+PageStudio.CLASS.CELL);
		}
	},
	
	/*	unfortunately, to have a sortable within a sortable without encountering problems
		we need to add a container div for the sortable fields.
	*/
	createWrapper: function() {
		this.wrapper = new Element("div", { id: this.element.id+"_wrapper", className: PageStudio.CLASS.WRAPPER });
		this.element.childElements().each(function(child) {
			this.wrapper.insert(child);
		}, this);
		this.element.insert(this.wrapper);
	},	
	
	makeSortable: function() {
		Sortable.create(this.wrapper, { "tag": "div", "only": PageStudio.CLASS.CELL_ITEM, "handle": PageStudio.CLASS.FIELD_HANDLE, "containment": $$("."+PageStudio.CLASS.WRAPPER), "dropOnEmpty": true, "zindex": 99999, "scroll": window, "overlap": "vertical", "constraint": null, "greedy": true, "onUpdate": this.onSorted });
		PSDroppableReceiver.add(this.wrapper, null, { "greedy": true, "accept": PageStudio.CLASS.DRAG_WRAPPER, "hoverclass": "ps_cell_on_mouse_over", "onReceive": this.listeners.onComponentDropped });
	},
	
	addComponent: function(body, component) {
		new PSAddComponent({ "body": body, "cell": this, "component": component, "undoable": true });
	},
	
	addClassName: function(className) {
		this.classes.push(className);
		if(PageStudio.previewCellStyles) {
			this.element.addClassName(className);
		}
	},
	
	removeClassName: function(className) {
		for(var i = 0; i < this.classes.length; i++)
		{
			if(this.classes[i] == className)
			{
				this.classes.splice(i, 1);
				break;
			}
		}
		if(PageStudio.previewCellStyles) {
			this.element.removeClassName(className);
		}
	},
	
	hasClassName: function(className) {
		for(var i = 0; i < this.classes.length; i++)
		{
			if(this.classes[i] == className)
			{
				return true;
			}
		}
		return false;
	},
	
	select: function($super) {
		$super();
		this.toolbar.show();
	},
	
	deselect: function($super) { 
		$super();
		this.toolbar.hide();
	},
	
	remove: function($super, undoable) {
		var cellItems = this.element.select("." + PageStudio.CLASS.CELL_ITEM).pluck("PSObj");
		cellItems.invoke("remove", undoable);
		$super(undoable);
		document.stopObserving(PageStudio.EVENT.SORTABILITY_STARTED, this.listeners.onSortablityStarted);
	},
	
	onSortablityStarted: function(e) {
		this.makeSortable();
	},
	
	onComponentDropped: function(e) {
		this.addComponent(e.memo.element.droppable, e.memo.object.draggable);
		e.stop();
	},
	
	onCellItemAdded: function(e) {
		this.makeSortable();
	},
	
	onSorted: function(ele) {
		PageStudio.setDirty(true);
	},
	
	onResizeStart: function(e) {
		PageStudio.setDirty(true);
	},
	
	onResizeEnd: function(e) {
		PageStudio.setDirty(true);
	},
	
	onPreviewCellStylesChanged: function(e) {
		var action;
		if(e.memo.preview) action = this.element.addClassName.bind(this.element); 
		else action = this.element.removeClassName.bind(this.element);
		for(var i = 0; i < this.classes.length; i++)
		{
			action(this.classes[i]);
		}
	}
});

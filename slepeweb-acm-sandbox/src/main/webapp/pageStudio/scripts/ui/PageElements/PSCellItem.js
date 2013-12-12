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

var PSCellItem = Class.create(PSPageElement, {
	initialize: function($super, params) {
		$super(params);
	},
	
	initParams: function($super, params) {
		$super(params);	
		this.component = params.component;
		if(typeof(this.component) == "undefined") {
			this.getComponent();
		}
	},
	
	getComponent: function() {
		//overridden by extending classes.
		return true;
	},
	
	createElement: function($super, parent) {
		$super(parent);
		this.element.addClassName(PageStudio.CLASS.CELL_ITEM);
		this.createToolbar();
		this.element.up().fire(PageStudio.EVENT.CELL_ITEM_ADDED, { "item": this });
	},
	
	createToolbar: function() {
		this.toolbar = new PSItemTab({ "target": this.element, "transition": "appear", "item": this });
		this.toolbar.disable();
	},
	
	select: function($super) {
		$super();
		this.element.addClassName(PageStudio.CLASS.CELL_ITEM + "_selected");
		document.observe("pagestudio:cellItemSelected", this.listeners.onOtherSelected);
		this.element.fire("pagestudio:cellItemSelected", { "object": this });
	},
	
	deselect: function($super) {
		$super();
		document.stopObserving("pagestudio:cellItemSelected", this.listeners.onOtherSelected);
		this.element.removeClassName(PageStudio.CLASS.CELL_ITEM + "_selected");
	},
	
	remove: function($super, undoable) {
		$super(undoable);
		document.stopObserving("pagestudio:cellItemSelected", this.listeners.onOtherSelected);
	},
	
	undoRemove: function($super, parentNode, previous, next) {
		$super(parentNode, previous, next);
		this.element.up().fire(PageStudio.EVENT.CELL_ITEM_ADDED, { "item": this });
	},
	
	onMouseEnter: function($super, e) {
		this.toolbar.show();
		$super(e);
		this.element.addClassName(PageStudio.CLASS.CELL_ITEM + "_on_mouse_over");
	},
	
	onMouseLeave: function($super, e) {
		this.toolbar.hide();
		$super(e);
		this.element.removeClassName(PageStudio.CLASS.CELL_ITEM + "_on_mouse_over");
	}
});
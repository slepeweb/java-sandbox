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

var PSDialog = Class.create(PSElement, {
	initialize: function($super, params) {	
		$super(params);
	},
	
	initParams: function($super, params) {
		var defaults = { parent: PageStudio.uiContainer, label: "", width: "auto", tool: null, removeOnClose: false, modal: false  };
		
		if (!params) {
			params = defaults;
		}
		
		$super(params);
		
		this.parent = typeof(params.parent) != "undefined" ? params.parent : defaults.parent;
		this.label = typeof(params.label) != "undefined" ? params.label : defaults.label;
		this.width = typeof(params.width) != "undefined" ? params.width : defaults.width;
		this.tool = typeof(params.tool) != "undefined" ? params.tool : defaults.tool;
		this.removeOnClose = typeof(params.removeOnClose) != "undefined" ? params.removeOnClose : defaults.removeOnClose;
		this.modal = typeof(params.modal) != "undefined" ? params.modal : defaults.modal;
	},
	
	createElement: function($super) {
		this.elementName = "dialog";
		$super(this.parent);
		this.element.addClassName(PSDialog.CLASS.DIALOG);
		this.element.addClassName(PageStudio.CLASS.RESET);
		
		this.element.setStyle({ "width": this.width });
		
		this.createHandle();
		
		this.body = new Element("div", { "className": PSDialog.CLASS.BODY });
		this.element.insert(this.body);
		
		
		this.hide(); // hide the dialog by default
		PSUnselectableHelper.makeUnselectable(this.element);
	},
	
	createHandle: function() {
		this.handle = new Element("div", { "className": PSDialog.CLASS.HANDLE });
		
		this.handle.insert(new Element("div", { "className": "ps_favico"}));
		this.handle.insert(this.label);
		
		//create divs for border styling.
		var leftEdge = new Element("div", { "className": PSDialog.CLASS.CORNER + " " + PSDialog.CLASS.CORNER_LEFT });
		var rightEdge = new Element("div", { "className": PSDialog.CLASS.CORNER + " " + PSDialog.CLASS.CORNER_RIGHT });
		this.handle.insert({ "top": leftEdge, "bottom": rightEdge });
		
		this.createCloseButton();
		
		this.element.insert(this.handle);
	},
	
	createCloseButton: function() {
		this.closeButton = new PSButton({ "className": PSDialog.CLASS.CLOSE_BUTTON, "action": PSDialog.EVENT.CLOSE_CLICKED  });
		this.handle.insert(this.closeButton.element);
	},
	
	createFooter: function() {
		this.footer = new Element("div", { "className": PSDialog.CLASS.FOOTER } );
	},
	
	createListeners: function($super) {
		$super();
		this.listeners.onCloseClicked = this.onCloseClicked.bindAsEventListener(this);
		this.listeners.onToolClosed = this.onToolClosed.bindAsEventListener(this);
	},
	
	attachEvents: function($super) {
		$super();
		this.element.observe(PSDialog.EVENT.CLOSE_CLICKED, this.listeners.onCloseClicked);
		this.element.observe(PageStudio.EVENT.TOOL_CLOSED, this.listeners.onToolClosed);
		this.handle.observe("mousedown", this.listeners.onMouseDown);
		this.makeDraggable();
		Droppables.add(this.element);
	},
	
	makeDraggable: function() {
		this.draggable = new Draggable(this.element, { "handle": this.handle, "zindex": "10000", "starteffect": null, "endeffect": null, "onEnd": this.onDragEnd.bindAsEventListener(this), "quiet": true });
	},
	
	select: function($super) {
		$super();
		this.element.addClassName(PSDialog.CLASS.SELECTED);
	},
	
	deselect: function($super) {
		$super();
		this.element.removeClassName(PSDialog.CLASS.SELECTED);
	},
	
	focus: function($super) {
		$super();
		this.element.setStyle({ "zIndex": PSDialog.topIndex++ });
		this.element.addClassName(PSDialog.CLASS.FOCUSED);
	},
	
	blur: function($super) {
		$super();
		this.element.removeClassName(PSDialog.CLASS.FOCUSED);
	},
	
	show: function(center) {
		this.element.show();
		this.select();
		this.focus();
		this.element.fire(PSDialog.EVENT.OPENED, { "dialog": this });
		if (center) {
			this.center();
		}
		else {
			this.position();
		}
	},
	
	//can be overridden by subclasses to provide special positioning rules.
	position: function() {
		this.center();
	},
	
	center: function() {
		var viewOffset = this.element.viewportOffset();
		var viewDims = document.viewport.getDimensions();
		var elementPos = this.element.positionedOffset();
		var elementDims = this.element.getDimensions();
		var newTop = (elementPos.top - viewOffset.top) + ((viewDims.height - elementDims.height) / 2);
		var newLeft = (elementPos.left - viewOffset.left) + ((viewDims.width - elementDims.width) / 2);
		this.element.setStyle({ "top": newTop + "px", "left": newLeft + "px" });
		this.ensureVisibility();
	},
	
	close: function() {
		this.hide();
		if (this.removeOnClose && this.element.parentNode) {
			this.element.remove();
		}
	},
	
	hide: function() {
		this.element.hide();
		this.element.fire(PSDialog.EVENT.CLOSED, { "dialog": this });
	},
	
	/* 
	 * Ensures that some part of the dialog handle is visible.
	 * This is currently disabled in IE.js.
	 */
	ensureVisibility: function() {
		var viewport = document.viewport.getDimensions();
		var position = this.element.viewportOffset();
		
		if (position.top < this.handle.getHeight()) {
			var newtop = this.element.positionedOffset().top - position.top;
			this.element.setStyle({ "top": newtop +"px" });
		}
		else if (position.top + this.handle.getHeight() > viewport.height) {
			var newtop = this.element.positionedOffset().top - (position.top - viewport.height) - this.handle.getHeight();
			this.element.setStyle({ "top": newtop +"px" });
		}	
		
		if (position.left + this.element.getWidth() <= 40) {
			this.element.setStyle({ "left": ""+(-(this.element.getWidth()-40))+"px" });
		}
		else if (position.left + 40 > viewport.width) {
			this.element.setStyle({ "left": viewport.width - 40+"px" });	
		}
		this.element.fire(PSDialog.EVENT.REPOSITIONED, { "dialog": this });
	},
	
	onCloseClicked: function(e) {
		this.close();
	},
	
	onToolClosed: function(e) {
		this.close();
	},
	
	onDragEnd: function(e) {
		this.ensureVisibility();
	}
});

PSDialog.topIndex = 999;

PSDialog.CLASS = {
	DIALOG: "ps_dialog",
	SELECTED: "ps_dialog_selected",
	FOCUSED: "ps_dialog_focused",
	BODY: "ps_dialog_body",
	HANDLE: "ps_dialog_handle",
	FOOTER: "ps_dialog_footer",
	CLOSE_BUTTON: "ps_dialog_close",
	CORNER: "ps_dialog_corner",
	CORNER_LEFT: "ps_dialog_corner_left",
	CORNER_RIGHT: "ps_dialog_corner_right",
	TABLE: "ps_dialog_table",
	TABLE_FIELDSET: "ps_table_fieldset"
};

PSDialog.EVENT = {
	OPENED: "pagestudio:dialogOpened",
	CLOSED: "pagestudio:dialogClosed",
	SELECTED: "pagestudio:dialogSelected",
	CLOSE_CLICKED: "pagestudio:closeClicked",
	SHOW_DIALOG_CLICKED: "pagestudio:showDialogClicked",
	REPOSITIONED: "pagestudio:dialogRepositioned"
};

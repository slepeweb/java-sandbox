
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

var PSElement = Class.create({
	initialize: function(params) {
		this.createListeners();
		this.isSelected = false;
		this.hasFocus = false;
		this.elementName = this.elementName ? this.elementName : "element";
		this.elementType = "div";
		this.initParams(params);
		this.createElement(params.parent);
		if(this.element) { //element may not have been created successfully
			this.attachEvents();
		}
		this.afterInitialize();
	},
	
	afterInitialize: function() {
		//extended in subclasses.
	},
	
	initParams: function(params) {
		this.element = params.element;
	},
	
	createListeners: function(extras) {
		this.listeners = {
			onClick: this.onClick.bindAsEventListener(this),
			onDblClick: this.onDblClick.bindAsEventListener(this),
			onMouseEnter: this.onMouseEnter.bindAsEventListener(this),
			onMouseLeave: this.onMouseLeave.bindAsEventListener(this),
			onMouseOver: this.onMouseOver.bindAsEventListener(this),
			onMouseOut: this.onMouseOut.bindAsEventListener(this),
			onMouseDown: this.onMouseDown.bindAsEventListener(this),
			onMouseUp: this.onMouseUp.bindAsEventListener(this),
			onOtherSelected: this.onOtherSelected.bindAsEventListener(this),
			onOtherFocused: this.onOtherFocused.bindAsEventListener(this),
			onFocus: this.onFocus.bindAsEventListener(this),
			onBlur: this.onBlur.bindAsEventListener(this),
			onContextMenu: this.onContextMenu.bindAsEventListener(this)
		};
		if(extras) Object.extend(this.listeners, extras);
	},
	
	createElement: function(parent) {
		if(!this.element) {
			this.element = new Element(this.elementType, { id: PSIDHelper.getId(this.elementName) });
			if(parent) {
				parent.insert(this.element);
			}
		}
		else if(this.element){
			if (!this.element.id) {
				this.element.writeAttribute("id", PSIDHelper.getId(this.elementName));
			}
		}
		PageStudio.preventContributorFade(this.element);
		this.element.PSObj = this;
	},
	
	attachEvents: function() {
		this.element.observe("click", this.listeners.onClick);
		this.element.observe("dblclick", this.listeners.onDblClick);
		
		this.element.observe("mouseenter", this.listeners.onMouseEnter);
		this.element.observe("mouseleave", this.listeners.onMouseLeave);
		
		this.element.observe("mouseover", this.listeners.onMouseOver);
		this.element.observe("mouseout", this.listeners.onMouseOut);
		this.element.observe("mousedown", this.listeners.onMouseDown);
		this.element.observe("mouseup", this.listeners.onMouseUp);
		this.element.observe("contextmenu", this.listeners.onContextMenu);
		this.element.observe(PageStudio.EVENT.FOCUS, this.listeners.onFocus);
		this.element.observe(PageStudio.EVENT.BLUR, this.listeners.onBlur);
	},
	
	detachEvents: function() {
    this.element.stopObserving("click", this.listeners.onClick);
    this.element.stopObserving("dblclick", this.listeners.onDblClick);
    
    this.element.stopObserving("mouseenter", this.listeners.onMouseEnter);
    this.element.stopObserving("mouseleave", this.listeners.onMouseLeave);
    
    this.element.stopObserving("mouseover", this.listeners.onMouseOver);
    this.element.stopObserving("mouseout", this.listeners.onMouseOut);
    this.element.stopObserving("mousedown", this.listeners.onMouseDown);
    this.element.stopObserving("mouseup", this.listeners.onMouseUp);
    this.element.stopObserving("contextmenu", this.listeners.onContextMenu);
    this.element.stopObserving(PageStudio.EVENT.FOCUS, this.listeners.onFocus);
    this.element.stopObserving(PageStudio.EVENT.BLUR, this.listeners.onBlur);

		document.stopObserving("pagestudio:"+this.elementName+"Selected", this.listeners.onOtherSelected);
	},
	
	select: function() {
		this.element.addClassName("ps_"+this.elementName+"_selected");
		this.isSelected = true;
		this.element.fire("pagestudio:"+this.elementName+"Selected", { "object": this });
		document.observe("pagestudio:"+this.elementName+"Selected", this.listeners.onOtherSelected);
	},
	
	deselect: function() {
		this.element.removeClassName("ps_"+this.elementName+"_selected");
		document.stopObserving("pagestudio:"+this.elementName+"Selected", this.listeners.onOtherSelected);
		this.isSelected = false;
	},
	
	remove: function() {
		this.element.fire(PageStudio.EVENT.REMOVED);
		this.deselect();
		this.detachEvents();
		this.element.remove();
	},
	
	refresh: function() {
		this.parent = this.element.up();
	},
	
	focus: function() {
		if(!this.hasFocus) {
			this.hasFocus = true;
			document.observe("click", this.listeners.onOtherFocused);
			this.element.fire(PageStudio.EVENT.FOCUS, { "object": this });
			document.observe(PageStudio.EVENT.FOCUS, this.listeners.onOtherFocused);
		}
	},
	
	blur: function() {
		this.hasFocus = false;
		this.element.fire(PageStudio.EVENT.BLUR, { "object": this });
		document.stopObserving(PageStudio.EVENT.FOCUS, this.listeners.onOtherFocused);
	},
	
	show: function() {
		this.element.show();
	},
	
	hide: function() {
		this.element.hide();
	},
	
	onClick: function(e) {		
		if (!this.isSelected) {
			this.onSelect(e);
		}
	},
	
	onOtherFocused: function(e) {
		if(this.hasFocus && !e.element().ancestors().member(this.element)) {
			this.blur();
		}
	},
	
	onDblClick: function(e) {
		
	},
	
	onMouseOver: function(e) {
		
	},
	
	onMouseOut: function(e) {
		
	},
	
	onMouseDown: function(e) {
		this.focus();
	},
	
	onMouseUp: function(e) {
		
	},
	
	onMouseEnter: function(e) {
		this.element.addClassName("ps_"+this.elementName+"_on_mouse_over");
	},
	
	onMouseLeave: function(e) {
		this.element.removeClassName("ps_"+this.elementName+"_on_mouse_over");
	},
	
	onSelect: function(e) {
		this.select();
	},
	
	onDeselect: function(e) {
		this.deselect();
	},
	
	onOtherSelected: function(e) {
		if (e.memo.object != this) {
			this.onDeselect(e);
		}
	},
	
	onFocus: function(e) {
		if(e.memo.object != this) {
			this.focus();	
		}
	},
	
	onBlur: function(e) {
	},
	
	onContextMenu: function(e) {
	}
});
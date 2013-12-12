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

var PSComponent = Class.create(PSCellItem, {
	initialize: function($super, params)
	{
		$super(params);
		this.doConfiguration(params);
	},
		
	initParams: function($super, params) {
		if (params.element) {
			//if the element is already part of the page, remove the content while it is set up.
			this.content = params.element.innerHTML;
			params.element.update();
		}
		this.status = "unknown";
		$super(params);
		this.url = params.url;
	},
	
	attachEvents: function($super) {
		$super();
		this.element.observe("pagestudio:displayConfigurationClicked", this.onDisplayConfigurationClicked.bindAsEventListener(this));
	},
	
	getComponent: function() {
		if (this.element) {
			var cid = this.element.readAttribute("cid");
			if (cid) {
				this.component = PageStudio.components.get(cid);
			}
		}
		if(!this.component) {
			this.component = {
					"componentType": "unavailable",
					"id": this.element.readAttribute("cid") 
			};
		}
	},
	
	updateStatus: function() {
		if (this.element) {
			if (this.component.componentType == "configurable") {
				this.status = "configured";
			}
			else {
				this.status = "static";
			}
		}
		else {
			if (this.component.componentType == "configurable") {
				this.status = "unconfigured";
			}
			else {
				this.status = "new";
			}
		}
	},
	
	createElement: function($super, parent) {
		this.elementName = "component";
		this.updateStatus();
		$super(parent);
		if (this.component){
			if (this.component.type == "Component") {
				this.element.writeAttribute({
					"cid": this.component.id,
					"componentType": this.component.componentType
				});
			}
		}
		
		this.element.addClassName(PageStudio.CLASS.COMPONENT);
		//add a wrapper to contain the component, this keeps it seperate from the toolbars so it can be updated easily. 
		this.wrapper = new Element("div", { "className": "ps_component_wrapper", "contentEditable": false });
		this.element.insert(this.wrapper);
		//need to insert after wrapper is inserted and use innerHTML to prevent script evaluation. 
		if (this.content) this.wrapper.innerHTML = this.content;
	},
	
	doConfiguration: function(params)
	{
		if (this.component.componentType == "configurable") {
			if(this.status == "unconfigured") {
				if(params.item) {
					this.copyConfiguration(params.item);
				}
				else {
					this.displayConfiguration(true);
				}
			}
			else if(this.status == "new") {
				this.display();
			}
		}
		else {
			this.display();
		}
	},
	
	copyConfiguration: function(item)
	{
		var params = PageStudio.getRequestItemParams({
			"action": PageStudio.ACTION.COPY_COMPONENT,
			"cid": this.component.id,
			"cInstId": this.element.id,
			"copyTarget": item.element.id
		});
		
		//OnSuccess: Display the component.
		//OnFailure: Display the configuration form as if it were
		//a new component.
		new PageStudio.Request(PageStudio.URL.COMPONENT_FACTORY, { 
			method: "post",
			parameters: params,
			onSuccess: this.display.bind(this),
			onFailure: this.displayConfiguration.bind(this, true)
		});	
	},
	
	displayConfiguration: function(first) {
		if (this.component.componentType == "configurable") {
			this.dialog = new PSConfigureComponentDialog({ "component": this, "parent": PageStudio.uiContainer, "label": "Configure Component", "removeOnClose": true });
			if (first) {
				//if it's the first time the component is being configured, cancelling should remove the component.
				this.listeners.onInitialConfigCancelled = this.onInitialConfigCancelled.bindAsEventListener(this);
				this.dialog.element.observe("pagestudio:closeClicked", this.listeners.onInitialConfigCancelled);
			}
			this.dialog.show();
		}
	},
	
	createToolbar: function($super) {
		if (this.component.componentType == "configurable") {
			this.toolbar = new PSConfigurableComponentTab({ "target": this.element, "transition": "appear", "item": this });
		}
		else {
			$super();
		}
	},
	
	display: function() {
		if (this.dialog) {
			this.dialog.element.stopObserving("pagestudio:closeClicked", this.listeners.onInitialConfigCancelled);
			this.dialog = null;
		}
		var params = PageStudio.getRequestItemParams({
			"action": PageStudio.ACTION.GET_COMPONENT,
			"cid": this.component.id,
			"cInstId": this.element.id			
		});
		this.wrapper.addClassName(PageStudio.CLASS.LOADING);
		new PageStudio.Request(PageStudio.URL.COMPONENT_FACTORY, { 
			method: "get",
			parameters: params,
			evalScripts: true,
			onSuccess: this.insertResponse.bind(this),
			onFailure: this.onComponentLoadError.bind(this),
			onComplete: function() {
				this.wrapper.removeClassName(PageStudio.CLASS.LOADING);
			}.bind(this)
		});	
	},
	
	insertResponse: function(t) {
		this.wrapper.update(t.responseText);
	},
	
	onDisplayConfigurationClicked: function(e){
		this.displayConfiguration();
	},
	
	onInitialConfigCancelled: function(e) {
		if (e.memo.status != "configured") {
			this.dialog.element.stopObserving("pagestudio:closeClicked", this.listeners.onInitialConfigCancelled);
			this.element.remove();
		}
	},
	
	onComponentLoadError: function(t) {
		if(this.status == "new") {
			PageStudio.displayExceptionAlert(PageStudio.locale.get("ps_component_exception"), t);
		}
		var message = new Element("p", { "class": "ps_error" }).update(PageStudio.locale.get("component_error"));
		var link = new Element("a", { "class": "ps_error" }).update(PageStudio.locale.get("component_error_click_here"));
		link.observe("click", function(e){
			e.stop();
			PageStudio.displayExceptionAlert(PageStudio.locale.get("ps_component_exception"), t);
		});
		this.wrapper.update(message).insert(link);
	}
});

var PSMarkupComponent = new Class.create({
	initialize: function(mc) {
		if(!mc.retrieve("markupComponent")) {
			mc.store("markupComponent", this);
			this.element = mc;
			this.component = this.getComponent();
			if(this.component) {
				this.retrieveMarkup();
				this.attachEvents();
				this.element.removeAttribute("id");
			}
		}
	},
	
	attachEvents: function() {
		this.listeners = {
			"onClick": this.onClick.bindAsEventListener(this)
		};
		this.element.observe("click", this.listeners.onClick);
	},
	
	//If the mark-up contains links, we should disable them while in PS to prevent them accidentally being clicked.
	disableLinks: function() {
		this.bodyElement.select("a").each(function(link) { 
			link.observe("click", function(e) { 
				e.stop();
				//forward the click event to the mark-up component's click handler.
				this.onClick(e);
			}.bindAsEventListener(this));
		}, this);
	},
	
	getComponent: function() {
		//Find the component details.
		var details = this.element.select(".ps_markup_component_details")[0];
		if (details) {
			return details.innerHTML.evalJSON();
		}
		return null;
	},
	
	retrieveMarkup: function() {
		PSToolTipHelper.add(this.element, this.component.name);
		var params = PageStudio.getRequestItemParams(Object.extend({
			"action": PageStudio.ACTION.GET_COMPONENT,
			"configuration": PageStudio.ACTION.SAVE_COMPONENT_CONFIGURATION,
			"cid": this.component.cid,
			"cInstId": this.component.id,
			"markup": true
		}, this.component.configuration.unescapeHTML().toQueryParams()));
		
		//Find or create the body span
		var bodies = this.element.select(".ps_markup_component_body");
		this.bodyElement = bodies.length > 0 ? bodies[0] : new Element("span", { "className": "ps_markup_component_body" });
		this.element.insert({ "top": this.bodyElement });					
		PSUnselectableHelper.makeUnselectable(this.bodyElement);
		
		//Load the mark-up
		this.bodyElement.fire(PSComponent.EVENT.MARKUP_COMPONENT_LOADING);
		this.bodyElement.update(PageStudio.locale.get("loading"));
		new PageStudio.Request(PageStudio.URL.COMPONENT_FACTORY, { 
			method: "get",
			parameters: params,
			evalScripts: true,
			onSuccess: function(t) {
				this.bodyElement.update(t.responseText);
				this.disableLinks();
				this.bodyElement.fire(PSComponent.EVENT.MARKUP_COMPONENT_LOADED);
			}.bindAsEventListener(this),
			onFailure: function(t) {
				this.bodyElement.fire(PSComponent.EVENT.MARKUP_COMPONENT_LOAD_ERROR);
				PageStudio.displayExceptionAlert(PageStudio.locale.get("ps_load_markup_exception"), t);
			}.bindAsEventListener(this)
		});
	},
	
	onClick: function(e) {
		this.select();
	},
	
	onOtherSelected: function(e) {
		if(e.memo.object != this) {
			this.deselect();
		}
	},
	
	onClickOut: function(e) {
		this.deselect();
	},
	
	select: function() {
		this.element.stopObserving("click", this.listeners.onClick);
		this.element.fire(PSComponent.EVENT.MARKUP_COMPONENT_SELECTED, { "object": this, "component": this.component, "element": this.element });
		PSSelectionHelper.selectNode(this.element);
		this.element.addClassName("ps_markup_component_selected");
		this.selectionListener = this.onOtherSelected.bindAsEventListener(this);
		this.clickOutListener = this.onClickOut.bindAsEventListener(this);
		document.observe(PSComponent.EVENT.MARKUP_COMPONENT_SELECTED, this.selectionListener);
		this.element.parentNode.observe(PSField.EVENT.MOUSE_DOWN, this.clickOutListener);
	},
	
	deselect: function() {
		
		this.element.observe("click", this.listeners.onClick);
		this.element.removeClassName("ps_markup_component_selected");
		document.stopObserving(PSComponent.EVENT.MARKUP_COMPONENT_SELECTED, this.selectionListener);
		if(this.element.parentNode) {
			this.element.fire(PSComponent.EVENT.MARKUP_COMPONENT_DESELECTED);
			this.element.parentNode.stopObserving(PSField.EVENT.MOUSE_DOWN, this.clickOutListener);
		}
		delete this.clickOutListener;
		delete this.selectionListener;
	}
});

PSComponent.EVENT = {
	DISPLAY_CONFIGURATION: "pagestudio:displayConfigurationClicked",
	MARKUP_COMPONENT_LOADING: "pagestudio:markupComponentLoading",
	MARKUP_COMPONENT_LOADED: "pagestudio:markupComponentLoaded",
	MARKUP_COMPONENT_LOAD_ERROR: "pagestudio:markupComponentLoadError",
	MARKUP_COMPONENT_SELECTED: "pagestudio:markupComponentSelected",
	MARKUP_COMPONENT_DESELECTED: "pagestudio:markupComponentDeselected"
};

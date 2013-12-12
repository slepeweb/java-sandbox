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

var PSBrowser = Class.create({
	initialize: function(category) {
		this.category = category;
		this.sortCategory();
		this.createElement();
		this.attachEvents();
	},
	
	attachEvents: function() {
		document.observe(PageStudio.EVENT.COMPONENT_JSON_LOADED, this.refresh.bindAsEventListener(this));
		document.observe(PageStudio.EVENT.COMPONENTS_UPDATED, this.refresh.bindAsEventListener(this));
	},
	
	createElement: function() {
		this.element = new Element("div", { className: "ps_browser" });
		this.list = new Element("div", {className: "ps_browser_list"});
		this.buttonGroup = new PSButtonGroup();
		this.componentFilter = this.createComponentFilter();
		this.element.insert(this.componentFilter);
		this.element.insert(this.list);
		
		this.populate();		
	},
	
	insertInto: function(element, params) {
		element.insert(this.element, params);
		this.element.fire(PageStudio.EVENT.BROWSER_LOADED);
	},
	
	updateCategory: function(category) {
		this.category = category;
		this.sortCategory();
		this.refresh();
	},
	
	sortCategory: function() {
		this.category = this.category.sortBy(function(item){ return item.name; });
	},
	
	refresh: function() {
		this.list.update();
		this.populate();
	},
	
	populate: function() {
		this.items = new Hash();
		for (var i = 0; i < this.category.length; i++) {
			var component = this.category[i];
			this.addItem(component);
		}
	},
	
	addItem: function(component) {
		//The create item method is defined in the implementing class.
		var item = this.createItem(component);
		this.items.set(component.id, item.element);
		this.list.insert(item.element);
	},
	
	createComponentFilter: function() {
		var element = new Element("div", { className: "ps_component_filter" });
		var form = new PSFilterInput({ "label": PageStudio.locale.get("Filter"), "filter": this.applyFilter.bind(this) }); 
		element.insert(form.element);
		this.filter = $A([""]);
		return element;
	},
	
	doFilter: function(component) {
		var match = true;
		for(var i = 0; i < this.filter.length; i++) {
			var word = this.filter[i];
			var wmatch = false;
			for(var val in component) {
				if (component[val] != null && component[val].toLowerCase && component[val].toLowerCase().indexOf(word) != -1 || word == "") {
					wmatch = true;
					break;
				}
			}
			if(wmatch == false) { match = false; break; }
		}
		if (match) {
			var item = this.items.get(component.id);
			item.show();
			item.fire(PSBrowser.EVENT.ITEM_FILTERED, { "component": component });
		}
	},
	
	applyFilter: function(text) {
		this.filter = text.toLowerCase().split(" ");
		this.items.values().invoke("hide");
		this.category.each(this.doFilter.bind(this));
	},
	
	remove: function() {
		document.stopObserving("pagestudio:componentJSONLoaded", this.refresh.bindAsEventListener(this));
		this.element.stopObserving();
		this.element.descendants().invoke("stopObserving");
		this.element.remove();
	},
	
	hide: function() {
		this.element.hide();
	},
	
	show: function() {
		this.element.show();
	}
});

PSBrowser.EVENT = {
		ITEM_FILTERED: "pagestudio:itemFiltered"
};

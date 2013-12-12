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

var PSComponentBrowser = Class.create(PSBrowser, {
	initialize: function($super, category, displayHeaders) {
		this.displayHeaders = displayHeaders;
		$super(category);
		this.element.addClassName("ps_component_browser");
	},
	
	sortCategory: function($super) {
		//If the components are being grouped under headers by type, we need to
		//include the type in the comparison.
		if(this.displayHeaders) {
			var delimeter = "@@--@@";//Separate the type from the name.
			this.category = this.category.sortBy(function(item) {
				return item.type + delimeter + item.name;
			});
		}
		else {
			$super();
		}
	},
	
	attachEvents: function($super) {
		$super();
		this.element.observe(PSComponentBrowser.EVENT.HEADER_CLICKED, this.onHeaderClicked.bindAsEventListener(this));
	},
	
	createItem: function(component) {
		var item = null;
		if (component.type == "Image") {
			item = new PSImageBrowserItem(component);
		}
		else {
			item = new PSComponentBrowserItem(component);
		}
		return item;
	},
	
	addItem: function($super, component) {
		if(this.displayHeaders && component.type != this.currentType) {
			this.currentType = component.type;
			var header = new PSHeaderButton({ "className": PSComponentBrowser.CLASS.CATEGORY_HEADER, "label": { "text": this.currentType, "length": 100 }, "action": PSComponentBrowser.EVENT.HEADER_CLICKED, "memo": { "type": this.currentType } });
			this.list.insert(header.element);
		}
		$super(component);
	},
	
	hideType: function(type) {
		this.category.each(function(component) {
			if(component.type == type) this.items.get(component.id).hide();
		}, this);
	},
	
	showType: function(type) {
		this.category.each(function(component) {
			if(component.type == type) this.doFilter(component);
		}, this);
	},
	
	onHeaderClicked: function(e) {
		if (e.memo.toggled) {
			this.showType(e.memo.type);
		}
		else {
			this.hideType(e.memo.type);
		}
	}
});

PSComponentBrowser.CLASS = {
		CATEGORY_HEADER: "ps_category_header"
};

PSComponentBrowser.EVENT = {
		HEADER_CLICKED: "pagestudio:componentHeaderClicked"
};

var PSHeaderButton = Class.create(PSButton, {
	
	CLASS: {
		TOGGLED: "ps_header_toggled"
	},
	
	initialize: function($super, params) {
		$super(params);
		this.memo.toggled = true;
		this.element.addClassName(this.CLASS.TOGGLED);
	},
	
	attachEvents: function($super) {
		$super();
		document.observe(PSBrowser.EVENT.ITEM_FILTERED, this.onComponentShown.bindAsEventListener(this));
	},

	fire: function($super) {
		this.toggle();
		$super();
	},
	
	toggle: function() {
		this.memo.toggled = !this.memo.toggled;
		if (this.memo.toggled) {
			this.element.addClassName(this.CLASS.TOGGLED);
		}
		else {
			this.element.removeClassName(this.CLASS.TOGGLED);
		}
	},
	
	onComponentShown: function(e) {
		if(this.memo.toggled == false && e.memo.component.type == this.memo.type) {
			this.toggle();
		}
	}
});
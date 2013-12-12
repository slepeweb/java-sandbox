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

var PSSelectComponentTool = Class.create(PSTool, {
	initialize: function($super) {
		this.currentBrowser = null;
		$super();
		this.createListeners();
		this.attachEvents();
		this.browsers = new Hash();
	},
		
	createElement: function($super) {
		$super();
		this.element.addClassName("ps_tool_select_component");
		this.categoryList = new Element("div", { className: "ps_category_list" });
		this.componentList = new Element("div", { className: "ps_component_list" });		
		this.insert(this.categoryList);
		this.insert(this.componentList);
		this.insert(this.confirmation);
		this.categoryListScrollable = new PSScrollable.VerticalBars({ element:this.categoryList });
	},
	
	attachEvents: function() {
		document.observe(PageStudio.EVENT.COMPONENT_JSON_LOADING, this.onBeginLoadingCategoryList.bindAsEventListener(this));
		document.observe(PageStudio.EVENT.COMPONENT_JSON_LOADED, this.onComponentJSONLoaded.bindAsEventListener(this));
		this.element.observe(PSSelectComponentTool.EVENT.SELECT_CATEGORY_CLICKED, this.onCategorySelected.bindAsEventListener(this));
		document.observe(PageStudio.EVENT.COMPONENTS_UPDATED, this.onComponentsUpdated.bindAsEventListener(this));
	},
	
	createListeners: function() {
		this.listeners = {
			onKeyDown: this.onKeyDown.bindAsEventListener(this)
		};
	},
	
	attachFocusEvents: function() {
		document.observe("keydown", this.listeners.onKeyDown);
	},
	
	detachFocusEvents: function() {
		document.stopObserving("keydown", this.listeners.onKeyPress);
	},
	
	onKeyDown: function(e) {
		if(e.keyCode == Event.KEY_DOWN) {
			e.stop();
			this.selectNextCategory();
		}
		else if(e.keyCode == Event.KEY_UP) {
			e.stop();
			this.selectPreviousCategory();
		}
	},
	
	onBeginLoadingCategoryList: function(e) {
		this.clearCategoryList();
	},
	
	onCategorySelected: function(e) {
		this.selectCategoryByName(e.memo.categoryName);
	},
	
	onComponentsUpdated: function(e) {
		this.updateCategoryList(true);
		this.browsers.each(function(pair){
			pair.value.updateCategory(this.categories.get(pair.key));
		}, this);
	},
	
	onComponentJSONLoaded: function(e) {
		this.updateCategoryList(false);
		this.createCategoryListItems();
	},
	
	addCategory: function(category) {
		if(!this.categories.keys().member(category)) {
			this.categories.set(category, new Array());
		}
	},
	
	selectNextCategory: function() {
		this.selectCategoryByIndex(this.selectedCategoryIndex + 1);
	},
	
	selectPreviousCategory: function() {
		this.selectCategoryByIndex(this.selectedCategoryIndex - 1);
	},
	
	selectCategory: function(categoryName, index) {
		this.selectedCategory = categoryName;
		this.selectedCategoryIndex = index;
		this.populateComponentList(this.selectedCategory);	
	},
	
	selectCategoryByIndex: function(index) {
		if(index > -1 && index < this.categories.size()) {
			var button = this.categoryButtonGroup.getAt(index);
			button.select();
			this.categoryListScrollable.scrollToElement(button.element);
			var keys = this.categories.keys();
			if(keys.length > index && index > -1) {
				this.selectCategory(keys[index], index);
			}
		}
	},
	
	selectCategoryByName: function(name) {
		var keys = this.categories.keys();
		var index = keys.indexOf(name);
		if(index != -1) {
			this.selectCategory(name, index);
		}
	},
	
	addComponent: function(category, component) {
		this.addCategory(category);
		this.categories.get(category).push(component);
		this.categories.get(PageStudio.locale.get("All")).push(component);
	},
	
	addComponents: function(components) {
		for (var i = 0; i < components.values().length; i++) {
			var JSON = components.values()[i];
			var category = JSON.category ? JSON.category : JSON.type;		
			this.addComponent(PageStudio.locale.get(category), JSON);
		}
	},
	
	clearCategoryList: function() {
		this.categoryList.update();//clears the categoryList
		this.categoryList.addClassName(PageStudio.CLASS.LOADING);
	},
	
	updateCategoryList: function(update) {
		this.categoryList.removeClassName(PageStudio.CLASS.LOADING);		
		this.categories = new Hash();
		//Field and Image categories should appear even if they are empty.
		this.addCategory(PageStudio.locale.get("All"));
		this.addCategory(PageStudio.locale.get("Field"));
		this.addCategory(PageStudio.locale.get("Image"));

		var fieldNames = PageStudio.Data.getFieldNames();
		if(fieldNames) {
			for (var i = 0; i < fieldNames.length; i++) {
				this.addComponent("Field", PageStudio.Data.getField(fieldNames[i]));
			}
		}
		if(PageStudio.images) this.addComponents(PageStudio.images);
		if (PageStudio.components) this.addComponents(PageStudio.components);
	},
	
	//get an icon from one of the components in the category.
	getCategoryIcon: function(categoryName) {
		var comps = this.categories.get(categoryName);
		for (var i = 0; i < comps.length; i++) {
			var comp = comps[i];
			if (comp.icon && comp.icon != "null") {
				return comp.icon;
			}
		}
		return PSSelectComponentTool.ICON.OTHER;
	},
	
	createCategoryListItems: function() {
		this.categoryButtonGroup = new PSButtonGroup();
		var categories = this.categories.keys();
		if(categories.length > 0) {
			for (var i = 0; i < categories.length; i++) {
				var categoryName = categories[i];
				var icon = null;
				switch(i) {
					case 0:
						icon = PSSelectComponentTool.ICON.ALL;
						break;
					case 1:
						icon = PSSelectComponentTool.ICON.FIELDS;
						break;
					case 2:
						icon = PSSelectComponentTool.ICON.IMAGES;
						break;
					default:
						icon = this.getCategoryIcon(categoryName);
						break;
				}
				var button = new PSButton({ "className": "ps_category_list_item", "label": { "text": PageStudio.locale.get(categoryName), "length": 15 }, "action": PSSelectComponentTool.EVENT.SELECT_CATEGORY_CLICKED, "memo": { "categoryName": categoryName }, "group": this.categoryButtonGroup, "icon": icon });
				this.categoryList.insert(button.element);
			}
			if (this.currentBrowser == null) {
				//display the first category
				this.categoryButtonGroup.getAt(0).select();
				this.selectCategoryByName(PageStudio.locale.get("All"));
			}
		}
	},
	
	getBrowser: function(categoryName) {
		var browser = this.browsers.get(categoryName);
		if (browser != null) return browser;
		else {
			var category = this.categories.get(categoryName);
			if(this.selectedCategoryIndex == 2) {
				browser = new PSImageBrowser(category);
			}
			else {
				browser = new PSComponentBrowser(category, (this.selectedCategoryIndex == 0));
			}
			browser.insertInto(this.componentList);
			this.browsers.set(categoryName, browser);
			return browser;
		}
	},
	
	populateComponentList: function(categoryName) {
		if (this.currentBrowser) {
			this.currentBrowser.hide();
		}
		this.currentBrowser = this.getBrowser(categoryName);
		this.currentBrowser.show();
	}
});

PSSelectComponentTool.ICON = {
	ALL: "pageStudio/images/ui/select_component/categories/all.png",
	FIELDS: "pageStudio/images/ui/select_component/categories/fields.png",
	IMAGES: "pageStudio/images/ui/select_component/categories/images.png",
	OTHER: "pageStudio/images/ui/select_component/categories/other.png"
};
PSSelectComponentTool.EVENT = {
	SELECT_CATEGORY_CLICKED: "pagestudio:selectCategoryClicked" 
};

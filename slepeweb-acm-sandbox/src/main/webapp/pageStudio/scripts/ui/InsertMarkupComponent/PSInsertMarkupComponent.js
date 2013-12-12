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
var PSInsertMarkupComponentDialog = Class.create(PSDialog, {
	createElement: function($super) {
		$super();
		this.tool = new PSInsertMarkupComponentTool();		
		this.body.insert(this.tool.element);
		
		this.element.id = "ps_insert_markup_component";
		this.element.addClassName("ps_insert_markup_component");
		new PSResizableHelper(this.element, { grip: true, handle: "right bottom" });
	},
	
	show: function($super, center) {
		$super();
		this.tool.onShow();
	}
});

var PSInsertMarkupComponentTool = Class.create(PSTool, {
	initialize: function($super, params) {
		$super(params);
		this.attachEvents();
		this.selectedItem = null;
		this.selectedElement = null;
	},
	
	createElement: function($super) {
		$super();
		this.element.addClassName("ps_tool_insert_markup_component");
		
		this.filterInput = new PSFilterInput({ "label": PageStudio.locale.get("Filter"), "filter": this.applyFilter.bind(this) });
		
		this.componentList = new PSListView({});		
		this.componentList.setColumnData(PageStudio.locale.get("Name"), PageStudio.locale.get("Category"));
	
		this.configureFieldSet = new Element("fieldset", { "className": "ps_imc_configure_fieldset" });
		this.configureLegend = new Element("legend");
		this.configureFieldSet.insert(this.configureLegend);
		this.componentDescription = new Element("div", { "className": "ps_imc_component_description" });
		
		this.configureFieldSet.insert(this.componentDescription);
		
		this.configureFormBody = new Element("div", { "className": "ps_imc_configure_form_body" });
		this.configureFieldSet.insert(this.configureFormBody);
		
		var controls = new Element("div", { "className": "ps_controls" });
	
		this.okButton = new PSNativeButton({ "label": PageStudio.locale.get("OK"), "action": PSInsertMarkupComponentTool.EVENT.OKAY_CLICKED  });
		this.okButton.disable();
		this.cancelButton = new PSNativeButton({ "label": PageStudio.locale.get("Cancel"), "action": PSInsertMarkupComponentTool.EVENT.CANCEL_CLICKED });
		this.configureButton = new PSNativeButton({ "label": PageStudio.locale.get("Configure"), "action": PSInsertMarkupComponentTool.EVENT.CONFIGURE_CLICKED });
		this.configureButton.element.hide();
		
		this.insert(this.filterInput.element);
		this.insert(this.componentList.element);
		this.insert(this.configureFieldSet);
		
		controls.insert(this.cancelButton.element).insert(this.okButton.element).insert(this.configureButton.element);
		
		this.insert(controls);
		
		this.displayComponentDescription(null);
	},
	
	attachEvents: function() {
		var componentListener = this.onComponentJSONLoaded.bindAsEventListener(this);
		document.observe(PageStudio.EVENT.COMPONENT_JSON_LOADED, componentListener);
		document.observe(PageStudio.EVENT.COMPONENTS_UPDATED, componentListener);
		this.componentList.element.observe(PSListView.EVENT.SELECTION_CHANGED, this.onListSelectionChanged.bindAsEventListener(this));
		this.element.observe(PSInsertMarkupComponentTool.EVENT.CONFIGURE_CLICKED, this.onConfigureClicked.bindAsEventListener(this));
		this.element.observe(PSInsertMarkupComponentTool.EVENT.OKAY_CLICKED, this.onOkayClicked.bindAsEventListener(this));
		this.element.observe(PSInsertMarkupComponentTool.EVENT.CANCEL_CLICKED, this.onCancelClicked.bindAsEventListener(this));
		
		document.observe(PSComponent.EVENT.MARKUP_COMPONENT_SELECTED, this.onComponentSelected.bindAsEventListener(this));
		document.observe(PSComponent.EVENT.MARKUP_COMPONENT_DESELECTED, this.onComponentDeselected.bindAsEventListener(this));	
	},
	
	createInstanceId: function() {
		if(!this.componentId) {
			this.componentId = this.selectedItem.cid ? this.selectedItem.id : PSIDHelper.getId("ps_markup_component");
		}
	},
	
	reset: function() {
		this.selectedItem = null;
		this.selectedElement = null;
		this.componentId = null;
		this.displayComponentDescription(null);
		this.componentList.reset();
	},
	
	//Construct the mark-up component wrapper and details/body elements but don't load the mark-up (this will occur when the field refreshes and detects the mark-up component).
	insertComponent: function() {
		//Serialize the configuration form to a query string.
		var configuration = this.configureForm ? this.configureForm.toQueryString() : "";
		//var component = this.componentList.selectedItem.data;
		var component = this.selectedItem;
		var id, cid;
		if(component.cid) {
			cid = component.cid;
			id = component.id;
		}
		else {
			cid = component.id;
			id = PSIDHelper.getId("ps_markup_component");
		}
		//Put all the mark-up component data in to a JSON object.
		var data = {
				"id": id,
				"location": component.location,
				"cid": cid,
				"name": component.name,
				"type": component.componentType,
				"configuration": configuration
		};
		
		var wrapper = new Element("span", { "className": "ps_markup_component_wrapper", "onload": "this.contentEditable=false", "contentEditable": "false" });
		var details = new Element("span", { "className": "ps_markup_component_details", "onload": "this.contentEditable=false", "contentEditable": "false", "style": "overflow: hidden; height: 0; width: 0; display: block; display: inline-block !important; visibility: hidden; opacity: 0;" });
		var body = new Element("span", { "className": "ps_markup_component_body", "onload": "this.contentEditable=false", "contentEditable": "false", "style": "zoom:1;" });
		
		wrapper.insert(details.update(Object.toJSON(data))).insert(body);
		Event.fire(document, PSInsertMarkupComponentTool.EVENT.COMPONENT_SUBMITTED, { "markup": wrapper, "element": this.selectedElement });
		this.selectedElement = null;
	},
	
	onComponentSelected: function(e) {
		this.reconfigureComponent(e.memo.component, e.memo.element);
	},
	
	onComponentDeselected: function(e) {
		this.reset();
	},
	
	onComponentJSONLoaded: function(e) {
		this.populateComponentList();
	},
	
	onListSelectionChanged: function(e) {
		this.displayComponentDescription(e.memo.data);
		this.selectedItem = e.memo.data;
	},
	
	onOkayClicked: function(e) {
		if(this.selectedItem) {
			if(this.selectedItem.componentType == "configurable") {
				this.submitConfiguration();
			}
			else {
				this.createInstanceId();
				this.insertComponent();
				this.close();
			}
		}
	},

	onCancelClicked: function(e) {
		this.close();
	},
	
	onConfigureClicked: function(e) {
		if (this.componentList.selectedItem != null) {
			this.retrieveConfigForm(this.componentList.selectedItem.data);
		}
	},
	
	onShow: function(e) {
		if(!this.selectedItem || this.selectedItem.componentType != "configurable") {
			this.displayComponentDescription(this.componentList.selectedItem ? this.componentList.selectedItem.data : null);
		}
		if(this.selectedElement && this.selectedElement.parentNode == null) this.selectedElement = null;
	},
	
	close: function($super) {
		$super();
		this.reset();
	},
	
	onSubmissionSuccess: function(t) {
		this.insertComponent();
		this.close();
	},
	
	onSubmissionFailure: function(t) {
		PageStudio.displayExceptionAlert(PageStudio.locale.get("ps_component_exception"), t);
	},
	
	submitConfiguration: function() {
		this.configureForm.submit(this.componentId, this.onSubmissionSuccess.bindAsEventListener(this), this.onSubmissionFailure.bindAsEventListener(this));
	},
	
	reconfigureComponent: function(component, element) {
		this.componentList.items.get(component.cid).select();
		this.selectedElement = element;
		if(component.type == "configurable") {
			this.selectedItem = component;
			this.componentId = element.id;
			this.retrieveConfigForm(component);
		}
	},
	
	retrieveConfigForm: function(component) {
		this.createInstanceId();
		var cid;
		if(component.cid) {
			cid = component.cid;
		}
		else {
			cid = component.id;
		}
		this.componentDescription.hide();
		this.configureLegend.update(PageStudio.locale.get("Configuration"));
		this.configureFieldSet.addClassName(PageStudio.CLASS.LOADING);
		this.configureButton.disable();
		var params = PageStudio.getRequestItemParams({
			"action": PageStudio.ACTION.GET_COMPONENT,
			"configuration": PageStudio.ACTION.GET_CONFIGURATOR_DETAILS,
			"cid": cid,
			"cInstId": this.componentId		
		});
		
		if(component.configuration) {
			Object.extend(params, component.configuration.unescapeHTML().parseQuery());
		}

		new PageStudio.Request(PageStudio.URL.COMPONENT_FACTORY, { 
			method: "get",
			parameters: params,
			evalScripts: true,
			onSuccess: this.onRetrieveComponentConfigurationSuccess.bindAsEventListener(this, component),
			onFailure: this.onRetrieveComponentConfigurationFailure.bindAsEventListener(this, component),
			onComplete: this.onRetrieveComponentConfigurationComplete.bindAsEventListener(this)
		});
	},
	
	onRetrieveComponentConfigurationSuccess: function(t, component) {
		this.displayConfigurationForm(t.responseJSON, component);
	},
	
	onRetrieveComponentConfigurationFailure: function(t, component) {
		this.displayComponentDescription(component);
		this.configureButton.enable();
	},
	
	onRetrieveComponentConfigurationComplete: function(t) {
		this.configureFieldSet.removeClassName(PageStudio.CLASS.LOADING);
	},
	
	displayConfigurationForm: function(configuration, component) {
		this.configureFormBody.show();
		if(this.configureForm) this.configureForm.remove();
		this.configureForm = new PSConfigureComponentForm(configuration, component, { "markup": true });
		this.configureButton.hide();
		this.okButton.show();
		this.okButton.enable();
		this.configureFormBody.insert(this.configureForm.element);
	},
	
	displayComponentDescription: function(component) {
		this.configureFormBody.hide();
		this.configureLegend.update(PageStudio.locale.get("Description"));
		//remove any configuration form
		if(this.configureFormTable) {
			this.configureFormTable.remove();
			delete this.configureFormTable;
		}
		//
		if(component != null) {
			this.componentDescription.update(component.description);
			if(component.thumbnail && component.thumbnail != "null") {
				this.componentDescription.insert({ "top": new Element("img", { "src": component.thumbnail, "className": "ps_markup_component_thumbnail" }) });
			}
			if(component.componentType == "configurable") {
				this.okButton.hide();
				this.okButton.disable();
				this.configureButton.show();
				this.configureButton.enable();
			}
			else {
				this.configureButton.element.hide();
				this.okButton.show();
				this.okButton.enable();
			}
		}
		else {
			this.componentDescription.update(PageStudio.locale.get("no_component_selected"));
			this.okButton.disable();
			this.okButton.show();
			this.configureButton.hide();
		}
		this.componentDescription.show();
		//vertically centre the description
		var marginTop = (this.configureFieldSet.getHeight() / 2) - (this.componentDescription.getHeight() / 2) - 10;
		this.componentDescription.setStyle({ "marginTop": marginTop + "px" });
	},
	
	populateComponentList: function() {
		this.componentList.clear();
		PageStudio.components.values().each(this.addComponentToList, this);
	},
	
	addComponentToList: function(component) {
		if(component.markup) {
			this.componentList.add(component, component.icon, component.name, component.type);
		}
	},
	
	doFilter: function(item) {
		var match = true;
		for(var i = 0; i < this.filter.length; i++) {
			var word = this.filter[i];
			var wmatch = false;
			for(val in item.data) {
				if (item.data[val].toLowerCase && item.data[val].toLowerCase().indexOf(word) != -1 || word == "") {
					wmatch = true;
					break;
				}
			}
			if(wmatch == false) { match = false; break; }
		}
		if (match) {
			item.show();
		}
	},
	
	applyFilter: function(text) {
		this.filter = text.toLowerCase().split(" ");
		this.componentList.items.values().invoke("hide");
		this.componentList.items.values().each(this.doFilter.bind(this));
	}
});

PSInsertMarkupComponentTool.EVENT = {
		OKAY_CLICKED: "pagestudio:insertMarkupComponentOkayClicked",
		CANCEL_CLICKED: "pagestudio:insertMarkupComponentcancelClicked",
		CONFIGURE_CLICKED: "pagestudio:insertMarkupComponentconfigureClicked",
		COMPONENT_SUBMITTED: "pagestudio:insertMarkupComponentSubmitted"
};

var PSListView = Class.create(PSElement, {
	initialize: function($super, params) {
		$super(params);
		this.items = new Hash();
		this.selectedItem = null;
		this.columns = new Array();
		this.sortColumn = null;
		this.sortOrder = 0;
	},
	
	attachEvents: function($super) {
		$super();
		document.observe(PageStudio.EVENT.COMPONENT_JSON_LOADED, this.refresh.bindAsEventListener(this));
		this.element.observe("pagestudio:listViewItemSelected", this.onItemSelected.bindAsEventListener(this));
	},
	
	createElement: function($super) {
		$super();
		this.element.addClassName("ps_list_view");
		this.table = new Element("table", { "className": "ps_list_view_table" });
		this.header = new Element("thead", { "className": "ps_list_view_header" });
		this.headerRow = new Element("tr", { "className": "ps_list_view_header_row" });
		this.body = new Element("tbody", { "className": "ps_list_view_body" });
		this.element.insert(this.table.insert(this.header.insert(this.headerRow)).insert(this.body));
	},
	
	setColumnData: function() {
		this.columnCount = arguments.length;
		if(arguments.length > 0) {
			this.sortColumn = 0;
			for(var i = 0; i < arguments.length; i++) {
				this.addColumn(arguments[i]);
			}
			//set the first header column to span two columns (for icon column)
			this.columns[0].writeAttribute("colspan", "2");
		}
	},
	
	addColumn: function(name) {
		var column = new Element("th", { "className": "ps_list_view_column "}).update(name);
		this.headerRow.insert(column);
		this.columns.push(column);
	},
	
	add: function(data, icon) {
		var columns = new Array();
		if(arguments.length > 2) {
			for (var i = 2; i < arguments.length; i++) {
				columns.push(arguments[i]);
			}
		}
		this.addItem(new PSListViewItem({}, data, icon, columns));
	},
	
	addItem: function(item) {
		this.items.set(item.data.id, item);
		this.body.insert(item.element);
		this.element.fire(PSListView.EVENT.ITEM_ADDED);
	},
	
	onItemSelected: function(e) {
		this.selectedItem = e.memo.object;
		this.element.fire(PSListView.EVENT.SELECTION_CHANGED, { "data": this.selectedItem.data });
	},
	
	clear: function() {
		this.items.values().invoke("remove");
		this.items = new Hash();
	},
	
	reset: function() {
		if(this.selectedItem) {
			this.selectedItem.deselect();
			this.selectedItem = null;
		}
	}
});

var PSListViewItem = Class.create(PSElement, {
	initialize: function($super, params, data, icon, columns) {
		this.data = data;
		this.columnData = columns;
		this.elementName = "listViewItem";
		this.icon = icon;
		$super(params);
	},
	
	createElement: function($super) {
		this.elementType = "tr";
		$super();	
		this.element.addClassName("ps_list_view_item");
		//create icon column
		var iconCell = new Element("td", { "className": "ps_list_view_item_cell ps_list_view_item_icon_cell" });
		this.element.insert(iconCell);
		if(this.icon) {
			iconCell.insert(new Element("img", { "src": this.icon, "className": "ps_list_view_item_icon" }));
		}
		for(var i = 0; i < this.columnData.length; i++) {
			this.element.insert(new Element("td", { "className": "ps_list_view_item_cell" }).update(this.columnData[i]));
		}
	},
	
	onClick: function($super, e) {
		$super(e);
		e.stop();
	},
	
	onMouseDown: function($super, e) {
		$super(e);
		e.stop();
	},
	
	onMouseUp: function($super, e) {
		$super(e);
		e.stop();
	}
});

PSListView.EVENT = {
		SELECTION_CHANGED: "pagestudio:listSelectionChanged",
		ITEM_ADDED: "pagestudio:itemAdded"
};

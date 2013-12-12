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

var PSChangeLayoutTool = Class.create(PSTool, {
	initialize: function($super) {
		this.isDeleting = false;
		this.isRenaming = false;
		
		this.layoutButtons = new Hash();
		this.layoutButtonGroup = new PSButtonGroup(this.onSelectionChanged.bind(this));
		$super();
		this.attachEvents();
		this.setViewMode("all");
	},
	
	createElement: function($super) {
		$super();
		this.element.addClassName("ps_tool_change_layout");
		this.insert(new Element("div", { "className": "ps_change_layout_instructions"}).update(PageStudio.locale.get("change_layout_instructions")));
		this.filterInput = new PSFilterInput({ "label": PageStudio.locale.get("Filter"), "filter": this.applyFilter.bind(this) });
		this.insert(this.filterInput.element); 
		this.createTypeSelectForm();
		this.layoutContainer = new Element("div", { "className": "change_layout_list_container ps_browser" } );
		this.insert(this.layoutContainer);
		this.createControls();
	},
	
	createControls: function() {
		var okCancelcontainer = new Element("div", { "className": "ps_change_layout_controls bottom_controls" });
		var container = new Element("div", { "className": "ps_change_layout_controls" });
		
		var addButton = new PSNativeButton({
			"label": PageStudio.locale.get("add"),
			"tooltip": { "title": PageStudio.locale.get("add_layout"), "text": PageStudio.locale.get("create_layout_tooltip_text") },
			"action": PSChangeLayoutTool.EVENT.CREATE_LAYOUT_CLICKED
		});
		
		this.deleteButton = new PSNativeButton({
			"label": PageStudio.locale.get("delete"),
			"tooltip": { "title": PageStudio.locale.get("delete_layout"), "text": PageStudio.locale.get("delete_selected_layout_tooltip_text") },
			"action": PSChangeLayoutTool.EVENT.DELETE_LAYOUT_CLICKED
		});
		
		this.editButton = new PSNativeButton({
			"label": PageStudio.locale.get("edit_ellip"),
			"tooltip": { "title": PageStudio.locale.get("edit_layout"), "text": PageStudio.locale.get("edit_selected_layout_tooltip_text") },
			"action": PSChangeLayoutTool.EVENT.EDIT_LAYOUT_CLICKED
		});
		
		this.updateEditButtons();
		
		this.okButton = new PSNativeButton({
			"label": PageStudio.locale.get("OK"),
			"action": PSChangeLayoutTool.EVENT.OKAY_CLICKED
		});
		
		this.updateOKButton();
		
		var cancelButton = new PSNativeButton({
			"className": "ps_save_layout_btn",
			"label": PageStudio.locale.get("Cancel"),
			"action": PSChangeLayoutTool.EVENT.CANCEL_CLICKED
		});
		
		container.insert(this.deleteButton.element).insert(this.editButton.element).insert(addButton.element);
		okCancelcontainer.insert(cancelButton.element).insert(this.okButton.element);
		
		this.insert(container).insert(okCancelcontainer);
	},
	
	createTypeSelectForm: function() {
		this.typeSelectForm = new Element("form", { "className": "ps_change_layout_type_select_form" });
		////** for IE unchecking radio button bug
		this.selectedRadio = "ps_layout_type_all";
		//**
		this.typeSelectForm.insert(new Element("input", { "id": "ps_layout_type_all", "type": "radio", "name": "layoutType", "value": "all", "checked": "true" }));
		this.typeSelectForm.insert(new Element("label", { "className": "ps_layout_type_all_label", "for": "ps_layout_type_all" }).update(PageStudio.locale.get("all_layouts")));
		this.typeSelectForm.insert(new Element("input", { "id": "ps_layout_type_site", "type": "radio", "name": "layoutType", "value": "site" }));
		this.typeSelectForm.insert(new Element("label", { "className": "ps_layout_type_site_label", "for": "ps_layout_type_site" }).update(PageStudio.locale.get("site_layouts")));
		this.typeSelectForm.insert(new Element("input", { "id": "ps_layout_type_other", "type": "radio", "name": "layoutType", "value": "other" }));
		this.typeSelectForm.insert(new Element("label", { "className": "ps_layout_type_other_label", "for": "ps_layout_type_other" }).update(PageStudio.locale.get("other_layouts")));
		this.insert(this.typeSelectForm);
	},
	
	attachEvents: function() {
		document.observe(PageStudio.EVENT.LAYOUT_JSON_LOADING, this.onBeginLoadingLayoutList.bindAsEventListener(this));
		document.observe(PageStudio.EVENT.LAYOUT_JSON_LOADED, this.populateLayoutList.bindAsEventListener(this));
		if(PageStudio.eventSupported("onchange", this.typeSelectForm)) {
			this.typeSelectForm.observe("change", this.onTypeSelectionChanged.bindAsEventListener(this));
		}
		else {
			//IE doesn't support the form onChange event so we can just listen for clicks.
			this.typeSelectForm.observe("click", this.onTypeSelectionChanged.bindAsEventListener(this));
		}		
		//** for IE unchecking radio button bug
		document.observe(PSDialog.EVENT.OPENED, this.onDialogOpened.bindAsEventListener(this));
		//**
		
		document.observe(PageStudio.EVENT.LAYOUT_ADDED, this.onLayoutAdded.bindAsEventListener(this));
		document.observe(PageStudio.EVENT.LAYOUT_REMOVED, this.onLayoutRemoved.bindAsEventListener(this));
		
		document.observe(PSCreateLayoutTool.EVENT.LAYOUT_EDITED, this.onLayoutEdited.bindAsEventListener(this));
		
		this.element.observe(PSChangeLayoutTool.EVENT.OKAY_CLICKED, this.onOkayClicked.bindAsEventListener(this));
		this.element.observe(PSChangeLayoutTool.EVENT.DELETE_LAYOUT_CLICKED, this.onDeleteClicked.bindAsEventListener(this));
	},
	
	applyFilter: function(text)
	{
		this.filter = text.toLowerCase().split(" ");
		var buttons = this.layoutButtons.values();
		buttons.invoke("hide");
		buttons.each(function(button){
			var match = true;
			var titleWords = button.label.text.split(" ");
			for(var i = 0; i < this.filter.length; i++) {
				var word = this.filter[i];
				var wmatch = false;
				for(var w = 0; w < titleWords.length; w++) {
					var titleWord = titleWords[w];
					if (titleWord.toLowerCase().indexOf(word) != -1 || word == "") {
						wmatch = true;
						break;
					}
				}
				if(wmatch == false) { match = false; break; }
			}
			if (match) {
				//only reveal buttons for the correct view mode.
				switch(this.viewMode)
				{
					case "site":
						if(!button.siteLayout) return; // return acts as a 'continue' within the prototype each loop.
						break;
					case "other":
						if(button.siteLayout) return;
						break;
				}
				button.show();
			}
		}, this);
	},
	
	show: function($super)
	{
		$super();
		this.scrollToButton(this.layoutButtonGroup.getSelected());
	},
	
	refresh: function() {
		this.layoutContainer.addClassName(PageStudio.CLASS.LOADING);
		if(this.layoutButtonGroup) {
			this.layoutButtonGroup.clear();
		}
		this.populateLayoutList();
	},
	
	addLayoutButton: function(layout, update) {
		var button = new PSLayoutButton({ "className": PSChangeLayoutTool.CLASS.LIST_ITEM, "label": { "text": decodeURIComponent(layout.name), "length": 28 }, "action": PSChangeLayoutTool.EVENT.LAYOUT_CLICKED, "memo": layout, "group": this.layoutButtonGroup });
		this.layoutButtons.set(layout.name, button);
		button.addThumbnail(decodeURIComponent(layout.html));
		this.layoutContainer.insert(button.element);
		
		if(update)
		{
			this.alphabetizeButton(button);
			button.select();
			this.setViewMode("all");
		}
		
		return button;
	},
	
	populateLayoutList: function(){
		var layouts = PageStudio.layouts.values();
		this.layoutContainer.removeClassName(PageStudio.CLASS.LOADING);
		for (var i = 0; i < layouts.length; i++) {
			this.addLayoutButton(layouts[i]);
		}
	},
	
	deleteLayout: function(layout, button)
	{
		if(this.selectedLayout && confirm(PageStudio.locale.get("confirm_delete_layout")))
		{	
			this.isDeleting = true;
			this.updateDeleteOverlay();
			this.updateEditButtons();
			
			new PageStudio.Request(PageStudio.URL.LAYOUT_DESIGNER, {
				"method": "delete",
				"onSuccess": this.onDeleteSuccess.bindAsEventListener(this, layout),
				"onFailure": this.onDeleteFailure.bindAsEventListener(this),
				"onComplete": this.onDeleteComplete.bindAsEventListener(this),
				"parameters": PageStudio.getRequestItemParams({ "action": PageStudio.ACTION.REMOVE_LAYOUT, "layoutName": decodeURIComponent(layout.name) })
			});
		}
	},
	
	onSelectionChanged: function(selectedButton)
	{
		this.updateSelection(selectedButton ? selectedButton.memo : null);
	},
	
	onDeleteSuccess: function(t, layout)
	{
		PageStudio.removeLayout(layout.name);
	},
	
	onDeleteFailure: function(t)
	{
		PageStudio.displayExceptionAlert(PageStudio.locale.get("delete_layout_exception"), t);
	},
	
	onDeleteComplete: function(t)
	{
		this.isDeleting = false;
		this.updateDeleteOverlay();
		this.updateEditButtons();
	},
	
	onLayoutAdded: function(e) {
		this.addLayoutButton(e.memo, true);
	},
	
	onLayoutRemoved: function(e) {
		var button = this.layoutButtons.unset(e.memo.name);
		button.remove();
		this.layoutButtonGroup.remove(button);
		this.selectedLayout = null;
		this.updateSelection(null);
	},
	
	onLayoutEdited: function(e) {
		PageStudio.removeLayout(e.memo.oldVersion.name);
		PageStudio.addLayout(e.memo.name, e.memo.html, true);
	},
	
	alphabetizeButton: function(newButton)
	{
		this.layoutButtonGroup.buttons.each(function(button){
			if(newButton.label.text < button.label.text)
			{
				button.element.insert({ "before": newButton.element });
				throw $break;
			}
		});
	},
	
	scrollToButton: function(button)
	{
		if(button != null)
		{
			var offset = button.element.positionedOffset();
			this.layoutContainer.scrollTop = offset.top;
		}
	},
	
	updateEditButtons: function()
	{
		if(!this.isDeleting && this.selectedLayout && this.selectedLayout.siteLayout)
		{
			this.deleteButton.enable();
			this.editButton.enable();
		}
		else
		{
			this.deleteButton.disable();
			this.editButton.disable();
		}
	},
	
	updateOKButton: function()
	{
		if(this.selectedLayout)
		{
			this.okButton.enable();
		}
		else
		{
			this.okButton.disable();
		}
	},
	
	updateSelection: function(layout)
	{
		this.selectedLayout = layout;
		this.updateEditButtons();
		this.updateOKButton();
	},
		
	onBeginLoadingLayoutList: function(e) {
		this.layoutContainer.update();//clear the layoutList
		this.layoutContainer.addClassName("ps_loading");
	},
	
	onTypeSelectionChanged: function(e) {
		var buttons = this.typeSelectForm.getInputs("radio", "layoutType");
		var option;
		for (var i = 0; i < buttons.length; i++) {
			if (buttons[i].checked) {
				//** for IE unchecking radio button bug
				this.selectedRadio = buttons[i].id;
				//** 
				option = buttons[i].getValue();
			}
		}
		this.setViewMode(option);
		
		var filterValue = this.filterInput.getValue();
		if(filterValue != "")
		{
			this.applyFilter(filterValue);
		}
	},
	
	setViewMode: function(option)
	{
		this.viewMode = option;
		switch(option) {
			case "all":
				this.layoutButtons.values().each(function(button){
					button.show();
				});
				break;
			case "site":
				this.layoutButtons.values().each(function(button){
					button.memo.siteLayout ? button.show() : button.hide();
				});
				break;
			case "other":
				this.layoutButtons.values().each(function(button){
					button.memo.siteLayout ? button.hide() : button.show();
				});
				break;
		}
		this.updateEditButtons();
	},
	
	onOkayClicked: function(e) {
		//Only close if there is no change or if the change completes successfully.
		if(!this.selectedLayout || PageStudio.selectedArea.setLayout(this.selectedLayout)) {
			this.close();
		}
	},
		
	onDeleteClicked: function(e) {
		this.deleteLayout(this.selectedLayout, this.layoutButtonGroup.getSelected());
	},
	
	updateDeleteOverlay: function()
	{
		if(this.isDeleting)
		{
			this.deletingOverlay = PSTool.getOverlay(this.layoutContainer);
		}
		else
		{
			if(this.deletingOverlay)
			{
				this.deletingOverlay.remove();
				delete this.deletingOverlay;
			}
		}
	},	
	
	reset: function() {
		this.layoutButtonGroup.setSelected(null);
		this.updateSelection(null);
	},
	
	onDialogOpened: function(e) {
		//** for IE unchecking radio button bug
		if(e.memo.dialog && e.memo.dialog.tool == this) {
			$(this.selectedRadio).checked = true;
		}
		//**
		this.reset();
	}	
});

PSChangeLayoutTool.CLASS = {
	LIST_ITEM: "ps_layout_list_item",
	SITE_LAYOUT: "ps_site_layout"
};
	
PSChangeLayoutTool.EVENT = {
	LAYOUT_CLICKED: "pagestudio:layoutClicked",
	CREATE_LAYOUT_CLICKED: "pagestudio:addLayoutClicked",
	DELETE_LAYOUT_CLICKED: "pagestudio:deleteLayoutClicked",
	EDIT_LAYOUT_CLICKED: "pagestudio:editLayoutClicked",
	CANCEL_CLICKED: "pagestudio:changeLayoutCancelClicked",
	OKAY_CLICKED: "pagestudio:changeLayoutOkayClicked"
};

var PSCreateLayoutTool = Class.create(PSTool, {
	initialize: function($super) {
		$super();
		this.hasValidName = false;
		this.isSaving = false;
		this.attachEvents();
	},
	
	createElement: function($super) {
		$super();
		this.element.addClassName("ps_tool_create_layout");
		this.insert(new Element("div", { "className": "ps_change_layout_instructions"}).update(PageStudio.locale.get("create_layout_instructions")));
		this.createLayoutPreviewArea();
		this.createControls();
	},
	
	attachEvents: function() {
		this.element.observe(PSCreateLayoutTool.EVENT.REMOVE_CELL_CLICKED, this.onRemoveCellClicked.bindAsEventListener(this));
		this.element.observe(PSCreateLayoutTool.EVENT.ADD_CELL_CLICKED, this.onAddCellClicked.bindAsEventListener(this));
		this.element.observe(PSCreateLayoutTool.EVENT.SAVE_CLICKED, this.onSaveClicked.bindAsEventListener(this));
		this.element.observe(PSCreateLayoutTool.EVENT.IMPORT_CLICKED, this.onImportClicked.bindAsEventListener(this));
		
		this.element.observe("submit", function(e) {
			e.stop();
			this.save();
		}.bindAsEventListener(this));
		if (PageStudio.eventSupported("oninput", this.inputName)) {
			this.inputName.observe("input", this.onNameInputChange.bindAsEventListener(this));
		}
		else {
			this.inputName.observe("paste", this.onNameInputChange.bindAsEventListener(this));
			this.inputName.observe("keyup", this.onNameInputChange.bindAsEventListener(this));
		}
	},
	
	createLayoutPreviewArea: function() {
		var wrapper = new Element("div", { "className": "ps_layout_preview_wrapper"});
		this.previewWidth = new Element("div", { "className": "ps_layout_preview_width" });
		PSToolTipHelper.add(this.previewWidth, { "title": "Preview Width", "text": PageStudio.locale.get("preview_width_tooltip_text") });
		this.preview = new Element("div", { "className": "ps_layout_preview" });
		this.insert(this.previewWidth).insert(wrapper.insert(this.preview));
	},
	
	createControls: function() {
		var container = new Element("div", { "className": "ps_change_layout_controls bottom_controls" });
		
		var form = new Element("form", { "className": "ps_create_layout_form" });
		
		this.inputName = new Element("input", { "name": "layout_name", "className": "ps_input" });
		var label = new Element("label", { "for": "layout_name" }).update(PageStudio.locale.get("layout_name_label"));
		this.insert(form.insert(label).insert(this.inputName));
		
		this.importButton = new PSNativeButton({
			"label": PageStudio.locale.get("Import"),
			"className": "ps_import_layout_button",
			"tooltip": { "title": PageStudio.locale.get("import_current_layout"), "text": PageStudio.locale.get("import_current_layout_tooltip_text") },
			"action": PSCreateLayoutTool.EVENT.IMPORT_CLICKED
		});
		
		this.saveButton = new PSNativeButton({
			"label": PageStudio.locale.get("Save"),
			"action": PSCreateLayoutTool.EVENT.SAVE_CLICKED
		});
		this.updateSaveButton();
		
		var cancelButton = new PSNativeButton({
			"label": PageStudio.locale.get("Cancel"),
			"action": PSCreateLayoutTool.EVENT.CANCEL_SAVE_CLICKED
		});
		
		container.insert(cancelButton.element);
		container.insert(this.saveButton.element);
		container.insert(this.importButton.element);
		
		this.addCellButton = new PSNativeButton({
			"icon": { 
				"enabled": "/pageStudio/images/ui/Regular/s24x24/Add.png",
				"disabled": "/pageStudio/images/ui/Disabled/s24x24/Add.png",
				"hot": "/pageStudio/images/ui/Hot/s24x24/Add.png"
			},
			"className": "ps_edit_layout_button ps_add_cell_button",
			"tooltip": PageStudio.locale.get("add_cell"),
			"action": PSCreateLayoutTool.EVENT.ADD_CELL_CLICKED
		});
		
		this.removeCellButton = new PSNativeButton({
			"icon": { 
				"enabled": "/pageStudio/images/ui/Regular/s24x24/Delete.png",
				"disabled": "/pageStudio/images/ui/Disabled/s24x24/Delete.png",
				"hot": "/pageStudio/images/ui/Hot/s24x24/Delete.png"
			},
			"className": "ps_edit_layout_button ps_remove_cell_button",
			"tooltip": PageStudio.locale.get("remove_cell"),
			"action": PSCreateLayoutTool.EVENT.REMOVE_CELL_CLICKED
		});
		
		this.updateRemoveCellButton();
		
		this.insert(this.addCellButton.element);
		this.insert(this.removeCellButton.element);
		
		this.insert(container);
	},
	
	updatePreviewWidth: function()
	{
		this.areaWidth = PageStudio.selectedArea.element.getWidth();
		var template = new Template(PageStudio.locale.get("change_layout_preview_width_template"));
		var message = template.evaluate({ "width": this.areaWidth });
		this.previewWidth.update(message);
	},
	
	updateLayoutPreview: function(cells) {
		this.preview.update(); //clear
		
		this.updateRemoveCellButton();
		
		this.updatePreviewWidth();

		cells.each(function(cell) {
			this.addCellToPreview(cell);
		}, this);
		
		this.makeLayoutDirty();
		
		this.initialiseSortable();
	},
	
	addCellToPreview: function(cell) {
		var clone = cell.clone(false);
		clone.className = PageStudio.CLASS.CELL;
		clone.PSObj = null;
		clone.removeAttribute("id"); //clone will have a duplicate ID so we remove it.
		clone.identify();//however, we must have ID for certain sorting functionality to work.
		clone.removeAttribute("style");
		var styleWidth = cell.getStyle("width");
		var width = this.getCellWidth(cell, styleWidth);
		clone.setStyle({ "width": width });
		clone.update(styleWidth);
		PSToolTipHelper.add(clone, width, true);
		this.preview.insert(clone);
		this.addCellEventHandler(clone);
		return clone;
	},
	
	getCellWidth: function(cell, styleWidth) {
		var width = styleWidth ? styleWidth : cell.getStyle("width");
		if (width.endsWith("px")) {
			width = Math.floor((parseInt(styleWidth) / this.areaWidth) * this.preview.getWidth()) + "px";
		}
		return width;
	},
	
	addCellEventHandler: function(cell)
	{
		cell.setStyle({ "position": "relative" });
		new PSResizableHelper(cell, { "handle": "right", "minWidth": "0", "minHeight": "0" });
		
		var selectedClassName = "ps_selected";
		cell.observe("click", function(e){
			var selected = cell.hasClassName(selectedClassName);
			if(!e.ctrlKey)
			{
				this.preview.select(".ps_selected").invoke("removeClassName", selectedClassName);
			}
			else if(selected) {
				cell.removeClassName("ps_selected");
			}
			
			if(!selected) {
				cell.addClassName("ps_selected");
			}
			
			this.updateRemoveCellButton();
		}.bindAsEventListener(this));
		
		/*
		 * Resize handling: when a cell is resized, it's width will be converted to a pixel value.
		 * If it was previously a percentage value, we should conceal this and convert the width back
		 * to a percentage after the resize has completed.
		 */
		var percentageWidth;
		var theStyleWidth;
		
		//Check if the cell width is a percentage.
		cell.observe(PageStudio.EVENT.RESIZE_START, function(e) {
			percentageWidth = e.memo.style.width.indexOf("%") != -1;
			PSToolTipHelper.remove(cell);
		}.bindAsEventListener(this));
		
		//Update the cell label with the new width.
		cell.observe(PageStudio.EVENT.RESIZE, function(e) {
			if(percentageWidth) {
				theStyleWidth = Math.round((cell.getWidth() / cell.up().getWidth()) * 100) + "%";
			}
			else {
				theStyleWidth = cell.getStyle("width");
			}
			cell.update(theStyleWidth);
		}.bindAsEventListener(this));
		
		//Apply the converted style width if necessary.
		cell.observe(PageStudio.EVENT.RESIZE_END, function(e) {
			if(percentageWidth) {
				cell.setStyle({ "width": theStyleWidth });
			}
			this.makeLayoutDirty();
			PSToolTipHelper.add(cell, theStyleWidth, true);
		}.bindAsEventListener(this));
	},
	
	reset: function() {
		this.preview.update();
		
		this.editingLayout = null;
		
		this.isDirty = false;
		this.nameDirty = false;
		this.layoutDirty = false;
		
		this.updateSavingOverlay();
		this.updateInput();
		
		this.tipVisible = false;
		if(this.tip) this.tip.hide();
		
		this.inputName.clear();
		this.inputName.activate();
		this.setHasValidName(false);
	},
	
	updateInput: function()
	{
		this.isSaving ? this.inputName.disable() : this.inputName.enable();
	},
	
	show: function($super) {
		$super();
		this.reset();
		this.updatePreviewWidth();
	},
	
	validateName: function(value) {
		this.issues = {};
		if (value && value != "") {
			var encodedName = encodeURIComponent(value);
			if((PageStudio.layouts && PageStudio.layouts.get(encodedName) == null)) {
				return true;
			}
			else {
				//If the name is the original name of the layout being edited then it is valid.
				if(this.editingLayout != null && this.editingLayout.name == encodedName) {
					return true;
				}
				this.issues.duplicate = true;
			}
		}
		else {
			this.issues.empty = true;
		}
		return false;
	},
	
	onSaveClicked: function(e) {
		this.save();
	},
	
	onImportClicked: function(e) {
		var cells = PageStudio.selectedArea.element.select("." + PageStudio.CLASS.CELL);
		
		if(this.getPreviewCells().length == 0
			|| confirm(PageStudio.locale.get("confirm_import_layout")))
		{
			this.updateLayoutPreview(cells);
		}
	},
	
	onRemoveCellClicked: function(e) {
		var cells = this.preview.select(".ps_selected");
		if(cells.length > 0) {
			cells.each(function(cell) {
				cell.stopObserving();
				cell.remove();
			});
			this.makeLayoutDirty();
		}
		this.updateRemoveCellButton();
	},
	
	onAddCellClicked: function(e) {
		this.addCell();
	},
	
	addCell: function() {
		var cell = this.addCellToPreview(
			new Element("div", { "className": PageStudio.CLASS.CELL, "style": "width: 100%;"})
		);
		this.makeLayoutDirty();
		this.initialiseSortable();
		this.scrollToCell(cell);
	},
	
	scrollToCell: function(cell) {
		var offset = cell.positionedOffset();
		this.preview.parentNode.scrollTop = offset.top;
	},
	
	updateDirty: function()
	{
		if((this.layoutDirty || this.nameDirty) != this.isDirty)
		{
			this.isDirty ^= true;
			this.updateSaveButton();
		}
	},
	
	makeLayoutDirty: function()
	{
		if (this.layoutDirty != this.checkLayoutDirty())
		{
			this.layoutDirty ^= true;
			this.updateDirty();
		}
	},
	
	checkLayoutDirty: function()
	{
		if(this.editingLayout && this.editingLayout.layoutCache)
		{
			var cells = this.getPreviewCells();
			var cache = this.editingLayout.layoutCache;
	  
			if(cells.length != cache.length) return true;
			for (var i = 0; i < cells.length; i++)
			{
				if(cells[i].innerText != cache[i]) return true;
			}
		}
		return false;
	},
	
	makeNameDirty: function(layoutName)
	{
		if(!this.nameDirty)
		{
			this.nameDirty = true;
			this.updateDirty();
		}
		else 
		{
			if(this.editingLayout && this.editingLayout.name == encodeURIComponent(layoutName))
			{
				this.nameDirty = false;
				this.updateDirty();
			}
		}
	},
	
	initialiseSortable: function()
	{
		Sortable.create(this.preview, { "tag": "div", "only": PageStudio.CLASS.CELL, "zindex": 99999, "scroll": window,
			"overlap": "null", "constraint": null, "greedy": true, "onUpdate": this.makeLayoutDirty.bind(this)
		});
	},
	
	save: function()
	{
		this.isSaving = true;
		
		this.updateSaveButton();
		this.updateSavingOverlay();
		this.updateInput();
		
		var parser = new PSLayoutParser(false, true);
		var layout = parser.createXML($A([ this.preview ]));
		var name = this.inputName.getValue();
				
		if(layout && this.validateName(name)) {
			
			var params;
			
			if(this.editingLayout)
			{
				params = {
					"action": PageStudio.ACTION.EDIT_LAYOUT,
					"layoutName": decodeURIComponent(this.editingLayout.name),
					"newLayoutName": decodeURIComponent(name)
				};
			}
			else
			{
				params = {
					"action": PageStudio.ACTION.SAVE_LAYOUT,
					"layoutName": decodeURIComponent(name)
				};
			}
			
			params.layoutXML = layout;
			
			new PageStudio.Request(PageStudio.URL.LAYOUT_DESIGNER, {
				method: "post",
				parameters: PageStudio.getRequestItemParams(params),
				onSuccess: this.onSubmissionSuccess.bindAsEventListener(this, name),
				onFailure: this.onSubmissionFailure.bindAsEventListener(this),
				onComplete: this.onSubmissionComplete.bindAsEventListener(this)
			});
		}
	},
	
	displaySavingOverlay: function()
	{
		if(!this.savingOverlay)
		{
			this.savingOverlay = PSTool.getOverlay(this.preview.parentNode);
		}
	},
	
	hideSavingOverlay: function()
	{
		if(this.savingOverlay) {
			this.savingOverlay.remove();
			delete this.savingOverlay;
		}
	},
	
	updateSavingOverlay: function()
	{
		if(this.isSaving)
		{
			this.displaySavingOverlay();
		}
		else
		{
			this.hideSavingOverlay();
		}
	},
	
	updateRemoveCellButton: function()
	{
		this.preview.select(".ps_selected").length > 0 ? this.removeCellButton.enable() : this.removeCellButton.disable();
	},
	
	updateSaveButton: function()
	{
		!this.isSaving && this.hasValidName && (!this.editingLayout || this.isDirty) ? this.saveButton.enable() : this.saveButton.disable();
	},
	
	onSubmissionComplete: function(t) {
		this.isSaving = false;
		this.reset();
	},
	
	//On successful submission, the server will return the HTML represenation of
	//the layout and we can add it to the layouts list.
	onSubmissionSuccess: function(t, name) {
		if(this.editingLayout)
		{
			this.isDirty = false;
			this.element.fire(PSCreateLayoutTool.EVENT.LAYOUT_EDITED, { "oldVersion": this.editingLayout, "name": name, "html": t.responseText });
		}
		else
		{
			PageStudio.addLayout(name, t.responseText, true);
			this.element.fire(PSCreateLayoutTool.EVENT.LAYOUT_CREATED);	
		}
	},
	
	onSubmissionFailure: function(t) {
		PageStudio.displayExceptionAlert(PageStudio.locale.get("create_layout_failed"), t);
	},
	
	createTip: function(message) {
		if(!this.tip) {
			this.tip = new Element("div", { "id": "ps_duplicate_name_tip", "className": "ps_reset ps_tool_tip ps_text_tool_tip" });
			this.tip.insert(new Element("span"));
			this.inputName.insert({ "after": this.tip });
			this.tip.hide();
		}
		
		//Prevent an IE bug by clearing the tooltip and adding the text
		//as a textnode. Avoids using innerHTML.
		if(this.tip.hasChildNodes()) {
			for(var i = 0; i < this.tip.childNodes.length; i++) {
				this.tip.removeChild(this.tip.childNodes[i]);
			}
		}		
		this.tip.appendChild(document.createTextNode(message));
			
		var top = "-"+(this.tip.getHeight() + 2) + "px";
		this.tip.setStyle({ "top": top });
	},
	
	showTip: function() {
		if (this.pe) this.pe.stop();
		this.pe = new PeriodicalExecuter(function(pe) {
			this.tipVisible = true;
			this.pe.stop();
			Effect.Appear(this.tip, { "duration": "0.8", "queue": { "position": "end", "scope": "issuetip" } });
		}.bind(this), 0.5);
	},
	
	hideTip: function() {
		if(this.pe) this.pe.stop();
		if(this.tipVisible) {
			Effect.Fade(this.tip, { "duration": "0.8", "queue": { "position": "end", "scope": "issuetip" } });
			this.tipVisible = false;
		}
	},
	
	updateTip: function()
	{
		if(!this.tip) this.createTip(PageStudio.locale.get("change_layout_duplicate_name_tip"));
		!this.hasValidName && this.issues && this.issues.duplicate ? this.showTip() : this.hideTip();
	},
	
	onNameInputChange: function(e) {
		var layoutName = this.inputName.getValue();
		this.makeNameDirty(layoutName);
		this.setHasValidName(this.validateName(layoutName));
	},
	
	setHasValidName : function(valid)
	{
		//Only continue if valid state has changed.
		if(valid != this.hasValidName)
		{
			this.hasValidName = valid;
			this.updateTip();
			this.updateSaveButton();
		}
	},
	
	getPreviewCells: function()
	{
		return this.preview.select("." + PageStudio.CLASS.CELL);
	},
	
	editLayout: function(layout)
	{
		this.editingLayout = layout;
		this.setHasValidName(true);
		this.inputName.setValue(decodeURIComponent(layout.name));
		this.preview.update(decodeURIComponent(layout.html));
		
		var cells = this.getPreviewCells();
		
		this.updateLayoutPreview(cells);
		//store the innerHTML and cell order for dirtiness comparison.
		this.editingLayout.layoutCache = this.createLayoutCache(this.getPreviewCells());
	},
	
	createLayoutCache: function(cells)
	{
		var cache = new Array();
		for(var i = 0; i < cells.length; i++)
		{
			cache.push(cells[i].innerText);
		}
		return cache;
	}
});

PSCreateLayoutTool.EVENT = {
		IMPORT_CLICKED: "pagestudio:createLayoutImportClicked",
		SAVE_CLICKED: "pagestudio:createLayoutSaveClicked",
		CANCEL_SAVE_CLICKED: "pagestudio:createLayoutCancelClicked",
		LAYOUT_CREATED: "pagestudio:layoutCreated",
		LAYOUT_EDITED: "pagestudio:layoutEdited",
		ADD_CELL_CLICKED: "pagestudio:addCellClicked",
		REMOVE_CELL_CLICKED: "pagestudio:removeCellClicked"
};
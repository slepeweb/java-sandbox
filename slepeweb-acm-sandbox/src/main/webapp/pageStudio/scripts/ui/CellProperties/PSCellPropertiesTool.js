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

var PSCellPropertiesTool = Class.create(PSTool, {
	initialize: function($super) {
		$super();
		this.createListeners();
		this.attachEvents();
	},
	
	attachEvents: function() {
		Event.observe(document, PageStudio.EVENT.CELL_SELECTED, this.onCellSelected.bindAsEventListener(this));
		this.addCustomStyleButton.observe("click", this.listeners.onAddCustomClassClicked);
		this.addClassButton.observe("click", this.listeners.onAddClassClicked);
		this.removeClassButton.observe("click", this.listeners.onRemoveClassClicked);
		
		if (PageStudio.eventSupported("oninput", this.cellWidthInput)) {
			this.cellWidthInput.observe("input", this.listeners.onFormWidthChange);
		}
		else {
			this.cellWidthInput.observe("paste", this.listeners.onFormWidthChange);
			this.cellWidthInput.observe("keyup", this.listeners.onFormWidthChange);
		}
		this.widthUnitSelect.observe("change", this.listeners.onFormUnitChange);
	},
	
	createListeners: function() {
		this.listeners = {
			onFormWidthChange: this.onFormWidthChange.bindAsEventListener(this),
			onFormUnitChange: this.onFormUnitChange.bindAsEventListener(this),
			onCellWidthChange: this.onCellWidthChange.bindAsEventListener(this),
			onResizeEnd: this.onResizeEnd.bindAsEventListener(this),
			onCellRemoved: this.onCellRemoved.bindAsEventListener(this),
			onAddCustomClassClicked: this.onAddCustomClassClicked.bindAsEventListener(this),
			onAddClassClicked: this.onAddClassClicked.bindAsEventListener(this),
			onRemoveClassClicked: this.onRemoveClassClicked.bindAsEventListener(this)
		};
	},
	
	createElement: function($super) {
		$super();
		this.element.addClassName("ps_tool_cell_properties");
		this.insert(new Element("div", { "className": PSCellPropertiesTool.CLASS.DESCRIPTION }).update(PageStudio.locale.get("cell_properties_instructions")));
		this.createCellIdForm();
		this.createCellWidthForm();
		this.createTable();
		this.createClassesForm();
	},
	
	createTable: function() {
		var table_label = new Element("legend").update(PageStudio.locale.get("cell_properties_other_label"));
		var form_area = new Element("fieldset", { "className": "other_properties_fieldset" });
		
		this.insert(form_area);
		
		var form = new Element("form");
		
		form_area.insert(table_label);
		form_area.insert(form);
		
		form.insert(
			new Element("table", { "className": PSDialog.CLASS.TABLE }).insert(
				new Element("thead").insert(
					new Element("tr").insert(
						new Element("th").update(PageStudio.locale.get("Property"))
					).insert(
						new Element("th", { "colspan": "2" }).update(PageStudio.locale.get("Value"))
					)
				)
			).insert(
				new Element("tbody").insert(
					new Element("tr").insert(
						new Element("td").update(new Element("label").update(PageStudio.locale.get("id")))
					).insert(
						new Element("td", { "className": "cell_properties_value"}).update(this.cellIdInput)
					).insert(
						new Element("td", { "className": "cell_id_btn" }).insert(this.cellIdEditBtn).insert(this.cellIdSubmitBtn)
					)
				).insert(
					new Element("tr").insert(
						new Element("td").update(new Element("label").update(PageStudio.locale.get("Width")))
					).insert(
						new Element("td", { "className": "cell_properties_value" }).update(this.cellWidthInput)
					).insert(
						new Element("td").update(this.widthUnitSelect)
					)
				)
			)
		);
	},
	
	createStyleList: function(parentEl, labelLocale)
	{
		var container = new Element("div", { "className": "style_list_container" });
		var labelEl = new Element("label", { "className": "list_label" }).update(PageStudio.locale.get(labelLocale));
		var list = new Element("select", { "className": "style_list", "multiple": true, "size": 8 });
		parentEl.insert(container.insert(labelEl).insert(list));
		return list;
	},
	
	createClassesForm: function() {
		var classes_label = new Element("legend").update(PageStudio.locale.get("cell_properties_classes_label"));
		var form_area = new Element("fieldset", { "className": "cell_class_fieldset" });
		
		form_area.insert(classes_label);
		this.insert(form_area);
		
		this.addClassButton = new Element("button", {"type": "button"}).update("&#8594;");
		this.removeClassButton = new Element("button", {"type": "button"}).update("&#8592;");
		
		var customStylesContainer = new Element("div", { "className": "ps_custom_styles_container" });
		this.customClassInput = new Element("input");
		this.addCustomStyleButton = new Element("button", {"type": "button"}).update(PageStudio.locale.get("cell_properties_classes_add"));
		
		var buttonContainer = new Element("div", { "className": "classes_buttons_container" });
		
		buttonContainer.insert(this.addClassButton).insert(this.removeClassButton);

		this.availableStyleList = this.createStyleList(form_area, "cell_properties_available_classes");
		form_area.insert(buttonContainer);
		this.selectedStyleList = this.createStyleList(form_area, "cell_properties_selected_classes");
		customStylesContainer.insert(this.customClassInput).insert(this.addCustomStyleButton);
		form_area.insert(customStylesContainer);
		
		//Preview option
		var previewOptionArea = new Element("div", { "className": "ps_preview_styles_option_container"});
		form_area.insert(previewOptionArea);
		var id = "ps_cell_properties_preview_styles_checkbox";
		this.previewCheckBox = new Element("input", { "id": id, "type": "checkbox" });
		
		this.previewCheckBox.checked = PageStudio.getPreviewCellStyles();
		
		previewOptionArea.insert(this.previewCheckBox);
		previewOptionArea.insert(new Element("label", { "for": id }).update(PageStudio.locale.get("cell_properties_preview_option_label")));
		this.previewCheckBox.observe("click", function(e) {
			PageStudio.setPreviewCellStyles((this.previewCheckBox.checked));
		}.bindAsEventListener(this));
		
		var params = PageStudio.getRequestItemParams({
			action: PageStudio.ACTION.GET_STYLE_LIST
		});
		
		new PageStudio.Request(PageStudio.URL.LAYOUT_DESIGNER, {
			method: "get", parameters: params, 
			onSuccess: this.populateStyleList.bindAsEventListener(this), 
			onFailure: this.onFailure.bindAsEventListener(this),
			onComplete: this.onComplete.bindAsEventListener(this)
		});
	},
	
	createCellIdForm: function() {
		this.cellIdInput = new Element("input", { "className": "cell_id_input" });
		this.cellIdEditBtn =
			new Element("button", { "type": "button", "className": "cell_id_button" }).update(PageStudio.locale.get("cell_properties_edit"));
		this.cellIdSubmitBtn =
			new Element("button", { "type": "button", "className": "cell_id_button" }).update(PageStudio.locale.get("cell_properties_apply"));
		
		PSUnselectableHelper.makeSelectable(this.cellIdInput);
		
		var edit = function() {
			this.cellIdSubmitBtn.removeAttribute("disabled");
			this.cellIdSubmitBtn.show();
			
			this.cellIdEditBtn.hide();
			
			this.cellIdInput.enable();
			this.cellIdInput.select();
		}.bind(this);
		
		this.resetCellIdForm = function() {
			this.cellIdInput.setValue(this.cell ? this.cell.element.id : "");
			this.cellIdInput.disable();
			
			this.cellIdSubmitBtn.removeAttribute("disabled");
			this.cellIdSubmitBtn.hide();
			
			this.cellIdEditBtn.removeAttribute("disabled");
			this.cellIdEditBtn.show();
		}.bind(this);
		
		var submit = function() {
			this.cellIdInput.disable();
			this.cellIdSubmitBtn.writeAttribute("disabled", true);
			
			var value = this.cellIdInput.getValue();
			if (value == "") {
				edit();
				alert(PageStudio.locale.get("cell_properties_id_null"));
			}
			else if ($(value) != null && $(value) != this.cell.element) {
				edit();
				alert(PageStudio.locale.get("cell_properties_id_invalid"));
			}
			else {
				this.cell.element.id = value;
				this.resetCellIdForm();
			}
		}.bind(this);
		
		var onEditBtnClicked = function(e) {
			e.returnValue = false;
			e.stop();
			
			edit();
			
			return false;
		};
		
		var onSubmitBtnClicked = function(e) {
			e.returnValue = false;
			e.stop();
			
			submit();
			
			return false;
		};
		
		this.cellIdEditBtn.observe("click", onEditBtnClicked);
		this.cellIdSubmitBtn.observe("click", onSubmitBtnClicked);
	},
	
	createCellWidthForm: function() {
		this.cellWidthInput = new Element("input", { className: "cell_width_input" });
		PSUnselectableHelper.makeSelectable(this.cellWidthInput);
		this.widthUnitSelect = new Element("select", { className: "cell_width_unit_select" }).insert(
			new Element("option", { value: "%" }).update("%")
		).insert(
			new Element("option", { value: "px" }).update("px")
		);
		//add numeric key check
		var validateNumericKeyPress = function(e) {
			var code = e.charCode ? e.charCode : e.keyCode;
			//checks for undo/redo shortcuts
			if (!(code >= 48 && code <= 57)
				&& !(code >= 96 && code <= 105)
				&& !(code == Event.KEY_RIGHT || code == Event.KEY_LEFT
					|| code == Event.KEY_BACKSPACE || code == Event.KEY_DELETE
					|| code == Event.KEY_HOME || code == Event.KEY_END))
			{
				e.stop();
			}
		};
		this.cellWidthInput.observe("keydown", validateNumericKeyPress);
		this.cellWidthInput.observe("keypress", validateNumericKeyPress);
	},
	
	onFailure: function(t) {
		PageStudio.displayExceptionAlert(PageStudio.locale.get("ps_load_styles_exception"), t);
	},
	
	onComplete: function(e) {
		//Retrieve all the available classes from the cells currently on the page.
		$$("." + PageStudio.CLASS.CELL).each(this.addClassesFromCell, this);
	},
	
	populateStyleList: function(t) {
		$H(t.responseJSON).each(this.addStyle, this);
	},
	
	addStyleToList: function(list, style) {
		var styleStr = style.value ? style.value.style : "";
		list.insert(
			new Element("option", { "style": unescape(styleStr), "label": unescape(style.key), "value": style.key }).update(style.key)
		);
	},
	
	applyClassToCell: function(className) {
		this.cell.addClassName(className);
	},
	
	removeClassFromCell: function(className) {
		this.cell.removeClassName(className);
	},
	
	moveStyle: function(option, listTo) {
		listTo.insert(option);
	},
	
	moveSelectedStyles: function(listFrom, listTo) {
		var selected = new Array();
		for (var i = 0; i < listFrom.options.length; i++)
		{
			if(listFrom.options[i].selected)
			{
				selected.push(listFrom.options[i]);
			}
		}
		for (var i = 0; i < listTo.options.length; i++)
		{
			listTo.options[i].selected = false;
		}
		for (var i = 0; i < selected.length; i++)
		{
			this.moveStyle(selected[i], listTo);
		}
		return selected;
	},
	
	addClassesFromCell: function(cell) {
		var classes = PageStudio.stripPageStudioClasses(cell.className);
		for(var i = 0; i < classes.length; i++)
		{
			this.addStyle({ "key": classes[i], "value": "" });
		}
	},
	
	addStyle: function(style, selected) {
		//filter out element, id or multiple class selectors
		if (style.key.indexOf("#") == -1 && style.key.indexOf(".") == -1 && style.key != "")
		{
			if(this.listIndexOf(this.selectedStyleList, style.key) == -1)
			{
				if(selected)
				{
					//if the style already exists in the availabel styles list, move it.
					var index = this.listIndexOf(this.availableStyleList, style.key);
					if(index != -1)
					{
						this.moveStyle(this.availableStyleList.options[index], this.selectedStyleList);
					}
					else {
						this.addStyleToList(this.selectedStyleList, style);
					}
				}
				else if (this.listIndexOf(this.availableStyleList, style.key) == -1)
				{
					this.addStyleToList(this.availableStyleList, style);
				}
			}
		}
	},
	
	update: function(cell) {
		if (this.cell != cell) {
			if (this.cell) {
				this.cell.element.stopObserving(PageStudio.EVENT.RESIZE, this.listeners.onCellWidthChange);
				this.cell.element.stopObserving(PageStudio.EVENT.RESIZE_END, this.listeners.onResizeEnd);
				this.cell.element.stopObserving(PageStudio.EVENT.REMOVED, this.listeners.onCellRemoved);
			}
			this.cell = cell;
			this.cell.element.observe(PageStudio.EVENT.RESIZE, this.listeners.onCellWidthChange);
			this.cell.element.observe(PageStudio.EVENT.RESIZE_END, this.listeners.onResizeEnd);
			this.cell.element.observe(PageStudio.EVENT.REMOVED, this.listeners.onCellRemoved);
			this.updateFormStyleSelect();
			this.updateFormUnitSelect();
			this.updateFormWidth();
			this.resetCellIdForm();
		}
	},
	
	updateCellWidth: function(undoable) {
		if (this.cell) {
			var oldWidth = this.cell.element.getStyle("width");
			var units = $F(this.widthUnitSelect);
			var width = null;
			
			if (units == "px") width = parseInt($F(this.cellWidthInput));
			else width = parseFloat($F(this.cellWidthInput));
			
			if(!isNaN(width)){
				if (width != "" ) width = width + units;
				else width = 100 + units;
				
				if (width != oldWidth) this.cell.element.setStyle({ "width": width });
			}
		}
	},
	
	checkOptions: function(list, valid) {
		var invalid = new Array();
		for (var i = 0; i < list.options.length; i++) {
			var option = list.options[i];
			if(this.cell.hasClassName(option.value) == valid)
			{
				invalid.push(option);
			}
		}
		return invalid;
	},
	
	updateFormStyleSelect: function() {
		var available = this.checkOptions(this.selectedStyleList, false);
		var selected = this.checkOptions(this.availableStyleList, true);
		
		for(var i = 0; i < available.length; i++)
		{
			this.moveStyle(available[i], this.availableStyleList);
		}
		
		for(var i = 0; i < selected.length; i++)
		{
			this.moveStyle(selected[i], this.selectedStyleList);
		}
	},
	
	updateFormUnitSelect: function() {
		if (this.cell) {
			if (this.cell.element.getStyle("width").indexOf("px") != -1) {
				this.widthUnitSelect.selectedIndex = 1;
			}
			else {
				this.widthUnitSelect.selectedIndex = 0;
			}
		}
	},
	
	updateFormWidth: function() {
		var width = parseInt(this.cell.element.getStyle("width"));
		this.cellWidthInput.value = width;
	},
	
	onCellSelected: function(e) {
		this.update(e.memo.object);
	},
	
	onResizeEnd: function(e) {
		this.updateCellWidth();
	},
	
	onFormWidthChange: function(e) {
		this.updateCellWidth(true);
	},
	
	onCellWidthChange: function(e) {
		this.convertWidth();
		this.updateFormWidth();
		
	},
	
	onFormUnitChange: function(e) {
		this.convertWidth();
	},
	
	onCellRemoved: function(e) {
		this.close();
	},
	
	onAddCustomClassClicked: function(e) {
		var className = this.customClassInput.getValue();
		var style = { "key": className, "style": "" };
		this.addStyle(style, true);
		this.applyClassToCell(className);
		this.customClassInput.clear();
	},
	
	onAddClassClicked: function(e) {
		var added = this.moveSelectedStyles(this.availableStyleList, this.selectedStyleList);
		for(var i = 0; i < added.length; i++)
		{
			this.applyClassToCell(added[i].value);
		}
	},
	
	onRemoveClassClicked: function(e) {
		var removed = this.moveSelectedStyles(this.selectedStyleList, this.availableStyleList);
		for(var i = 0; i < removed.length; i++)
		{
			this.removeClassFromCell(removed[i].value);
		}
	},
	
	listIndexOf: function(list, value) {
		for (var i = 0; i < list.options.length; i++)
		{
			if(list.options[i].value == value) return i;
		}
		return -1;
	},
	
	//switches the selected cell width between a fixed width value and dynamic percentage
	//calculates the new width value that will appear as similar as possibly to the original width
	convertWidth: function() {
		if (this.cell) {
			var width;
			var element = this.cell.element;
			if (($F(this.widthUnitSelect) == "px")) {
				//deduct the borders from the total width
				var borderWidth = parseInt(element.getStyle("borderLeftWidth")) + parseInt(element.getStyle("borderRightWidth"));
				width = (element.getWidth()-borderWidth) + "px";
			}
			else {
				width = Math.round((element.getWidth() / element.up().getWidth()) * 100) + "%";
			}
			element.setStyle({ width: width });
		}
		this.updateFormWidth();
	},
	
	doAfterDialogOpened: function() {
		this.updateFormUnitSelect();
		//IE doesn't like setting the checkbox value when the checkbox itself is hidden
		//so we set it after the dialog is opened.
		this.previewCheckBox.checked = PageStudio.getPreviewCellStyles();
	}
});


PSCellPropertiesTool.CLASS = {
	DESCRIPTION: "ps_cell_properties_description"
};

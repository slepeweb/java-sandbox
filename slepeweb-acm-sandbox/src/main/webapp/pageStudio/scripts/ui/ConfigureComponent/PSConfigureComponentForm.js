var PSConfigureComponentForm = Class.create({
	initialize: function(json, component, options) {
		this.rows = new Hash();
		this.properties = $H(json);
		this.component = component;
		options = options ? options : {};
		this.isMarkupComponent = (options.markup);
		this.createElement(options);
	},
	
	createElement: function(options) {
		//create the properties table including thead and tbody
		this.body = new Element("tbody");
		this.element = new Element("form", { "className": "ps_config_form_body" });
		var table = new Element("table", { "className": PSDialog.CLASS.TABLE }).insert(
			new Element("thead").insert(
				new Element("tr").insert(
					new Element("th").update(PageStudio.locale.get("Property"))
				).insert(
					new Element("th").update(PageStudio.locale.get("Value"))
				)
			)
		).insert(this.body);
		
		if(options.fieldset) {
			var fieldSet = new Element("fieldset", { "className": PSDialog.CLASS.TABLE_FIELDSET }).update(new Element("legend").update(options.fieldset));
			this.element.insert(fieldSet.insert(table));
		}
		else {
			this.element.insert(table);
		}
		this.properties.values().each(this.addProperty, this);
	},
	
	//addProperty function creates a new row in the properties table containing the property form field
	addProperty: function(property) {
		var tr = new Element("tr");
		var td = new Element("td");
		var propertyLabel = new Element("label", { "className": "label", "for": "ps_cf_"+property.name }).update(property.name.capitalize());
		var propertyInput = null;
		if (property.type == "dsv") {
			propertyInput = this.createDSVField(property);
		}
		else if (property.type == "singlevaluelist" || property.type == "list") {
			propertyInput = this.createListField(property);
		}
		else if(property.type == "itemKey") {
			propertyInput = this.createItemKeyField(property);
		}
		else if(property.type == "itemKeyList") {
			propertyInput = this.createItemKeyListField(property);
		}
		else {
			propertyInput = this.createInputField(property);
		}
		
		tr.insert(new Element("td").update(propertyLabel));
		tr.insert(new Element("td").update(propertyInput));
		
		this.body.insert(tr);
		
		this.rows.set(property.name, { "label": propertyLabel, "input": propertyInput, "property": property });
	},
	
	createInputField: function(property) {
		// If no values are set, assume this is a new instance and display the default values defined.
		var noValuesSet = this.properties.values().all(function (n) { return n.value.length == 0; });
		var value = (noValuesSet) ? property.defaultValue : property.value;
		var params = { "className": property.type, "id": "ps_cf_" + property.name, "name": property.name, "value": value };

		if (property.type == "password") {
			params.type = "password";
		}
		var propertyInput = new Element("input", params);
		
		if (property.type == "numeric") {
			var validateNumericKeyPress = function(e) {
				var code = e.charCode ? e.charCode : e.keyCode;
				if (code > 106 || e.shiftKey || e.ctrlKey || (code > 64 && code < 91)) {
					e.stop();
				}
			};
			
			propertyInput.observe("keydown", validateNumericKeyPress);
			propertyInput.observe("keypress", validateNumericKeyPress);
			propertyInput.observe("keydown", validateNumericKeyPress);
		}
		
		return propertyInput;
	},
	
	createListField: function(property) {
		var propertyInput = new Element("select", { "className": property.type, "id": "ps_cf_"+property.name, "name": property.name });
		
		var range = property.range.split("|");
		var values;
		
		if (property.type == "list") {
			propertyInput.writeAttribute("multiple", "multiple");
			values = property.value ? property.value.split("|") : property.defaultValue.split("|");
		}
		else {
			values = new Array();
			values.push(property.value ? property.value : property.defaultValue);
		}
		
		for (var i = 0; i < range.length; i++) {
			var item = range[i];
			var option = new Element("option", { "value": item }).update(item);
			if (values.member(item)) {
				option.writeAttribute("selected", "selected");
			}
			propertyInput.insert(option);
		}
		
		return propertyInput;
	},
	
	createDSVField: function(property) {
		var field = new PSDSVField(property);
		return field.element;
	},
	
	createItemKeyField: function(property) {
		var field = new PSItemKeyField(property);
		return field.element;
	},
	
	createItemKeyListField: function(property) {
		var field = new PSItemKeyListField(property);
		return field.element;
	},
	
	highlightInvalidProperties: function(invalidProperties) {
		this.rows.values().each(function(row) {
			row.input.removeClassName(PageStudio.CLASS.INVALID);
			row.label.update().insert(row.property.name.capitalize());
		});
		for (var i = 0; i < invalidProperties.length; i++) {
			var row = this.rows.get(invalidProperties[i]);
			row.input.addClassName(PageStudio.CLASS.INVALID);
			
			var name = row.property.name.capitalize() + "*";
			row.label.update().insert(name);
		}
		alert(PageStudio.locale.get("invalid_configuration"));
	},
	
	submit: function(cInstId, success, failure) {
		var params = PageStudio.getRequestItemParams(this.element.serialize().toQueryParams());
		
		Object.extend(params, {
			"action": PageStudio.ACTION.GET_COMPONENT,
			"configuration": PageStudio.ACTION.SAVE_COMPONENT_CONFIGURATION,
			"cid": this.component.id,
			"cInstId": cInstId,
			"markup": this.isMarkupComponent
		});
		new PageStudio.Request(PageStudio.URL.COMPONENT_FACTORY, { 
			method: "post",
			parameters: params,
			evalScripts: true,
			onSuccess: this.onSubmissionSuccess.bindAsEventListener(this, success),
			onFailure: this.onSubmissionFailure.bindAsEventListener(this, failure)
		});
	},
	
	onSubmissionSuccess: function(t, callback) {
		callback(t);
	},
	
	onSubmissionFailure: function(t, callback) {
		if(t.responseJSON && t.responseJSON.invalidProperties) {
			this.highlightInvalidProperties($A(t.responseJSON.invalidProperties));
		}
		else {
			callback(t);
		}
	},
	
	remove: function() {
		this.rows.each(function(row) {
			row.value.input.stopObserving();
		}, this);
		this.element.stopObserving();
		this.element.remove();
	},
	
	toQueryString: function() {
		return Object.toQueryString(Form.serialize(this.element, { "hash": true }));
	}
});

var PSItemKeyField = Class.create({
	initialize: function(property) {
		this.element = new Element("table", { "className": "ps_item_key_field" });
		var tbody = new Element("tbody");
		var tr = new Element("tr");
		var col1 = new Element("td");
		var col2 = new Element("td", { "style": "width: 45px;" });
		this.element.insert(tbody.insert(tr.insert(col1).insert(col2)));
		var input = new Element("input", { "readonly": true });
		if(property.metadata.name) {
			var name = unescape(property.metadata.name);
			PSToolTipHelper.add(input, name);
			input.setValue(name);
		}
		var valueInput = new Element("input", { "type": "hidden", "name": property.name, "value": property.metadata.originalKey } );
		var button = new Element("button").update(PageStudio.locale.get("browse"));
		button.observe("click", function(e) {
			e.stop();
			PageStudio.openItemPicker(property.accept, function(itemData) {
				if(itemData && itemData.length > 0) {
					var item = itemData[0];
					var name = unescape(item.name);
					input.setValue(name);
					PSToolTipHelper.add(input, name);
					valueInput.setValue(item.originalkey);
				}
			});
		});
		col1.insert(input);
		col1.insert(valueInput);
		col2.insert(button);
	}
});

var PSItemKeyListField = Class.create({
	initialize: function(property) {
		this.element = new Element("table", { "className": "ps_item_key_list_field" });
		var tbody = new Element("tbody");
		var tr1 = new Element("tr");
		var tr2 = new Element("tr");
		var col1 = new Element("td");
		var col2 = new Element("td");
		this.element.insert(tbody.insert(tr1.insert(col1)));
		this.element.insert(tbody.insert(tr2.insert(col2)));
		var input = new Element("select", { "multiple": true, "readonly": true });
		
		var values = new Hash();

		var addValue = function(fullName, key) {
			var val = values.get(key);
			if (val) return;
			var valueInput = new Element("input", { "type": "hidden", "name": property.name, "value": key } );
			var valueOption = new Element("option", { "label": fullName, "value": key });
			values.set(key, valueInput);
			valueOption.store("input", valueInput);
			input.insert(valueOption);

			// IE workaround to ensure that new option text is shown.
			valueOption.update().insert(fullName);

			col1.insert(valueInput); 
		};
		
		function getSelectedItems()
		{
			var selected = new Array();
			for (var i = 0; i < input.options.length; i++) {
				var opt = input.options[i];
				if(opt.selected) {
					selected.push(opt);
				}
			}
			return selected;
		}
		
		var removeSelectedValues = function() {
			getSelectedItems().each(function(opt) {
				values.unset(opt.readAttribute("value"));
				opt.retrieve("input").remove();
				opt.remove();
			});
			removeBtn.disabled = true;
		};
		
		if(property.metadata.names) {
			for(var i = 0; i < property.metadata.names.length; i++) {
				addValue(unescape(property.metadata.names[i]), property.metadata.originalKeys[i]);
			}
		}
		
		var addBtn = new Element("button").update(PageStudio.locale.get("add"));
		addBtn.observe("click", function(e) {
			e.stop();
			PageStudio.openItemPicker(property.accept, function(itemData) {
				if(itemData) {
					for (var i = 0; i < itemData.length; i++) {
						var item = itemData[i];
						var name = unescape(item.name);
						addValue(name, item.originalkey);
					}
				}
			});
		});
		
		var removeBtn = new Element("button").update(PageStudio.locale.get("remove"));
		removeBtn.disabled = true;
		
		removeBtn.observe("click", function(e) {
			e.stop();
			removeSelectedValues();
		});
		
		input.observe('change', function(e){
			if(getSelectedItems().length == 0) {
				removeBtn.disabled = true;
			}
			else {
				removeBtn.disabled = false;
			}
		}.bindAsEventListener(this));
		
		col1.insert(input);
		col2.insert(addBtn);
		col2.insert(removeBtn);
	}
});

var PSDSVField = Class.create({
	initialize: function(property) {
		this.createElement(property);
		this.attachEvents();
	},
	
	createElement: function(property) {
		var params = { "type": "hidden", "className": property.type, "id": "ps_cf_"+property.name, "name": property.name, "value": property.value };	
		this.formValue = new Element("input", params);
		this.element = new Element("div");
		this.input = new Element("input");
		this.selectID = "ps_cf_select_"+property.name;
		this.display = new Element("select", { "id": this.selectID, "className": "ps_cf_dsv_select", "multiple": "multiple" });
		
		this.values = property.value ? property.value.split("|") : property.defaultValue.split("|");		
		
		for (var i = 0; i < this.values.length; i++) {
			var value = this.values[i];
			this.addOption(value);
		}
		this.formValue.setValue(this.values.join("|"));
		
		this.addButton = new Element("button", { "disabled": "true" }).update(PageStudio.locale.get("Add"));
		this.removeButton = new Element("button", { "disabled": "true" }).update(PageStudio.locale.get("Remove"));
		this.upButton = new Element("button", { "className": "ps_cf_move_btn ps_cf_up_btn", "disabled": "true" }).update("&uarr;");
		this.downButton = new Element("button", { "className": "ps_cf_move_btn ps_cf_down_btn", "disabled": "true" }).update("&darr;");
		
		var table = new Element("table").insert(
			new Element("tbody").insert(
				new Element("tr").insert(
					new Element("td", { "colspan": "4", "rowspan": "2"}).insert(this.display)
				).insert(
					new Element("td").insert(this.upButton)
				)
			).insert(
				new Element("tr").insert(
					new Element("td").insert(this.downButton)
				)
			).insert(
				new Element("tr").insert(
					new Element("td").insert(this.input)
				).insert(
					new Element("td").insert(this.addButton)
				).insert(
					new Element("td", { "colspan": "2" }).insert(this.removeButton)
				)
			)
		);
		
		this.element.insert(this.formValue);
		this.element.insert(table);
	},
	
	attachEvents: function() {
		this.addButton.observe("click", this.onAddClicked.bindAsEventListener(this));
		this.removeButton.observe("click", this.onRemoveClicked.bindAsEventListener(this));
		
		this.display.observe("change", this.onSelectionChange.bindAsEventListener(this));
		this.display.observe("focus", this.onSelectionFocus.bindAsEventListener(this));
		this.display.observe("blur", this.onSelectionBlur.bindAsEventListener(this));
		
		this.upButton.observe("click", this.onUpClick.bindAsEventListener(this));
		this.downButton.observe("click", this.onDownClick.bindAsEventListener(this));
		
		if (PageStudio.eventSupported("oninput", "div")) {
			this.input.observe("input", this.onInputChange.bindAsEventListener(this));
		}
		else {
			this.input.observe("paste", this.onInputChange.bindAsEventListener(this));
			this.input.observe("keyup", this.onInputChange.bindAsEventListener(this));
		}
	},
	
	addOption: function(value, index) {
		var option = new Element("option", { "value": value }).update(decodeURIComponent(value));
		if (index == null || index == -1) {
			this.display.insert(option);
		}
		else {
			this.display.options[index].insert({ "before": option });
		}
		
	},
	
	addValue: function(value, index) {
		value = encodeURIComponent(value);
		this.addOption(value, index);
		this.values = this.display.select("option").pluck("value");
		this.formValue.setValue(this.values.join("|"));
	},
	
	removeSelectedOptions: function() {
		if (this.display.selectedIndex != "undefined") {
			this.values = new Array();
			var selected = new Array();
			for (var i = 0; i < this.display.options.length; i++) {
				var option = this.display.options[i];
				if(option.selected) {
					selected.push(option);
				}
				else {
					this.values.push(option.value);
				}
			}
			selected.invoke("remove");
			this.formValue.setValue(this.values.join("|"));
		}
		this.removeButton.disabled = true;
		this.setMoveButtonState();
	},
	
	setMoveButtonState: function() {
		this.removeButton.disabled = (this.display.selectedIndex == -1);
		this.upButton.disabled = (this.display.selectedIndex < 1);
		this.downButton.disabled = (this.display.selectedIndex == -1 || $A(this.display.options).last().selected);
	},
	
	moveSelection: function(up) {
		var options = this.display.options;
		if (!up) {
			options = $A(options).reverse();
		}
		for (var i = 0; i < options.length; i++) {
			var option = options[i];
			if (option.selected) {
				if (up) {
					var previous = option.previous();
					if (previous) {
						previous.insert({"before": option });
					}
				}
				else {
					var next = option.next();
					if(next) {
						next.insert({ "after": option });
					}
				}
			}
		}
		
		this.values = this.display.select("option").pluck("value");
		this.formValue.setValue(this.values.join("|"));
		this.setMoveButtonState();
	},
	
	submitValue: function() {
		var input = this.input.getValue();
		if (!input.empty()) {
			this.input.clear();
			var newValues = input.split("|");
			for (var i = 0; i < newValues.length; i++) {
				this.addValue(newValues[i], this.display.selectedIndex);
			}
		}
		this.addButton.disabled = true;
		this.setMoveButtonState();
	},
	
	onAddClicked: function(e) {
		e.stop();
		this.submitValue();
	},
	
	onRemoveClicked: function(e) {
		e.stop();
		this.removeSelectedOptions();
	},
	
	onInputChange: function(e) {
		if (e.keyCode == Event.KEY_RETURN) {
			e.stop();
			this.submitValue();
		}
		else {
			if (this.input.getValue().length != 0) {
				this.addButton.disabled = false;
			}
			else {
				this.addButton.disabled = true;
			}
		}
	},
	
	onSelectionChange: function(e) {
		this.setMoveButtonState();
	},
	
	onSelectionFocus: function(e) {
		this.display.observe("keydown", this.onSelectionKeydown.bindAsEventListener(this));
	},
	
	onSelectionBlur: function(e) {
		this.display.stopObserving("keydown");
	},
	
	onSelectionKeydown: function(e) {
		//select all shortcut
		if (e.keyCode == "65" && e.ctrlKey == true) {
			var options = this.display.options;
			if (options.length > 0) {
				for (var i = 0; i < options.length; i++) {
					options[i].selected = true;
				}
				this.setMoveButtonState();
				e.stop();
			}
		}
		else if(e.keyCode == Event.KEY_DELETE || e.keyCode == Event.KEY_BACKSPACE) {
			e.stop();
			this.removeSelectedOptions();
		}
	},
	
	onUpClick: function(e) {
		e.stop();
		this.moveSelection(true);
	},
	
	onDownClick: function(e) {
		e.stop();
		this.moveSelection(false);
	}
});

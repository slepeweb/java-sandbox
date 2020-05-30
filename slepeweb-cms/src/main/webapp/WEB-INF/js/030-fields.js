_cms.field = {
	behaviour: {},
	support: {},
	refresh: {},
	sel: {
		ALL_INPUTS: "#field-tab input",
		ALL_SELECTS: "#field-tab select",
		UPDATE_BUTTON: "#field-button",
		LANGUAGE_SELECT: "#field-language-selector select",
	}
};

_cms.field.behaviour.update = function(nodeKey) {	
	// Add behaviour to submit item field updates 
	$(_cms.field.sel.UPDATE_BUTTON).click(function () {
		var theDialog = $("#dialog-fields-confirm");
		theDialog.dialog({
			resizable: false,
			height:200,
			modal: true,
			buttons: {
				"Update field values": function() {
					$.ajax(_cms.ctx + "/rest/item/" + nodeKey + "/update/fields", {
						type: "POST",
						cache: false,
						data: _cms.field.getFieldsFormInputData(), 
						dataType: "json",
						success: function(obj, status, z) {
							theDialog.dialog("close");
							_cms.support.flashMessage(obj);
							_cms.field.refresh.tab(nodeKey);
						},
						error: function(obj, status, z) {
							theDialog.dialog("close");
							_cms.support.serverError();
						},
					});
				},
				Cancel: function() {
					$(this).dialog("close");
				}
			}
		});
	});
}

//Get form field names and values for forms on item-editor 
_cms.field.getFieldsFormInputData = function() {
	var result = {};
	var language = $(_cms.field.sel.LANGUAGE_SELECT).val();
	if (! language) {
		language = _siteDefaultLanguage;
	}
	
	result["language"] = language;
	var selector = "#form-fields-lang input, #form-fields-lang textarea, #form-fields-lang select";
	selector = selector.replace(/lang/g, language);
	
	$(selector).each(function(i, obj) {
		var ctrl = $(obj);
		var type = ctrl.attr("type");
		var param = ctrl.attr("name");
		var str;
		if (type == "radio") {
			if (ctrl.is(':checked')) {
				result[param] = ctrl.val();
			}
		}
		else if (type == "checkbox") {
			if (ctrl.is(':checked')) {
				str = result[param];
				if (! str) {
					str = "";
				}
				if (str.length > 0) {
					str += "|";
				}
				str += ctrl.val();
				result[param] = str;
			}
		}
		else {
			result[param] = ctrl.val();
		}
	});
	return result;
}

_cms.field.toggleFieldDivs = function(lang) {
	$(".hideable").each(function() {
		var ele = $(this);
		if (ele.attr("id").endsWith(lang)) {
			ele.show();
		}
		else {
			ele.hide();
		}
	});
}

_cms.field.setlanguage = function() {
	var language = localStorage.getItem("language");
	if (! language) {
		language = _cms.siteDefaultLanguage;
	}

	$(_cms.field.sel.LANGUAGE_SELECT).val(language);
	_cms.field.toggleFieldDivs(language);
}
	
_cms.field.behaviour.changelanguage = function() {
	$(_cms.field.sel.LANGUAGE_SELECT).change(function(){
		var lang = $(this).val();
		localStorage.setItem("language", lang);
		_cms.field.toggleFieldDivs(lang);
	});
}

_cms.field.behaviour.formchange = function() {
	$(_cms.field.sel.ALL_INPUTS + "," + _cms.field.sel.ALL_SELECTS).change(function() {
		if ($(this).attr("name") != "language") {
			_cms.support.enable(_cms.field.sel.UPDATE_BUTTON);
		}
	});
}

_cms.field.behaviour.all = function(nodeKey) {
	_cms.field.behaviour.update(nodeKey);
	_cms.field.behaviour.changelanguage();
	_cms.field.behaviour.formchange();
	
	// Not really a behaviour, but required after the tab has been refreshed
	_cms.field.setlanguage();
}

_cms.field.refresh.tab = function(nodeKey) {
	_cms.support.refreshtab("field", nodeKey, _cms.field.behaviour.all);
};

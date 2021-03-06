_cms.field = {
	behaviour: {},
	support: {},
	refresh: {},
	sel: {
		FIELD_TAB: "#field-tab",
		LANGUAGE_SELECT: "#field-language-selector select",
	}
};

_cms.field.sel.FORM = "".concat(_cms.field.sel.FIELD_TAB, " form");
_cms.field.sel.ALL_FORM_ELEMENTS = "".concat(_cms.field.sel.FORM, " :input");
_cms.field.sel.UPDATE_BUTTON = _cms.field.sel.FIELD_TAB + " button.action",
_cms.field.sel.RESET_BUTTON = _cms.field.sel.FIELD_TAB + " button.reset",

_cms.support.setTabIds(_cms.field, "field");

_cms.field.behaviour.update = function(nodeKey) {	
	// Add behaviour to submit item field updates 
	$(_cms.field.sel.UPDATE_BUTTON).click(function () {
		//_cms.dialog.open(_cms.dialog.confirmFieldUpdate);
		_cms.field.update(nodeKey)
	});
}

_cms.field.update = function(nodeKey) {
	$.ajax(_cms.ctx + "/rest/item/" + nodeKey + "/update/fields", {
		type: "POST",
		cache: false,
		data: _cms.field.getFieldsFormInputData(), 
		dataType: "json",
		success: function(obj, status, z) {
			_cms.dialog.close(_cms.dialog.confirmFieldUpdate);
			_cms.support.flashMessage(obj);
			_cms.field.refresh.tab(nodeKey);
		},
		error: function(obj, status, z) {
			_cms.dialog.close(_cms.dialog.confirmFieldUpdate);
			_cms.support.serverError();
		},
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

_cms.field.behaviour.cancel = function(nodeKey) {
	// Add behaviour to cancel update.
	$(_cms.field.sel.RESET_BUTTON).click(function (e) {
		_cms.support.resetForm(_cms.field.refresh.tab, nodeKey, e);
	});
}

_cms.field.behaviour.formchange = function() {
	if (_cms.editingItemIsWriteable) {
		$(_cms.field.sel.ALL_FORM_ELEMENTS).mouseleave(function() {
			if ($(this).attr("name") != "language") {
				if (_cms.support.enableIf(_cms.field.sel.UPDATE_BUTTON, 
						_cms.field.originalFormState != $(_cms.field.sel.FORM).serialize())) {
					
					_cms.support.enable(_cms.field.sel.RESET_BUTTON);
				}
				else {
					_cms.support.disable(_cms.field.sel.RESET_BUTTON);
				}
			}
		});
	}
}

_cms.field.onrefresh = function(nodeKey) {
	_cms.field.behaviour.update(nodeKey);
	_cms.field.behaviour.cancel(nodeKey);
	_cms.field.behaviour.changelanguage();
	_cms.field.behaviour.formchange();
	
	// Not really a behaviour, but required after the tab has been refreshed
	_cms.field.setlanguage();
	
	$(_cms.field.sel.FIELD_TAB + " .datepicker").datepicker({
		dateFormat: "dd/mm/yy",
		changeMonth: true,
		changeYear: true
	});
	
	_cms.field.originalFormState = $(_cms.field.sel.FORM).serialize();
}

_cms.field.refresh.tab = function(nodeKey) {
	_cms.support.refreshtab("field", nodeKey, _cms.field.onrefresh);
};

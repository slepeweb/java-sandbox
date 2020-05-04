_cms.field = {
	behaviour: {},
	support: {},
	refresh: {},
};

_cms.field.behaviour.update = function(nodeKey) {	
	// Add behaviour to submit item field updates 
	$("#field-button").click(function () {
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
	var language = $("#field-language-selector select").val();
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

	$("#field-language-selector select").val(language);
	_cms.field.toggleFieldDivs(language);
}
	
_cms.field.behaviour.changelanguage = function() {
	$("#field-language-selector select").change(function(){
		var lang = $(this).val();
		localStorage.setItem("language", lang);
		_cms.field.toggleFieldDivs(lang);
	});
}

_cms.field.behaviour.all = function(nodeKey) {
	_cms.field.behaviour.update(nodeKey);
	_cms.field.behaviour.changelanguage();
	
	// Not really a behaviour, but required after the tab has been refreshed
	_cms.field.setlanguage();
}

_cms.field.refresh.tab = function(nodeKey) {
	_cms.support.refreshtab("field", nodeKey, _cms.field.behaviour.all);
};

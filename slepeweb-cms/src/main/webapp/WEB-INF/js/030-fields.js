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
		var formData = _cms.field.getFieldsFormInputData();
		var error = false;
		$.each(formData, function(name, value) {
			if (value == "VALIDATION_ERROR") {
				error = true;
				return false;
			}
		});
		
		if (! error) {
			_cms.field.update(nodeKey, formData);
		}
		else {
			_cms.dialog.open(_cms.dialog.badFieldValueFormat);
		}
	});
}

_cms.field.update = function(nodeKey, formData) {
	_cms.support.ajax('POST', '/rest/item/' + nodeKey + '/update/fields', {data: formData, dataType: 'json'}, 
		// On success
		function(resp, status, z) {
			_cms.dialog.close(_cms.dialog.confirmFieldUpdate);
			_cms.support.flashMessage(resp);
			_cms.field.refresh.tab(nodeKey);
			_cms.undoRedo.displayAll(resp.data);
		},
		// On error
		function(obj, status, z) {
			_cms.dialog.close(_cms.dialog.confirmFieldUpdate);
			_cms.support.serverError();
		}
	);
}

//Get form field names and values for forms on item-editor 
_cms.field.getFieldsFormInputData = function() {
	var result = {};
	var language = $(_cms.field.sel.LANGUAGE_SELECT).val();
	if (! language) {
		language = _cms.siteDefaultLanguage;
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
			// Check field value respects validation constraints
			var regexpStr = ctrl.attr("data-validation");
			var fieldValue = ctrl.val();
			ctrl.removeClass("highlight");
			
			if (regexpStr && fieldValue) {
				var regexp = new RegExp(regexpStr, "i");
				if (! regexp.test(fieldValue)) {
					result[param] = "VALIDATION_ERROR";
					ctrl.addClass("highlight");
				}
				else {
					result[param] = fieldValue;
				}
			}
			else {
				result[param] = fieldValue;
			}
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

_cms.field.setButtonStates = function() {
	if (_cms.support.enableIf(_cms.field.sel.UPDATE_BUTTON, 
			_cms.field.originalFormState != $(_cms.field.sel.FORM).serialize())) {
		
		_cms.support.enable(_cms.field.sel.RESET_BUTTON);
	}
	else {
		_cms.support.disable(_cms.field.sel.RESET_BUTTON);
	}
}

_cms.field.behaviour.formchange = function() {
	if (_cms.editingItemIsWriteable) {
		$(_cms.field.sel.ALL_FORM_ELEMENTS).mouseleave(function() {
			if ($(this).attr("name") != "language") {
				_cms.field.setButtonStates();
			}
		});
		
		$(_cms.field.sel.FORM + ' button').mouseenter(_cms.field.setButtonStates);
	}
}

_cms.field.behaviour.guidanceIcon = function() {
	$("span.field-guidance-icon").click(function(e){
		let guidanceDiv = $(this).parent().parent().next();
		let dialog = $("#field-guidance");
		if (dialog.attr("data-variable") != guidanceDiv.attr("data-variable")) {
			dialog.html(guidanceDiv.html());
			dialog.attr("data-variable", guidanceDiv.attr("data-variable"));
		}
		
		_cms.dialog.open(_cms.dialog.fieldGuidance);
	});
}

_cms.field.behaviour.widefield = function() {
	// First, close and clear the widefield editor(s)
	$("div#widefield-editor").empty();
	$('#widefield-wrapper').css('visibility', 'hidden');
	
	$("div#widefield-open-icon").click(function(e) {
		let div$ = $(this).parent().parent()
		let text$ = div$.find('textarea')
		
		$('textarea#widefield-editor').val(text$.val())
		sessionStorage.setItem('current-widefield', div$.attr('id'))
		$('#widefield-wrapper').css('visibility', 'visible')
	})
}

_cms.field.onpageload = function() {
	$("div#widefield-close-icon").click(function(e) {
		let id = sessionStorage.getItem('current-widefield')
		let sel = 'form#field-form div#' + id
		let div$ = $(sel)
		let text$ = div$.find('textarea')
		text$.val($('textarea#widefield-editor').val())
		$('#widefield-wrapper').css('visibility', 'hidden')
	})
	
	$("div#widefield-p-icon").click(function(e) {
		_cms.field.widefieldInsert('<p></p>')
	})

	$("div#widefield-code-icon").click(function(e) {
		_cms.field.widefieldInsert('<code></code>')
	})

	$("div#widefield-ul-icon").click(function(e) {
		_cms.field.widefieldInsert(`<ul>
	<li></li>
	<li></li>
	<li></li>
	<li></li>
	<li></li>
	<li></li>
</ul>`)
	})

	$("div#widefield-h2-icon").click(function(e) {
		_cms.field.widefieldInsert('<h2></h2>')
	})

	$("div#widefield-h3-icon").click(function(e) {
		_cms.field.widefieldInsert('<h3></h3>')
	})

	$("div#widefield-div-icon").click(function(e) {
		_cms.field.widefieldInsert('<div></div>')
	})

//	$("div#widefield-div2b-icon").click(function(e) {
//		_cms.field.widefieldInsert('<div class="twoblock"></div>')
//	})

	$("div#widefield-xlink-icon").click(function(e) {
		_cms.field.widefieldInsert('<a class="xlink" href="/$_1234"></a>')
	})

	$("div#widefield-ximg-icon").click(function(e) {
		_cms.field.widefieldInsert(`<div class="ximg" data-id="1234">Optional floated text</div>
			/* Optional attrs: data-width, data-caption
			   Optional class: border */`)
	})

	$("div#widefield-xcomp-icon").click(function(e) {
		_cms.field.widefieldInsert('<div class="xcomp" data-enum="1"></div>')
	})

$("div#widefield-table-icon").click(function(e) {
	_cms.field.widefieldInsert(`<table>
  <tr>
    <td></td>
    <td></td>
    <td></td>
    <td></td>
    <td></td>
    <td></td>
  </tr>
</table>`)
})
}

_cms.field.widefieldInsert = function(str) {
	$('textarea#widefield-editor').insertAtCaret(str)
}

/*
_cms.field.formatHtml = function(html) {
    var tab = '\t';
    var result = '';
    var indent= '';

    html.split(/>\s*</).forEach(function(element) {
        if (element.match( /^\/\w/ )) {
            indent = indent.substring(tab.length);
        }

        result += indent + '<' + element + '>\r\n';

        if (element.match( /^<?\w[^>]*[^\/]$/ ) && !element.startsWith("input")  ) { 
            indent += tab;              
        }
    });

    return result.substring(1, result.length-3);
}
*/

_cms.field.onrefresh = function(nodeKey) {
	_cms.field.behaviour.update(nodeKey);
	_cms.field.behaviour.cancel(nodeKey);
	_cms.field.behaviour.changelanguage();
	_cms.field.behaviour.formchange();
	_cms.field.behaviour.guidanceIcon();
	_cms.field.behaviour.widefield();
	
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

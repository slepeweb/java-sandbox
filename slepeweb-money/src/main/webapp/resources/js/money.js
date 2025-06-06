var _money = {
	shared: {},
	schedule: {},
	transaction: {},
	transandsched: {},
	search: {},
	chart: {},
	service: {},
	reconcile: {},
};

_money.shared.isNotEmpty = function(field, sel, errors, isWarning) {
	let ok = true;
	
	if (sel.startsWith('input')) {
		ok = $(sel).val().trim() !== '';
	}
	else if (sel.startsWith('select')) {
		ok = $(sel).find(':selected').val() !== '';
	}
	
	_money.shared.addFormError(ok, 'Field "' + field + '" is empty', errors, isWarning)
	return ok;
}

_money.shared.initFormErrorObject = function() {
	return {ok: true, items: []};	
} 

_money.shared.addFormError = function(isOk, msg, errors, warning) {
	// By default, form errors are fatal.
	var isWarning = warning !== undefined;
	errors.ok = errors.ok && isOk;
	if (! isOk) {
		errors.items.push({msg: msg, fatal: ! isWarning});
	}
}

_money.shared.warningOnlyFormErrors = function(errors) {
	for (var i = 0; i < errors.items.length; i++) {
		if (errors.items[i].fatal) {
			return false;
		}
	}
	return true;
}

_money.shared.displayFormErrors = function(errors, e) {
	if (! errors.ok) {
		var d = ! _money.shared.warningOnlyFormErrors(errors) ? $("div#form-error-dialog") : $("div#form-warning-dialog");
		var s = '<ul>'
		for (var i = 0; i < errors.items.length; i++) {
			s += '<li>' + errors.items[i].msg + '</li>';
		}
		s += '</ul>';
		
		d.html('<p>Please address the following errors</p>' + s)
		d.dialog("open");				
		e.preventDefault();
	}

}

_money.shared.getAllPayees = function(andThenExecute) {
	$.ajax({
		url: webContext + "/rest/payee/list/all",
		type: "GET",
		contentType: "application/json",
		dataType: "json",
		success: function(data) {
			// init the widget with response data and let it do the filtering
			$("#payee").autocomplete({
				source: data,
				minLength: 2,
				change: function() {
					if (andThenExecute) {
						andThenExecute();
					}
				}
			});
		},
		error: function(x, t, m) {
			console.trace();
		}
	});
}

_money.shared.eraseFormField = function(ele$) {
	let input$ = ele$.prev();
	if (input$.attr('id') !== 'amount' || ! _money.transaction.reconciled) {
		input$.val('');
		input$.focus();
	}
}

$(function() {
	// Dialog common to most/all forms, display when forms contain errors
	$("#form-error-dialog").dialog({
		autoOpen: false, 
		modal: true,
		buttons: [
			{
				text: "Continue editing",
				icon: "ui-icon-arrowreturnthick-1-w",
				click: function() {
					$(this).dialog("close");
				}
			}
		]
	});
	
	$("#form-warning-dialog").dialog({
		autoOpen: false, 
		modal: true,
		buttons: [
			{
				text: "Ignore",
				icon: "ui-icon-arrowreturnthick-1-w",
				click: function() {
					window.localStorage.setItem("ignoreFormSubmissionErrors", 1);
					$("input#submit-button").click();
					$(this).dialog("close");
				}
			},
			{
				text: "Continue editing",
				icon: "ui-icon-arrowreturnthick-1-w",
				click: function() {
					$(this).dialog("close");
				}
			}
		]
	});
	
	$('tr.category-list span.next-category').click(function() {
		let span$ = $(this)
		span$.empty()
		span$.parent().next().removeClass('hidden');
	})

	$('tr.category-list span.trash-category').click(function() {
		let ele$ = $(this).parent().find('span.next-category')
		ele$.css('display', 'hidden')
		ele$ = ele$.next()
		ele$.val('')
		ele$ = ele$.next()
		ele$.val('')
		ele$ = ele$.next()
		ele$.val('')
		ele$ = ele$.next()
		ele$.val('0.00')
	})

});

_money.shared.ajax = function(method, url, data, success, fail) {
	let params = {
		type: method,
		cache: false,
		xhr: data.xhr,
		data: data.data,
		contentType: data.contentType,
		dataType: data.dataType,
		mimeType: data.mimeType,
		success: success,
		processData: data.processData,
		error: fail
	}
	
	if (! params.error) {
		params.error = function(a, b, c) {
			console.log("Server error:", '\na:', a, '\nb:', b, '\nc:', c);
		}
	}
	
	$.ajax(webContext + url, params);
}




/*
	The stuff below is not common to all js - needs to be re-factored
*/

$(function() {
	
	$("#account-selector").change(function(e) {	
		var accountId = $("#account-selector").find(":selected").val();
		window.location = webContext + "/transaction/list/" + accountId;
	});
	
	$("#year-selector").change(function(e) {	
		var accountId = $("#account-selector").find(":selected").val();
		var monthId = $("#year-selector").find(":selected").val();
		window.location = webContext + "/transaction/list/" + accountId + "/" + monthId;;
	});
	
	$("#accordion").accordion({
		active: false,
		collapsible: true,
		heightStyle: 'content'
	});
	
	$("#accordion-accounts").accordion({
		active: 0,
		collapsible: true,
		heightStyle: 'content'
	});
	
});	

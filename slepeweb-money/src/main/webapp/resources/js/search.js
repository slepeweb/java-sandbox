_money.search.disableExecuteOption = function() {
		$('#execute-option input').prop('disabled', true);
		$('#execute-option').css('opacity', 0.5);
		$('#save-option input').prop('checked', true);
	}

$(function() {
	_money.shared.getAllPayees();
	
	if (_money.search.formMode == 'create') {
		_money.search.disableExecuteOption();
	}

	$('input, select').change(function() {
		if (_money.search.formMode == 'update' && $(this).attr('name') != 'submit-option') {
			_money.search.disableExecuteOption();
		}
	});

		$("form#advanced-search-form input#cancel-button").click(function(e){
		window.location = webContext + "/search/list"
	});	
	
	$("input[id^='major']").off().change(function(e) {	
		_money.service.minorcats.updateMinorCategories($(this));
	});	  

	$('form#advanced-search-form i.fa-eraser').click(function() {
		_money.shared.eraseFormField($(this));
	});

});

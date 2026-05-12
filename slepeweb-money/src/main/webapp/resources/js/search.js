_money.search.disableExecuteOption = function() {
		$('#execute-option input').prop('disabled', true);
		$('#execute-option').css('opacity', 0.5);
		$('#save-option input').prop('checked', true);
}

_money.search.onPayeeSelected = function() {
	$('select#transferAccount').val('-1');
	_money.search.setCategoryGroupVisibility();
}

_money.search.setCategoryGroupVisibility = function() {
	let isPayeeSet = $('input#payee').val() !== '';
	let isTransfer = $('select#transferAccount').val() !== '-1';
	
	if (isTransfer && ! isPayeeSet) {
		// Hide all category groups
		$('tr.category-list').addClass('invisible');
		$('tr.add-category-group').addClass('invisible');
	}
	else {
		// Display all populated groups, plus one empty group
		let firstEmptyGroupIsOpen = false;

		$('tr.category-list').each(function() {
			let tr$ = $(this);
			let major = tr$.find('select.category:first').val();
			if (major !== '') {
				tr$.removeClass('invisible');
			}
			else if (! firstEmptyGroupIsOpen) {
				tr$.removeClass('invisible');
				firstEmptyGroupIsOpen = true;
			}
		})

		$('tr.add-category-group').removeClass('invisible');
	}	
}


$(function() {
	_money.shared.getAllPayees(_money.search.onPayeeSelected);
	_money.search.setCategoryGroupVisibility();
	
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

	$('select#transferAccount').change(function() {
		let name = $(this).val();
		if (name.match(/\w+/)) {
			$('input#payee').val('');
			$('tr.category-list, tr.add-category-group').addClass('invisible');
		}
		
		_money.search.setCategoryGroupVisibility();
	});
	
	$('form i.fa-eraser').click(function() {
		_money.shared.eraseFormField($(this));
	});
	
});

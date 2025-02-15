 /*
	Determines the visibility of various sections of the transaction form, dependent
	on the payment type. Certain sections will be visible, while others will be hidden.
*/
_money.transandsched.setComponentVisibilities = function() {				
	var paymentType = $("input[name='paymenttype']:checked").val();
	
	if (paymentType == "standard") {
		// Set form for a standard/normal transaction
		$("tr.payee td, tr.category td").css("display", "table-cell");
		$("tr.transfer td, tr.category-list td").css("display", "none");
	}
	else if (paymentType == "transfer") {
		$("tr.category td, tr.category-list td").css("display", "none");
		$("tr.payee td").css("display", "none");
		$("tr.transfer td").css("display", "table-cell");
	}
	else if (paymentType == "split") {				
		$("tr.category td, tr.transfer td").css("display", "none");
		$("tr.payee td, tr.category-list td").css("display", "table-cell");
	}
}

/*
	Checks whether the split amounts add up to the total.
*/
_money.transandsched.checkSplits = function(errors) {
	var _isSplit = $("input[name='paymenttype']:checked").val() == 'split';
	
	if (_isSplit) {
	  var sum = 0;
	  $("input[name^='amount_']").each(function(i, ele){
		  sum += parseFloat($(ele).val().replace(",", ""));
	  });
	  
	  var total = parseFloat($("input[name='amount']").val().replace(",", ""));
	  var debitOrCredit = $("input[name='debitorcredit']:checked").val();
	  if (debitOrCredit == 'debit') {
		  total = -total;
		  sum = -sum;
	  }
	  
	  if (Math.abs(total - sum) > 0.001) {
			_money.shared.addFormError(false, 'Split payments do not match total', errors, true /* ie, is a warning, NOT fatal */);
		}
	}
}

_money.transandsched.checkCategoryEtc = function(errors) {
	var paymentType = $("input[name='paymenttype']:checked").val();
	
	if (paymentType === 'standard') {
		_money.shared.isNotEmpty('Category', 'select#major', errors, 1);
		_money.shared.isNotEmpty('Payee', 'input#payee', errors, 1);
	}
	else if (paymentType === 'transfer') {
		_money.shared.isNotEmpty('Transfer a/c', 'select#xferaccount', errors);
	}
}


$(function() {
	// Event handler for when payment type changes
	$("input[name='paymenttype']").change(function() {	
		_money.transandsched.setComponentVisibilities();
	});


	// Cancel button event handler
	$("#cancel-button").click(function(){
		var target = _money.context === 'transaction' ? 
			`/transaction/list/${_money.transaction.accountid}` : '/schedule/list';
			
		window.location = webContext + target;
	});

	// Form submission - data check
	$("input#submit-button").click(function(e) {
		var ignoreErrors = window.localStorage.getItem("ignoreFormSubmissionErrors") !== null;
		if (! ignoreErrors) {
			var errors = _money.context === 'transaction' ?
				_money.transaction.checkFormComplete() : _money.schedule.checkFormComplete();
	
			_money.shared.displayFormErrors(errors, e);
		}
		else {
			window.localStorage.removeItem("ignoreFormSubmissionErrors")	
		}
	});
	
	// Run scripts selected functions when page loads
	_money.transandsched.setComponentVisibilities();
	
});


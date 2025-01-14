 /*
	Determines the visibility of various sections of the transaction form, dependent
	on the payment type. Certain sections will be visible, while others will be hidden.
*/
_money.transandsched.setComponentVisibilities = function() {				
	var paymentType = $("input[name='paymenttype']:checked").val();
	
	if (paymentType == "standard") {
		// Set form for a standard/normal transaction
		$(".payee td, .category td").css("display", "table-cell");
		$(".transfer td, .splits-list td").css("display", "none");
	}
	else if (paymentType == "transfer") {
		$(".category td, .splits-list td").css("display", "none");
		$(".payee td, .transfer td").css("display", "table-cell");
	}
	else if (paymentType == "split") {				
		$(".category td, .transfer td").css("display", "none");
		$(".payee td, .splits-list td").css("display", "table-cell");
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
		_money.shared.isNotEmpty('Category', 'select#major', errors);
	}
	else if (paymentType === 'transfer') {
		_money.shared.isNotEmpty('Transfer a/c', 'select#xferaccount', errors);
	}
}


class SplitService {
	constructor(templtA, templtB) {
		this.storageSelector = '#counter-store';
		this.innerTemplate = templtA;
		this.minorCategoryOptionsTemplate = templtB;
		this.splitCounters = null;
	}
	
	addSplit(button) {
		var counters = this.retrieveCounters();
		counters.splitCount += 1;
		counters.lastSplitId += 1;
		var splitId = counters.lastSplitId;
		this.storeCounters(counters);
		
		var inner = this.innerTemplate.
			replace(/\[counter\]/g, splitId.toString()).
			replace(/\[major\]/, "").
			replace(/\[memo\]/, "").
			replace(/\[amount\]/, "");
		
		$(inner).insertBefore(button);
		this.resetClickBehaviours();
		this.resetMajorCategoryChangeBehaviours();
	}

	
	resetClickBehaviours() {
		var fn1 = this.addSplit;
		var fn2 = this.retrieveCounters;
		var fn3 = this.storeCounters;
		
		$("#add-split-button").off().click(function(e) {
			fn1($(e.currentTarget));
		});
		
		$(".trash-split").off().click(function(e) {
			$(this).parent().remove();
			var counters = fn2();
			counters.splitCount -= 1;
			fn3(counters);
		});
	}
	
	storeCounters(counters) {
		$(this.storageSelector).val(JSON.stringify(counters));
	}
	
	retrieveCounters() {
		var objStr = $(this.storageSelector).val();
		if (objStr) {
			return JSON.parse(objStr);
		}
		
		return {
			splitCount: 0,
			lastSplitId: 0
		};
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
	
	/*
	var num = $(".split-inputs").length;
	_money.service.splits = new SplitService();
	_money.service.splits.splitCounters = {splitCount: num, lastSplitId: num};
	_money.service.splits.resetClickBehaviours();
	*/
});


/* 
	When the user changes the payee field, and the major category field is empty, a number of
	the other fields are automatically populated with the corresponding data from the most
	recent transaction with the same payee.
	
	The autofilled fields are: minor, memo, amount, debit/credit.
	
	This autofill code does NOT handle splits.
*/

_money.transaction.fillLastPaymentDetails = function() {
	
	var payeeName = $("#payee").val();
	var majorEle = $("#major");	
	var major = majorEle.find(':selected').val();
	var memo = $("input[name='memo']").val();
	
	if (payeeName && ! major) {
		$.ajax({
			url: webContext + "/rest/transaction/latest/bypayee/" + payeeName,
			type: "GET",
			contentType: "application/json",
			dataType: "json",
			success: function(trn) {
				majorEle.val(trn.majorCategory);
				var promet = _updateMinorCategories(majorEle);
				promet.done(function(res) {						  
					if (! memo) {
						$("input[name='memo']").val(trn.memo);
					}
					
					$("select[name='minor']").val(trn.minorCategory);
					
					var amountStr = trn.amountInPounds;
					var len = amountStr.length;		
					
					if (len > 0 && amountStr.substring(0, 1) == '-') {
						amountStr = amountStr.substring(1);
					}
					
					if (trn.amount < 0) {
						$("#debit").prop("checked", true);
					}
					else {
						$("#credit").prop("checked", true);
					}
					$("#amount").val(amountStr);
				});
			},
			error: function(x, t, m) {
				console.trace();
			}
		});
	}
}

_money.transaction.checkFormComplete = function() {
	
	var errors = _money.shared.initFormErrorObject();	
	_money.shared.isNotEmpty('Date', 'input#entered', errors);
	_money.shared.isNotEmpty('Account', 'select#account', errors);
	_money.shared.isNotEmpty('Payee', 'input#payee', errors);
	_money.transandsched.checkSplits(errors);
	_money.transandsched.checkCategoryEtc(errors);

	return errors;
}


$(function() {
	_money.shared.getAllPayees(_money.transaction.fillLastPaymentDetails);
});
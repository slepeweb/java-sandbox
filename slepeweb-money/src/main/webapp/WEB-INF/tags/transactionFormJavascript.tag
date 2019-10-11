<%@ tag %><%@ include file="/WEB-INF/jsp/tagDirectives.jsp" %>
	
<!-- transactionFormJavascript.tag -->	

<script>
	/*
		Function index:
			_setComponentVisibilities
			_checkSplits
			_autofill
	*/
	
	/*
		Determines the visibility of various sections of the transaction form, dependent
		on the payment type. Certain sections will be visible, while others will be hidden.
	*/
	var _setComponentVisibilities = function() {				
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
	var _checkSplits = function(e) {
		var _isSplit = $("input[name='paymenttype']:checked").val() == 'split';
		
		if (_isSplit) {
		  var sum = 0;
		  $("input[name^='amount-']").each(function(i, ele){
			  sum += parseFloat($(ele).val().replace(",", ""));
		  });
		  
		  var total = parseFloat($("input[name='amount']").val().replace(",", ""));
		  var debitOrCredit = $("input[name='debitorcredit']:checked").val();
		  if (debitOrCredit == 'debit') {
			  total = -total;
			  sum = -sum;
		  }
		  
		  if (Math.abs(total - sum) > 0.001) {
				var d = $("#splits-error-dialog");
				var s = d.html();
				d.html(s.replace("__totalamount__", total.toFixed(2)).replace("__splitamounts__", sum.toFixed(2)));
				d.dialog("open");
				
				if (e) {
					e.preventDefault();
				}
			}
		}
	}
	
	/* 
		When the user changes the payee field, and the major category field is empty, a number of
		the other fields are automatically populated with the corresponding data from the most
		recent transaction with the same payee.
		
		The autofilled fields are: minor, memo, amount, debit/credit.
		
		This autofill code does NOT handle splits.
	*/
	
	var _autofill = function() {
		
		var payeeName = $("#payee").val();
		var majorEle = $("#major");	
		var major = majorEle.val();
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
	
	$(function() {		
		// Event handler to execute _checkSplits
		$("#submit-button").click(function(e){
			_checkSplits(e);
		});
	
		// Dialog for when _checkSplits detects an error
		$("#splits-error-dialog").dialog({
			autoOpen: false, 
			modal: true,
			buttons: [
				{
					text: "Ignore",
					icon: "ui-icon-play",
					click: function() {
						$("#transaction-form").submit();
						$(this).dialog("close");
					}
				},
				{
					text: "Cancel",
					icon: "ui-icon-arrowreturnthick-1-w",
					click: function() {
						$(this).dialog("close");
					}
				}
			]
		});
	
		// Event handler for when payment type changes
		$("input[name='paymenttype']").change(function(e) {	
			_setComponentVisibilities();
		});
	
	
		// Use jQuery datepicker widget for setting transaction dates
		$(".datepicker").datepicker({
			dateFormat: "yy-mm-dd",
			changeMonth: true,
			changeYear: true
		});
	
		// Cancel button event handler
		$("#cancel-button").click(function(e){
			window.location = webContext + "/transaction/list/${_transaction.account.id}"
		});
	
	
		// Run scripts selected functions when page loads
		_checkSplits();
		_setComponentVisibilities();
	});
</script>

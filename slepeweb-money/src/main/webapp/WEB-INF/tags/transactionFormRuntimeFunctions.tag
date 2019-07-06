<%@ tag %><%@ include file="/WEB-INF/jsp/tagDirectives.jsp" %>

/*
	Function index:
		_updateMinorCategories
		_setComponentVisibilities
		_checkSplits
		_autofill
*/

/*
	Updates minor categories available for a given major category.
	This applies to split transactions as well.
*/
var _updateMinorCategories = function(majorEle) {
	var deferred = $.Deferred();
	var majorVal = majorEle.find(":selected").val();
	var name = majorEle.attr("name");
	var split = name.length > 5;
	var index = -1;
	if (split) {
		index = name.substring("major".length + 1);
	}
	
	$.ajax(webContext + "/rest/category/minor/list/" + majorVal, {
		type: "GET",
		cache: false,
		dataType: "json",
		success: function(obj, status, z) {
			var select = $("select[name='minor" + (split ? "_" + index : "") + "']");
			select.empty();
			$.each(obj.data, function(index, minor) {
				select.append("<option value='" + minor + "'>" + minor + "</option>");
			});
			
			deferred.resolve("Categories updated");
		},
		error: function(x, t, m) {
			deferred.reject(x + t + m);
		}
	});
	
	return deferred.promise();
}
  
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
		$(".payee td, .category td, .splits-list td").css("display", "none");
		$(".transfer td").css("display", "table-cell");
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
	  $("input[name^='amount_']").each(function(i, ele){
		  sum += parseFloat($(ele).val().replace(",", ""));
	  });
	  
	  var total = parseFloat($("input[name='amount']").val().replace(",", ""));
	  if (Math.abs(total - sum) > 0.001) {
			var d = $("#splits-error-dialog");
			var s = d.html();
			d.html(s.replace("__totalamount__", total.toString()).replace("__splitamounts__", sum.toFixed(2)));
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
	var major = majorEle.find(":selected").val();
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
	
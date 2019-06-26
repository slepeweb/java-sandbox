<%@ tag %><%@ include file="/WEB-INF/jsp/tagDirectives.jsp" %>

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

$("input[name='paymenttype']").change(function(e) {	
	_setComponentVisibilities();
});

_setComponentVisibilities();

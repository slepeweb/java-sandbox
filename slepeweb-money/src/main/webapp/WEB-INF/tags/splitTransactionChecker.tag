<%@ tag %><%@ include file="/WEB-INF/jsp/tagDirectives.jsp" %>

$("#submit-button").click(function(e){
	checkSplits(e);
});

var checkSplits = function(e) {
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
 
$("#splits-error-dialog").dialog({
	autoOpen: false, 
	modal: true,
	buttons: [
		{
			text: "Cancel",
			icon: "ui-icon-arrowreturnthick-1-w",
			click: function() {
				$(this).dialog("close");
			}
		}
	]
});

checkSplits();


<%@ tag %><%@ include file="/WEB-INF/jsp/tagDirectives.jsp" %>

$("#submit-button").click(function(e){
	checkSplits();
});

var checkSplits = function() {
  if (_isSplit) {
	  var sum = 0;
	  $("input[name^='amount_']").each(function(i, ele){
		  sum += parseFloat($(ele).val());
	  });
	  
	  var total = parseFloat($("input[name='amount']").val());
	  if (Math.abs(total - sum) > 0.001) {
			var d = $("#splits-error-dialog");
			var s = d.html();
			d.html(s.replace("__totalamount__", total.toString()).replace("__splitamounts__", sum.toFixed(2)));
			d.dialog("open");
		  e.preventDefault();
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


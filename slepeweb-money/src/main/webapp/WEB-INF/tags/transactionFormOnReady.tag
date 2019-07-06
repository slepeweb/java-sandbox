<%@ tag %><%@ include file="/WEB-INF/jsp/tagDirectives.jsp" %>

/*
	Setup the payee field with autocomplete functionality. This is appropriate
	for the payee field, as there are so many payees.
*/
$.ajax({
  url: webContext + "/rest/payee/list/all",
  type: "GET",
  contentType: "application/json",
  dataType: "json",
  success: function(data) {
    // init the widget with response data and let it do the filtering
    $("#payee").autocomplete({
      source: data,
      minLength: 2,
      change: function(e, selection) {
      	_autofill();
      }
    });
  },
  error: function(x, t, m) {
    console.trace();
  }
});

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



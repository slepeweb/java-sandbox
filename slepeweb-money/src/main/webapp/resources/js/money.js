$(function() {
	
	$("#account_selector").change(function(e) {	
		var accountId = $("#account_selector").find(":selected").val();
		window.location = "../" + accountId;
	});
});	

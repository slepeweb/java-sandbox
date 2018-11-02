$(function() {
	
	$("#account-selector").change(function(e) {	
		var accountId = $("#account-selector").find(":selected").val();
		window.location = "../" + accountId;
	});
});	

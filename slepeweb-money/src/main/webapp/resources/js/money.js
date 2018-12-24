$(function() {
	
	$("#account-selector").change(function(e) {	
		var accountId = $("#account-selector").find(":selected").val();
		window.location = webContext + "/transaction/list/" + accountId;
	});
	
	$("#year-selector").change(function(e) {	
		var accountId = $("#account-selector").find(":selected").val();
		var monthId = $("#year-selector").find(":selected").val();
		window.location = webContext + "/transaction/list/" + accountId + "/" + monthId;;
	});
	
//	$("#tabs").tabs({
//		active: 1,
//		collapsible: true
//	});
	
	$("#accordion").accordion({
		active: false,
		collapsible: true,
		heightStyle: content
	});
	
	$("#accordion-accounts").accordion({
		active: 0,
		collapsible: true,
		heightStyle: content
	});
		
	$("#lift-limit").click(function(e){
		window.location = window.location + "/all";
	});
	
//	$(".nav-hide").click(function(e){
//		$("nav").animate({
//			width: '2px'
//		}, "slow");
//	});
});	

$(function() {
	_money.shared.getAllPayees();
	
	$("#cancel-button").click(function(e){
		window.location = webContext + "/search/list"
	});		
});

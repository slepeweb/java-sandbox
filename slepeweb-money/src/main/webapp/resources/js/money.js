$(function() {
	
	$("#account-selector").change(function(e) {	
		var accountId = $("#account-selector").find(":selected").val();
		window.location = webContext + "/transaction/list/" + accountId;
	});
	
	$("#tabs").tabs({
		active: 1,
		collapsible: true
	});
	
	$("#accordion").accordion({
		active: 1,
		collapsible: true,
		heightStyle: content
	});
	
	$("#accordion-accounts").accordion({
		active: 0,
		collapsible: true,
		heightStyle: content
	});
	
	$(".category-menu").click(function(e){
		var id = $(this).parent().attr("data-id");
		var dialog = $("#category-menu-dialog");
		if (dialog.css("visibility") == "visible") {
			dialog.css("visibility", "hidden");
			return;
		}
		
		var menuOffset = $(this).offset();
		dialog.offset({left: menuOffset.left - 150, top: menuOffset.top});		
		dialog.css("visibility", "visible");
		dialog.attr("data-id", id);
		e.stopPropagation();
	});
	
	$(".category-menu-close").click(function(e){
		$("#category-menu-dialog").css("visibility", "hidden");
	});
	
	$(".category-menu-find").click(function(e){
		var id = $(this).parent().parent().attr("data-id");
		window.location = "/money/transaction/list/by/category/" + id;
	});
});	

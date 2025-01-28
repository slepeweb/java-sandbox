
$(function() {
	$('form#chart-form button#add-group-button').click(function(){
		let targetGroupId = $('tr.category-list.invisible').attr('data-id');
		$(`tr.category-list[data-id=${targetGroupId}]`).removeClass('invisible');
	})
	
	$("form#chart-form input#cancel-button").click(function(e){
		window.location = webContext + "/chart/list"
	});
});

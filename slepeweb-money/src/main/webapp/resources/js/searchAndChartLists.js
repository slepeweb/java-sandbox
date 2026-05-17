/*
This js is required on 2 pages: searchList.jsp and chartList.jsp.
It defines the behaviour when the user clicks on list entry icons to
either execute the function, or delete the definition.
*/

$(function() {
	let pageId = $('div#main').attr("data-pageId");
	let entity = pageId === 'searchList' ? 'search' : (pageId === 'chartList' ? 'chart' : '');

	$("i.fa-caret-square-right").click(function (e) {
		var id = $(this).attr("data-id");
		window.location = `${webContext}/${entity}/get/${id}`;
	});	

	$("i.fa-trash").click(function (e) {
		_money.param.entityType = entity;
		_money.param.entityId = $(this).attr("data-id");
		$("#delete-dialog").dialog("open");
	});

})
$(function() {
	$(".del-check").click(function () {
		var filename = $(this).attr("data-f");
		$("#dialog-trash-confirm").dialog({
			resizable: false,
			height:225,
			modal: true,
			buttons: {
				"Delete file": function() {
					$(this).dialog("close");
					window.location = "/secam/app/index.py?d=" + filename;
				},
				Cancel: function() {
					$(this).dialog("close");
				}
			}
		});
	});
});
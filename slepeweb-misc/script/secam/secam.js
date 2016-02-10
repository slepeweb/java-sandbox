$(function() {
	$(".del-check").click(function () {
		var file_list = "";
		$(".deleteable-video:checked").each(function(index, element){
			if (file_list.length > 0) {
				file_list += ",";
			}
			file_list += $(this).attr("value");
		});
		
		var list_length = file_list.split(",").length;
		if (file_list.length > 0) {
			$("#num-files-target").html(list_length);
			$("#dialog-trash-confirm").dialog({
				resizable: false,
				height:225,
				modal: true,
				buttons: {
					"Delete file(s)": function() {
						$(this).dialog("close");
						window.location = "/secam/app/index.py?d=" + file_list;
					},
					Cancel: function() {
						$(this).dialog("close");
					}
				}
			});
		}
	});
});
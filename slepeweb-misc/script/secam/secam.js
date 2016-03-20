var status = null;

function manageButtons(msg) {
	if (msg == "stop") {
		$("#button-stopgo").val("go").empty().append("Continue surveillance")
		$("#button-photo").attr("disabled", "disabled");
	}
	else if (msg == "go") {
		$("#button-stopgo").val("stop").empty().append("Pause surveillance")
		$("#button-photo").removeAttr("disabled");
	}
}

$(function() {
	$(".del-check").click(function() {
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
	
	$.ajax({
		url : "/secam/app/index.py/status",
		dataType : "text",
		cache : false
	}).done(function(resp) {
		$(".flash").empty().append("Status: " + resp);
		manageButtons(resp);
	}).fail(function(jqXHR, status) {
		//console.log(status);
	});		
	
	$("#button-photo,#button-stopgo").click(function() {	
		var msg = $(this).attr("value");
		$.ajax({
			url : "/secam/app/index.py/putm?msg=" + msg,
			dataType : "text",
			cache : false
		}).done(function(resp) {
			$(".flash").empty().append("'" + msg + "' message sent");
			manageButtons(msg);
		}).fail(function(jqXHR, status) {
			//console.log(status);
		});		
	});

	$("#button-refresh").click(function(e) {	
		location.reload(true);
	});
	
	$("html", "body").scrollTo("#bop");
});


/*
setTimeout(function(){
	location.reload(true);
}, 5000);
*/
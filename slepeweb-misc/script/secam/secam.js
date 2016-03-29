var status = null;
var indexPath = "/secam/app/py/index.py";

function manageButtons(msg) {
	if (msg == "stop") {
		$("#button-stopgo").val("go").empty().append("Continue surveillance")
	}
	else if (msg == "go") {
		$("#button-stopgo").val("stop").empty().append("Pause surveillance")
	}
}

function reloadTable() {
	$.ajax({
		url : indexPath + "/table",
		cache : false
	}).done(function(resp) {
		$("#main").empty().append(resp);
		
		// Parse page to add behaviour
		$(".backup-button").click(function() {
			var filename = $(this).attr("value");
			$.ajax({
				url : indexPath + "/backup?plik=" + filename,
				dataType : "text",
				cache : false
			}).done(function(resp) {
				$(".flash").empty().append("File " + filename + " backed up");
				reloadTable();
			}).fail(function(jqXHR, status) {
				//console.log(status);
			});		
		});

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
							$.ajax({
								url : indexPath + "/delete?files=" + file_list,
								dataType : "text",
								cache : false
							}).done(function(resp) {
								$(".flash").empty().append(resp);
								reloadTable();
								$("#dialog-trash-confirm").dialog("close");
							}).fail(function(jqXHR, status) {
								//console.log(status);
								$("#dialog-trash-confirm").dialog("close");
							});		
						},
						Cancel: function() {
							$(this).dialog("close");
						}
					}
				});
			}
		});
		
		$("#button-photo,#button-stopgo").click(function() {	
			var msg = $(this).attr("value");
			$.ajax({
				url : indexPath + "/putm?msg=" + msg,
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
	}).fail(function(jqXHR, status) {
		//console.log(status);
	});		
}

$(function() {	
	$.ajax({
		url : indexPath + "/status",
		dataType : "text",
		cache : false
	}).done(function(resp) {
		$(".flash").empty().append("Status: " + resp);
		reloadTable();
		manageButtons(resp);
	}).fail(function(jqXHR, status) {
		//console.log(status);
	});		
		
	$("html", "body").scrollTo("#bop");
});

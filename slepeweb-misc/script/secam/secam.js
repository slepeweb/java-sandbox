var status = null;
var indexPath = "/secam/app/py/index.py";

function manageButtons(map) {
	if (map.status == "stop") {
		$("#button-stopgo").val("go").empty().append("Continue surveillance");
	}
	else if (map.status == "go") {
		$("#button-stopgo").val("stop").empty().append("Pause surveillance");
	}
	
	var sel = "#brightness option[value=" + map.settings.brightness + "]";
	$(sel).attr("selected", "selected");
	
	sel = "#contrast option[value=" + map.settings.contrast + "]";
	$(sel).attr("selected", "selected");
	
	sel = "#mode option[value=" + map.settings.mode + "]";
	$(sel).attr("selected", "selected");
	
	sel = "#iso option[value=" + map.settings.iso + "]";
	$(sel).attr("selected", "selected");
}

function reloadTable() {
	$.ajax({
		url : indexPath + "/table",
		cache : false
	}).done(function(resp) {
		$("#main").empty().append(resp);
		
		// Parse page to add behaviour
		
		// Backup files to dropbox
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
				console.log(status);
			});		
		});

		// Delete files
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
								console.log(status);
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
		
		// Send contro messages to camera (spibox.py)
		$("#button-photo,#button-stopgo").click(function() {	
			var msg = $(this).attr("value");
			$.ajax({
				url : indexPath + "/putm?msg=" + msg + "&json=1",
				dataType : "text",
				cache : false
			}).done(function(resp) {
				var obj = $.parseJSON(resp)
				$(".flash").empty().append("Status: " + obj["status"]);
				manageButtons(obj);
			}).fail(function(jqXHR, status) {
				console.log(status);
			});		
		});

		// Send more control messages to camera (spibox.py)
		$(".ctrl").change(function() {	
			var ctrl = $(this).attr("id");
			var value = $(this).find(":selected").text();
			var msg = ctrl + "," + value;
			$.ajax({
				url : indexPath + "/camera?ctrl=" + ctrl + "&value=" + value,
				dataType : "text",
				cache : false
			}).done(function(resp) {
				var obj = $.parseJSON(resp)
				$(".flash").empty().append("Status: " + obj["status"]);
				manageButtons(obj);
			}).fail(function(jqXHR, status) {
				console.log(status);
			});		
		});

		$("#button-refresh").click(function(e) {	
			location.reload(true);
		});
	}).fail(function(jqXHR, status) {
		console.log(status);
	});		
}

$(function() {	
	$.ajax({
		url : indexPath + "/putm?msg=status&json=1",
		dataType : "text",
		cache : false
	}).done(function(resp) {
		var obj = $.parseJSON(resp)
		$(".flash").empty().append("Status: " + obj["status"]);
		reloadTable();
		manageButtons(obj);
	}).fail(function(jqXHR, status) {
		console.log(status);
	});		
		
	//$("html", "body").scrollTo("#bop");
});

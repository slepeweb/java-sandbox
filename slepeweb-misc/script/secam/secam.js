var status = null;
var indexPath = "/secam/app/py/index.py";

function manageButtons(map) {
	if (map["status"] == "stop") {
		$("#button-stopgo").val("go").empty().append("Continue surveillance");
	}
	else if (map["status"] == "go") {
		$("#button-stopgo").val("stop").empty().append("Pause surveillance");
	}
	
	$("#brightness option[value=" + map["brightness"] + "]").attr("selected", "selected");
	$("#contrast option[value=" + map["contrast"] + "]").attr("selected", "selected");
	$("#mode option[value=" + map["mode"] + "]").attr("selected", "selected");
	$("#iso option[value=" + map["iso"] + "]").attr("selected", "selected");
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
				//console.log(status);
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
		
		// Send contro messages to camera (spibox.py)
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

		// Send more control messages to camera (spibox.py)
		$(".ctrl").change(function() {	
			var ctrl = $(this).attr("id");
			var value = $(this).find(":selected").text();
			var msg = ctrl + "," + value;
			$.ajax({
				url : indexPath + "/putm?msg=" + msg,
				dataType : "text",
				cache : false
			}).done(function(resp) {
				$(".flash").empty().append("'" + msg + "' message sent");
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

function toMap(s) {
	var pair;
	var map = {};
	var parts = s.split(",");
	for (var i = 0; i < parts.length; i++) {
		pair = parts[i].split("=");
		if (pair.length == 2) {
			map[pair[0]] = pair[1];
		}
	}
	return map;
}

$(function() {	
	$.ajax({
		url : indexPath + "/status",
		dataType : "text",
		cache : false
	}).done(function(resp) {
		var map = toMap(resp);
		$(".flash").empty().append("Status: " + map["status"]);
		reloadTable();
		manageButtons(map);
	}).fail(function(jqXHR, status) {
		//console.log(status);
	});		
		
	//$("html", "body").scrollTo("#bop");
});

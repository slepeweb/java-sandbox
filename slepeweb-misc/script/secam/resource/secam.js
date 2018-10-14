var status = null;
var rootPath = "/secam/app/";
var indexPath = rootPath + "py/index.py";
var securePath = rootPath + "secure/index.py";

function manageButtons(map) {
	if (map.status == "stop") {
		$("#button-stopgo").val("go").empty().append("Continue surveillance");
	} else if (map.status == "go") {
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
		var table = $(resp);
		table.find(".iframe").colorbox({
			iframe : true,
			opacity : 0.5,
			closeButton : true,
			width : "90%",
			height : "80%",
			top : "15%"
		});
		table.find(".group2").colorbox({
			rel : 'group2',
			transition : "none",
			current : 'Media item {current} of {total}'
		});
		$("#video-table").empty().append(table);

		// Parse page to add behaviour
		// Backup files to dropbox
		$(".backup-button").click(function() {
			var filename = $(this).attr("value");
			$.ajax({
				mimeType : "application/json",
				url : indexPath + "/backup?plik=" + filename,
				dataType : "text",
				cache : false
			}).done(function(resp) {
				var obj = $.parseJSON(resp)
				$(".flash").empty().append(obj["msg"]);
				reloadTable();
			}).fail(function(jqXHR, status) {
				console.log(status);
			});
		});

		// Delete files
		$(".del-check").click(function() {
			var file_list = "";
			$(".deleteable-video:checked").each(function(index, element) {
				if (file_list.length > 0) {
					file_list += ",";
				}
				file_list += $(this).attr("value");
			});

			var list_length = file_list.split(",").length;
			if (file_list.length > 0) {
				$("#num-files-target").html(list_length);
				$("#dialog-trash-confirm").dialog({
					resizable : false,
					height : 225,
					modal : true,
					buttons : {
						"Delete file(s)" : function() {
							$.ajax({
								mimeType : "application/json",
								url : indexPath + "/delete?files=" + file_list,
								dataType : "text",
								cache : false
							}).done(function(resp) {
								var obj = $.parseJSON(resp)
								$(".flash").empty().append(obj["msg"]);
								reloadTable();
								$("#dialog-trash-confirm").dialog("close");
							}).fail(function(jqXHR, status) {
								console.log(status);
								$("#dialog-trash-confirm").dialog("close");
							});
						},
						Cancel : function() {
							$(this).dialog("close");
						}
					}
				});
			}
		});

	}).fail(function(jqXHR, status) {
		console.log(status);
	});
}

function manageLiveVideoPlayer(playing_live_video) {
	if (playing_live_video) {
		$("#video-table-wrapper").css("display", "none")
		$("#controls").css("display", "none")
		$("#live-video img").attr("src", "http://www.buttigieg.org.uk:8083/?action=stream")
	} else {
		$("#video-table-wrapper").css("display", "")
		$("#controls").css("display", "")
		$("#live-video img").attr("src", "/secam/app/resource/images/video-play.png")
	}
}

function updatePage() {
	$.ajax({
		mimeType : "application/json",
		url : indexPath + "/status",
		dataType : "text",
		cache : false
	}).done(function(resp) {
		var status = $.parseJSON(resp)
		$(".flash").empty().append(status["msg"]);
		reloadTable();
		manageButtons(status);
		manageLiveVideoPlayer(status["livevideo"])
	}).fail(function(jqXHR, status) {
		console.log(status);
	});
}

// After page is fully loaded ...
$(function() {
	updatePage();

	// Apply behaviour to photo and video buttons
	$("#button-photo,#button-stopgo").click(function() {
		var msg = $(this).attr("value");
		$.ajax({
			mimeType : "application/json",
			url : indexPath + "/putm?msg=" + msg + "&json=1",
			dataType : "text",
			cache : false
		}).done(function(resp) {
			var obj = $.parseJSON(resp)
			$(".flash").empty().append(obj["msg"]);
			window.setTimeout(updatePage, 2000);
		}).fail(function(jqXHR, status) {
			console.log(status);
		});
	});

	// Identify currently selected values for camera settings
	$(".ctrl").change(function() {
		var ctrl = $(this).attr("id");
		var value = $(this).find(":selected").text();
		var msg = ctrl + "," + value;
		$.ajax({
			mimeType : "application/json",
			url : indexPath + "/camera?ctrl=" + ctrl + "&value=" + value,
			dataType : "text",
			cache : false
		}).done(function(resp) {
			var obj = $.parseJSON(resp)
			$(".flash").empty().append(obj["msg"]);
			manageButtons(obj);
		}).fail(function(jqXHR, status) {
			console.log(status);
		});
	});

	$("#button-refresh").click(function(e) {
		location.reload(true);
	});

	$(".controls-toggle").click(function() {
		window.location = securePath;
	});

	$("#live-video img").click(function() {
		/*
		 * &json=1 means return a json text string, which will be parsed later
		 * into a json object.
		 */
		$.ajax({
			mimeType : "application/json",
			url : indexPath + "/putm?msg=livevideo&json=1",
			dataType : "text",
			cache : false
		}).done(function(resp) {
			var obj = $.parseJSON(resp)
			$(".flash").empty().append(obj["msg"]);
			window.setTimeout(updatePage, 2000);
		}).fail(function(jqXHR, status) {
			console.log(status);
		});
	});
});

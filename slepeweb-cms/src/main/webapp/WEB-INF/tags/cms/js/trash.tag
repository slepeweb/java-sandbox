<%@ tag %><%@ 
    taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%><%@ 
    taglib prefix="cms" tagdir="/WEB-INF/tags/cms"%><%@ 
    taglib prefix="cmsjs" tagdir="/WEB-INF/tags/cms/js"%>
        
<cms:debug>/* tags/cms/js/trash.tag */</cms:debug>

// Add behaviour to show trash contents 
$("#trash-show-button").click(function () {
	$.ajax(_ctx + "/rest/trash/get", {
		cache: false,
		dataType: "html",
		mimeType: "text/html",
		
		success: function(html, status, z) {
			var mydiv = $("#trash-container");
			mydiv.empty().append(html);

			// Add behaviour to empty the trash 
			$("#trash-empty-button").click(function () {
				var selection = null;
				$("#trash-action input:checked").each(function() {
				    selection = $(this).attr("value");
				});

				var url = "/rest/trash/empty/" + selection;
				var params = null;
				
				if (selection == "selected") {
					var idList = "";
					$("#trash-table input:checked").each(function() {
					    idList += ($(this).attr("value") + ",");
					});
					params = {id: idList};
				}
				
				$.ajax(_ctx + url, {
					cache: false,
					dataType: "json",
					data: params,
					
					success: function(obj, status, z) {
						var mydiv = $("#trash-container");
						mydiv.empty();
						flashMessage(obj);
					},
					error: function(json, status, z) {
						serverError();
					},
				});
			});
			
			// Add behaviour to restore the trash 
			$("#trash-restore-button").click(function () {
				var selection = null;
				$("#trash-action input:checked").each(function() {
				    selection = $(this).attr("value");
				});

				var url = "/rest/trash/restore/" + selection;
				var params = null;
				
				if (selection == "selected") {
					var idList = "";
					$("#trash-table input:checked").each(function() {
					    idList += ($(this).attr("value") + ",");
					});
					params = {id: idList};
				}
				
				$.ajax(_ctx + url, {
					cache: false,
					dataType: "json",
					data: params,
					
					success: function(obj, status, z) {
						var mydiv = $("#trash-container");
						fetchItemEditor(nodeKey, obj);
					},
					error: function(json, status, z) {
						serverError();
					},
				});
			});
		},
		error: function(json, status, z) {
			serverError();
		},
	});
});	

// Add behaviour to trash an item 
$("#trash-button").click(function () {
	var theDialog = $("#dialog-trash-confirm");
	theDialog.dialog({
		resizable: false,
		height:200,
		modal: true,
		buttons: {
			"Delete all items": function() {
				$.ajax(_ctx + "/rest/item/" + nodeKey + "/trash", {
					type: "POST",
					cache: false,
					data: {key: nodeKey}, 
					dataType: "json",
					success: function(obj, status, z) {
						theDialog.dialog("close");
						fetchItemEditor(obj.data, obj);
					},
					error: function(json, status, z) {
						theDialog.dialog("close");
						serverError();
					}
				});
			},
			Cancel: function() {
				$(this).dialog("close");
			}
		}
	});
});

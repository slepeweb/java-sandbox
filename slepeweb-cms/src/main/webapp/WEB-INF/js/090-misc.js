_cms.misc = {
	behaviour: {
		trash: {}
	},
	trash: {}
};

_cms.misc.behaviour.reindex = function(nodeKey) {
	// Add behaviour to re-index content for search 
	$("#reindex-button").click(function () {
		$.ajax(_cms.ctx + "/rest/search/reindex/" + nodeKey, {
			type: "GET",
			cache: false,
			dataType: "json",
			
			success: function(obj, status, z) {
				_cms.support.flashMessage(obj);
			},
			error: function(json, status, z) {
				_cms.support.serverError();
			},
		});
	});
}

_cms.misc.trash.visible = false;

_cms.misc.behaviour.trash.show = function(nodeKey) {
	// Add behaviour to show trash contents 
	$("#trash-show-button").click(function () {
		if (! _cms.misc.trash.visible) {
			$.ajax(_cms.ctx + "/rest/trash/get", {
				cache: false,
				dataType: "html",
				mimeType: "text/html",
				
				success: function(html, status, z) {
					var mydiv = $("#trash-container");
					mydiv.empty().append(html);
					_cms.misc.behaviour.trash.empty();
					_cms.misc.behaviour.trash.restore();
					_cms.misc.trash.visible = true;
					$("#trash-show-button").empty().append("Hide bin ...");
				},
				error: function(json, status, z) {
					_cms.support.serverError();
				},
			});
		}
		else {
			$("#trash-container").empty();
			_cms.misc.trash.visible = false;
			$("#trash-show-button").empty().append("Show bin ...");
		}
	});	
}

_cms.misc.behaviour.trash.action = function(nodeKey) {
	// Add behaviour to trash an item 
	$("#trash-button").click(function () {
		var theDialog = $("#dialog-trash-confirm");
		theDialog.dialog({
			resizable: false,
			height:200,
			modal: true,
			buttons: {
				"Delete all items": function() {
					$.ajax(_cms.ctx + "/rest/item/" + nodeKey + "/trash", {
						type: "POST",
						cache: false,
						data: {key: nodeKey}, 
						dataType: "json",
						success: function(obj, status, z) {
							theDialog.dialog("close");
							_cms.support.flashMessage(obj);
							
							if (! obj.error) {
								var node = _cms.leftnav.tree.getNodeByKey(nodeKey);
								if (node) {
									var parent = node.getParent();
									node.remove();
									_cms.leftnav.tree.activateKey(parent.key);
								}
							}
						},
						error: function(json, status, z) {
							theDialog.dialog("close");
							_cms.support.serverError();
						}
					});
				},
				Cancel: function() {
					$(this).dialog("close");
				}
			}
		});
	});
}

_cms.misc.behaviour.trash.empty = function() {
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
		
		$.ajax(_cms.ctx + url, {
			cache: false,
			dataType: "json",
			data: params,
			
			success: function(obj, status, z) {
				var mydiv = $("#trash-container");
				mydiv.empty();
				_cms.support.flashMessage(obj);
			},
			error: function(json, status, z) {
				_cms.support.serverError();
			},
		});
	});
}

_cms.misc.behaviour.trash.restore = function() {
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
		
		$.ajax(_cms.ctx + url, {
			cache: false,
			dataType: "json",
			data: params,
			
			success: function(obj, status, z) {
				var mydiv = $("#trash-container");
				_cms.support.renderItemForms(nodeKey, "core-tab");
				//_cms.support.fetchItemEditor(nodeKey, obj);
			},
			error: function(json, status, z) {
				_cms.support.serverError();
			},
		});
	});
}

// Behaviours to apply once html is loaded/reloaded
_cms.misc.behaviour.trash.all = function(nodeKey) {
	_cms.misc.behaviour.reindex(nodeKey);
	_cms.misc.behaviour.trash.show(nodeKey);
	_cms.misc.behaviour.trash.action(nodeKey);
}
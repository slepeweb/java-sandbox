_cms.misc = {
	behaviour: {
		trash: {}
	},
	trash: {
		visible: false
	},
	refresh: {}
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

_cms.misc.refresh.trash = function(fn) {
	$.ajax(_cms.ctx + "/rest/trash/get", {
		cache: false,
		dataType: "html",
		mimeType: "text/html",
		
		success: function(html, status, z) {
			var mydiv = $("#trash-container");
			mydiv.empty().append(html);
			
			// Apply behaviour for new/replaced buttons
			_cms.misc.behaviour.trash.empty();
			_cms.misc.behaviour.trash.restore();		
			
			if (fn) {
				fn();
			}
		},
		error: function(json, status, z) {
			_cms.support.serverError();
		},
	});
}

_cms.misc.behaviour.trash.showOrHide = function() {
	// Add behaviour to show trash contents 
	$("#trash-show-button").click(function () {
		if (! _cms.misc.trash.visible) {
			_cms.misc.refresh.trash();
			_cms.misc.trash.visible = true;
			$("#trash-show-button").empty().append("Hide bin ...");
		}
		else {
			$("#trash-container").empty();
			_cms.misc.trash.visible = false;
			$("#trash-show-button").empty().append("Show bin ...");
		}	
	});	
}

_cms.misc.behaviour.trash.empty = function() {
	// Add behaviour to empty the trash 
	$("#trash-empty-button").click(function () {
		
		// Are we emptying specific items, or all of them?
		var option = _cms.misc.trash.getOption();
		var url = "/rest/trash/empty/" + option;
		var params = _cms.misc.trash.getIdParams(option);
		
		$.ajax(_cms.ctx + url, {
			cache: false,
			dataType: "json",
			data: params,
			
			success: function(obj, status, z) {
				_cms.misc.refresh.trash();
				_cms.support.flashMessage(obj);
			},
			error: function(json, status, z) {
				_cms.support.serverError();
			},
		});
	});
}

_cms.misc.trash.getOption = function() {
	// Are we emptying specific itesm, or all of them?
	var option = null;
	$("#trash-action input:checked").each(function() {
	    option = $(this).attr("value");
	});
	
	return option;
}

_cms.misc.trash.getIdParams = function(option) {
	if (option == "selected") {
		var idList = "";
		$("#trash-table input:checked").each(function() {
		    idList += ($(this).attr("value") + ",");
		});
		return {id: idList};
	}	
	return null;
}

_cms.misc.behaviour.trash.restore = function() {
	// Add behaviour to restore the trash 
	$("#trash-restore-button").click(function () {
		
		// Are we restoring specific items, or all of them?
		var option = _cms.misc.trash.getOption();
		var url = "/rest/trash/restore/" + option;
		var params = _cms.misc.trash.getIdParams(option);
		
		$.ajax(_cms.ctx + url, {
			cache: false,
			dataType: "json",
			data: params,
			
			success: function(obj, status, z) {
				/*
				 * Reload the page. Leave the user to
				 * navigate the tree to confirm the restored items are there.
				 */ 
				_cms.support.fetchItemEditor(_cms.editingItemId, 
						{error: false, messageEncoded: encodeURI("Page reloaded; check leftnav for restored items")}, 
						"core-tab");
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
	/*
	 * empty() and restore() are triggered by buttons introduced by the refresh() function,
	 * so do not appear here.
	 * 
	 * _cms.misc.behaviour.trash.empty();
	 * _cms.misc.behaviour.trash.restore();
	 */
	_cms.misc.behaviour.trash.showOrHide();
}
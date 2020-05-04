_cms.support.refreshtabs = function(html, activeTab) {
	var tabsdiv = $("#item-editor");
	tabsdiv.empty().append(html);
	
	// Re-build tabs 
	if (tabsdiv.hasClass("ui-tabs")) {
		tabsdiv.tabs("destroy");
	}
	
	// Focus on same tab as previous render
	var tabName;
	var tabNum = 0;
	var activeTabId = 0;
	
	if (activeTab) {
		$("#editor-tabs a").each(function() {
			tabName = $(this).attr("href").substring(1);
			if (activeTab == tabName) {
				activeTabId = tabNum;
				return false;
			}
			tabNum++;
		});
	}
	
	$("#item-editor").tabs({active: activeTabId});
}

_cms.support.toStatus = function(err, msg) {
	var obj = {};
	obj.error = err;
	obj.message = msg;
	return obj;
};

/*
 * Standard error message.
 */
_cms.support.serverError = function() {
	_cms.support.flashMessage(_cms.support.toStatus(true, "Server error"));
};

/*
 * Displays a flash message, in red for errors, and green for info messages.
 */
_cms.support.flashMessage = function(status) {
	var clazz, msg;
	if (status) {
		clazz = status.error ? "red" : "green";
		msg = status.message;
	}
	else {
		clazz = "red";
		msg = "";
	}
	
	$("#status-block").removeClass("red").removeClass("green").addClass(clazz).append(msg);
	var audio = $("#bell");
	if (audio && audio.get(0)) {
		audio.get(0).play();
	}
};

_cms.support.displayCommerceElements = function(target) {
	var ele = target.find("option:selected");
	var flag = ele.attr("data-isproduct");

	if (flag == 1) {
		$("#core-commerce").css("display", "block");
	}
	else {
		$("#core-commerce").css("display", "none");
	}
};

/*
 * Tells the browser to get the item editor page for a given item.
 */
/*
_cms.support.fetchItemEditor = function(nodeKey, status, tabName) {
	var url = _cms.pageEditorUrlPrefix + nodeKey;
	
	if (status) {
		var param = status.error ? "error" : "success";
		url += ("?status=" + param + "&msg=" + status.messageEncoded);	
	}
	
	if (tabName) {
		url += "&tab=" + tabName;
	}
	
	window.location = url; 
};
*/	

/*
 * Shows a modal giving the user a chance to confirm his selection.
 */
_cms.support.showDialog = function(id, relocate) {
	$("#" + id).dialog({
		modal: true,
		buttons: {
			Ok: function() {
				$(this).dialog("close");
				if (relocate) {
					window.location = relocate;
				}
			}
		}
	});
};

_cms.support.addHistoryBehaviour = function(selector) {
	selector.unbind();
	selector.change(function() {	
		_cms.support.renderItemForms(selector.val(), "core-tab");
	});
};

_cms.support.refreshHistory = function(siteId) {
	$.ajax(_cms.ctx + "/rest/item/history/" + siteId, {
		type: "POST",
		cache: false,
		//data: {key: nodeKey}, 
		dataType: "json",
		mimeType: "application/json",
		
		// On successful loading of forms 
		success: function(list, status, z) {
				var selector = $("#history-selector");
				selector.empty();
				for (var i = 0; i < list.length; i++) {
					selector.append("<option value='" + list[i].itemId + "'>" + list[i].name + "</option>");
				}

				_cms.support.addHistoryBehaviour(selector);					
		},
		error: function(list, status, z) {
			console.log("Error: " + list);
		}
	});
};

_cms.support.refreshtab = function(tab, nodeKey, behavioursFunction) {
	$.ajax(_cms.ctx + "/rest/item/" + nodeKey + "/refresh/" + tab, {
		type: "GET",
		cache: false,
		dataType: "html",
		mimeType: "text/html",
		
		success: function(html, status, z) {
			var ele = $("#" + tab + "-tab");
			ele.empty();
			ele.append(html);
			
			if (behavioursFunction) {
				behavioursFunction(nodeKey);
			}
		},
		error: function(html, status, z) {
			console.log("Error: " + html);
		}
	});
}

_cms.support.renderItemForms = function(nodeKey, activeTab) {
	$.ajax(_cms.ctx + "/rest/item/editor", {
		cache: false,
		data: {key: nodeKey}, 
		dataType: "html",
		mimeType: "text/html",
		
		// On successful loading of forms 
		success: function(html, status, z) {
			_cms.init(nodeKey, html, activeTab);
			_cms.support.refreshHistory(_cms.siteId);
			_cms.core.behaviour.update(nodeKey);
			_cms.field.behaviour.all(nodeKey);
			_cms.add.behaviour.all(nodeKey);
			_cms.copy.behaviour.submit(nodeKey);	
			_cms.version.behaviour.all(nodeKey);
			_cms.links.behaviour.all(nodeKey, true);
			_cms.media.behaviour.upload(nodeKey);
			_cms.misc.behaviour.trash.all(nodeKey);
			_cms.misc.behaviour.reindex(nodeKey);			
		}
	});
};


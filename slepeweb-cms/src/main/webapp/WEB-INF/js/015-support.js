_cms.support.refreshAllTabs = function(html, activeTab) {
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
	
	$("header #status-block").removeClass("red").removeClass("green").addClass(clazz).append(msg);
	
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
_cms.support.fetchItemEditor = function(nodeKey, status, tabName) {
	var url = _cms.pageEditorUrlPrefix;
	if (nodeKey) {
		url += nodeKey;
	}
	
	if (status) {
		var param = status.error ? "error" : "success";
		url += ("?status=" + param + "&msg=" + status.messageEncoded);	
	}
	
	if (tabName) {
		url += "&tab=" + tabName;
	}
	
	window.location = url; 
};

_cms.support.addHistoryBehaviour = function(selector) {
	selector.unbind();
	selector.change(function() {	
		_cms.leftnav.activateKey(selector.val());
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
			
			_cms.support.disableFormsIfReadonly();
		},
		error: function(html, status, z) {
			console.log("Error: " + html);
		}
	});
}

_cms.support.disable = function(selector) {
	$(selector).attr("disabled", "disabled");
}

_cms.support.enable = function(selector) {
	$(selector).removeAttr("disabled");
}

_cms.support.enableIf = function(selector, condition) {
	if (condition) {
		_cms.support.enable(selector);
		return true;
	}
	else {
		_cms.support.disable(selector);
		return false;
	}
}

_cms.support.fs = function(base, name) {
	return _cms.support.f(base, name, "select");
}

_cms.support.fi = function(base, name) {
	return _cms.support.f(base, name, "input");
}

_cms.support.f = function (base, name, type) {
	return base + " " + type + "[name='" + name + "']";
}

_cms.support.setTabIds = function(obj, tabname) {
	obj.TABNAME = tabname;
	obj.TABID = tabname + "-tab";
}

_cms.support.isBlankOrInRange = function(value, min, max) {
	if (value && value > -1) {
		return value >= min && value <= max;
	}
	return true;
}

_cms.support.resetForm = function(fn, nodeKey, e) {
	fn(nodeKey);
	_cms.support.flashMessage(_cms.support.toStatus(true, "Form reset"));
	e.stopPropagation();
}

_cms.support.updateItemName = function(name) {
	$(".current-item-name").empty().html(name);
}

_cms.support.updateSitemapNavigationLinks = () => {
	['left', 'right', 'up', 'down'].forEach((dirn) => {
		let link$ = $('div#leftnav-hider i.fa-angle-' + dirn);
		let key = $('div#' + dirn + '-nav-key').html();
		link$.off();
		
		if (key > -1) {
			link$.click(() => {
				_cms.leftnav.activateKey(key);
			})
			
			link$.removeClass('invisible');
		}
		else {
			link$.addClass('invisible');
		}
	})		
}

_cms.support.disableFormsIfReadonly = function() {
	if (! _cms.editingItemIsWriteable) {
		$("#item-editor input, #item-editor textarea, #item-editor select, #item-editor button").attr("disabled", "disabled");
		$(".readonly-layer").css("z-index", "1").css("opacity", "0.2");
		_cms.support.flashMessage({error: true, message: "Not Authorised"});
	}
	else {
		$(".readonly-layer").css("z-index", "-1").css("opacity", "0");
	}
}

_cms.support.displayTrashFlag = function(isFlagged) {
	var i$ = $('div#trash-item-flag i');
	
	if (! isFlagged) {
		i$.removeClass('flagged').attr('title', 'Flag this item for deletion');
	}
	else {
		i$.addClass('flagged').attr('title', 'Undo deletion flag');
	}
}

_cms.support.trashFlagger = function() {
	var isFlagged = $('div#current-item-flagged4trash').html() === 'yes';
	_cms.support.displayTrashFlag(isFlagged);
	
	$('div#trash-item-flag i').off().click(function() {
		var i$ = $(this);
		var isFlagged = i$.hasClass('flagged');
		var url = _cms.ctx + "/rest/item/" + _cms.editingItemId + "/flag/" + (isFlagged ? 'un' : '') + "trash"
			
		$.ajax(url, {
			type: "GET",
			cache: false,
			dataType: "json",
			
			success: function(flagged, status, z) {
				_cms.support.displayTrashFlag(flagged);
				_cms.misc.trashflags.refresh(_cms.editingItemId);
			},
			error: function(resp, status, z) {
				console.log("Error: " + resp);
			}
		});
	});
}

_cms.support.ajax = function(method, url, data, success, fail) {
	let params = {
		type: method,
		cache: false
	}
	
	if (data.data) {
		params.data = data.data;
	}
	
	if (data.datatype) {
		params.dataType = data.datatype;
	}
	
	if (data.mimetype) {
		params.mimeType = data.mimetype;
	}
	
	if (success) {
		params.success = success;
	}
	
	if (fail) {
		params.error = fail;
	}
	else {
		params.error = function(resp, status, z) {
			console.log("Error: " + resp);
		}
	}
	
	$.ajax(url, params);
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
			_cms.core.onrefresh(nodeKey);
			_cms.field.onrefresh(nodeKey);
			_cms.add.onrefresh(nodeKey);
			_cms.copy.onrefresh(nodeKey);	
			_cms.version.onrefresh(nodeKey);
			_cms.links.onrefresh(nodeKey);
			_cms.media.onrefresh(nodeKey);
			_cms.misc.onrefresh(nodeKey);
			_cms.move.onrefresh(nodeKey);			
			_cms.support.disableFormsIfReadonly();
			_cms.support.trashFlagger();
		}
	});
};


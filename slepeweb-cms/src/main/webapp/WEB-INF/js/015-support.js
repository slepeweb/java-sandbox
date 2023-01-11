_cms.support.refreshAllTabs = function(html, activeTab) {
	var tabsdiv = $("#item-editor");
	//tabsdiv.empty().append(html);
	tabsdiv.html(html);
	
	// Re-build tabs 
	if (tabsdiv.hasClass("ui-tabs")) {
		tabsdiv.tabs("destroy");
	}

	// Focus on same tab as previous render
	_cms.support.activateTab(activeTab);
}

_cms.support.getActiveTab = function() {
	let tab = $("li.ui-tabs-active").attr("aria-controls");
	if (! tab) {
		tab = 'core-tab';
	}
	return tab;
}

_cms.support.activateTab = function(name) {
	let activeTabId = 0;
	let tabNum = 0;
	
	$("#editor-tabs a").each(function() {
		let tabName = $(this).attr("href").substring(1);
		if (tabName === name) {
			activeTabId = tabNum;
			return false;
		}
		tabNum++;
	});
	
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

_cms.support.addHistoryBehaviour = function(selector) {
	selector.unbind();
	selector.change(function() {	
		_cms.leftnav.navigate(selector.val());
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
		let key = eval('_cms.' + dirn + 'NavKey');
		link$.off();
		
		if (key > -1) {
			link$.click(() => {
				_cms.leftnav.navigate(key);
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

_cms.support.displayItemFlag = function(isFlagged) {
	var i$ = $('div#item-flag i');
	
	if (! isFlagged) {
		i$.removeClass('flagged').attr('title', 'Flag this item');
	}
	else {
		i$.addClass('flagged').attr('title', 'Un-flag this item');
	}
}

_cms.support.itemFlagger = {}

_cms.support.itemFlagger.onPageLoad = function() {
	// Flag siblings button
	$('div#item-sibling-flag').click(function() {
		_cms.misc.flaggedItems.ajax("/rest/item/" + _cms.editingItemId + "/flag/siblings", function(args) {
			_cms.support.displayItemFlag(true);
		});
	});
	
	// Un-flag button
	$('div#item-flag-clear').click(function() {
		_cms.misc.flaggedItems.ajax("/rest/flaggedItems/unflag/all", function(args) {
			_cms.support.displayItemFlag(false);
		});
	});
}


_cms.support.itemFlagger.onItemLoad = function() {
	var isFlagged = _cms.currentItemFlagged === 'yes';
	_cms.support.displayItemFlag(isFlagged);
	
	$('div#item-flag i').off().click(function() {
		var i$ = $(this);
		var isFlagged = i$.hasClass('flagged');
		var url = _cms.ctx + "/rest/item/" + _cms.editingItemId + "/" + (isFlagged ? 'un' : '') + "flag";
			
		$.ajax(url, {
			type: "GET",
			cache: false,
			dataType: "json",
			
			success: function(flagged, status, z) {
				_cms.support.displayItemFlag(flagged);
				_cms.misc.flaggedItems.refresh(_cms.editingItemId);
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
		cache: false,
		xhr: data.xhr,
		data: data.data,
		contentType: data.contentType,
		dataType: data.dataType,
		mimeType: data.mimeType,
		success: success,
		processData: data.processData,
		success: success,
		error: fail
	}
	
	if (! params.error) {
		params.error = function(a, b, c) {
			_cms.support.serverError();
			console.log("Server error:", '\na:', a, '\nb:', b, '\nc:', c);
		}
	}
	
	$.ajax(_cms.ctx + url, params);
}

/*
	Re-calculates content according to a newly selected item. This applies to
	both editor tabs, and to page-level controls. (Remember, editor tabs get updated
	far more with ajax calls compared with page re-loads).
*/
_cms.support.renderItemForms = function(nodeKey, activeTab, callback, args) {

	$.ajax(_cms.ctx + "/rest/item/editor", {
		cache: false,
		data: {key: nodeKey}, 
		dataType: "html",
		mimeType: "text/html",
		
		// On successful loading of forms 
		success: function(html, status, z) {
			// origId of currently selected item
			_cms.editingItemId = nodeKey;
			
			// Insert the calculated html into the editor tabs
			_cms.support.refreshAllTabs(html, activeTab);
			
			// Update the item identifier in all tabs
			_cms.support.updateItemName(_cms.currentItemName);
			
			// Update the next/previous links
			_cms.support.updateSitemapNavigationLinks();
		
			// Required for delete confirmation dialog
			$(".num-deletable-items").html(_cms.numDeletableItems);
			
			// Update list of recently edited items
			_cms.support.refreshHistory(_cms.siteId);
			
			// Update flag indicating current item is 'flagged'!
			_cms.support.itemFlagger.onItemLoad();
			
			// Assign behaviours to new html
			_cms.core.onrefresh(nodeKey);
			_cms.field.onrefresh(nodeKey);
			_cms.add.onrefresh(nodeKey);
			_cms.copy.onrefresh(nodeKey);	
			_cms.version.onrefresh(nodeKey);
			_cms.links.onrefresh(nodeKey);
			_cms.media.onrefresh(nodeKey);
			_cms.misc.onrefresh(nodeKey);
			_cms.move.onrefresh(nodeKey);
			
			// Disable forms if user doesn't have access to update
			_cms.support.disableFormsIfReadonly();
			
			// Optinal callback function call
			if (callback) {
				callback(args);
			}
		}
	});
};


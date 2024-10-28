_cms.misc = {
	behaviour: {
		trash: {}
	},
	trash: {
		visible: false
	},
	refresh: {},
	sel: {
		MISC_TAB: "#misc-tab",
		PUBLISH_BUTTON: "#publish-button",
		SEARCH_BUTTON: "#reindex-button",
		TRASH_CONTAINER: "#trash-container",
		SHOW_TRASH_BUTTON: "#trash-show-button",
		EMPTY_TRASH_BUTTON: "#trash-empty-button",
		RESTORE_TRASH_BUTTON: "#trash-restore-button",
		SELECTED_TRASH_OPTION: "#trash-action input:checked",
		SELECTED_TRASH_ITEMS: "#trash-table input:checked",
		PUBLISH_PROGRESSBAR: "#publish-progressbar",
		SEARCH_PROGRESSBAR: "#reindex-progressbar",
		PUBLISH_OPTION: "#misc-tab input[name=publish_option]:checked",
		SEARCH_OPTION: "#misc-tab input[name=searchable_option]:checked",
		ANY_OPTION: "#misc-tab input[type=radio]",
	},
};

// The trash icon used to be in the 'misc' editor tab, but has now been moved
// to the page header.
_cms.misc.sel.DELETE_BUTTON = "div#trash-action-in-header > i";

_cms.support.setTabIds(_cms.misc, "misc");

_cms.misc.behaviour.section_ops = function(nodeKey) {
	
	// Add behaviour to re-index content for search 
	$(_cms.misc.sel.SEARCH_BUTTON).click(function () {
		var option = {
				searchable_option: $(_cms.misc.sel.SEARCH_OPTION).val(),
		}
		
		_cms.misc.opSection(
				nodeKey,
				"POST",
				"/rest/item/" + nodeKey + "/searchable/section",
				$(_cms.misc.sel.SEARCH_PROGRESSBAR),
				option);
	});

	// Add behaviour to publish an entire section 
	$(_cms.misc.sel.PUBLISH_BUTTON).click(function () {
		var option = {
				publish_option: $(_cms.misc.sel.PUBLISH_OPTION).val(),
		}
		
		_cms.misc.opSection(
				nodeKey,
				"POST",
				"/rest/item/" + nodeKey + "/publish/section",
				$(_cms.misc.sel.PUBLISH_PROGRESSBAR),
				option);
	});
	
	$(_cms.misc.sel.ANY_OPTION).click(function(){
		var ancestor = $(this).parent().parent().parent();
		var button = ancestor.find("button");
		button.removeAttr("disabled");
	});
}

_cms.misc.opSection = function(nodeKey, method, url, bar, data, success, error) {
	_cms.misc.updateProgressbar(bar, false);
	
	if (! success) {
		success = function(resp, status, z) {
			_cms.support.flashMessage(resp);
			_cms.misc.updateProgressbar(bar, "destroy");
			_cms.support.refreshtab("core", nodeKey);
			_cms.undoRedo.displayAll(resp.data);
		}
	}
	
	if (! error) {
		error = function(json, status, z) {
			_cms.support.serverError();
			_cms.misc.updateProgressbar(bar, "destroy");
		}
	}
	
	_cms.support.ajax(method, url, {dataType: "json", data: data},
		success,
		error);
}

_cms.misc.refresh.trash = function(fn) {
	_cms.support.ajax('GET', '/rest/trash/get', {dataType: "html", mimeType: "text/html"},
		function(html, status, z) {
			var mydiv = $(_cms.misc.sel.TRASH_CONTAINER);
			mydiv.html(html);
			
			// Apply behaviour for new/replaced buttons
			_cms.misc.behaviour.trash.empty();
			_cms.misc.behaviour.trash.restore();		
			
			if (fn) {
				fn();
			}
		}
	);
}

_cms.misc.behaviour.trash.showOrHide = function() {
	// Add behaviour to show trash contents 
	$(_cms.misc.sel.SHOW_TRASH_BUTTON).click(function () {
		if (! _cms.misc.trash.visible) {
			_cms.misc.refresh.trash();
			_cms.misc.trash.visible = true;
			$(_cms.misc.sel.SHOW_TRASH_BUTTON).empty().append("Hide bin ...");
		}
		else {
			$(_cms.misc.sel.TRASH_CONTAINER).empty();
			_cms.misc.trash.visible = false;
			$(_cms.misc.sel.SHOW_TRASH_BUTTON).empty().append("Show trash bin ...");
		}	
	});	
}

_cms.misc.behaviour.trash.empty = function() {
	// Add behaviour to empty the trash 
	$(_cms.misc.sel.EMPTY_TRASH_BUTTON).click(function () {
		
		// Are we emptying specific items, or all of them?
		var option = _cms.misc.trash.getOption();
		var url = "/rest/trash/empty/" + option;
		var params = _cms.misc.trash.getIdParams(option);
		
		_cms.support.ajax('GET', url, {dataType: "json", data: params},			
			function(obj, status, z) {
				_cms.misc.refresh.trash();
				_cms.support.flashMessage(obj);
			}
		);
	});
}

_cms.misc.trash.getOption = function() {
	// Are we emptying specific itesm, or all of them?
	var option = null;
	$(_cms.misc.sel.SELECTED_TRASH_OPTION).each(function() {
	    option = $(this).attr("value");
	});
	
	return option;
}

_cms.misc.trash.getIdParams = function(option) {
	if (option == "selected") {
		var idList = "";
		$(_cms.misc.sel.SELECTED_TRASH_ITEMS).each(function() {
		    idList += ($(this).attr("value") + ",");
		});
		return {id: idList};
	}	
	return null;
}

_cms.misc.behaviour.trash.restore = function() {
	// Add behaviour to restore the trash 
	$(_cms.misc.sel.RESTORE_TRASH_BUTTON).click(function () {
		
		// Are we restoring specific items, or all of them?
		var option = _cms.misc.trash.getOption();
		var url = "/rest/trash/restore/" + option;
		var params = _cms.misc.trash.getIdParams(option);
		
		_cms.support.ajax('GET', url, {dataType: "json", data: params},			
			function(resp, status, z) {
				if (! resp.error) {
					// Load _FIRST_ restored item into the leftnav. First identify its parent ...
					var parentNode = _cms.leftnav.tree.getNodeByKey(resp.data[0].key);
					
					if (parentNode != null) {
						// Add restored item to parent node in leftnav.
						// parentNode.addNode(resp.data[1]);
						
						// NO - you end up getting only one node below the parent.
						// SO, force-reload ALL items below the parent:
						parentNode.load(true);
					}
					
					// Navigate to the restored item
					_cms.leftnav.navigate(resp.data[1].key, _cms.core.TABID, function() {
						_cms.support.flashMessage(resp);
					});
				}
				else {
					_cms.support.flashMessage(_cms.support.toStatus(true, "Restore failed"));
				}
			}
		);	
	});
}

_cms.misc.behaviour.trash.trash = function(nodeKey) {
	$(_cms.misc.sel.DELETE_BUTTON).click(function() {
		$(".num-descendants").empty().html(_cms.numDeletableItems);
		_cms.dialog.open(_cms.dialog.confirmTrash);
	});
}

_cms.misc.accordionListener = function(e, ui) {
	if (ui.newHeader.text().startsWith('Flagged')) {
		_cms.flags.refreshDialog();
		
		if (_cms.flags.numberOf() > 0) {
			_cms.dialog.open(_cms.dialog.flaggedItems);
		}
	}
	else if (ui.newHeader.text().startsWith('Trash')) {
		_cms.misc.refresh.trash();
	}
}

_cms.misc.updateProgressbar = function(bar, value) {
	if (bar) {
		if (value == false) {
			bar.progressbar({value: value});
		}
		else if (value == "destroy") {
			bar.progressbar("destroy");
		}
	}
}

// Behaviours to apply once html is loaded/reloaded
_cms.misc.onrefresh = function(nodeKey) {
	_cms.misc.behaviour.section_ops(nodeKey);
	/*
	 * empty() and restore() are triggered by buttons introduced by the refresh() function,
	 * so do not appear here.
	 * 
	 * _cms.misc.behaviour.trash.empty();
	 * _cms.misc.behaviour.trash.restore();
	 */
	_cms.misc.behaviour.trash.showOrHide();
	_cms.flags.behaviour();
	_cms.flags.refreshFlaggedSection();
	
	$('#misc-accordion').accordion({
		heightStyle: "content",
		collapsible: true,
		active: false,
		activate: _cms.misc.accordionListener,
	});
	
}
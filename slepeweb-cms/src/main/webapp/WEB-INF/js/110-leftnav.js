_cms.leftnav = {
	behaviour: {},
	define: {},
	dialog: {}
};

// Other modes are "link" and "move"
_cms.leftnav.mode = "navigate";

_cms.leftnav.define.fancytree = function() {
	
	// Manage the left-hand navigation
	$("#leftnav").fancytree({
		source: {
			url: _cms.ctx + "/rest/leftnav/lazy/thread",
			data: _cms.queryParams,
			cache: false,
			checkbox: true,
			complete: function() {				
				// On completion of loading the tree, activate the node for the current item
				_cms.leftnav.tree = $("#leftnav").fancytree("getTree");
				var key = "" + _cms.editingItemId;
				var node = _cms.leftnav.tree.getNodeByKey(key);
				
				if (node) {
					_cms.leftnav.tree.activateKey(node.key);
				}
				
				_cms.leftnav.behaviour.click();
			}
		},
		lazyLoad: function(event, data) {
			var node = data.node;
			data.result = {
				url: _cms.ctx + "/rest/leftnav/lazy/one",
				data: {key: _cms.leftnav.removeShortcutMarker(node.key)}
			};
		},
		activate: function(event, data) {
			if (_cms.leftnav.mode == "navigate") {
				if (! data.node.data.shortcut) {
					// Update the item forms
					var tabName = $("li.ui-tabs-active").attr("aria-controls");
					_cms.support.renderItemForms(data.node.key, tabName);
				}
				else {
					// Do not allow the user to work with the shortcut item - automatically
					// navigate to the real item
					var key = _cms.leftnav.removeShortcutMarker(data.node.key);
					var node = _cms.leftnav.tree.getNodeByKey(key);
					
					if (node) {
						_cms.leftnav.tree.activateKey(node.key);search
					}
					else {
						// The 'real' item hasn't been loaded yet - ask the server for the breadcrumb trail
						if (! _cms.leftnav.loadBreadcrumbs(key)) {
							_cms.support.flashMessage(_cms.support.toStatus(false, "Failed to retrieve breadcrumb trail"));
						}
					}
				}
			}
			else if (_cms.leftnav.mode == "link") {
				$("#link-target-identifier").html("'" + _cms.leftnav.tree.activeNode.title + "'");
				$("#addlinkdiv input[name=childId]").val(_cms.leftnav.tree.activeNode.key);
			}
			else if (_cms.leftnav.mode == "move") {
				$("#move-target-identifier").html("'" + data.node.title + "'");
				_cms.move.activateActionButton();
			}
			
			// Close left nav as soon as node has been selected
			_cms.leftnav.mode = "navigate";
			_cms.leftnav.dialog.close();
		}
	});	
}

_cms.leftnav.loadBreadcrumbs = function(key, fn, args) {
	$.ajax(_cms.ctx + "/rest/breadcrumbs/" + key, {
		cache: false,
		dataType: "json",
		mimeType: "application/json",
		success: function(json, status, z) {
			_cms.leftnav.tree.loadKeyPath(json, function(node, stats) {
				if (fn) {
					fn(args);
				}
				
				if (stats === "ok") {
				    node.setActive();
				    return true;
				}
			});
		},
		error: function(json, status, z) {
			return false;
		}
	});
}

/* We are filtering the left array, by comparing it's elements with the right array.
 * All links that are NOT shortcuts will be filtered out. So will all MATCHING shortcuts.
 * leftIsFancytreeNode = true indicates the left array contains fancytree nodes, and the right array contains form data.
 * leftIsFancytreeNode = false indicates the left array contains form data, and the right array contains fancytree nodes.
 */
_cms.leftnav.filterShortcuts = function(left, leftIsFancytreeNode, right) {
	if (left) {
		return left.filter(function(node, index, fullArray) {
		
			var isShortcut = leftIsFancytreeNode ? node.data.shortcut : node.shortcut;	
			if (! isShortcut) {
				return false;
			}
			
			if (right) {
				for (var j = 0; j < right.length; j++) {
					isShortcut = leftIsFancytreeNode ? right[j].shortcut : right[j].data.shortcut
					if (isShortcut && right[j].key == node.key) {
						return false;
					}
				}
			}
			
			return true;
		});
	}
}

_cms.leftnav.refreshShortcuts = function(parentKey, updatedChildData) {
	var existingParentNode = _cms.leftnav.tree.getNodeByKey(parentKey);
	if (existingParentNode) {
		var existingChildren = existingParentNode.getChildren();
	
		// Remove non-shortcuts, and matching shortcuts from each array
		// First, existing shortcuts
		var filteredExisting = _cms.leftnav.filterShortcuts(existingChildren, true, updatedChildData);
		
		// Next, the updated form data
		var filteredUpdates = _cms.leftnav.filterShortcuts(updatedChildData, false, existingChildren);;
		
		// Remove any nodes remaining in the filteredExisting array
		if (filteredExisting) {
			for (var j = 0; j < filteredExisting.length; j++) {
				existingParentNode.removeChild(filteredExisting[j]);
			}
		}
		
		// Add any nodes remaining in the filteredUpdates array
		if (filteredUpdates) {
			for (var i = 0; i < filteredUpdates.length; i++) {
				existingParentNode.addNode(filteredUpdates[i]);
			}		
		}
	}
};

/* Nodes representing shortcuts in the FancyTree have '.s' appended to their standard key value,
 * so that they are distinguishable from the node representing the 'real' item. This method
 * identifies the numeric part preceding the '.s' suffix.
*/
_cms.leftnav.removeShortcutMarker = function(key) {
	var cursor = key.indexOf(".s");
	if (cursor > -1) {
		return key.substring(0, cursor);
	}
	return key;
};

_cms.leftnav.behaviour.click = function() {
	$("#leftnav-hider").click(function(event) {
		_cms.leftnav.mode = "navigate";
		_cms.leftnav.dialog.open();
	});
}

_cms.leftnav.dialog.open = function() {
	_cms.leftnav.dialog.obj.dialog("open");
}

_cms.leftnav.dialog.close = function() {
	_cms.leftnav.dialog.obj.dialog("close");
}

_cms.leftnav.define.dialog = function() {
	_cms.leftnav.dialog.obj = $("#dialog-leftnav").dialog({
		  autoOpen: false,
		  minHeight: 250,
		  minWidth: 350,
		  modal: true,
		  title: "Content structure",
		  buttons: {
			  Close: function() {
				  _cms.leftnav.dialog.close();
			  }
		  },
		  close: function() {}
	});
}


// Behaviours to apply once html is loaded/reloaded
_cms.leftnav.behaviour.all = function() {
}

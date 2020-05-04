_cms.leftnav = {
	behaviour: {},
};

_cms.leftnav.behaviour.fancytree = function() {
	// Manage the left-hand navigation
	$("#leftnav").fancytree({
		extensions: ["dnd"],
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
				_cms.leftnav.behaviour.mouseleave();
			}
		},
		lazyLoad: function(event, data) {
			var node = data.node;
			data.result = {
				url: _cms.ctx + "/rest/leftnav/lazy/one",
				data: {key: _cms.leftnav.removeShortcutMarker(node.key)}
			};
		},
		dnd: {
			autoExpandMS: 400,
			focusOnClick: false,
			preventVoidMoves: true, // Prevent dropping nodes 'before self', etc.
			preventRecursiveMoves: true, // Prevent dropping nodes on own descendants
			
			dragExpand: function(node, data) {
				return false;
			},
			
			dragStart: function(node, data) {
				return true;
			},
			dragEnter: function(node, data) {
				return true;
			},
			dragDrop: function(node, data) {
			/*
				- data.otherNode is the item being dragged (mover)
				- node is item which is the target of the drop (target)
			*/
				var theDialog = $("#dialog-move-confirm");
				theDialog.dialog({
					resizable: false,
					height:200,
					modal: true,
					buttons: {
						"Move item": function() {
							// Don't allow root items to be moved
							if (! data.otherNode.parent.key.startsWith("root")) {
								$.ajax(_cms.ctx + "/rest/item/" + _cms.leftnav.removeShortcutMarker(data.otherNode.key) + "/move", {
									type: "POST",
									cache: false,
									data: {
										targetId: _cms.leftnav.removeShortcutMarker(node.key),
										targetParentId: _cms.leftnav.removeShortcutMarker(node.parent.key),
										moverParentId: _cms.leftnav.removeShortcutMarker(data.otherNode.parent.key),
										moverIsShortcut: data.otherNode.data.shortcut,
										mode: data.hitMode
									}, 
									dataType: "json",
									success: function(obj, status, z) {
										theDialog.dialog("close");
										data.otherNode.moveTo(node, data.hitMode);
										_cms.support.flashMessage(obj);
									},
									error: function(json, status, z) {
										theDialog.dialog("close");
										_cms.support.serverError();
									}
								});
							}
							else {
								$(this).dialog("close");
							}
						},
						Cancel: function() {
							$(this).dialog("close");
						}
					}
				});
			}
		},
		activate: function(event, data) {
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
					_cms.leftnav.tree.activateKey(node.key);
				}
				else {
					// The 'real' item hasn't been loaded yet - ask the server for the breadcrumb trail
					$.ajax(_cms.ctx + "/rest/breadcrumbs/" + key, {
						cache: false,
						dataType: "json",
						mimeType: "application/json",
						success: function(json, status, z) {
							_cms.leftnav.tree.loadKeyPath(json, function(node, stats) {
								if (stats === "ok") {
								    node.setActive();
								}
							});
						},
						error: function(json, status, z) {
							_cms.support.flashMessage(_cms.support.toStatus(false, "Failed to retrieve breadcrumb trail"));
						}
					});
				}
			}
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
		_cms.leftnavStatus = ! _cms.leftnavStatus;
		if (_cms.leftnavStatus) {
			$("#leftnav").show();
		}
		else {
			$("#leftnav").hide();
		}
	});
}

_cms.leftnav.behaviour.mouseleave = function() {
	$("#leftnav").mouseleave(function() {
		$("#leftnav").hide();
		_cms.leftnavStatus = false;
	});
}

// Behaviours to apply once html is loaded/reloaded
_cms.leftnav.behaviour.all = function() {
	_cms.leftnav.behaviour.fancytree();
}

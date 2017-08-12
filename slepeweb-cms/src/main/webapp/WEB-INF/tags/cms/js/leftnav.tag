<%@ tag %><%@ 
    taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%><%@ 
    taglib prefix="cms" tagdir="/WEB-INF/tags/cms"%><%@ 
    taglib prefix="cmsjs" tagdir="/WEB-INF/tags/cms/js"%>
        
<cms:debug>/* tags/cms/js/leftnav.tag */</cms:debug>

// Manage the left-hand navigation
$("#leftnav").fancytree({
	extensions: ["dnd"],
	source: {
		url: _ctx + "/rest/leftnav/lazy/thread",
		data: queryParams,
		cache: false,
		checkbox: true,
		complete: function() {
			// On completion of loading the tree, activate the node for the current item
			_tree = $("#leftnav").fancytree("getTree");
			var key = "" + _editingItemId;
			var node = _tree.getNodeByKey(key);
			_tree.activateKey(node.key);
		}
	},
	lazyLoad: function(event, data) {
		var node = data.node;
		data.result = {
			url: _ctx + "/rest/leftnav/lazy/one",
			data: {key: removeShortcutMarker(node.key)}
		};
	},
	dnd: {
		autoExpandMS: 400,
		focusOnClick: true,
		preventVoidMoves: true, // Prevent dropping nodes 'before self', etc.
		preventRecursiveMoves: true, // Prevent dropping nodes on own descendants
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
						$.ajax(_ctx + "/rest/item/" + removeShortcutMarker(data.otherNode.key) + "/move", {
							type: "POST",
							cache: false,
							data: {
								targetId: removeShortcutMarker(node.key),
								targetParentId: removeShortcutMarker(node.parent.key),
								moverParentId: removeShortcutMarker(data.otherNode.parent.key),
								moverIsShortcut: data.otherNode.data.shortcut,
								mode: data.hitMode
							}, 
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
		}
	},
	activate: function(event, data) {
		if (! data.node.data.shortcut) {
			// Update the item forms
			var tabName = $("li.ui-tabs-active").attr("aria-controls");
			renderItemForms(data.node.key, tabName);
		}
		else {
			// Do not allow the user to work with the shortcut item - automatically
			// navigate to the real item
			var key = removeShortcutMarker(data.node.key);
			var node = _tree.getNodeByKey(key);
			
			if (node) {
				_tree.activateKey(node.key);
			}
			else {
				// The 'real' item hasn't been loaded yet - ask the server for the breadcrumb trail
				$.ajax(_ctx + "/rest/breadcrumbs/" + key, {
					cache: false,
					dataType: "json",
					mimeType: "application/json",
					success: function(json, status, z) {
						_tree.loadKeyPath(json, function(node, stats) {
							if (stats === "ok") {
							    node.setActive();
							}
						});
					},
					error: function(json, status, z) {
						flashMessage(toStatus(false, "Failed to retrieve breadcrumb trail"));
					}
				});
			}
		}
	}
});	


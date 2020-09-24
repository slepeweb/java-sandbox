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
				data: {key: node.key}
			};
		},
		activate: function(event, data) {
			if (_cms.leftnav.mode == "navigate") {
				// Update the item forms
				var tabName = $("li.ui-tabs-active").attr("aria-controls");
				_cms.support.renderItemForms(data.node.key, tabName);
			}
			else if (_cms.leftnav.mode == "link") {
				$("#link-target-identifier").html("'" + _cms.leftnav.tree.activeNode.title + "'");
				$("#addlinkdiv input[name=childId]").val(_cms.leftnav.tree.activeNode.key);
			}
			else if (_cms.leftnav.mode == "move") {
				$("#move-target-identifier").html("'" + data.node.title + "'");
				_cms.move.check_data_is_complete();
			}
			
			// Close left nav as soon as node has been selected
			_cms.leftnav.mode = "navigate";
			_cms.leftnav.dialog.close();
		}
	});	
}

_cms.leftnav.activateKey = function(key, fn, args) {
	var node = _cms.leftnav.tree.getNodeByKey(key);
	
	if (node) {
		_cms.leftnav.tree.activateKey(node.key);
	}
	else {
		// The 'real' item hasn't been loaded yet - ask the server for the breadcrumb trail
		if (! _cms.leftnav.loadBreadcrumbs(key, fn, args)) {
			_cms.support.flashMessage(_cms.support.toStatus(false, "Failed to retrieve breadcrumb trail"));
		}
	}
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

_cms.leftnav.refreshShortcut = function(nodeKey, action, type) {
	if (action != "none" && type) {
		var node = _cms.leftnav.tree.getNodeByKey(nodeKey);
		if (node && /* node.addClass is only available in later versions */ node.addClass) {
			var clazz = "cms-icon-shortcut-" + type;
			if (action == "add") {
				node.addClass(clazz);
			}
			else if (action == "remove") {
				node.removeClass(clazz);
			}
		}
	}
}

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

_cms.leftnav = {
	behaviour: {},
	define: {},
	dialog: {}
};

/* 
	Modes are "navigate", "link" and "move".
	
	Initially set to "", otherwise the 'activate' function would get
	triggered, and in turn, make a second request to /cms/rest/item/editor
	by executing _cms.support.renderItemForms().
*/
_cms.leftnav.mode = "";

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
				_cms.support.renderItemForms(data.node.key, _cms.activeTab);
			}
			else if (_cms.leftnav.mode == "link") {
				$("#link-target-identifier").html("'" + _cms.leftnav.tree.activeNode.title + "'");
				$("#addlinkdiv input[name=childId]").val(_cms.leftnav.tree.activeNode.key);
			}
			else if (_cms.leftnav.mode == "move") {
				$("#move-target-identifier").html("'" + data.node.title + "'");
				_cms.move.check_data_is_complete();
			}
			else if (_cms.leftnav.mode == "move-flagged") {
				$("#move-target-identifier2").html("'" + data.node.title + "'");
				_cms.flags.move.check_data_is_complete();
			}
			
			// Close left nav as soon as node has been selected
			_cms.leftnav.mode = "navigate";
			_cms.leftnav.dialog.close();
		}
	});	
}

// Navigate to a new item. 
_cms.leftnav.navigate = function(key, tab, successCallback, args) {
	// TODO: Need a better way to provide the preferred editor tab name to the 'activate'
	//       function in FancyTree. This way will have to do for now.
	if (! tab) {
		tab = _cms.support.getActiveTab();
	}
	_cms.activeTab = tab.endsWith('-tab') ? tab : tab + '-tab';
	
	let doSuccessCallback = function() {
		if (successCallback) {
			successCallback(args);
		}
	}
	
	// Is the item of interest already visible in the tree?
	var node = _cms.leftnav.tree.getNodeByKey(key);
	
	if (node) {
		/* 
			'setActive()' triggers the 'activate' function of the FancyTree object,
			which in turn triggers '_cms.support.renderItemForms'. 
			HOWEVER, these events will only take place IFF the node is not
			already active.
		*/
		if (node.isActive()) {
			node.setActive(false);
		}
		node.setActive(true);
		doSuccessCallback();
	}
	else {
		// This item hasn't been loaded into the fancytree yet 
		// - ask the server for the breadcrumb trail, and update the leftnav accordingly
		_cms.leftnav.loadBreadcrumbs(
			key, 
			function() {
				doSuccessCallback();
			},
			function() {
				_cms.support.flashMessage(_cms.support.toStatus(false, "Failed to update the leftnav tree"));
			}
		);
	}
}

_cms.leftnav.loadBreadcrumbs = function(key, successCallback, errorCallback) {
	_cms.support.ajax('GET', '/rest/breadcrumbs/' + key, {dataType: 'json', mimeType: 'application/json'},
		function(json, status, z) {
			_cms.leftnav.tree.loadKeyPath(json, function(node, status) {
				//console.log('_cms.leftnav.tree.loadKeyPath:', node.key, status);
				if (status === "ok") {
				    node.setActive();
				    successCallback
				}
			});
		},
		errorCallback
	);
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
	$("#leftnav-hider i.fa-sitemap").click(function(event) {
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

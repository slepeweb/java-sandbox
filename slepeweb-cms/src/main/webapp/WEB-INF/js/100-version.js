_cms.version = {
	behaviour: {},
	refresh: {},
	sel: {
		VERSION_BUTTON: "#version-button",
		REVERT_BUTTON: "#revert-button",
	}
};

_cms.support.setTabIds(_cms.version, "version");

_cms.version.behaviour.action = function(nodeKey) {
	// Add behaviour to create a new version 
	$(_cms.version.sel.VERSION_BUTTON).click(function () {
		_cms.support.ajax('POST', '/rest/item/' + nodeKey + '/version', {dataType: 'json'},
			function(obj, status, z) {
				_cms.support.flashMessage(obj);
				// All tabs should be refreshed since the current item has changed
				_cms.leftnav.navigate(nodeKey, _cms.version.TABID);
			}
		);
	});
}

_cms.version.behaviour.revert = function(nodeKey) {
	// Add behaviour to revert to a previous version 
	$(_cms.version.sel.REVERT_BUTTON).click(function () {
		_cms.support.ajax('POST', '/rest/item/' + nodeKey + '/revert', {dataType: 'json'},
			function(obj, status, z) {
				_cms.support.flashMessage(obj);
				// All tabs should be refreshed since the current item has changed
				_cms.leftnav.navigate(nodeKey, _cms.version.TABID);
			}
		);
	});
}

_cms.version.refresh.tab = function(nodeKey) {
	_cms.support.refreshtab(_cms.version.TABNAME, nodeKey, _cms.version.onrefresh);
};


// Behaviours to apply once html is loaded/reloaded
_cms.version.onrefresh = function(nodeKey) {
	_cms.version.behaviour.action(nodeKey);
	_cms.version.behaviour.revert(nodeKey);
}
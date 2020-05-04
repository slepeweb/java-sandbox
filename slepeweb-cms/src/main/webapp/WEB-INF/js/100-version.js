_cms.version = {
	behaviour: {},
	refresh: {}
};

_cms.version.behaviour.action = function(nodeKey) {
	// Add behaviour to create a new version 
	$("#version-button").click(function () {
		$.ajax(_cms.ctx + "/rest/item/" + nodeKey + "/version", {
			type: "POST",
			cache: false,
			dataType: "json",
			success: function(obj, status, z) {
				_cms.support.flashMessage(obj);
				// All tabs should be refreshed since the current item has changed
				_cms.support.renderItemForms(nodeKey, "version-tab");
			},
			error: function(json, status, z) {
				_cms.support.serverError();
			},
		});
	});
}

_cms.version.behaviour.revert = function(nodeKey) {
	// Add behaviour to revert to a previous version 
	$("#revert-button").click(function () {
		$.ajax(_cms.ctx + "/rest/item/" + nodeKey + "/revert", {
			type: "POST",
			cache: false,
			dataType: "json",
			success: function(obj, status, z) {
				_cms.support.flashMessage(obj);
				// All tabs should be refreshed since the current item has changed
				_cms.support.renderItemForms(nodeKey, "version-tab");
			},
			error: function(json, status, z) {
				_cms.support.serverError();
			},
		});
	});
}

_cms.version.refresh.tab = function(nodeKey) {
	_cms.support.refreshtab("version", nodeKey, _cms.version.behaviour.all);
};


// Behaviours to apply once html is loaded/reloaded
_cms.version.behaviour.all = function(nodeKey) {
	_cms.version.behaviour.action(nodeKey);
	_cms.version.behaviour.revert(nodeKey);
}
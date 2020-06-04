var _cms = {
	support: {
		dialog: {}
	},
	leftnav: {
		behaviour: {}
	}
}

_cms.init = function(nodeKey, html, activeTab) {
	// TODO: not sure that this duplication is necessary
	_cms.editingItemId = nodeKey;
	
	// Re-build the item editor tabs, and select the required tab
	_cms.support.refreshtabs(html, activeTab);
	
	_cms.editingItemName = $("#current-item-name").html();
	$("#currently-editing").html(_cms.editingItemName);
}
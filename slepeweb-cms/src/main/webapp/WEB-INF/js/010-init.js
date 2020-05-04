var _cms = {
	support: {},
	leftnav: {
		behaviour: {}
	}
}

_cms.init = function(nodeKey, html, activeTab) {
	// TODO: not sure that this duplication is necessary
	_cms.editingItemId = nodeKey;
	
	// Re-build the item editor tabs, and select the required tab
	_cms.support.refreshtabs(html, activeTab);
}
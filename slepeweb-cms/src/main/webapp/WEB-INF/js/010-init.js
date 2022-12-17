var _cms = {
	support: {
		dialog: {}
	},
	leftnav: {
		behaviour: {}
	}
}

_cms.init = function(nodeKey, html, activeTab) {
	// Re-build the item editor tabs, and select the required tab
	_cms.support.refreshAllTabs(html, activeTab);
}
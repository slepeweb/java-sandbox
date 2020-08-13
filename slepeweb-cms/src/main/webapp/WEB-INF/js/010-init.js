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
	_cms.support.refreshAllTabs(html, activeTab);
	
	// Update the item identifier in all tabs
	_cms.support.updateItemName($("#current-item-name").html());

	// Required for delete confirmation dialog
	_cms.numDeletableItems = $("#num-deletable-items").html();
	$(".num-deletable-items").html(_cms.numDeletableItems);
}
_cms.core = {
	behaviour: {},
	refresh: {}
};

_cms.core.behaviour.update = function(nodeKey) {
	// Add behaviour to submit core item updates 
	$("#core-button").click(function () {
		var isProduct = $("#itemIsProductFlag").val() == "true";
		var args = {
			name: $("#core-tab input[name='name']").val(),
			simplename: $("#core-tab input[name='simplename']").val(),
			template: $("#core-tab select[name='template']").val(),
			searchable: $("#core-tab input[name='searchable']").is(':checked'),
			published: $("#core-tab input[name='published']").is(':checked'),
			tags: $("#core-tab input[name='tags']").val()
		};
		
		if (isProduct) {
				args.partNum = $("#core-tab input[name='partNum']").val();
				args.price = Math.floor($("#core-tab input[name='price']").val() * 100);
				args.stock = $("#core-tab input[name='stock']").val();
		}
		
		$.ajax(_cms.ctx + "/rest/item/" + nodeKey + "/update/core", {
			type: "POST",
			cache: false,
			data: args, 
			dataType: "json",
			success: function(resp, status, z) {
				if (! resp.error) {
					// Name may have changed, so navigation tree will need updating
					var node = _cms.leftnav.tree.getNodeByKey(nodeKey);
					if (node) {
						node.setTitle(resp.data[0].title);
					}
				}
				
				_cms.support.flashMessage(resp.data[0]);
				_cms.core.refresh.tab(nodeKey);
				
				if (resp.data[2]) {
					// The published status of the item has changed
					_cms.version.refresh.tab(nodeKey);
				}
				
				if (resp.data[1]) {
					// The first version has reach published status
					// TODO: This is NOT needed on a staging server
					_cms.media.refresh.tab(nodeKey);
				}
			},
			error: function(json, status, z) {
				_cms.support.serverError();
			}
		});
	});
	
	// TODO: there are NO date field on the core tab - move to field tab
	$("#core-tab .datepicker").datepicker({
		dateFormat: "yy-mm-dd",
		changeMonth: true,
		changeYear: true
	});
}

_cms.core.refresh.tab = function(nodeKey) {
	_cms.support.refreshtab("core", nodeKey, _cms.core.behaviour.update);
};

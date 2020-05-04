_cms.copy = {
	behaviour: {},
};

_cms.copy.behaviour.submit = function(nodeKey) {

	// Add behaviour to copy an item 
	$("#copy-button").click(function () {
		$.ajax(_cms.ctx + "/rest/item/" + nodeKey + "/copy", {
			type: "POST",
			cache: false,
			data: {
				name: $("#copy-tab input[name='name']").val(),
				simplename: $("#copy-tab input[name='simplename']").val()
			}, 
			dataType: "json",
			success: function(obj, status, z) {
				_cms.support.flashMessage(obj);
				
				if (! obj.error) {
					var sourceNode = _cms.leftnav.tree.getNodeByKey(nodeKey);
					var newNode = sourceNode.getParent().addNode(obj.data);

					// This triggers a call to loads the editor with the newly created item
					newNode.setActive();
				}
			},
			error: function(json, status, z) {
				_cms.support.serverError();
			},
		});
	});
}

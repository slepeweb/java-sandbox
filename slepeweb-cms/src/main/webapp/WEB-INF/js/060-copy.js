_cms.copy = {
	behaviour: {},
	sel: {
		COPY_BUTTON: "#copy-button",
		COPY_TAB: "#copy-tab",
	}
};

_cms.copy.sel.NAME_INPUT = _cms.support.fi(_cms.copy.sel.COPY_TAB, "name");
_cms.copy.sel.SIMPLENAME_INPUT = _cms.support.fi(_cms.copy.sel.COPY_TAB, "simplename");
_cms.copy.sel.ALL_INPUTS = "".concat(_cms.copy.sel.NAME_INPUT, ",", _cms.copy.sel.SIMPLENAME_INPUT);

_cms.copy.behaviour.submit = function(nodeKey) {

	// Add behaviour to copy an item 
	$(_cms.copy.sel.COPY_BUTTON).click(function () {
		$.ajax(_cms.ctx + "/rest/item/" + nodeKey + "/copy", {
			type: "POST",
			cache: false,
			data: {
				name: $(_cms.copy.sel.NAME_INPUT).val(),
				simplename: $(_cms.copy.sel.SIMPLENAME_INPUT).val()
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

_cms.copy.behaviour.formchange = function(nodeKey) {
	$(_cms.copy.sel.ALL_INPUTS).mouseleave(function() {
		_cms.support.enableIf(_cms.copy.sel.COPY_BUTTON,
				$(_cms.copy.sel.NAME_INPUT).val() && $(_cms.copy.sel.SIMPLENAME_INPUT).val());
	});
}

_cms.copy.onrefresh = function(nodeKey) {
	_cms.copy.behaviour.submit(nodeKey);
	_cms.copy.behaviour.formchange();
}

_cms.copy = {
	behaviour: {},
	refresh: {},
	sel: {
		COPY_TAB: "#copy-tab",
	}
};

_cms.copy.sel.NAME_INPUT = _cms.support.fi(_cms.copy.sel.COPY_TAB, "name");
_cms.copy.sel.SIMPLENAME_INPUT = _cms.support.fi(_cms.copy.sel.COPY_TAB, "simplename");
_cms.copy.sel.ALL_INPUTS = "".concat(_cms.copy.sel.NAME_INPUT, ",", _cms.copy.sel.SIMPLENAME_INPUT);
_cms.copy.sel.COPY_BUTTON = _cms.copy.sel.COPY_TAB + " button.action";
_cms.copy.sel.RESET_BUTTON = _cms.copy.sel.COPY_TAB + " button.reset";
_cms.copy.sel.FORM = _cms.copy.sel.COPY_TAB + " form";

_cms.support.setTabIds(_cms.copy, "copy");

_cms.copy.behaviour.submit = function(nodeKey) {

	// Add behaviour to copy an item 
	$(_cms.copy.sel.COPY_BUTTON).click(function () {
		_cms.support.ajax('POST', '/rest/item/' + nodeKey + '/copy', 
			{
				data: {
					name: $(_cms.copy.sel.NAME_INPUT).val(),
					simplename: $(_cms.copy.sel.SIMPLENAME_INPUT).val()
				}, 
				dataType: 'json'
			},
			function(resp, status, z) {
				_cms.support.flashMessage(resp);
				_cms.undoRedo.displayAll(resp.data[1]);
				
				if (! resp.error) {
					var sourceNode = _cms.leftnav.tree.getNodeByKey(nodeKey);
					var newNode = sourceNode.getParent().addNode(resp.data[0]);
					_cms.leftnav.navigate(newNode.key, 'core');
				}
			}
		);
	});
}

_cms.copy.setButtonStates = function() {
	if ($(_cms.copy.sel.FORM).serialize() !== _cms.copy.originalFormState) {
		_cms.support.enable(_cms.copy.sel.RESET_BUTTON);
		
		if ($(_cms.copy.sel.NAME_INPUT).val()) {
			_cms.support.enable(_cms.copy.sel.COPY_BUTTON);
		}
		else {
			_cms.support.disable(_cms.copy.sel.COPY_BUTTON);
		}
	}
	else {
		_cms.support.enable(_cms.copy.sel.COPY_BUTTON);
		_cms.support.disable(_cms.copy.sel.RESET_BUTTON);
	}			
}

_cms.copy.behaviour.formchange = function(nodeKey) {
	if (_cms.editingItemIsWriteable) {
		$(_cms.copy.sel.ALL_INPUTS).mouseleave(_cms.copy.setButtonStates);		
		$(_cms.copy.sel.FORM + ' button').mouseenter(_cms.copy.setButtonStates);		
	}
}

_cms.copy.refresh.tab = function(nodeKey) {
	_cms.support.refreshtab("copy", nodeKey, _cms.copy.onrefresh);
};

_cms.copy.behaviour.reset = function(nodeKey) {
	$(_cms.copy.sel.RESET_BUTTON).click(function (e) {
		_cms.support.resetForm(_cms.copy.refresh.tab, nodeKey, e);
	});
}

_cms.copy.onrefresh = function(nodeKey) {
	_cms.copy.behaviour.submit(nodeKey);
	_cms.copy.behaviour.formchange();
	_cms.copy.behaviour.reset(nodeKey);
	_cms.copy.originalFormState = $(_cms.copy.sel.FORM).serialize();
	
}

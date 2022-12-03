_cms.move = {
	behaviour: {},
	refresh: {},
	sel: {
		MOVE_TAB: "#move-tab",
		MOVE_WRAPPER: "#move-wrapper",
		MOVE_BUTTON: "#move-item-button",
		MOVE_TARGET_ID: "#move-target-identifier",
	}
};

_cms.move.sel.POSITION_SELECTOR = _cms.support.fs(_cms.move.sel.MOVE_WRAPPER, "position");
_cms.move.sel.ITEM_PICKER = _cms.move.sel.MOVE_WRAPPER + " i.itempicker";
_cms.move.sel.MOVE_BUTTON = _cms.move.sel.MOVE_TAB + " button.action",
_cms.move.sel.RESET_BUTTON = _cms.move.sel.MOVE_TAB + " button.reset",

_cms.support.setTabIds(_cms.move, "move");

_cms.move.action = function(nodeKey) {
	var position = $(_cms.move.sel.POSITION_SELECTOR).val();
	var moverNode = _cms.leftnav.tree.getNodeByKey(nodeKey);
	var targetNode = _cms.leftnav.tree.activeNode;
	var targetKey = targetNode.key;
	
	if (position != 'none' && moverNode && targetNode) {			
		if (! moverNode.parent.key.startsWith("root")) {
			$.ajax(_cms.ctx + "/rest/item/" + moverNode.key + "/move", {
				type: "POST",
				cache: false,
				data: {
					targetId: targetNode.key,
					targetParentId: targetNode.parent.key,
					moverParentId: moverNode.parent.key,
					mode: position
				}, 
				dataType: "json",
				success: function(obj, status, z) {
					if (! obj.error) {
						moverNode.moveTo(targetNode, position);
						targetNode.setActive(false);
						_cms.move.refresh.tab(nodeKey);
						_cms.core.refresh.tab(nodeKey);
						// TODO: _cms.undoRedo.displayAll(obj.data);
					}
					_cms.support.flashMessage(obj);
				},
				error: function(json, status, z) {
					_cms.support.serverError();
				}
			});
		}
	}
};

_cms.move.behaviour.action = function(nodeKey) {
	$(_cms.move.sel.MOVE_BUTTON).click(function() {
		_cms.move.action(nodeKey);
	});
}

_cms.move.behaviour.itempicker = function() {
	$(_cms.move.sel.ITEM_PICKER).click(function() {
		_cms.leftnav.mode = "move";
		_cms.leftnav.dialog.open();
	});
}

_cms.move.dataIsComplete = function() {
	var position = $(_cms.move.sel.POSITION_SELECTOR).val();
	var target = $(_cms.move.sel.MOVE_TARGET_ID).html();
	return position != "none" && target.startsWith("'");
}

_cms.move.behaviour.changePosition = function() {
	$(_cms.move.sel.POSITION_SELECTOR).change(function(){
		_cms.move.check_data_is_complete();
	});
}

_cms.move.check_data_is_complete = function() {
	var isComplete = $(_cms.move.sel.POSITION_SELECTOR).val() != "none" && 
		$(_cms.move.sel.MOVE_TARGET_ID).html().startsWith("'");
	
	if (_cms.support.enableIf(_cms.move.sel.MOVE_BUTTON, isComplete)) {
		_cms.support.enable(_cms.move.sel.RESET_BUTTON);
	}
	else {
		_cms.support.disable(_cms.move.sel.RESET_BUTTON);
	}
}

_cms.move.behaviour.reset = function(nodeKey) {
	$(_cms.move.sel.RESET_BUTTON).click(function (e) {
		_cms.support.resetForm(_cms.move.refresh.tab, nodeKey, e);
	});
}

_cms.move.refresh.tab = function(nodeKey) {
	_cms.support.refreshtab(_cms.move.TABNAME, nodeKey, _cms.move.onrefresh);
};

// Behaviours to apply once html is loaded/reloaded
_cms.move.onrefresh = function(nodeKey) {
	_cms.move.behaviour.action(nodeKey.toString());
	_cms.move.behaviour.reset(nodeKey);
	_cms.move.behaviour.itempicker();
	_cms.move.behaviour.changePosition();
}


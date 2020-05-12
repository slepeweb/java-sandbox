_cms.move = {
	behaviour: {},
	tree: null,
};

_cms.move.action = function(nodeKey) {
	var position = $("#move-wrapper select[name='position']").val();
	var moverNode = _cms.leftnav.tree.getNodeByKey(nodeKey);
	var targetNode = _cms.leftnav.tree.activeNode;
	var targetKey = targetNode.key;
	
	if (position != 'none' && moverNode && targetNode) {			
		if (! moverNode.parent.key.startsWith("root")) {
			$.ajax(_cms.ctx + "/rest/item/" + _cms.leftnav.removeShortcutMarker(moverNode.key) + "/move", {
				type: "POST",
				cache: false,
				data: {
					targetId: _cms.leftnav.removeShortcutMarker(targetNode.key),
					targetParentId: _cms.leftnav.removeShortcutMarker(targetNode.parent.key),
					moverParentId: _cms.leftnav.removeShortcutMarker(moverNode.parent.key),
					moverIsShortcut: moverNode.data.shortcut,
					mode: position
				}, 
				dataType: "json",
				success: function(obj, status, z) {
					if (! obj.error) {
						moverNode.moveTo(targetNode, position);
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
	$("#move-item-button").click(function() {
		_cms.move.action(nodeKey);
	});
}

_cms.move.behaviour.itempicker = function() {
	$("#move-wrapper i.itempicker").click(function() {
		_cms.leftnav.mode = "move";
		_cms.leftnav.dialog.open();
	});
}

_cms.move.dataIsComplete = function() {
	var position = $("#move-wrapper select[name='position']").val();
	var target = $("#move-target-identifier").html();
	return position != "none" && target.startsWith("'");
}

_cms.move.activateActionButton = function() {
	var button = $("#move-item-button");
	 
	if (_cms.move.dataIsComplete()) {
		button.removeAttr("disabled");
	}
	else {
		button.attr("disabled", "disabled");
	}
}

_cms.move.behaviour.position = function() {
	$("#move-wrapper select[name='position']").change(function(){
		_cms.move.activateActionButton();
	});
}


// Behaviours to apply once html is loaded/reloaded
_cms.move.behaviour.all = function(nodeKey) {
	_cms.move.behaviour.action(nodeKey);
	_cms.move.behaviour.itempicker();
	_cms.move.behaviour.position();
}


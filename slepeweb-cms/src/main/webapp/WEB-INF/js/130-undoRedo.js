_cms.undoRedo.displayAll = function(status) {
	_cms.undoRedo.display('undo', 'div#undo-icon', status.undo);
	_cms.undoRedo.display('redo', 'div#redo-icon', status.redo);
}

_cms.undoRedo.display = function(which, container, status) {
	let div$ = $(container);
	let i$ = div$.find('i');
	div$.css('visibility', status.available ? 'visible' : 'hidden');
	i$.attr('title', status.help);
}

// Options are: undo, redo, clear
_cms.undoRedo.behaviour = function(selector, option) {
	let icon$ = $(selector + ' i');
	
	icon$.off().click(function() {
		_cms.support.ajax('GET', '/rest/item/update/' + option, {dataType: 'json'},
			function(resp, status, z) {
				let undoRedoStatus = resp.data[0];
				let action = resp.data[1];
				let targetItemOrigId = resp.data[2];
				let moverData = resp.data[3];
				
				// Re-render the undo/redo icons on the page, according to the new status
				_cms.undoRedo.displayAll(undoRedoStatus);
				
				// Undo/redo might affect another item, ie not the same one that is
				// currently being edited. Re-render the forms for the item affected, 
				// just in case.
				if (option !== 'clear') {
					if (targetItemOrigId !== parseInt(_cms.editingItemId)) {
						// The undo/redo operation was not on the current item - navigate to the target item
						_cms.leftnav.navigate(targetItemOrigId, action, function() {
							_cms.support.flashMessage({error: resp.error, message: resp.message});
						});
					}
					else if (action !== null) {
						// Refresh the tab for the given action (eg core, fields, media, etc)
						_cms[action].refresh.tab(targetItemOrigId);
						_cms.support.activateTab(action + '-tab');
						_cms.support.flashMessage({error: resp.error, message: resp.message});
					}
					
					if (action === 'move') {
						// Update the left nav
						var position = moverData[0];
						var moverNode = _cms.leftnav.tree.getNodeByKey(targetItemOrigId);
						var targetNode = _cms.leftnav.tree.getNodeByKey(moverData[1]);
						moverNode.moveTo(targetNode, position);
						moverNode.setActive(true);
						
						// Refresh the core tab, to reflect the change in the item path
						_cms.core.refresh.tab(targetItemOrigId);
					}
				}
				else {
					_cms.support.flashMessage({error: resp.error, message: resp.message});
				}	
			}
		);
	});
}

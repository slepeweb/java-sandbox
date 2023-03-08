_cms.flags = {
	move: {},
	sel: {
		FLAGGED_ITEMS_SECTION: 'div#flagged-items-section',
		COPY_DATA_SECTION: 'div#copy-data-section',
		COPY_DOWN_ARROW: 'p#copy-data-downarrow',
		MOVE_SECTION: 'div#move-flagged-section',
	}
};

_cms.flags.sel.COPY_DATA_FORM = _cms.flags.sel.COPY_DATA_SECTION + ' div#copy-flagged-data-form';
_cms.flags.sel.COPY_BUTTON = _cms.flags.sel.FLAGGED_ITEMS_SECTION + ' button#copy-data-button';
_cms.flags.sel.COPY_DATA_REFRESHER = _cms.flags.sel.COPY_DATA_SECTION + ' i.refresher';
_cms.flags.sel.TRASH_BUTTON = _cms.flags.sel.FLAGGED_ITEMS_SECTION + ' button#trash-button';
_cms.flags.sel.MOVE_ITEM_PICKER = _cms.flags.sel.MOVE_SECTION + ' i.itempicker';
_cms.flags.sel.MOVE_MODE_SELECT = _cms.flags.sel.MOVE_SECTION + ' select[name=position]';
_cms.flags.sel.MOVE_ACTION_BUTTON = _cms.flags.sel.FLAGGED_ITEMS_SECTION + ' button#move-flagged-button';
_cms.flags.sel.MOVE_TARGET_ID = _cms.flags.sel.MOVE_SECTION + ' #move-target-identifier2';

_cms.flags.numberOf = function() {
	return $('div#flagged-items-dialog li').length;
}

_cms.flags.reportIfNotExist = function() {
	let exist = _cms.flags.numberOf() > 0;
	if (! exist) {
		_cms.support.flashMessage(_cms.support.toStatus(true, 'No action taken - zero items flagged'));
	}
	return exist;
}

_cms.flags.trashItems = function() {
	if (_cms.flags.reportIfNotExist()) {
		_cms.dialog.open(_cms.dialog.eggTimer);
		_cms.support.ajax('GET', '/rest/flaggedItems/trash/all', {}, function(a,b,c) {
			_cms.dialog.close(_cms.dialog.eggTimer);
			window.location = _cms.ctx + '/page/editor/' + _cms.rootItemOrigId +'?status=success&msg=Flagged items trashed - now on Homepage'; 
		});
	}
}

_cms.flags.behaviour = function() {
	// Trash button
	$(_cms.flags.sel.TRASH_BUTTON).click(function(e) {
		_cms.dialog.open(_cms.dialog.confirmTrashFlagged);
	});
	
	// Copy data button
	$(_cms.flags.sel.COPY_BUTTON).click(function(e) {
		if (_cms.flags.reportIfNotExist()) {
			_cms.dialog.open(_cms.dialog.eggTimer);
			
			var params = {
				dataType: 'json',
				mimeType: 'application/json',
				data: {},
			};
			
			_cms.flags.collateFormData('copy-core-data', '0', params);
			_cms.flags.collateFormData('copy-fieldvalue', '1', params);
			let url = '/rest/flaggedItems/copy/all/' + _cms.editingItemId;
					
			_cms.support.ajax('POST', url, params, function(resp, status, z) {
				if (resp.data[0]) {
					// The current item was subject to the copy action, so refresh the respective tabs
					_cms.core.refresh.tab(_cms.editingItemId);
					_cms.field.refresh.tab(_cms.editingItemId);
				}
				
				_cms.dialog.close(_cms.dialog.eggTimer);
				_cms.support.flashMessage(_cms.support.toStatus(resp.error, resp.message));
				_cms.undoRedo.displayAll(resp.data[1]);
			});
		}
	});
	
	// Copy dialog show or hide button
	$(_cms.flags.sel.COPY_DOWN_ARROW).click(function() {
		var div$ = $(_cms.flags.sel.COPY_DATA_SECTION);
		var i$ = $(this).find('i');
		var hide = 'hide';
		var uparrow = 'fa-angle-up';
		var downarrow = 'fa-angle-down';
		
		if (div$.hasClass(hide)) {
			div$.removeClass(hide);
			i$.removeClass(downarrow);
			i$.addClass(uparrow);
		}
		else {
			div$.addClass(hide);
			i$.removeClass(uparrow);
			i$.addClass(downarrow);
		}
	});	
	
	// Refresh data in copy-flagged-items form
	$(_cms.flags.sel.COPY_DATA_REFRESHER).click(function() {
		_cms.flags.refreshCopyForm(_cms.editingItemId);
	});
	
	// Item picker, for move destination
	$(_cms.flags.sel.MOVE_ITEM_PICKER).click(function() {
		_cms.leftnav.mode = "move-flagged";
		_cms.leftnav.dialog.open();
	});	
	
	// Mode selector for move op
	$(_cms.flags.sel.MOVE_MODE_SELECT).change(function(){
		_cms.flags.move.check_data_is_complete();
	});

	// Move button
	$(_cms.flags.sel.MOVE_ACTION_BUTTON).click(function(e) {
		if (_cms.flags.reportIfNotExist()) {
			_cms.dialog.open(_cms.dialog.eggTimer);
			
			var params = {
				mode: $(_cms.flags.sel.MOVE_MODE_SELECT).val(),
				target: _cms.leftnav.tree.activeNode.key
			};
			
			_cms.support.ajax('POST', '/rest/flaggedItems/move', {data: params, dataType: 'json'}, 
				function(resp,b,c) {
					_cms.dialog.close(_cms.dialog.eggTimer);
					
					if (! resp.error) {
						window.location = _cms.ctx + '/page/editor/' + resp.data + 
							'?status=' + (resp.error ? 'error' : 'success') + '&msg=' + resp.message; 
					}
				});
		}
	});
}

_cms.flags.collateFormData = function(clazz, type, params) {
	$(`${_cms.flags.sel.COPY_DATA_SECTION} input.${clazz}:checked`).each(function(index, ele) {
		var checkbox$ = $(ele);
		var input$ = checkbox$.next().next();
		var name = type + '$' + checkbox$.attr('data-name');
		var value;
					
		if (input$.attr('type') === 'text' || input$[0].tagName === 'TEXTAREA') {
			value = input$.val();
		}
		else if (input$.attr('type') === 'checkbox') {
			value = input$.prop('checked') ? 'checked' : 'unchecked';
		}
		
		params.data[name] = value;
	});
}

_cms.flags.refreshDialog = function() {
	_cms.support.ajax('GET', '/rest/flaggedItems/list', {dataType: 'html', mimeType: 'text/html'}, 
		function(html, status, z) {
			$('#flagged-items-dialog').html(html);
			
			// Display the current number of flagged items
			let numFlags = _cms.flags.numberOf();
			$('span.num-flags').html(numFlags.toString());
			
			// Hide the entire 'flagged items' section if there are no flagged items
			_cms.support.hideIf(_cms.flags.sel.FLAGGED_ITEMS_SECTION, numFlags === 0);
		}
	);
}

_cms.flags.refreshFlaggedSection = function() {
	// This may update the number of flagged items
	//if (_cms.dialog.flaggedItems.open) {
		_cms.flags.refreshDialog();
	//}
	
}

_cms.flags.refreshCopyForm = function(key) {
	_cms.support.ajax('GET', '/rest/item/' + key + '/refresh/copyFlaggedForm', {dataType: 'html', mimeType: 'text/html'}, 
		function(html, status, z) {
			$(_cms.flags.sel.COPY_DATA_FORM).html(html);
		}
	);
}

_cms.flags.move.check_data_is_complete = function() {
	var isComplete = $(_cms.flags.sel.MOVE_MODE_SELECT).val() != "none" && 
		$(_cms.flags.sel.MOVE_TARGET_ID).html().startsWith("'");
	
	_cms.support.enableIf(_cms.flags.sel.MOVE_ACTION_BUTTON, isComplete);
}



_cms.flags = {
};

_cms.flags.exist = function() {
	let exist = $('div#flagged-items-dialog li').length > 0;
	if (! exist) {
		_cms.support.flashMessage(_cms.support.toStatus(true, 'No action taken - zero items flagged'));
	}
	return exist;
}

_cms.flags.behaviour = function() {
	// Trash button
	$('div#flagged-items-section button#trash-button').click(function(e) {
		if (_cms.flags.exist()) {
			_cms.dialog.open(_cms.dialog.eggTimer);
			_cms.support.ajax('GET', '/rest/flaggedItems/trash/all', {}, function(a,b,c) {
				_cms.dialog.close(_cms.dialog.eggTimer);
				window.location = _cms.ctx + '/page/editor/' + _cms.rootItemOrigId +'?status=success&msg=Flagged items trashed - now on Homepage'; 
			});
		}
	});
	
	// Copy data button
	$('div#flagged-items-section button#copy-data-button').click(function(e) {
		if (_cms.flags.exist()) {
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
	$('p#copy-data-downarrow').click(function() {
		var div$ = $('div#copy-data-section');
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
	$('div#copy-data-section i.refresher').click(function() {
		_cms.flags.refreshCopyForm(_cms.editingItemId);
	});
}

_cms.flags.collateFormData = function(clazz, type, params) {
	$(`div#copy-data-section input.${clazz}:checked`).each(function(index, ele) {
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
		}
	);
}

_cms.flags.refreshDialogIfOpen = function() {
	if (_cms.dialog.flaggedItems.open) {
		_cms.flags.refreshDialog();
	}
}

_cms.flags.refreshCopyForm = function(key) {
	_cms.support.ajax('GET', '/rest/item/' + key + '/refresh/copyFlaggedForm', {dataType: 'html', mimeType: 'text/html'}, 
		function(html, status, z) {
			$('div#copy-flagged-data-form').html(html);
		}
	);
}

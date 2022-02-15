_cms.dialog = {
	linkNotDefined: {obj: null},
	duplicateTarget: {obj: null},
	illegalTarget: {obj: null},
	badLinkDataFormat: {obj: null},
	badFieldValueFormat: {obj: null},
	confirmFieldUpdate: {obj: null},
	confirmTrash: {obj: null},
	addLink: {obj: null},
	linkGuidance: {obj: null},
	searchresults: {obj: null},
	sel: {
		LINK_NOT_DEFINED: "#link-not-defined-dialog",
		LINK_DATA_FORMAT: "#link-data-format-dialog",
		FIELD_VALUE_FORMAT: "#field-value-format-dialog",
		DUPLICATE_TARGET: "#duplicate-link-target-dialog",
		ILLEGAL_TARGET: "#illegal-link-target-dialog",
		CONFIRM_FIELD_UPDATE: "#confirm-field-update-dialog",
		CONFIRM_TRASH_ACTION: "#confirm-trash-dialog",
	}
}

_cms.dialog.define = function(title, selector, width, height, buttons, close, isModal = true) {
	var obj = $(selector).dialog({
		  autoOpen: false,
		  minHeight: height,
		  minWidth: width,
		  modal: isModal,
		  title: title,
		  buttons: buttons,
		  close: close,
	});
	
	return obj;
}

_cms.dialog.open = function(d) {
	d.obj.dialog("open");
}

_cms.dialog.close = function(d) {
	d.obj.dialog("close");
}

_cms.dialog.confirmTrash.define = function() {
	var close = function() {
		_cms.dialog.close(_cms.dialog.confirmTrash);
	}
	
	var buttons = {
		"Delete all items": function() {
			_cms.core.trashItem(_cms.editingItemId);
		},
		Cancel: function() {
			close();
		}
	}
	
	_cms.dialog.confirmTrash.obj = _cms.dialog.define("Confirm deletion", 
			_cms.dialog.sel.CONFIRM_TRASH_ACTION, 200, 200, buttons, close);
}

_cms.dialog.confirmFieldUpdate.define = function() {
	var close = function() {
		_cms.dialog.close(_cms.dialog.confirmFieldUpdate);
	}
	
	var buttons = {
		"Update field values": function() {
			_cms.field.update(_cms.editingItemId);
		},
		Cancel: function() {
			close();
		}
	}
	
	_cms.dialog.confirmFieldUpdate.obj = _cms.dialog.define("Confirm updates", _cms.dialog.sel.CONFIRM_FIELD_UPDATE, 200, 200, buttons, close);
}

_cms.dialog.addLink.define = function() {	
	var close = function() {
		_cms.dialog.close(_cms.dialog.addLink);
	}
	
	var buttons = {
		Use: function() {
			if (_cms.links.check_for_use()) {
				_cms.links.use_form_data();
				_cms.links.shortcut.settings();
				close();
			}
		},
		Cancel: function() {
			close();
		}
	}
	
	_cms.dialog.addLink.obj = _cms.dialog.define(
			"Add/edit a link", _cms.links.sel.ADD_LINK_CONTAINER, 300, 250, buttons, close);
}

_cms.dialog.searchresults.define = function() {	
	var close = function() {
		_cms.dialog.close(_cms.dialog.searchresults);
	}
	
	var buttons = {
		Cancel: function() {
			close();
		}
	}
	
	_cms.dialog.searchresults.obj = _cms.dialog.define(
			"Search results", "#searchresults-container", 300, 600, buttons, close);
}

_cms.dialog.linkGuidance.define = function() {	
	var close = function() {
		_cms.dialog.close(_cms.dialog.linkGuidance);
	}
	
	var buttons = {
		Close: function() {
			close();
		}
	}
	
	_cms.dialog.linkGuidance.obj = _cms.dialog.define(
			"Link data guidance", "#link-guidance", 300, 600, buttons, close, false);
}

_cms.dialog.warning = function(title, d, selector) {
	  var close = function() {
		  _cms.dialog.close(d);
	  }

	  var buttons = {
		  Close: function() {
			  close();
		  }
	  }
	  
	  d.obj = _cms.dialog.define(title, selector, 250, 200, buttons, close);
}

_cms.dialog.onpageload = function() {
	_cms.dialog.confirmTrash.define();
	_cms.dialog.confirmFieldUpdate.define();
	_cms.dialog.addLink.define();
	_cms.dialog.searchresults.define();
	_cms.dialog.linkGuidance.define();
	_cms.dialog.warning("Link not adequately defined", _cms.dialog.linkNotDefined, _cms.dialog.sel.LINK_NOT_DEFINED);
	_cms.dialog.warning("Duplicate link target", _cms.dialog.duplicateTarget, _cms.dialog.sel.DUPLICATE_TARGET);
	_cms.dialog.warning("Illegal target", _cms.dialog.illegalTarget, _cms.dialog.sel.ILLEGAL_TARGET);
	_cms.dialog.warning("Link data badly formatted", _cms.dialog.badLinkDataFormat, _cms.dialog.sel.LINK_DATA_FORMAT);
	_cms.dialog.warning("Field value badly formatted", _cms.dialog.badFieldValueFormat, _cms.dialog.sel.FIELD_VALUE_FORMAT);
}
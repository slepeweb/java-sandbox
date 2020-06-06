_cms.dialog = {
	linkNotDefined: {obj: null},
	duplicateTarget: {obj: null},
	illegalTarget: {obj: null},
	confirmFieldUpdate: {obj: null},
	confirmTrash: {obj: null},
	addLink: {obj: null},
	sel: {
		LINK_NOT_DEFINED: "#link-not-defined-dialog",
		DUPLICATE_TARGET: "#duplicate-link-target-dialog",
		ILLEGAL_TARGET: "#illegal-link-target-dialog",
		CONFIRM_FIELD_UPDATE: "#confirm-field-update-dialog",
		CONFIRM_TRASH_ACTION: "#confirm-trash-dialog",
	}
}

_cms.dialog.define = function(title, selector, width, height, buttons, close) {
	var obj = $(selector).dialog({
		  autoOpen: false,
		  minHeight: height,
		  minWidth: width,
		  modal: true,
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
			_cms.core.trashItem();
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
			if (_cms.links.check_for_use(true)) {
				_cms.links.use_form_data();
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
	_cms.dialog.warning("Link not adequately defined", _cms.dialog.linkNotDefined, _cms.dialog.sel.LINK_NOT_DEFINED);
	_cms.dialog.warning("Duplicate link target", _cms.dialog.duplicateTarget, _cms.dialog.sel.DUPLICATE_TARGET);
	_cms.dialog.warning("Illegal target", _cms.dialog.illegalTarget, _cms.dialog.sel.ILLEGAL_TARGET);
}
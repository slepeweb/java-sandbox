_cms.dialog = {
	warning: {obj: null},
	confirmation: {obj: null},
	trash: {obj: null},
	field: {obj: null},
	addLink: {obj: null},
	linkError: {obj: null},
	sel: {
		CONFIRMATION: "#confirmation-dialog",
		ERROR: "#warning-dialog",
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

_cms.dialog.open = function(d, id) {
	var all = d.selector;
	var one = d.selector;
	
	if (id) {
		all += " .message";
		one += " .message-" + id;
	}
	
	$(all).addClass("hide");
	$(one).removeClass("hide");
	
	d.obj.dialog("open");
}

_cms.dialog.close = function(d) {
	d.obj.dialog("close");
}

_cms.dialog.trash.define = function() {
	var close = function() {
		_cms.dialog.close(_cms.dialog.trash);
	}
	
	var buttons = {
		"Delete all items": function() {
			_cms.core.trashItem();
		},
		Cancel: function() {
			close();
		}
	}
	
	_cms.dialog.trash.selector = _cms.dialog.sel.CONFIRMATION;
	_cms.dialog.trash.obj = _cms.dialog.define("Confirm deletion", _cms.dialog.sel.CONFIRMATION, 200, 200, buttons, close);
}

_cms.dialog.field.define = function() {
	var close = function() {
		_cms.dialog.close(_cms.dialog.field);
	}
	
	var buttons = {
		"Update field values": function() {
			_cms.field.update(_cms.editingItemId);
		},
		Cancel: function() {
			close();
		}
	}
	
	_cms.dialog.field.selector = _cms.dialog.sel.CONFIRMATION;
	_cms.dialog.field.obj = _cms.dialog.define("Confirm updates", _cms.dialog.sel.CONFIRMATION, 200, 200, buttons, close);
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
	
	_cms.dialog.addLink.selector = _cms.links.sel.ADD_LINK_CONTAINER;
	_cms.dialog.addLink.obj = _cms.dialog.define(
			"Add/edit a link", _cms.links.sel.ADD_LINK_CONTAINER, 300, 250, buttons, close);
}

_cms.dialog.linkError.define = function() {
	  var close = function() {
		  _cms.dialog.close(_cms.dialog.linkError);
	  }

	  var buttons = {
		  Close: function() {
			  close();
		  }
	  }
	  
	  _cms.dialog.linkError.selector = _cms.dialog.sel.ERROR;
	  _cms.dialog.linkError.obj = _cms.dialog.define(
				"Link error", _cms.dialog.sel.ERROR, 250, 200, buttons, close);
}

_cms.dialog.onpageload = function() {
	_cms.dialog.trash.define();
	_cms.dialog.field.define();
	_cms.dialog.addLink.define();
	_cms.dialog.linkError.define();
}
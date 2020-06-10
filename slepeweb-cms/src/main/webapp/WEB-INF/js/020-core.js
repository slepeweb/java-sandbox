_cms.core = {
	behaviour: {},
	refresh: {},
	sel: {
		CORE_TAB: "#core-tab",
		UPDATE_BUTTON: "#core-button",
		PRODUCT_FLAG: "#itemIsProductFlag",
		TRASH_BUTTON: "#trash-button",
	}
};

_cms.core.sel.ALL_INPUTS = _cms.core.sel.CORE_TAB + "#core-tab input";
_cms.core.sel.ALL_SELECTS = _cms.core.sel.CORE_TAB + "#core-tab select";
_cms.core.sel.NAME_INPUT = _cms.support.fi(_cms.core.sel.CORE_TAB, "name");
_cms.core.sel.SIMPLENAME_INPUT = _cms.support.fi(_cms.core.sel.CORE_TAB, "simplename");
_cms.core.sel.TEMPLATE_SELECT = _cms.support.fs(_cms.core.sel.CORE_TAB, "template");
_cms.core.sel.SEARCHABLE_CHECKBOX = _cms.support.fi(_cms.core.sel.CORE_TAB, "searchable");
_cms.core.sel.PUBLISHED_CHECKBOX = _cms.support.fi(_cms.core.sel.CORE_TAB, "published");
_cms.core.sel.TAGS_INPUT = _cms.support.fi(_cms.core.sel.CORE_TAB, "tags");
_cms.core.sel.PARTNUM_INPUT = _cms.support.fi(_cms.core.sel.CORE_TAB, "");
_cms.core.sel.PRICE_INPUT = _cms.support.fi(_cms.core.sel.CORE_TAB, "");
_cms.core.sel.STOCK_INPUT = _cms.support.fi(_cms.core.sel.CORE_TAB, "");
_cms.core.sel.FORM = "".concat(_cms.core.sel.CORE_TAB, " form");

_cms.support.setTabIds(_cms.core, "core");

_cms.core.behaviour.update = function(nodeKey) {
	// Add behaviour to submit core item updates 
	$(_cms.core.sel.UPDATE_BUTTON).click(function () {
		var isProduct = $(_cms.core.sel.PRODUCT_FLAG).val() == "true";
		var args = {
			name: $(_cms.core.sel.NAME_INPUT).val(),
			simplename: $(_cms.core.sel.SIMPLENAME_INPUT).val(),
			template: $(_cms.core.sel.TEMPLATE_SELECT).val(),
			searchable: $(_cms.core.sel.SEARCHABLE_CHECKBOX).is(':checked'),
			published: $(_cms.core.sel.PUBLISHED_CHECKBOX).is(':checked'),
			tags: $(_cms.core.sel.TAGS_INPUT).val()
		};
		
		if (isProduct) {
				args.partNum = $(_cms.core.sel.PARTNUM_INPUT).val();
				args.price = Math.floor($(_cms.core.sel.PRICE_INPUT).val() * 100);
				args.stock = $(_cms.core.sel.STOCK_INPUT).val();
		}
		
		$.ajax(_cms.ctx + "/rest/item/" + nodeKey + "/update/core", {
			type: "POST",
			cache: false,
			data: args, 
			dataType: "json",
			success: function(resp, status, z) {
				if (! resp.error) {
					// Name may have changed, so navigation tree will need updating
					var node = _cms.leftnav.tree.getNodeByKey(nodeKey);
					if (node) {
						node.setTitle(resp.data[0].title);
					}
				}
				
				_cms.support.flashMessage(resp.data[0]);
				_cms.core.refresh.tab(nodeKey);
				
				if (resp.data[2]) {
					// The published status of the item has changed
					_cms.version.refresh.tab(nodeKey);
				}
				
				if (resp.data[1]) {
					// The first version has reach published status
					// TODO: This is NOT needed on a staging server
					_cms.media.refresh.tab(nodeKey);
				}
			},
			error: function(json, status, z) {
				_cms.support.serverError();
			}
		});
	});
	
	// TODO: there are NO date field on the core tab - move to field tab
	$("#core-tab .datepicker").datepicker({
		dateFormat: "yy-mm-dd",
		changeMonth: true,
		changeYear: true
	});
}

_cms.core.trashItem = function() {
	$.ajax(_cms.ctx + "/rest/item/" + _cms.editingItemId + "/trash", {
		type: "POST",
		cache: false,
		dataType: "json",
		success: function(obj, status, z) {
			_cms.dialog.close(_cms.dialog.confirmTrash);
			_cms.support.flashMessage(obj);
			
			if (! obj.error) {
				var node = _cms.leftnav.tree.getNodeByKey(_cms.editingItemId);
				if (node) {
					var parent = node.getParent();
					node.remove();
					
					// This will make the parent item the current item, and refresh the page accordingly,
					// thereby updating _cms.editingItemId
					_cms.leftnav.tree.activateKey(parent.key);
				}
			}
		},
		error: function(json, status, z) {
			_cms.dialog.close(_cms.dialog.confirmTrash);
			_cms.support.serverError();
		}
	});
}

_cms.core.behaviour.trash = function(nodeKey) {
	// Add behaviour to trash an item and put it in the bin.
	$(_cms.core.sel.TRASH_BUTTON).click(function () {
		_cms.dialog.open(_cms.dialog.confirmTrash, "b");
	});
}

_cms.core.behaviour.formchange = function() {
	$(_cms.core.sel.ALL_INPUTS + "," + _cms.core.sel.ALL_SELECTS).mouseleave(function() {
		_cms.support.enableIf(_cms.core.sel.UPDATE_BUTTON, 
				$(_cms.core.sel.FORM).serialize() !== _cms.core.originalFormState);
	});
}

_cms.core.refresh.tab = function(nodeKey) {
	_cms.support.refreshtab("core", nodeKey, _cms.core.onrefresh);
};

_cms.core.onrefresh = function(nodeKey) {
	_cms.core.behaviour.formchange();
	_cms.core.behaviour.update(nodeKey);
	_cms.core.behaviour.trash();
	_cms.core.originalFormState = $(_cms.core.sel.FORM).serialize();
}

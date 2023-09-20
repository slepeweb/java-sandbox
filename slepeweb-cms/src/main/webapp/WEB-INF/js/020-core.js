_cms.core = {
	behaviour: {},
	refresh: {},
	sel: {
		CORE_TAB: "#core-tab",
		CURRENTLY_EDITING_HEADER: "#currently-editing",
		PRODUCT_FLAG: "#itemIsProductFlag",
	}
};

_cms.core.sel.ALL_INPUTS = _cms.core.sel.CORE_TAB + " input";
_cms.core.sel.ALL_SELECTS = _cms.core.sel.CORE_TAB + " select";
_cms.core.sel.NAME_INPUT = _cms.support.fi(_cms.core.sel.CORE_TAB, "name");
_cms.core.sel.SIMPLENAME_INPUT = _cms.support.fi(_cms.core.sel.CORE_TAB, "simplename");
_cms.core.sel.TEMPLATE_SELECT = _cms.support.fs(_cms.core.sel.CORE_TAB, "template");
_cms.core.sel.OWNER_SELECT = _cms.support.fs(_cms.core.sel.CORE_TAB, "owner");
_cms.core.sel.SEARCHABLE_CHECKBOX = _cms.support.fi(_cms.core.sel.CORE_TAB, "searchable");
_cms.core.sel.PUBLISHED_CHECKBOX = _cms.support.fi(_cms.core.sel.CORE_TAB, "published");
_cms.core.sel.TAGS_INPUT = _cms.support.fi(_cms.core.sel.CORE_TAB, "tags");
_cms.core.sel.PARTNUM_INPUT = _cms.support.fi(_cms.core.sel.CORE_TAB, "");
_cms.core.sel.PRICE_INPUT = _cms.support.fi(_cms.core.sel.CORE_TAB, "");
_cms.core.sel.STOCK_INPUT = _cms.support.fi(_cms.core.sel.CORE_TAB, "");
_cms.core.sel.FORM = "".concat(_cms.core.sel.CORE_TAB, " form");
_cms.core.sel.UPDATE_BUTTON = _cms.core.sel.CORE_TAB + " button.action";
_cms.core.sel.RESET_BUTTON = _cms.core.sel.CORE_TAB + " button.reset";
_cms.core.sel.TAG_OPTIONS = "#tag-options";

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
			tags: $(_cms.core.sel.TAGS_INPUT).val(),
			owner: $(_cms.core.sel.OWNER_SELECT).val()
		};
		
		if (isProduct) {
				args.partNum = $(_cms.core.sel.PARTNUM_INPUT).val();
				args.price = Math.floor($(_cms.core.sel.PRICE_INPUT).val() * 100);
				args.stock = $(_cms.core.sel.STOCK_INPUT).val();
		}
		
		
		_cms.support.ajax('POST', '/rest/item/' + nodeKey + '/update/core', 
				{data: args, dataType: 'json'}, function(resp, status, z) {
				
			if (! resp.error) {
				_cms.support.flashMessage(_cms.support.toStatus(false, resp.message));
				
				// Name may have changed. If so, resp.data[0] will be not null.
				if (resp.data[0]) {
					var node = _cms.leftnav.tree.getNodeByKey(nodeKey);
					
					if (node) {
						node.setTitle(resp.data[0]);
					}
					
					_cms.support.updateItemName(resp.data[0]);
				}
				
				_cms.copy.refresh.tab(nodeKey);
				_cms.support.refreshHistory(_cms.siteId);
				
				_cms.core.refresh.tab(nodeKey);
				
				// The published status of the item has changed
				_cms.version.refresh.tab(nodeKey);

				// The first version has reached published status
				// TODO: This is NOT needed on a staging server
				// TODO: Why refresh the media tab?
				_cms.media.refresh.tab(nodeKey);
				
				_cms.undoRedo.displayAll(resp.data[3]);
			}
		});				
		
	});
}

_cms.core.trashItem = function(nodeKey) {
	_cms.support.ajax('POST', '/rest/item/' + nodeKey + '/trash', {dataType: 'json'}, 
		function(resp, status, z) {
			_cms.dialog.close(_cms.dialog.confirmTrash);
			_cms.support.flashMessage(resp);
			_cms.undoRedo.displayAll(resp.data);

			
			if (! resp.error) {
				var node = _cms.leftnav.tree.getNodeByKey(_cms.editingItemId);
				if (node) {
					var parent = node.getParent();
					node.remove();
					_cms.leftnav.navigate(parent.key, "core-tab");
				}
			}
		},
		function(json, status, z) {
			_cms.dialog.close(_cms.dialog.confirmTrash);
			_cms.support.serverError();
		}
	);
}

_cms.core.behaviour.reset = function(nodeKey) {
	$(_cms.core.sel.RESET_BUTTON).click(function (e) {
		_cms.support.resetForm(_cms.core.refresh.tab, nodeKey, e);
	});
}

_cms.core.setButtonStates = function() {
	if (_cms.support.enableIf(_cms.core.sel.UPDATE_BUTTON, 
			$(_cms.core.sel.FORM).serialize() !== _cms.core.originalFormState)) {
		
		_cms.support.enable(_cms.core.sel.RESET_BUTTON);
	}
	else {
		_cms.support.disable(_cms.core.sel.RESET_BUTTON);
	}
}

_cms.core.behaviour.formchange = function() {
	if (_cms.editingItemIsWriteable) {
		$(_cms.core.sel.ALL_INPUTS + "," + _cms.core.sel.ALL_SELECTS).mouseleave(_cms.core.setButtonStates);
		$(_cms.core.sel.FORM + ' button').mouseenter(_cms.core.setButtonStates);
	}
}

_cms.core.behaviour.tags = function() {
	$(_cms.core.sel.TAG_OPTIONS + " li").click(function() {
		let tagInput = $("#item-core-editor input[name=tags]");
		let existing = tagInput.val().trim();
		let newTag = $(this).html();
		
		if (! existing.includes(newTag)) {
			if (existing.length > 0) {
				existing += ", ";
			}
			existing += newTag;
		}
		
		tagInput.val(existing);
	});
	
	$("#tags-menu-icon").click(function() {
		let ele = $(_cms.core.sel.TAG_OPTIONS);
		if (ele.hasClass("hide")) {
			ele.removeClass("hide");
		}
	});
	
	$(_cms.core.sel.TAG_OPTIONS).mouseleave(function() {
		let ele = $(this);
		if (! ele.hasClass("hide")) {
			ele.addClass("hide");
		}
	});
}

_cms.core.refresh.tab = function(nodeKey) {
	_cms.support.refreshtab("core", nodeKey, _cms.core.onrefresh);
};

_cms.core.onrefresh = function(nodeKey) {
	_cms.core.behaviour.formchange();
	_cms.core.behaviour.update(nodeKey);
	_cms.core.behaviour.reset(nodeKey);
	_cms.core.behaviour.tags();
	_cms.core.originalFormState = $(_cms.core.sel.FORM).serialize();
}

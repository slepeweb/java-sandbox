_cms.core = {
	behaviour: {},
	refresh: {},
	sel: {
		ALL_INPUTS: "#core-tab input",
		ALL_SELECTS: "#core-tab select",
		UPDATE_BUTTON: "#core-button",
		NAME_INPUT: "#core-tab input[name='name']",
		SIMPLENAME_INPUT: "#core-tab input[name='simplename']",
		TEMPLATE_SELECT: "#core-tab select[name='template']",
		SEARCHABLE_CHECKBOX: "#core-tab input[name='searchable']",
		PUBLISHED_CHECKBOX: "#core-tab input[name='published']",
		TAGS_INPUT: "#core-tab input[name='tags']",
	}
};

_cms.core.behaviour.update = function(nodeKey) {
	// Add behaviour to submit core item updates 
	$(_cms.core.sel.UPDATE_BUTTON).click(function () {
		var isProduct = $("#itemIsProductFlag").val() == "true";
		var args = {
			name: $(_cms.core.sel.NAME_INPUT).val(),
			simplename: $(_cms.core.sel.SIMPLENAME_INPUT).val(),
			template: $(_cms.core.sel.TEMPLATE_SELECT).val(),
			searchable: $(_cms.core.sel.SEARCHABLE_CHECKBOX).is(':checked'),
			published: $(_cms.core.sel.PUBLISHED_CHECKBOX).is(':checked'),
			tags: $(_cms.core.sel.TAGS_INPUT).val()
		};
		
		if (isProduct) {
				args.partNum = $("#core-tab input[name='partNum']").val();
				args.price = Math.floor($("#core-tab input[name='price']").val() * 100);
				args.stock = $("#core-tab input[name='stock']").val();
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
			_cms.dialog.close(_cms.dialog.trash);
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
			_cms.dialog.close(_cms.dialog.trash);
			_cms.support.serverError();
		}
	});
}

_cms.core.behaviour.trash = function(nodeKey) {
	// Add behaviour to trash an item and put it in the bin.
	$("#trash-button").click(function () {
		_cms.dialog.open(_cms.dialog.trash, "b");
	});
}

_cms.core.behaviour.formchange = function() {
	$(_cms.core.sel.ALL_INPUTS + "," + _cms.core.sel.ALL_SELECTS).change(function() {
		_cms.support.enable(_cms.core.sel.UPDATE_BUTTON);
	});
}

_cms.core.refresh.tab = function(nodeKey) {
	_cms.support.refreshtab("core", nodeKey, _cms.core.behaviour.all);
};

_cms.core.behaviour.all = function(nodeKey) {
	_cms.core.behaviour.formchange();
	_cms.core.behaviour.update(nodeKey);
	_cms.core.behaviour.trash();
}

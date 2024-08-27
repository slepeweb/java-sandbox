/*
	Function Index
	--------------
	_cms.add.behaviour.add(nodeKey)
	_cms.add.behaviour.changelinktype()
	_cms.add.behaviour.changetemplate()
	_cms.add.behaviour.changetype()
	_cms.add.behaviour.formchange()
	_cms.add.behaviour.reset(nodeKey)
	_cms.add.check_data_is_complete()
	_cms.add.onrefresh(nodeKey)
	_cms.add.refresh.tab(nodeKey)
	_cms.add.setButtonStates()
	_cms.add.setLinkNames(linkType)
*/

_cms.add = {
	behaviour: {},
	refresh: {},
	sel: {
		ADD_TAB: "#add-tab",
	}
};

_cms.add.sel.RELATIVE_POSITION_SELECTOR = _cms.support.fs(_cms.add.sel.ADD_TAB, "relativePosition");
_cms.add.sel.TEMPLATE_SELECTOR = _cms.support.fs(_cms.add.sel.ADD_TAB, "template");
_cms.add.sel.LINK_TYPE_SELECTOR = _cms.support.fs(_cms.add.sel.ADD_TAB, "linktype");
_cms.add.sel.LINK_NAME_SELECTOR = _cms.support.fs(_cms.add.sel.ADD_TAB, "linkname");
_cms.add.sel.ITEMTYPE_SELECTOR = _cms.support.fs(_cms.add.sel.ADD_TAB, "itemtype");
_cms.add.sel.NAME_INPUT = _cms.support.fi(_cms.add.sel.ADD_TAB, "name");
_cms.add.sel.SIMPLENAME_INPUT = _cms.support.fi(_cms.add.sel.ADD_TAB, "simplename");
_cms.add.sel.PARTNUM_INPUT = _cms.support.fi(_cms.add.sel.ADD_TAB, "partNum");
_cms.add.sel.PRICE_INPUT = _cms.support.fi(_cms.add.sel.ADD_TAB, "price");
_cms.add.sel.STOCK_INPUT = _cms.support.fi(_cms.add.sel.ADD_TAB, "stock");
_cms.add.sel.ALPHA_SELECTOR = _cms.support.fs(_cms.add.sel.ADD_TAB, "alphaaxis");
_cms.add.sel.BETA_SELECTOR = _cms.support.fs(_cms.add.sel.ADD_TAB, "betaaxis");
_cms.add.sel.ALL_FORM_ELEMENTS = "".concat(_cms.add.sel.ADD_TAB, " select,", _cms.add.sel.ADD_TAB, " input");
_cms.add.sel.ADD_BUTTON = _cms.add.sel.ADD_TAB + " button.action";
_cms.add.sel.RESET_BUTTON = _cms.add.sel.ADD_TAB + " button.reset";
_cms.add.sel.FORM = _cms.add.sel.ADD_TAB, " form";


_cms.support.setTabIds(_cms.add, "add");

_cms.add.behaviour.add = function(nodeKey) {

	// Add behaviour to add new item 
	$(_cms.add.sel.ADD_BUTTON).click(function () {
		var position = $(_cms.add.sel.RELATIVE_POSITION_SELECTOR).val();
		
		$.ajax(_cms.ctx + "/rest/item/" + nodeKey + "/add", {
			type: "POST",
			cache: false,	<!-- _defaultBindingName: ${_linkTypeNameOptions._defaultBindingName} -->
			
			data: {
				relativePosition: position,
				template: $(_cms.add.sel.TEMPLATE_SELECTOR).val(),
				linktype: $(_cms.add.sel.LINK_TYPE_SELECTOR).val(),
				linkname: $(_cms.add.sel.LINK_NAME_SELECTOR).val(),
				itemtype: $(_cms.add.sel.ITEMTYPE_SELECTOR).val(),
				name: $(_cms.add.sel.NAME_INPUT).val(),
				simplename: $(_cms.add.sel.SIMPLENAME_INPUT).val(),
				partNum: $(_cms.add.sel.PARTNUM_INPUT).val(),
				price: $(_cms.add.sel.PRICE_INPUT).val(),
				stock: $(_cms.add.sel.STOCK_INPUT).val(),
				alphaaxis: $(_cms.add.sel.ALPHA_SELECTOR).val(),
				betaaxis: $(_cms.add.sel.BETA_SELECTOR).val()
			}, 
			dataType: "json",
			success: function(resp, status, z) {
				//_cms.support.flashMessage(resp);
				_cms.undoRedo.displayAll(resp.data[3]);
				
				if (! resp.error) {
					var nodeData = resp.data[0];
					var isShortcut = resp.data[1];
					var isMedia = resp.data[2];
					
					var parentNode = _cms.leftnav.tree.getNodeByKey(nodeKey.toString());
					
					if (position == 'alongside') {
						parentNode = parentNode.getParent();
					}
					
					if (parentNode != null) {
						var childNode = parentNode.addNode(nodeData);
						//childNode.setActive(true);
						
						var tab = "core-tab";
						if (isShortcut) {
							tab = "links-tab";
						}
						else if  (isMedia) {
							tab = "media-tab";
						}
						
						_cms.leftnav.navigate(childNode.key, tab, _cms.support.flashMessage, resp);
					}
				}
			},
			error: function(json, status, z) {
				_cms.support.serverError();
			},
		});
	});
}

_cms.add.behaviour.changetemplate = function() {
	//Add behaviour to template & itemtype selectors 
	$(_cms.add.sel.TEMPLATE_SELECTOR).change(function (e) {
		var typeSelector = $(_cms.add.sel.ITEMTYPE_SELECTOR);
		var target = $(e.target);
		
		if (target.val() != "0") {
			typeSelector.val("0");
			typeSelector.attr("disabled", "true");
		}
		else {
			typeSelector.removeAttr("disabled");
		}
		
		// Add commerce form controls when user selects template corresponding to Product item type 
		_cms.support.displayCommerceElements(target);
	});
}

_cms.add.behaviour.changelinktype = function() {
	//Add behaviour to linktype selector
	$(_cms.add.sel.LINK_TYPE_SELECTOR).change(function (e) {
		var linkTypeOption$ = $(e.target);
		cms.add.setLinkNames(linkTypeOption$.val());
		/*
		var linkNameDropdown$ = $(_cms.add.sel.LINK_NAME_SELECTOR);		
		var nameOptions = _cms.linkNameOptions[linkTypeOption$.val()]
		
		var h = ''; 
		for (var s of nameOptions) {
			h += _cms.support.toOptionHtml(s);
		}
		
		linkNameDropdown$.empty().html(h);
		*/
	});
}

_cms.add.behaviour.changetype = function() {
	// Add commerce form controls when user selects Product for item type 
	$(_cms.add.sel.ITEMTYPE_SELECTOR).change(function (e) {
		_cms.support.displayCommerceElements($(e.target));
	});
}

_cms.add.check_data_is_complete = function() {
	var template = $(_cms.add.sel.TEMPLATE_SELECTOR).val();
	var itemtype = $(_cms.add.sel.ITEMTYPE_SELECTOR).val();
	var name = $(_cms.add.sel.NAME_INPUT).val();
	
	return (template != 0 || itemtype != 0) && name;
}

_cms.add.setButtonStates = function() {
	if (_cms.support.enableIf(_cms.add.sel.ADD_BUTTON, _cms.add.check_data_is_complete())) {
		_cms.support.enable(_cms.add.sel.RESET_BUTTON);
	}
	else {
		_cms.support.disable(_cms.add.sel.RESET_BUTTON);
	}
}

_cms.add.behaviour.formchange = function() {
	if (_cms.editingItemIsWriteable) {
		$(_cms.add.sel.ALL_FORM_ELEMENTS).mouseleave(_cms.add.setButtonStates);
		$(_cms.add.sel.FORM + ' button').mouseenter(_cms.add.setButtonStates);
	}
}

_cms.add.behaviour.reset = function(nodeKey) {
	$(_cms.add.sel.RESET_BUTTON).click(function (e) {
		_cms.support.resetForm(_cms.add.refresh.tab, nodeKey, e);
	});
}

_cms.add.refresh.tab = function(nodeKey) {
	_cms.support.refreshtab("add", nodeKey, _cms.add.onrefresh);
}

_cms.add.setLinkNames = function(linkType) {
	var linkNameDropdown$ = $(_cms.add.sel.LINK_NAME_SELECTOR);		
	var nameOptions = _cms.linkNameOptions[linkType]
	
	var h = ''; 
	for (var s of nameOptions) {
		h += _cms.support.toOptionHtml(s);
	}
	
	linkNameDropdown$.empty().html(h);
}

// Behaviours to apply once html is loaded/reloaded
_cms.add.onrefresh = function(nodeKey) {
	_cms.add.behaviour.add(nodeKey);
	_cms.add.behaviour.changetype();
	_cms.add.behaviour.changelinktype();
	_cms.add.behaviour.changetemplate();
	_cms.add.behaviour.formchange();
	_cms.add.behaviour.reset(nodeKey);
	$(_cms.add.sel.LINK_TYPE_SELECTOR).val('binding');
	_cms.add.setLinkNames('binding');
}
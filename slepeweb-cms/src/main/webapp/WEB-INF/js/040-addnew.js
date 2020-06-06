_cms.add = {
	behaviour: {},
	sel: {
		ADD_BUTTON: "#add-button",
		ADD_TAB: "#add-tab",
	}
};

_cms.add.sel.RELATIVE_POSITION_SELECTOR = _cms.support.fs(_cms.add.sel.ADD_TAB, "relativePosition");
_cms.add.sel.TEMPLATE_SELECTOR = _cms.support.fs(_cms.add.sel.ADD_TAB, "template");
_cms.add.sel.ITEMTYPE_SELECTOR = _cms.support.fs(_cms.add.sel.ADD_TAB, "itemtype");
_cms.add.sel.NAME_INPUT = _cms.support.fi(_cms.add.sel.ADD_TAB, "name");
_cms.add.sel.SIMPLENAME_INPUT = _cms.support.fi(_cms.add.sel.ADD_TAB, "simplename");
_cms.add.sel.PARTNUM_INPUT = _cms.support.fi(_cms.add.sel.ADD_TAB, "partNum");
_cms.add.sel.PRICE_INPUT = _cms.support.fi(_cms.add.sel.ADD_TAB, "price");
_cms.add.sel.STOCK_INPUT = _cms.support.fi(_cms.add.sel.ADD_TAB, "stock");
_cms.add.sel.ALPHA_SELECTOR = _cms.support.fs(_cms.add.sel.ADD_TAB, "alphaaxis");
_cms.add.sel.BETA_SELECTOR = _cms.support.fs(_cms.add.sel.ADD_TAB, "betaaxis");
_cms.add.sel.ALL_FORM_ELEMENTS = "".concat(_cms.add.sel.ADD_TAB, " select,", _cms.add.sel.ADD_TAB, " input");

_cms.add.behaviour.add = function(nodeKey) {

	// Add behaviour to add new item 
	$(_cms.add.sel.ADD_BUTTON).click(function () {
		var position = $(_cms.add.sel.RELATIVE_POSITION_SELECTOR).val();
		
		$.ajax(_cms.ctx + "/rest/item/" + nodeKey + "/add", {
			type: "POST",
			cache: false,
			data: {
				relativePosition: position,
				template: $(_cms.add.sel.TEMPLATE_SELECTOR).val(),
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
			success: function(obj, status, z) {
				_cms.support.flashMessage(obj);
				
				if (! obj.error) {
					var nodeData = obj.data;
					var parentNode = _cms.leftnav.tree.getNodeByKey(nodeKey);
					
					if (position == 'alongside') {
						parentNode = parentNode.getParent();
					}
					
					if (parentNode != null) {
						var childNode = parentNode.addNode(nodeData);
						
						// This triggers a call to loads the editor with the newly created item
						childNode.setActive();
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

_cms.add.behaviour.formElementChange = function() {
	$(_cms.add.sel.ALL_FORM_ELEMENTS).change(function(){
		if (_cms.add.check_data_is_complete()) {
			_cms.support.enable(_cms.add.sel.ADD_BUTTON);
		}
		else {
			_cms.support.disable(_cms.add.sel.ADD_BUTTON);
		}
	});
}

// Behaviours to apply once html is loaded/reloaded
_cms.add.behaviour.all = function(nodeKey) {
	_cms.add.behaviour.add(nodeKey);
	_cms.add.behaviour.changetype();
	_cms.add.behaviour.changetemplate();
	_cms.add.behaviour.formElementChange();
}

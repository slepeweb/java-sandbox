_cms.add = {
	behaviour: {},
};

_cms.add.behaviour.add = function(nodeKey) {

	// Add behaviour to add new item 
	$("#add-button").click(function () {
		var position = $("#add-tab select[name='relativePosition']").val();
		
		$.ajax(_cms.ctx + "/rest/item/" + nodeKey + "/add", {
			type: "POST",
			cache: false,
			data: {
				relativePosition: position,
				template: $("#add-tab select[name='template']").val(),
				itemtype: $("#add-tab select[name='itemtype']").val(),
				name: $("#add-tab input[name='name']").val(),
				simplename: $("#add-tab input[name='simplename']").val(),
				partNum: $("#add-tab input[name='partNum']").val(),
				price: $("#add-tab input[name='price']").val(),
				stock: $("#add-tab input[name='stock']").val(),
				alphaaxis: $("#add-tab select[name='alphaaxis']").val(),
				betaaxis: $("#add-tab select[name='betaaxis']").val()
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
	$("#add-tab select[name='template']").change(function (e) {
		var typeSelector = $("#add-tab select[name='itemtype']");
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
	$("#add-tab select[name='itemtype']").change(function (e) {
		_cms.support.displayCommerceElements($(e.target));
	});
}

// Behaviours to apply once html is loaded/reloaded
_cms.add.behaviour.all = function(nodeKey) {
	_cms.add.behaviour.add(nodeKey);
	_cms.add.behaviour.changetype();
	_cms.add.behaviour.changetemplate();
}

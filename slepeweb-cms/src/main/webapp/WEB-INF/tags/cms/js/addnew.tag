<%@ tag %><%@ 
    taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%><%@ 
    taglib prefix="cms" tagdir="/WEB-INF/tags/cms"%><%@ 
    taglib prefix="cmsjs" tagdir="/WEB-INF/tags/cms/js"%>
        
<cms:debug>/* tags/cms/js/addnew.tag */</cms:debug>

// Add behaviour to add new item 
$("#add-button").click(function () {
	$.ajax(_ctx + "/rest/item/" + nodeKey + "/add", {
		type: "POST",
		cache: false,
		data: {
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
			flashMessage(obj);
			
			if (! obj.error) {
				var childNode = obj.data;
				var parentNode = _tree.getNodeByKey(nodeKey);
				parentNode.addNode(childNode);
			}
		},
		error: function(json, status, z) {
			serverError();
		},
	});
});

// Add commerce form controls when user selects template corresponding to Product item type 
$("#add-tab select[name='itemtype']").change(function (e) {
	displayCommerceElements($(e.target));
});

// Add commerce form controls when user selects Product for item type 
$("#add-tab select[name='template']").change(function (e) {
	displayCommerceElements($(e.target));
});

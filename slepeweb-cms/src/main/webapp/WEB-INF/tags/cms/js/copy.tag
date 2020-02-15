<%@ tag %><%@ 
    taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%><%@ 
    taglib prefix="cms" tagdir="/WEB-INF/tags/cms"%><%@ 
    taglib prefix="cmsjs" tagdir="/WEB-INF/tags/cms/js"%>
        
<cms:debug>/* tags/cms/js/copy.tag */</cms:debug>

// Add behaviour to copy an item 
$("#copy-button").click(function () {
	$.ajax(_ctx + "/rest/item/" + nodeKey + "/copy", {
		type: "POST",
		cache: false,
		data: {
			name: $("#copy-tab input[name='name']").val(),
			simplename: $("#copy-tab input[name='simplename']").val()
		}, 
		dataType: "json",
		success: function(obj, status, z) {
			flashMessage(obj);
			
			if (! obj.error) {
				var sourceNode = _tree.getNodeByKey(nodeKey);
				sourceNode.getParent().addNode(obj.data);
			}
		},
		error: function(json, status, z) {
			serverError();
		},
	});
});

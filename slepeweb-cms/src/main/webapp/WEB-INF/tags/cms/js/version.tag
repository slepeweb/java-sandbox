<%@ tag %><%@ 
    taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%><%@ 
    taglib prefix="cms" tagdir="/WEB-INF/tags/cms"%><%@ 
    taglib prefix="cmsjs" tagdir="/WEB-INF/tags/cms/js"%>
        
<cms:debug>/* tags/cms/js/version.tag */</cms:debug>
			
// Add behaviour to create a new version 
$("#version-button").click(function () {
	$.ajax(_ctx + "/rest/item/" + nodeKey + "/version", {
		type: "POST",
		cache: false,
		dataType: "json",
		success: function(obj, status, z) {
			flashMessage(obj);
		},
		error: function(json, status, z) {
			serverError();
		},
	});
});

// Add behaviour to revert to a previous version 
$("#revert-button").click(function () {
	$.ajax(_ctx + "/rest/item/" + nodeKey + "/revert", {
		type: "POST",
		cache: false,
		dataType: "json",
		success: function(obj, status, z) {
			flashMessage(obj);
		},
		error: function(json, status, z) {
			serverError();
		},
	});
});

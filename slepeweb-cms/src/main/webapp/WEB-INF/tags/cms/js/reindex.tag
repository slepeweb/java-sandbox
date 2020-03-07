<%@ tag %><%@ 
    taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%><%@ 
    taglib prefix="cms" tagdir="/WEB-INF/tags/cms"%><%@ 
    taglib prefix="cmsjs" tagdir="/WEB-INF/tags/cms/js"%>
        
<cms:debug>/* tags/cms/js/reindex.tag */</cms:debug>

// Add behaviour to re-index content for search 
$("#reindex-button").click(function () {
	$.ajax(_ctx + "/rest/search/reindex/" + _editingItemId, {
		type: "GET",
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
			

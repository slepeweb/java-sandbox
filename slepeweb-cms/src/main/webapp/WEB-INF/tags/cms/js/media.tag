<%@ tag %><%@ 
    taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%><%@ 
    taglib prefix="cms" tagdir="/WEB-INF/tags/cms"%><%@ 
    taglib prefix="cmsjs" tagdir="/WEB-INF/tags/cms/js"%>
        
<cms:debug>/* tags/cms/js/media.tag */</cms:debug>

// Add behaviour to update media content 
function progressHandlingFunction(e){
    if(e.lengthComputable){
        $("progress").attr({value:e.loaded,max:e.total});
    }
}

$("#media-button").click(function () {
	var formData = new FormData($("#media-form")[0]);
    $.ajax({
        url: _ctx + "/rest/item/" + nodeKey + "/update/media",
        type: "POST",
        xhr: function() {
            var myXhr = $.ajaxSettings.xhr();
            if(myXhr.upload) {
                myXhr.upload.addEventListener("progress",progressHandlingFunction, false);
            }
            return myXhr;
        },
        success: function() {
			flashMessage(toStatus(false, "Media successfully uploaded"));
        },
        error: function() {
			serverError();
        },
        data: formData,
        cache: false,
        contentType: false,
        processData: false
    });
});

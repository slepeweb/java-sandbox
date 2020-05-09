_cms.media = {
	behaviour: {},
};


_cms.media.behaviour.progressHandlingFunction = function(e) {	
	// Add behaviour to update media content 
    if(e.lengthComputable){
        $("progress").attr({value:e.loaded,max:e.total});
    }
}

_cms.media.behaviour.upload = function(nodeKey) {
	$("#media-button").click(function () {
		var formData = new FormData($("#media-form")[0]);
	    $.ajax({
	        url: _cms.ctx + "/rest/item/" + nodeKey + "/update/media",
	        type: "POST",
	        xhr: function() {
	            var myXhr = $.ajaxSettings.xhr();
	            if(myXhr.upload) {
	                myXhr.upload.addEventListener("progress", _cms.media.behaviour.progressHandlingFunction, false);
	            }
	            return myXhr;
	        },
	        success: function() {
	        	_cms.support.flashMessage(_cms.support.toStatus(false, "Media successfully uploaded"));
	        },
	        error: function() {
	        	_cms.support.serverError();
	        },
	        data: formData,
	        cache: false,
	        contentType: false,
	        processData: false
	    });
	});


	$("#media-form input[name=media]").click(function () {
		$("#media-form progress").attr("value", 0);
	});
}

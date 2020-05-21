_cms.media = {
	behaviour: {},
	refresh: {},
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
	        	_cms.media.refresh.tab(nodeKey);
	        	
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

_cms.media.thumbnailRequired = false;

_cms.media.behaviour.thumbnailRequired = function() {
	$("#media-form input[name='thumbnail']").click(function () {
		var ele = $("#media-form .thumbnail-option");
		if (! _cms.media.thumbnailRequired) {
			ele.show();
		}
		else {
			ele.hide();
		}
		
		_cms.media.thumbnailRequired = ! _cms.media.thumbnailRequired;
	});
}

_cms.media.behaviour.enableUploadButton = function() {
	$("#media-form input[name='media']").change(function(){
		$("#media-button").removeAttr("disabled");
	});
}

_cms.media.refresh.tab = function(nodeKey) {
	_cms.support.refreshtab("media", nodeKey, _cms.media.onrefresh);
}


_cms.media.onrefresh = function(nodeKey) {
	_cms.media.behaviour.upload(nodeKey);
	_cms.media.behaviour.thumbnailRequired();
	_cms.media.behaviour.enableUploadButton();
}
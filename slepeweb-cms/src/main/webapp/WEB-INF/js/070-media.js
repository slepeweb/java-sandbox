_cms.media = {
	behaviour: {},
	refresh: {},
	sel: {
		MEDIA_TAB: "#media-tab",
		UPLOAD_BUTTON: "#media-button",
		CANCEL_UPLOAD_BUTTON: "#cancel-media-button",
		FORM: "#media-form",
	}
};

_cms.media.sel.PROGRESS = _cms.media.sel.FORM + " progress";
_cms.media.sel.THUMBNAIL_CHECKBOX = _cms.support.fi(_cms.media.sel.FORM, "thumbnail");
_cms.media.sel.FILE_INPUT = _cms.support.fi(_cms.media.sel.FORM, "media");
_cms.media.sel.WIDTH_INPUT_CONTAINER = _cms.media.sel.FORM + " .thumbnail-option";
_cms.media.sel.WIDTH_INPUT = _cms.support.fi(_cms.media.sel.FORM, "width");
_cms.media.sel.ALL_FORM_ELEMENTS = _cms.media.sel.FORM + " :input";
_cms.media.sel.UPLOAD_BUTTON = _cms.media.sel.MEDIA_TAB + " button.action",
_cms.media.sel.RESET_BUTTON = _cms.media.sel.MEDIA_TAB + " button.reset",

_cms.support.setTabIds(_cms.media, "media");

_cms.media.behaviour.progressHandlingFunction = function(e) {	
	// Add behaviour to update media content 
    if(e.lengthComputable){
        $(_cms.media.sel.PROGRESS).attr({value:e.loaded,max:e.total});
    }
}

_cms.media.behaviour.upload = function(nodeKey) {
	$(_cms.media.sel.UPLOAD_BUTTON).click(function () {
		var formData = new FormData($(_cms.media.sel.FORM)[0]);
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


	$(_cms.media.sel.FILE_INPUT).click(function () {
		$(_cms.media.sel.PROGRESS).attr("value", 0);
	});
}

_cms.media.behaviour.thumbnailRequired = function() {
	$(_cms.media.sel.THUMBNAIL_CHECKBOX).click(function () {
		var ele = $(_cms.media.sel.WIDTH_INPUT_CONTAINER);
		var mimeType = $(this).attr("data-mimetype");
		var choice = $(_cms.media.sel.THUMBNAIL_CHECKBOX + ":checked").val()

		if (choice == "none") {
			ele.hide();
		}
		else {
			ele.show();
		}
		
		_cms.media.check_data_is_complete();
	});
}

_cms.media.behaviour.formchange = function() {
	$(_cms.media.sel.ALL_FORM_ELEMENTS).mouseleave(function(){
		_cms.media.check_data_is_complete();
	});
}

_cms.media.check_data_is_complete = function() {
	var isComplete = false;
	if ($(_cms.media.sel.FILE_INPUT).val()) {
		if ($(_cms.media.sel.THUMBNAIL_CHECKBOX).is(":checked")) {
			isComplete = $(_cms.media.sel.WIDTH_INPUT).val() != "";
		}
		else {
			isComplete = true;
		}
	}
	
	if (_cms.support.enableIf(_cms.media.sel.UPLOAD_BUTTON, isComplete)) {
		_cms.support.enable(_cms.media.sel.RESET_BUTTON);
	}
	else {
		_cms.support.disable(_cms.media.sel.RESET_BUTTON);
	}
}

_cms.media.behaviour.reset = function(nodeKey) {
	// Add behaviour to cancel upload.
	$(_cms.media.sel.RESET_BUTTON).click(function (e) {
		_cms.support.resetForm(_cms.media.refresh.tab, nodeKey, e);
	});
}

_cms.media.refresh.tab = function(nodeKey) {
	_cms.support.refreshtab("media", nodeKey, _cms.media.onrefresh);
}


_cms.media.onrefresh = function(nodeKey) {
	_cms.media.behaviour.upload(nodeKey);
	_cms.media.behaviour.reset(nodeKey);
	_cms.media.behaviour.thumbnailRequired();
	_cms.media.behaviour.formchange();
}
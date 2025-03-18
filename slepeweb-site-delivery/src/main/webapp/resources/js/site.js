let _site = {
	support: {},
}

_site.support.ajax = function(method, url, data, success, fail) {
	let params = {
		type: method,
		cache: false,
		xhr: data.xhr,
		data: data.data,
		contentType: data.contentType,
		dataType: data.dataType,
		mimeType: data.mimeType,
		success: success,
		processData: data.processData,
		error: fail
	}
	
	if (! params.error) {
		params.error = function(a, b, c) {
			console.log("Server error:", '\na:', a, '\nb:', b, '\nc:', c);
		}
	}
	
	$.ajax(url, params);
}

_site.support.openEditor = function(origid) {
	_site.support.ajax('GET', '/rest/passkey', {dataType: 'json', mimeType: 'application/json'}, function(resp) {
		if (! resp.error) {
			let url = `//${resp.data[0]}/cms/page/login?xpass=${resp.data[1]}&origid=${origid}`;
			window.open(url, '_blank');
		}
	});
}

$(function() {
	$('i#open-editor').click(function() {
		_site.support.openEditor($(this).attr('title'));
	})
})
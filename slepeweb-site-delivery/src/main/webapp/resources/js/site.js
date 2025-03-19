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

_site.support.checkSession = function() {
	_site.support.ajax('GET', '/rest/session', {dataType: 'json', mimeType: 'application/json'}, function(resp) {
		if (! resp.error) {
			let isExpiring = resp.data[0]
			let secondsRemaining = resp.data[1]
			
			if (isExpiring) {
				$('div#session-expiry-warning span').text('' + secondsRemaining)
				$('div#session-expiry-warning').removeClass('hidden')
				
				let audio = $("#bell");
				if (audio && audio.get(0)) {
					audio.get(0).play()
					window.setTimeout(function() {
						audio.get(0).play()
						}, 500);
				}
			}
		}
	});
}

$(function() {
	$('i#open-editor').click(function() {
		_site.support.openEditor($(this).attr('title'));
	})
	
	window.setInterval(_site.support.checkSession, 60 * 1000)
})
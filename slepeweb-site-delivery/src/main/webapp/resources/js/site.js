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
	_site.support.ajax('GET', '/rest/xpasskey', {dataType: 'json', mimeType: 'application/json'}, function(resp) {
		if (! resp.error) {
			let url = `//${resp.data[0]}/cms/page/login?xpass=${resp.data[1]}&origid=${origid}`;
			window.open(url, '_blank');
		}
	});
}

_site.support.checkSession = function() {
	_site.support.ajax('GET', '/rest/session/check', {dataType: 'json', mimeType: 'application/json'}, function(resp) {
		if (! resp.error) {
			let isExpiring = resp.data[0]
			let secondsRemaining = resp.data[1]
			
			if (isExpiring) {
				if (secondsRemaining <= 0) {
					_site.support.ajax('GET', `/rest/session/logout/${_site.siteId}`, {dataType: 'json', mimeType: 'application/json'}, function(resp) {
						if (resp.error) {
							console.log('Failed to end session cleanly')
						}
						window.open('/login', '_self')
					})
				}
				else {				
					$('div#session-expiry-warning span').text('' + secondsRemaining)
					$('div#session-expiry-warning').removeClass('hidden')
					_site.alertSound.volume = 1
					_site.alertSound.play()
				}
			}
		}
	});
}

_site.support.moveComponent = function(parent$) {
	let id = parent$.attr('data-enum')
	let comp$ = $(`div#component-${id}`)
	
	if (! comp$) {
		return
	}
	
	parent$.append(comp$)
}


$(function() {

	$('i#open-editor').click(function() {
		_site.support.openEditor($(this).attr('title'));
	})
	
	if (_site.isSecured) {
		window.setInterval(_site.support.checkSession, 60 * 1000)
	}
	
	$(document).one('mousemove click keydown', function() {
		console.log('Alerter bound to user event')
		_site.alertSound.volume = 0
		_site.alertSound.play().catch(() => console.log("Autoplay prevented, but now user interaction allows it."));
	});

})
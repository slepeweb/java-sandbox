_cms.search = {
	behaviour: {},
	refresh: {},
	sel: {
		SEARCH_BAR: "#search-bar",
		SEARCH_RESULTS: "#searchresults",
	}
};

_cms.search.behaviour.navigate = function() {
	$(`${_cms.search.sel.SEARCH_RESULTS} .navigate`).click(function(event) {
		var key = $(this).attr("href");
		_cms.leftnav.navigate(key);
		event.preventDefault();
	});
}

_cms.search.behaviour.xnavigate = function() {
	$(`${_cms.search.sel.SEARCH_RESULTS} .xnavigate`).click(function(event) {
		var targetId = $(this).attr("href");
		var url = '/rest/xpasskey/' + targetId;
	
		_cms.support.ajax('GET', url, {dataType: 'json', mimeType: 'application/json'}, function(resp) {
			if (! resp.error) {
				let url = `//${resp.data[0]}/cms_/page/login?xpass=${resp.data[1]}&origid=${targetId}`;
				window.open(url, 'cmse-' + _cms.siteShortname);
			}
		});
		event.preventDefault();
	});
}

_cms.search.onpageload = function() {
	$(`${_cms.search.sel.SEARCH_BAR} button`).click(function() {
		_cms.search.action();
	});
	
	$(`${_cms.search.sel.SEARCH_BAR} input[name=searchtext]`).keydown(function(e) {
		if (e.which == 13) {
			_cms.search.action();
		}
	});
}
	
_cms.search.action = function() {
	$.ajax(`${_cms.ctx}/rest/search`, {
		type: "POST",
		cache: false,
		data: {
			key: _cms.editingItemId,
			searchtext: $(`${_cms.search.sel.SEARCH_BAR} input[name=searchtext]`).val(),
		},
		dataType: "html",
		success: function(html, status, z) {
			$(_cms.search.sel.SEARCH_RESULTS).html(html);
				
			_cms.search.behaviour.navigate();			
			_cms.search.behaviour.xnavigate();			
			_cms.dialog.open(_cms.dialog.searchresults);
		},
		error: function(jqxhr, status, message) {
			console.log(message);
		},
	});
}



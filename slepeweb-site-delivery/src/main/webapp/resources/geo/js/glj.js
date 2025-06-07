let $doSearchAction = function(terms) {
	if (terms !== '') {
		window.location = `/searchresults?terms=${encodeURIComponent(terms)}`
	}
}

$(function() {
	$('div#search-input button').click(function() {
		$doSearchAction($(this).prev().val().trim())
	})
	
	$('div#search-input input').keyup(function(e) {
		if (e.which === 13) {
			$doSearchAction($(this).val().trim())
		}
	})
	
	$("div#history select").change(function(){
		var path = $(this).val();
		if (path != 'unset') {
			window.location = path;
		}
	});

})

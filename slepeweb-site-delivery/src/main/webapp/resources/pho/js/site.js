$(function() {
	$('div#tag-cloud div.tag-link').each(function(i, ele) {
		let div$ = $(this);
		div$.css('font-size', div$.attr('data-size'));
		div$.css('color', div$.attr('data-color'));
	});
	
	$('div#tag-cloud div.tag-link').click(function() {
		let value = $(this).attr('data-value');
		let form = $('#search-form');
		form.find('input[name=searchtext]').val(value);
		form.find('form').submit();
	});
	
	$('div#search-form i.clear-input').click(function() {
		$(this).prev().val('');
	});
});	

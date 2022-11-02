$(function() {
	$('#from-date, #to-date').datepicker({
		dateFormat: 'yy/mm/dd',
		changeMonth: true,
		changeYear: true,
	});

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
		$(this).next().val('');
	});
	
	$('select#top50-tags-selector').change(function() {
		let tagPlusNumber = $(this).find('option:selected').html();
		let tagValue = tagPlusNumber.split(" ")[0]
		window.location = '/searchresults?view=get&searchtext=' + tagValue
	});
	
});	

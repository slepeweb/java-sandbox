function yearInRange(year) {
	return year >= 1700 && year <= new Date().getFullYear();
}

$(function() {
	/*
	$('#from-date, #to-date').datepicker({
		dateFormat: 'yy/mm/dd',
		changeMonth: true,
		changeYear: true,
	});
	*/

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
		window.location = `${_site.searchresultsItemPath}?view=get&searchtext=` + tagValue
	});
	
	$('div#search-button-container input').click(function(e){
		let div$ = $('div#search-form');
		let text = div$.find('input[name=searchtext]').val();
		let now = new Date().getFullYear();
		let error = false;
		
		let year = parseInt(div$.find('input[name=from]').val());
		if (! isNaN(year) && ! yearInRange(year))  {
			window.alert("'From' year is out of range");
			error = true;
		}
		else {
			year = parseInt(div$.find('input[name=to]').val());
			if (! isNaN(year) && ! yearInRange(year))  {
				window.alert("'To' year is out of range");
				error = true;
			}
		}
		
		if (error) {
			e.preventDefault();
		}
	});
});	

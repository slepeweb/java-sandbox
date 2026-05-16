$(function() {
	$('form#chart-form button#add-group-button').click(function(){
		let targetGroupId = $('tr.category-list.invisible').attr('data-id');
		$(`tr.category-list[data-id=${targetGroupId}]`).removeClass('invisible');
	})
	
	$("form#chart-form input#cancel-button").click(function(e){
		window.location = webContext + "/chart/list"
	});
	
	$("#search-selector").autocomplete({
		source: _money.chart.searchOptions,
		minLength: 2,
		select: function(e, ui) {
			e.preventDefault()
			$("#search-selector").val('')
			let tr$ = $(`table#chart-table tr#tr${ui.item.value}`);
			tr$.removeClass('invisible');
			tr$.find('input').prop('checked', 'true')
		}
	});

	$('table#chart-table input[type=checkbox]').click(function() {
		let input$ = $(this)
		if (! input$.prop('checked')) {
			input$.parent().parent().addClass('invisible');
		}
	})
	
	$('form#chart-form').submit(function(e) {
		e.preventDefault()

		let ids = ''
		$('table#searches input').each(function() {
			let input$ = $(this)
			if (input$.prop('checked')) {
				ids += (input$.attr('name').substring(3) + ',')
			}
		})
		
		if (ids.length > 0) {
			ids = ids.substring(0, ids.length - 1)
		}
		console.log(`idlist = ${ids}`)
		$('input#idlist').val(ids)
		
		$('table#searches').remove()
		this.submit() // NOT $(this).submit !!!
	})
});

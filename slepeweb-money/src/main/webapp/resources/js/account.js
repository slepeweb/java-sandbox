$(function() {
	$('select[name=type]').change(function() {
		let type$ = $("select[name='type']")
		let type = type$.find(':selected').val()
		if (type === 'savings') {
			$('tr.savings, tr.accountnos').removeClass('hidden')
		}
		else if (type === 'current') {
			$('tr.accountnos').removeClass('hidden')
			$('tr.savings').addClass('hidden')
		}
		else {
			$('tr.savings, tr.accountnos').addClass('hidden')
		}
	})
});
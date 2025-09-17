$(function() {
	if (_flash) {
		$('div.flash-error').css('display', 'block')
	}
	
	if (_focus) {
		$(`input[name=${_focus}]`).focus()
	}
	
	$('input').click(function() {
		$('div.flash-error').css('display', 'none')
	})
})

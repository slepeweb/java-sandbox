const _displayTime = () => {
	$('#login-header span').html((new Date()).toLocaleTimeString())
}

$(function() {
	_displayTime()
	
	setInterval(() => {
		_displayTime()
	}, 1000)
	
	$('input[name=username]').focus()
})
$(function() {
	// Action code for when user submits 'lookup' form
	$('#lookup').click(() => {
		var company = $('#company').val()
		if (company) {
			// Proceed with db lookup for this user 
			socket.emit('company-lookup-request', {
				company: company,
				owner: _actor,
			})
		}
		else {
			$('#company').parent().effect('bounce', {complete: function() {
				$('#company').focus()
			}}, 1000)
		}
	})
		
	// Add reset behaviour to newly added span
	$('span.reset').click(() => {
		_setFields()
		_toggleDisplay()
	})		
			
	$('p#logout-icon i').click(() => {
		_flashMessage(`User ${_actor} logged out`)
		_welcome()
		_updateHttpSession('')
		socket.emit('retire-session', _actor)
		socket.emit('progress-request', _actor)	
		_actor = null
		_loginDialog.dialog('open')
	})
	
	$('#upload-icon i').click((e) => {
		_uploadDialog.dialog('open')
	})
	
	$('#user-list-icon i').click((e) => {
		_userListDialog.dialog('open')
	})
})

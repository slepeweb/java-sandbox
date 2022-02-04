socket.on('company-list-response', (list) => {
	var companies = []
	list.forEach((item, c) => {
		companies.push(item.company)
	})
	
	$('#company').autocomplete({
		source: companies
	})
	
	$('#company').focus()
})
	
// Updates the main page (mode B)
socket.on('company-lookup-response', (d) => {
	_setFields(d)
	_toggleDisplay()
})

socket.on('flash', (msg, error) => {
	_flashMessage(msg, error)
})

socket.on('login-response', (msg, err, username) => {
	_flashMessage(msg, err)
	var form = $('#login-dialog')
	
	if (! err) {
		_actor = username
		_loginDialog.dialog('close')
		
		_welcome(_actor)
		_progressBarUpdater(_actor)
		
		// Clear out the form
		form.find('input[name=username]').val('')
		form.find('input[name=password]').val('')
		
		// Retrieve data for this user
		socket.emit('company-list-request', _actor)
		
		$('#company').focus()
		
		// Store username in this http session
		_updateHttpSession(username)
	}
	else {
		form.find('input[name=password]').val('')
	}
})

socket.on('progress-response', (percent) => {
	$('#progressbar').progressbar({value: percent})
	
	if (percent <= 0) {
		if (_actor) {
			if (! _loginDialog.dialog('isOpen')) {
				_flashMessage('Session expired', true)
				_setFields()
			}
			
			_actor = null
			$('div.heading-rhs p.welcome').html('')
		}
		_loginDialog.dialog('open') 
	}
})

socket.on('user-list-response', (list, isAdmin, msg, err = false) => {
	_flashMessage(msg, err)
	
	var container = $('#user-list-dialog table')
	var s = '<tr><th>Login id</th><th>Email</th><th>Default login</th><th>Admin?</th><th>Actions</th></tr>'
	
	$.each(list, function(index, value) {
		s += `<tr><td>${value.username}</td><td>${value.email}</td><td>${value.defaultlogin}</td><td>${value.admin ? 'Yes' : 'No'}</td>`
		s += '<td><i class="far fa-edit user-edit" title="Edit" />'
		if (isAdmin) {
			s += '<i class="far fa-trash-alt user-delete" title="Delete" />'
		}
		s += '</td></tr>'
	})
	
	container.html(s)
	
	$('#user-list-dialog .user-edit').click(function () {
		_openUpsertDialog('update', _identifyUserFromList($(this)))
	})
	
	$('#user-list-dialog .user-delete').click(function() {
		// NOTE: $(this) gets the wrong object when inside a '=>' function
		//       The function needs to be declared using 'function' keyword
		//       in order for 'this' to be interpreted correctly
		
		socket.emit('user-delete-request', _actor, _identifyUserFromList($(this)).username)
	})
})

socket.on('user-upsert-response', (msg, err = false) => {
	_flashMessage(msg, err)
	if (! err) {
		_userUpsertDialog.dialog('close')
		_refreshUserList()
	}
})

socket.on('user-upsert-prepare-response', (uw, isAdmin) => {
	_readAndPrepareUserUpsertDialog(uw.mode, uw, isAdmin)	
	_userUpsertDialog.dialog('open')	
})

socket.on('user-delete-response', (msg, err = false) => {
	_flashMessage(msg, err)
	if (! err) {
		_refreshUserList()
	}
})



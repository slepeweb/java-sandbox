/*
	This code is ONLY used on the home page.
	
	The home page has 2 different display modes. An autocompleting text input
	is provided in mode A, to identify the company/website of interest. Mode B
	displays the result of the password lookup.
*/
var socket = io()

const flashMessage = (msg, error) => {
	var id = 'p#flash'
	$(id).html(msg).css('padding', '0.5em')
	console.log(msg)
	
	if (error) {
		$(id).addClass('error')
		console.log(error)
	}
	
	setTimeout(() => {
		$(id).html('').removeClass('error').css('padding', '0em')
	}, 3000)
}
	
socket.on('flash', (msg, error) => {
	flashMessage(msg, error)
})
	
// Action triggered by message from server when user session times out ... go to login page
socket.on('relogin', () => {
	window.location = '/users/login?err=User%20timed%20out%20-%20Please%20re-login'
})

// Action triggered by message from server to update the page (mode B)
socket.on('document', (d) => {
	_setFields(d)
	_toggleDisplay()
})

// Function to set the values on the page in mode B
const _setFields = (d) => {
	$('#company, #username, #password, #password2, #notes').val('')
	
	if (d) {
		$('#company').val(d.company)
		$('#username').val(d.username)
		$('#password').val(d.password)
		$('#password2').val(d.chunked)
		$('#notes').val(d.notes)
		
		if (d.partyid == 'none') {
			$('#result tr.hideif').hide()
		}
		else {
			$('#result tr.hideif').show()
		}
	}
}

// This function switches display mode
const _toggleDisplay = () => {
	var company = $('#company')
	var displayModeA = company.val()
	var hl = 'highlight'
	
	if (! displayModeA) {
		// Switch to initial display mode (A)
		$('p#instructionA').show()
		$('p#instructionB').hide()
		company.removeAttr('disabled')
		company.autocomplete('enable')
		company.focus()
		$('#main-table').removeClass(hl)
		$('tbody#result').hide()
		$('tbody#submit-button').show()
	}
	else {
		// Display lookup results (B)
		$('#instructionA').hide()
		$('#instructionB').show()
		company.attr('disabled', 'true')
		company.autocomplete('disable')
		$('#main-table').addClass(hl)
		$('tbody#result').show()
		$('tbody#submit-button').hide()
	}
}

// Make an ajax request to the server to identify the logged-in user
const _whoami = (onSuccess) => {
	$.ajax("/users/whoami", {
		type: "GET",
		cache: false,
		dataType: "json",
		success: function(iam, status, z) {
			onSuccess(iam)
		},
		error: function(iam, status, z) {
			console.error(iam, status, z)
		}
	})
}

var _progressValue = 100, _progressDecrement = 1, _progressInterval = 3000

// Function to update the progress bar, according to time left in session
const _progress = () => {
	_progressValue -= _progressDecrement
	if (_progressValue >= 0) {
		$('#progressbar').progressbar({value: _progressValue})
	}
	/*
	 * Not necessary, since server side knows when session has expired, and
	 * can send a message back to client to re-login
	 *
	else {
		window.location='/users/login?err=User%20session%20timed%20out'
	}
	*/
}

var _companies = null
var _uploadDialog = null, _uploadForm = null

// After page is fully loaded ...
$(function() {
	// Get the server to identify the logged-in user (from session data)
	_whoami((iam) => { 
		socket.emit('company-list-request', iam)
	})
	
	// Act upon message from server to render list of registered companies/websites
	socket.on('company-list', (list) => {
		_companies = []
		list.forEach((item, c) => {
			_companies.push(item.company)
		})
		
		$('#company').autocomplete({
			source: _companies
		})
		
		$('#company').focus()

		// Action code for when user submits 'lookup' form
		$('#lookup').click(() => {
			var company = $('#company').val()
			if (company) {
				// Proceed with db lookup for this user 
				_whoami((iam) => { 
					socket.emit('lookup', {
						company: company,
						owner: iam,
					})
				})
			}
		})
			
		// Add reset behaviour to newly added span
		$('span.reset').click(() => {
			_setFields()
			_toggleDisplay()
		})		
		
		// Initialise the progress bar
		_progressValue = parseInt($('#progressbar').attr('data-progress'))	
		_progressDecrement = parseInt($('#progressbar').attr('data-progress-decrement'))	
		_progressInterval = parseInt($('#progressbar').attr('data-progress-interval'))	
		_progress()
		
		setInterval(() => {
			_progress()
		}, _progressInterval)
	
	})
	
	// Animate the logout button/icon, to remind user NOT 
	// to leave the browser unattended.
	setInterval(() => {
		$('p#logout i').effect('bounce', {}, 1000)
	}, 30000)
	
	_uploadDialog = $('#upload-dialog').dialog({
		autoOpen: false,
		height: 400,
		width: 350,
		modal: true,
		title: 'Upload spreadsheet',
		buttons: {
			Submit: function() {
				var input = $('input[name=xlsx]')
				if (input.val()) {
					_uploadForm.submit()
				}
				else {
					input.effect('bounce', {}, 1000)
				}
			},
			Cancel: function() {
				_uploadDialog.dialog('close')
			}
		},
		close: function() {
			_uploadForm.reset()
		},
	})
	
	_uploadForm = _uploadDialog.find('form')[0]
	
	$('#upload-icon i').click((e) => {
		_uploadDialog.dialog('open')
	})
	
	// Flash message, if present
	if (_flashMsg) {
		flashMessage(_flashMsg, _flashClazz != 'none')
	}
});

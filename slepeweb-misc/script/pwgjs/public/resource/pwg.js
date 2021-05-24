var socket = io()

socket.on('flash', (msg, warn) => {
	var ele = $('div#flash p')
	if (warn) {
		ele.addClass('warning')
	}
	ele.html(msg);
	
	// Log the message
	console.log(msg)
	
	// If after 5 seconds the flash message is unchanged, then clear it out
	setTimeout(() => {
		if (ele.html() == msg) {
			ele.html('')
			ele.removeClass('warning')
		}
	}, 5000)
})

socket.on('relogin', () => {
	window.location = '/users/login?err=User%20timed%20out%20-%20Please%20re-login'
})

socket.on('document', (d) => {
	_setFields(d)
	_toggleDisplay()
})

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

var _progressValue = 0

const _progress = () => {
	_progressValue -= 1
	if (_progressValue >= 0) {
		$('#progressbar').progressbar({value: _progressValue})
	}
	else {
		window.location='/users/login?err=User%20session%20timed%20out'
	}
}

var _companies = null

// After page is fully loaded ...
$(function() {
	// Initialise prgress bar value
	_progressValue = parseInt($('#progressbar').attr('data-progress'))
	
	_whoami((iam) => { 
		socket.emit('company-list-request', iam)
	})
	 
	socket.on('company-list', (list) => {
		_companies = []
		list.forEach((item, c) => {
			_companies.push(item.company)
		})
		
		$('#company').autocomplete({
			source: _companies
		})
		
		$('#company').focus()

		$('#lookup').click(() => {
			var company = $('#company').val()
			if (company) {
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
		
	})
	
	setInterval(() => {
		$('p#logout i').effect('bounce', {}, 1000)
	}, 30000)
	
	
	_progress()
	
	// Progress bar loses 1% every 3 seconds, reaching zero in 300 secs,
	// which matches the session timeout
	setInterval(() => {
		_progress()
	}, 3 * 1000)
});

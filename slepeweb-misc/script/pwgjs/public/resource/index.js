var socket = io()

const _flashMessage = (msg, error) => {
	if (msg) {
		var id = 'p#flash'
		$(id).html(msg).css('padding', '0.5em')
		console.log(msg)
		
		if (error) {
			$(id).addClass('error')
		}
		
		setTimeout(() => {
			$(id).html('').removeClass('error').css('padding', '0em')
		}, 7000)
	}
}
	
const padTime = (s) => {
	var t = '' + s
	return t.length < 2 ? '0' + t : t
}

const _displayTime = () => {
	var d = new Date()
	var s = padTime(d.getHours()) + ':' + padTime(d.getMinutes())
	$('#heading p.timer').html(s)
}

const _welcome = (name) => {
	var s = name ? 'Welcome ' + name : ''
	$('div.heading-rhs p.welcome').html(s)
}

const _updateHttpSession = (username) => {
	// Store username in this http session
	$.ajax({
		type: "GET",
		url: "/session?username=" + username,
		cache: false,
		dataType: "json",
		success: function(dummy, status, z) {
			// do nothing
		},
		error: function(dummy, status, z) {
			// do nothing
		}
	})
}

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

/*
	The home page has 2 different display modes, PLUS several overlaying diaologs. 
	An autocompleting text input is provided in mode A, to identify the company/website 
	of interest. Mode B displays the result of the password lookup.
*/
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

const _progressBarUpdater = (u) => {
	if (u) {
		socket.emit('progress-request', u)	
		
		if (_progressScheduler) {
			clearInterval(_progressScheduler)
		}
		
		// Every 10 secs, check session is still live
		_progressScheduler = setInterval(() => {
			socket.emit('progress-request', u)
		}, 10000)
	}
}

const _submitLoginForm = () => {
	var form = $('#login-dialog')
	var u = {
		username: form.find('input[name=username]').val().trim(),
		password: form.find('input[name=password]').val().trim(),
	}
	
	if (u.username && u.password) {
		socket.emit('login-request', u)
	}
	else {
		form.effect('bounce', {}, 1000)
	}
}

var _progressScheduler = null

// After page is fully loaded ...
$(function() {
	// Animate the logout button/icon, to remind user NOT 
	// to leave the browser unattended.
	setInterval(() => {
		$('div.heading-rhs').effect('bounce', {}, 1000)
	}, 30000)
	
	// Display current time ...
	_displayTime()
	
	// ... and update every 30 secs
	setInterval(() => {
		_displayTime()
	}, 30000)
	
	// User welcome message
	_welcome(_actor)
	
	// Display the progress bar, and update display on regular interval
	_progressBarUpdater(_actor)
	
	// If user not logged in, open dialog
	if (! _actor) {
		_loginDialog.dialog('open')
	}
	else {
		socket.emit('company-list-request', _actor)
	}
})


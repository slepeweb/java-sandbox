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
		success: function(obj, status, z) {
			onSuccess(obj)
		},
		error: function(obj, status, z) {
			console.error(obj, status, z)
		}
	})
}

var _progressValue = 101
const _progress = () => {
	_progressValue -= 1
	$('#progressbar').progressbar({value: _progressValue})
}

var _companies = null

// After page is fully loaded ...
$(function() {
	socket.emit('company-list-request')
	 
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
				_whoami((res) => { 
					socket.emit('lookup', {
						company: company,
						id: res.id,
						key: res.key
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
	
	setInterval(() => {
		_progress()
	}, 6000)
});

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

socket.on('document', (d) => {
	if (d) {
		_setFields(d)
		_toggleForm()
	}
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

const _toggleForm = () => {
	var a = $('span.submit')
	var b = $('span.reset')
	var tbody = $('tbody#result')
	var h = 'hide'
	var hl = 'highlight'
	var calculationDisplayed = a.hasClass(h)
	
	if (/* current status is */ calculationDisplayed) {
		// Switch form to make another choice of company
		a.removeClass(h)
		b.addClass(h)
		$('#company').removeAttr('disabled')
		$('#company').autocomplete('enable')
		$('#company').focus()
		$('#main-table').removeClass(hl)
		$('#instruction').html(_startInstruction)
		tbody.hide()
	}
	else {
		// Switch form to show calculation
		b.removeClass(h)
		a.addClass(h)
		$('#company').autocomplete('disable')
		$('#company').attr('disabled', 'true')
		$('#main-table').addClass(hl)
		$('#instruction').html(_continueInstruction)
		tbody.show()
	}
}

var _companies = null
const _startInstruction = 'Start typing the company name, then click on the arrow'
const _continueInstruction = 'Click on the reset icon to start again'

// After page is fully loaded ...
$(function() {
	socket.emit('company-list-request')
	$('#instruction').html(_startInstruction)
	$('#company').focus()
	 
	socket.on('company-list', (list) => {
		_companies = []
		list.forEach((item, c) => {
			_companies.push(item.company)
		})
		
		$('#company').autocomplete({
			source: _companies,
		});	
		
		$('span.submit').click(() => {
			var company = $('#company').val()
			if (company) {
				socket.emit('lookup', {
					company: company,
					key: $('#key').val()
				})
			}
			else {
				// Flash a warning?
			}
		})	
			
		$('span.reset').click(() => {
			_setFields()
			_toggleForm()
		})		
	})

});

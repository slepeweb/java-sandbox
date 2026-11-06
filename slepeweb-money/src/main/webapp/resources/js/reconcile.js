_money.reconcile.loseCommas = function(s) {
	return s.replace(/,/g, '')
}

_money.reconcile.toPence = function(str) {
	return Math.round(_money.reconcile.loseCommas(str) * 100)
}

_money.reconcile.displayPounds = function(pence) {
	return (pence / 100).toFixed(2)
}

_money.reconcile.flash = function(msg, success = true) {
	let flasher$ = $('div.flash')
	if (! success) {
		flasher$.removeClass('success')
	}
	else {
		flasher$.addClass('success')
	}
	flasher$.text(msg)	
}

_money.reconcile.transactionIds = []
_money.reconcile.ready = false
_money.reconcile.START = 'Proceed'
_money.reconcile.RESTART = 'Discard All'


// All monetary amounts stored in (integer) pence
_money.reconcile.initialise = function(balanceNow, status = _money.reconcile.START) {
	
	// Dashboard gauges are updated by changes the js variables
	_money.reconcile.balanceNow = _money.reconcile.toPence(balanceNow)
	_money.reconcile.broughtForward = _money.reconcile.toPence($('span#balance-bf').text())
	_money.reconcile.toReconcile = _money.reconcile.balanceNow - _money.reconcile.broughtForward
	_money.reconcile.reconciled = 0
	_money.reconcile.outstanding = _money.reconcile.toReconcile - _money.reconcile.reconciled
	_money.reconcile.paused = $('input[name=paused]').val() == 'true'

	// Display numbers on dashboard		
	let displayPounds = _money.reconcile.displayPounds
	$('span#reconcile-target').text(displayPounds(_money.reconcile.toReconcile))
	$('span#amount-reconciled').text(displayPounds(_money.reconcile.reconciled))
	$('span#amount-outstanding').text(displayPounds(_money.reconcile.outstanding))
	
	let flash = null
	if (! _money.reconcile.paused) {
		flash = `Click '${_money.reconcile.START}' after entering latest balance`
	}
	else {
		$('table.trn_listing').removeClass('lolight')
		flash ='Continue after pause'
	}

	_money.reconcile.flash(flash)
	
	// Display debit amounts in red
	let input$ = $('input#balance-now')
	let clazz = 'debit-amount'
	if (_money.reconcile.balanceNow < 0) {
		input$.addClass(clazz)
	}
	else {
		input$.removeClass(clazz)
	}
	
	$('button#trigger-action').text(status)	
}

// You might want to restart the reconciliation process and discard all previous entries ...
_money.reconcile.restart = function() {
	let balanceNow = $('input#balance-now').val()
	_money.shared.ajax('GET', '/transaction/reconcile/clear', {dataType: 'json', contentType: 'application/json'}, function() {
		_money.reconcile.initialise(balanceNow)
		_money.reconcile.transactionIds = []

		_money.reconcile.flash('Previous actions discarded - start again')
		$('table.trn_listing tr').removeClass('reconciled')
		$('table.trn_listing').addClass('lolight')	
		$('div#menu-icons span').removeClass("opacity-full")
		$('button#commit').addClass('dimmed')
	})
}

_money.reconcile.updateDashboard = function(penceStr) {
	_money.reconcile.reconciled += Number(penceStr)
	$('span#amount-reconciled').text(_money.reconcile.displayPounds(_money.reconcile.reconciled))
	
	_money.reconcile.outstanding = _money.reconcile.toReconcile - _money.reconcile.reconciled
	$('span#amount-outstanding').text(_money.reconcile.displayPounds(_money.reconcile.outstanding))
}

// Reconcile a single transaction within the browser context.
_money.reconcile.reconcileTransaction = function(tr$) {
	_money.reconcile.updateDashboard(tr$.attr('data-pence'))
	_money.reconcile.transactionIds.push(Number(tr$.attr('data-id')))
	
	// Hide row in table
	tr$.addClass('reconciled')
	
	// Ensure undo, pause and restart buttons are enabled
	$('div#menu-icons span').addClass("opacity-full")
	
	// Have we reached our reconciliation target?
	if (Math.abs(_money.reconcile.outstanding) < 0.001) {
		// Enable the commit button
		$('button#commit').removeClass('dimmed')
		
		// Flag indicating that the reconciliation target has been reached, and db can be updated.
		_money.reconcile.ready = true
		_money.reconcile.flash(`Reconciliation comoplete - click 'Commit' to record in db`)
	}
	
	$("button#trigger-action").text(_money.reconcile.RESTART)
}

/*
	Submit reconciliation data: If form action is 'pause', then store data in the session.
	Otherwise commit the data to the db, and clear the user session.
*/
_money.reconcile.submitForm = function() {
	$('input[name=reconciledAmount]').val(_money.reconcile.balanceNow)
	$('input[name=transactionIds]').val(_money.reconcile.transactionIds.join())
	$('form#reconcile-form').submit()		
}


$(function() {
	let input$ = $('input#balance-now')
	let earlierBalanceNow = input$.val()
	input$.focus()
	
	_money.reconcile.initialise(earlierBalanceNow)
	
	$('input#balance-now').mouseleave(function() {
		let balanceNow = $(this).val()
		if (balanceNow != earlierBalanceNow) {
			earlierBalanceNow = balanceNow
			_money.reconcile.restart()
		}
	})

	$('button#trigger-action').click(function() 
	{
		let action = $(this).html()
		
		// If button reads 'Re-start' ...
		if (action == _money.reconcile.RESTART) {
			_money.reconcile.restart()
			return
		}	

		// So button must read 'Start' ... remove the lolighting effect
		$('table.trn_listing').removeClass('lolight')

		_money.reconcile.flash('In progress ...')
	})
	
	// When user reconciles a transaction ...
	$('td.reconcile-switch').click(function() {
		if ($('table.trn_listing').hasClass('lolight') || $('input#balance-now').val().length < 2) {
			// Ignore event
			return
		}
		
		_money.reconcile.reconcileTransaction($(this).parent())
	})
	
	$('button#commit').click(function() {
		if (! _money.reconcile.ready) {
			return
		}
		
		_money.reconcile.submitForm()
	})
	
	$('span#undo').click(function() {
		if (_money.reconcile.transactionIds.length == 0) {
			return;
		}
		
		let id = _money.reconcile.transactionIds.pop();
		let tr$ = $(`tr[data-id=${id}]`)
		tr$.removeClass('reconciled')
		
		_money.reconcile.updateDashboard(-1 * tr$.attr('data-pence'))
		
		if (_money.reconcile.transactionIds.length == 0) {
			$('div#menu-icons span').removeClass('opacity-full');
		}
	})
	
	// Pause the reconciliation process, submit the current data and store in the user session.
	$('span#pause').click(function() {
		if (_money.reconcile.transactionIds.length == 0) {
			return;
		}
		
		let href = $('form#reconcile-form').attr('action')
		$('form#reconcile-form').attr('action', href.replace('submit', 'pause'))
		_money.reconcile.submitForm()		
	})

	// Hide provisionally reconciled transactions
	$('tr[data-provisional=yes]').each(function() {
		_money.reconcile.reconcileTransaction($(this))
	})
})
_money.reconcile.loseCommas = function(s) {
	return s.replace(/,/g, '')
}

_money.reconcile.toPence = function(str) {
	return Math.round(_money.reconcile.loseCommas(str) * 100)
}

_money.reconcile.displayPounds = function(pence) {
	return (pence / 100).toFixed(2)
}

_money.reconcile.transactionIds = []
_money.reconcile.ready = false

// All monetary amounts stored in (integer) pence
_money.reconcile.initialiseData = function(balanceNow) {
	_money.reconcile.balanceNow = _money.reconcile.toPence(balanceNow)
	_money.reconcile.broughtForward = _money.reconcile.toPence($('span#balance-bf').text())
	_money.reconcile.toReconcile = _money.reconcile.balanceNow - _money.reconcile.broughtForward
	_money.reconcile.reconciled = 0
	_money.reconcile.outstanding = _money.reconcile.toReconcile - _money.reconcile.reconciled
}

_money.reconcile.updateDashboard = function(penceStr) {
	_money.reconcile.reconciled += Number(penceStr)
	$('span#amount-reconciled').text(_money.reconcile.displayPounds(_money.reconcile.reconciled))
	
	_money.reconcile.outstanding = _money.reconcile.toReconcile - _money.reconcile.reconciled
	$('span#amount-outstanding').text(_money.reconcile.displayPounds(_money.reconcile.outstanding))
}

_money.reconcile.processReconciliation = function(tr$) {
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
	}
}

_money.reconcile.submitForm = function() {
	$('input[name=reconciledAmount]').val(_money.reconcile.balanceNow)
	$('input[name=transactionIds]').val(_money.reconcile.transactionIds.join())
	$('form#reconcile-form').submit()		
}

$(function() {

	let displayPounds = _money.reconcile.displayPounds
	_money.reconcile.initialiseData($('input#balance-now').val())

	// When user updates the current balance on the statement ...
	$('input#balance-now').keyup(function() {		
		let input$ = $(this)
		if (input$.val().length < 2) {
			return
		}
		
		// Statement balance has been updated; re-initialise the page
		$('tr.reconciled').removeClass('reconciled')
		_money.reconcile.initialiseData(input$.val())
		
		// Display updates on page
		$('span#reconcile-target').text(displayPounds(_money.reconcile.toReconcile))
		$('span#amount-reconciled').text(displayPounds(_money.reconcile.reconciled))
		$('span#amount-outstanding').text(displayPounds(_money.reconcile.outstanding))
		
		// Display debit amounts in red
		let clazz = 'debit-amount'
		if (_money.reconcile.balanceNow < 0) {
			input$.addClass(clazz)
		}
		else {
			input$.removeClass(clazz)
		}
	})
	
	// When user reconciles a transaction ...
	$('td.reconcile-switch').click(function() {		
		if ($('input#balance-now').val().length < 2) {
			// Ignore event
			return
		}
		
		_money.reconcile.processReconciliation($(this).parent())
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
	
	$('span#pause').click(function() {
		if (_money.reconcile.transactionIds.length == 0) {
			return;
		}
		
		let href = $('form#reconcile-form').attr('action')
		$('form#reconcile-form').attr('action', href.replace('submit', 'pause'))
		_money.reconcile.submitForm()		
	})

	$('span#restart').click(function() {
		if (_money.reconcile.transactionIds.length == 0) {
			return;
		}
		
		_money.shared.ajax('GET', '/transaction/reconcile/clear', {dataType: 'json', contentType: 'application/json'}, function() {
			window.location.reload()
		})
	})

	// Hide provisionally reconciled transactions
	$('tr[data-provisional=yes]').each(function() {
		_money.reconcile.processReconciliation($(this))
	})
})
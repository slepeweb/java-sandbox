_money.reconcile.transactionIds = []
_money.reconcile.ready = false

// All monetary amounts stored in (integer) pence
_money.reconcile.broughtForward = 0
_money.reconcile.balanceNow = 0
_money.reconcile.reconcileTarget = 0
_money.reconcile.aggregatedAmount = 0
_money.reconcile.outstanding = 0

_money.reconcile.loseCommas = function(s) {
	return s.replace(/,/g, '')
}

_money.reconcile.toPence = function(str) {
	return Math.round(_money.reconcile.loseCommas(str) * 100)
}

_money.reconcile.displayPounds = function(pence) {
	return (pence / 100).toFixed(2)
}

_money.reconcile.updateReconciled = function(penceStr) {
	_money.reconcile.aggregatedAmount += Number(penceStr)
	$('span#amount-reconciled').text(_money.reconcile.displayPounds(_money.reconcile.aggregatedAmount))
	
	_money.reconcile.outstanding = _money.reconcile.reconcileTarget - _money.reconcile.aggregatedAmount
	$('span#amount-outstanding').text(_money.reconcile.displayPounds(_money.reconcile.outstanding))
}

$(function() {
	$('input#balance-now').val(0)
	let trim = _money.reconcile.loseCommas
	let toPence = _money.reconcile.toPence
	let displayPounds = _money.reconcile.displayPounds
	
	$('input#balance-now').keyup(function() {
		//$('tr.reconciled').removeClass('reconciled')
		
		let input$ = $(this)
		if (input$.val().length < 2) {
			return
		}
		
		_money.reconcile.balanceNow = toPence(input$.val())
		
		let p$ = input$.parent().parent()
		_money.reconcile.broughtForward = toPence(p$.find('span#balance-bf').text())
		_money.reconcile.reconcileTarget = _money.reconcile.balanceNow - _money.reconcile.broughtForward
		_money.reconcile.outstanding = _money.reconcile.reconcileTarget
		
		$('span#reconcile-target').text(displayPounds(_money.reconcile.reconcileTarget))
		$('span#amount-outstanding').text(displayPounds(_money.reconcile.reconcileTarget))
		$('p.dimmed, td.dimmed').removeClass('dimmed')
		
		// Display debit amounts in red
		let clazz = 'debit-amount'
		if (_money.reconcile.balanceNow < 0) {
			input$.addClass(clazz)
		}
		else {
			input$.removeClass(clazz)
		}
	})
	
	$('td.reconcile-switch').click(function() {
		
		if ($('input#balance-now').val().length < 2) {
			// Ignore event
			return
		}
		
		let tr$ = $(this).parent()
		_money.reconcile.updateReconciled(tr$.attr('data-pence'))
		_money.reconcile.transactionIds.push(Number(tr$.attr('data-id')))

		tr$.addClass('reconciled')	
		$('span#undo').addClass("opacity-full")
		
		// Have we reached our reconciliation target?
		if (Math.abs(_money.reconcile.outstanding) < 0.001) {
			$('button#commit').removeClass('dimmed')
			$('table tr.reconciled').removeClass('reconciled')
			$('table tbody td').addClass('dimmed').off('click')
			_money.reconcile.ready = true
		}
	})
	
	$('button#commit').click(function() {
		if (! _money.reconcile.ready) {
			return
		}
		
		$('input[name=reconciledAmount]').val(_money.reconcile.balanceNow)
		$('input[name=transactionIds]').val(_money.reconcile.transactionIds.join())
		$('form#reconcile-form').submit()		
	})
	
	$('span#undo').click(function() {
		if (_money.reconcile.transactionIds.length == 0) {
			return;
		}
		
		let id = _money.reconcile.transactionIds.pop();
		let tr$ = $(`tr[data-id=${id}]`)
		tr$.removeClass('reconciled')
		
		_money.reconcile.updateReconciled(-1 * tr$.attr('data-pence'))
		
		if (_money.reconcile.transactionIds.length == 0) {
			$('span#undo').removeClass('opacity-full');
		}
	})
})
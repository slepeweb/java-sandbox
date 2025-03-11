_money.reconcile.aggregatedAmount = 0
_money.reconcile.transactionIds = []
_money.reconcile.ready = false

$(function() {
	$('input#balance-now').val('')
	
	$('input#balance-now').keyup(function() {
		$('tr.reconciled').removeClass('reconciled')
		_money.reconcile.aggregatedAmount = 0
		
		let now$ = $(this)
		if (now$.val().length < 2) {
			return
		}
		
		let now = parseFloat(now$.val())
		let bf = parseFloat(now$.parent().parent().find('span#balance-bf').html())
		let target = (now - bf).toFixed(2)
		
		$('span#reconcile-target').html(target)
		$('span#amount-outstanding').html(target)
		$('p.dimmed, td.dimmed').removeClass('dimmed')
		
		let clazz = 'debit-amount'
		if (now < 0) {
			now$.addClass(clazz)
		}
		else {
			now$.removeClass(clazz)
		}
	})
	
	$('td.reconcile-switch').click(function() {
		
		if ($('input#balance-now').val().length < 2) {
			// Ignore event
			return
		}
		
		_money.reconcile.transactionIds.push(Number($(this).attr('data-id')))

				//$(this).parent().addClass('reconciled', 1000, 'linear')
		let td$ = $(this)
		let tr$ = td$.parent()
		let amount = parseFloat(tr$.find('td.amount span').html())
		_money.reconcile.aggregatedAmount += amount
		$('span#amount-reconciled').html(_money.reconcile.aggregatedAmount.toFixed(2))

		let outstanding$ = $('span#amount-outstanding')
		let outstanding = outstanding$.html()
		let diff = outstanding - amount
		outstanding$.html(diff.toFixed(2))
		tr$.addClass('reconciled')
		
		$('span#undo').addClass("opacity-full")
		
		if (Math.abs(diff) < 0.001) {
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
		
		let amount = Number($('input#balance-now').val()) * 100
		$('input[name=reconciledAmount]').val(amount)
		$('input[name=transactionIds]').val(_money.reconcile.transactionIds.join())		
		$('form#reconcile-form').submit()		
	})
	
	$('span#undo').click(function() {
		if (_money.reconcile.transactionIds.length == 0) {
			return;
		}
		
		let id = _money.reconcile.transactionIds.pop();
		let td$ = $(`td[data-id=${id}]`)
		td$.parent().removeClass('reconciled')
		
		if (_money.reconcile.transactionIds.length == 0) {
			$('span#undo').removeClass('opacity-full');
		}
	})
})
_money.schedule.checkFormComplete = function() {
	
	var errors = _money.shared.initFormErrorObject();	
	_money.shared.isNotEmpty('Name', 'input#label', errors);
	_money.shared.isNotEmpty('Interval', 'input#period', errors);
	_money.shared.isNotEmpty('Next date', 'input#nextdate', errors);
	_money.shared.isNotEmpty('Account', 'select#account', errors);
	_money.shared.isNotEmpty('Payee', 'input#payee', errors, 1);
	_money.transandsched.checkSplits(errors);
	_money.transandsched.checkCategoryEtc(errors);
	
	return errors
}

$(function(){
	_money.shared.getAllPayees();
});
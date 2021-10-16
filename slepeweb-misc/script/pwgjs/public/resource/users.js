const _refreshUserList = () => {
	socket.emit('user-list-request', _actor)
}

const _identifyUserFromList = (ele) => {
	var td = ele.parent().parent().children().first()
	var u = {}
	u.username = td.html()
	td = td.next()
	u.email = td.html()
	td = td.next()
	u.defaultlogin = td.html()
	td = td.next()
	u.admin = td.html() == 'Yes'	
	return u
}

/*
 * When we open the user upsert dialog, we will ALWAYS be setting the
 * form data, whether it will be an empty form (mode=add) or populated with
 * existing user data (mode=update). So, argument uw will always exist when
 * opening the dialog.
 */
const _openUpsertDialog = (formMode, uw) => {
	// Add formMode property to uw object, for convenience,
	// so that the value gets written to the corresponding hidden input field.
	uw.mode = formMode
	
	// Populate the form data depending upon whether the logged-in user
	// is an administrator
	socket.emit('user-upsert-prepare-request', uw, _actor)
}

const _rwUpsertDialog = (inputElement, property, ur, uw) => {
	var valueRead = inputElement.val().trim()
	ur[property] = valueRead
	
	if (uw) {
		inputElement.val(uw[property])
	}
}

/*
 * This function always returns the data in the user upsert form. If
 * argument uw exists, then the form fields are populated with the
 * contents of uw, that is AFTER reading the existing value. The
 * isAdmin argument is only relevant if uw provided.
 
 * NOTE: When this function is used to read the form data only, then 
 * arg uw will not be present, which is why formMode (which is sometimes 
 * buried within the uw object) needs to exist as a separate argument.
 */
const _readAndPrepareUserUpsertDialog = (formMode, uw, isAdmin) => {
	var ur = {}
	
	var form = $('#user-upsert-dialog form')
	var input = form.find('input[name=mode]')
	_rwUpsertDialog(input, 'mode', ur, uw)
	
	input = form.find('input[name=username]')
	_rwUpsertDialog(input, 'username', ur, uw)
	
	if (uw) {
		if (formMode == 'update') {
			input.attr('disabled', 'disabled')
			input.attr('placeholder', 'Leave empty if no change here')
			_userUpsertDialog.dialog('option', 'title', 'Update existing user details')
		}
		else {
			input.removeAttr('disabled')
			input.removeAttr('placeholder')
			_userUpsertDialog.dialog('option', 'title', 'Add new user')
		}
	}
	
	input = form.find('input[name=password]')
	_rwUpsertDialog(input, 'password', ur, uw)
	
	if (uw) {
		if (formMode == 'update') {
			input.attr('placeholder', 'Leave empty if no change here')
		}
		else {
			input.removeAttr('placeholder')
		}
	}
	
	input = form.find('input[name=email]')
	_rwUpsertDialog(input, 'email', ur, uw)
	input = form.find('input[name=defaultlogin]')
	_rwUpsertDialog(input, 'defaultlogin', ur, uw)
	
	/*
	 * Need to be able to distinguish between a) admin input field not being available,
	 * b) checked, or c) un-checked, so boolean values are not sufficient.
	 */
	input = form.find('input[name=admin]')
	var tr = input.parent().parent()
	var style = tr.attr('style')
	if (style && style.includes('display: none')) {
		ur.admin = ''
	}
	else {
		ur.admin = input.prop('checked') ? 'yes' : 'no'
	}
	
	if (uw) {
		if (isAdmin) {
			input.prop('checked', uw.admin)
			tr.show()
		}
		else {
			tr.hide()
		}
	}
			
	return ur
}

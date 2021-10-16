var _loginDialog
var _uploadDialog = null, _uploadForm = null
var _userListDialog = null, _userUpsertDialog = null, _userUpsertForm = null

// After page is fully loaded ...
$(function() {	
	_uploadDialog = $('#upload-dialog').dialog({
		autoOpen: false,
		height: 400,
		width: 350,
		modal: true,
		title: 'Upload spreadsheet',
		buttons: {
			Submit: function() {
				var input = $('input[name=xlsx]')
				if (input.val()) {
					var data = new FormData()
					var f = input[0].files[0]
					data.append('xlsx', f)
					
					$.ajax({
					    url: '/upload',
					    data: data,
					    cache: false,
					    contentType: false,
					    processData: false,
					    method: 'POST',
					    type: 'POST', // For jQuery < 1.9
					    success: function(res) {
					        _flashMessage(res.msg, res.err)
					        socket.emit('company-list-request', _actor)
					        _uploadDialog.dialog('close')
					    },
					    error: function(res, status, z) {
					        _flashMessage('Failed to upload file', true)
					        console.log(res, status, z)
					        _uploadDialog.dialog('close')
					    }
					})
				}
				else {
					input.effect('bounce', {}, 1000)
				}
			},
			Cancel: function() {
				_uploadDialog.dialog('close')
			}
		},
		close: function() {
			_uploadForm.reset()
		},
	})
	
	_uploadForm = _uploadDialog.find('form')[0]
	
	_loginDialog = $('#login-dialog').dialog({
		autoOpen: false,
		height: 400,
		width: 350,
		modal: true,
		title: 'Login',
		open: function() {$('input[name=username]').focus()},	
		buttons: {
			Submit: _submitLoginForm,
		},
		close: function() {
			$('#login-dialog form')[0].reset()
			_loginDialog.dialog('close')
		},
	})

	_userListDialog = $('#user-list-dialog').dialog({
		autoOpen: false,
		height: 400,
		width: 750,
		modal: true,
		title: 'User list',
		open: _refreshUserList,
		buttons: {
			'Add User': function() {
				var u = {
					username: '',
					password: '',
					email: '',
					defaultlogin: '',
					admin: false,
				}
				_openUpsertDialog('add', u)
			},
			Close: function() {
				_userListDialog.dialog('close')
			}
		},
		close: function() {
			_userListDialog.dialog('close')
		},
	})
	
	_userUpsertDialog = $('#user-upsert-dialog').dialog({
		autoOpen: false,
		height: 450,
		width: 500,
		modal: true,
		title: 'User properties',
		open: function(event, ui) {
		},
		buttons: {
			Submit: function() {
				var ur =  _readAndPrepareUserUpsertDialog('update')
				socket.emit('user-upsert-request', ur.mode, ur, _actor)
			},
			Cancel: function() {
				_userUpsertDialog.dialog('close')
			}
		},
		close: function() {
			_userUpsertDialog.dialog('close')
		},
	})
});

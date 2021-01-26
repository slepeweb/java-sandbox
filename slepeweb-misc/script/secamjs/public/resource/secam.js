var socket = io()

socket.on('flash', (msg, warning) => {
	var ele = $('div#flash p')
	ele.removeClass('warning')
	if (warning) {
		ele.addClass('warning')
		msg = `**${msg}**`
	}
	ele.html(msg);
	
	// Log the message
	console.log(msg)
	
	// Sound the bell
	var audio = $("#bell");
	if (audio && audio.get(0)) {
		audio.get(0).play();
	}
	
	// If after 5 seconds the flash message is unchanged, then clear it out
	setTimeout(() => {
		if (ele.html() == msg) {
			ele.html('')
		}
	}, 5000)
})

socket.on('table', (obj) => {
	var numRows = obj.length

	if (numRows > 0) {
		// Web page received message to render table
		var markup = '<table id="video-index-table">'
	     
		obj.forEach((item, index) => {
			var backupIcon = item.uploaded ? '' : `<i class="fas fa-cloud-upload-alt upload-icon" title="Upload this file" data-filename="${item.filename}"></i>`
			markup += `
	    	<tr>
	    		<td>${numRows - index}</td>
	    		<td><a class="table-row-data iframe" href="/media/${item.filename}" 
	    			data-id="${numRows - index}" data-filename="${item.filename}" data-size="2kb"
	    			title="filename=${item.filename}, size=${item.size}">${item.type}</a></td>
	    		<td>${item.date}</td>
	    		<td>${item.time}</td>
	    		<td><i class="far fa-trash-alt trash-icon" title="Delete this file" 
	    			data-filename="${item.filename}"></i>${backupIcon}</td>
	     	</tr>`
		})
		markup += '</table>'
	  
	  	var table = $('#video-table')
		table.html(markup);
		
		table.find(".iframe").colorbox({
			iframe : true,
			opacity : 0.5,
			closeButton : true,
			width : "90%",
			height : "80%",
			top : "15%",
			
			rel : 'group2',
			transition : "none",
			current : 'Media item {current} of {total}'
		});
		
		$('i.trash-icon').click(function() {
			$('i.filename-placeholder').html($(this).attr('data-filename'))
			_deleteDialog.dialog("open")
		})
		
		$('i.upload-icon').click(function() {		
	    	socket.emit('upload', $(this).attr('data-filename'))
		})
	}
	else {
		$('#video-table').html('<p>No media to list</p>')
	}
})

socket.on('surveillance', (bool) => {
	$('input#surveillance').prop('checked', bool)
})

// Update the DOM with new value of a single camera setting
socket.on('camera-setting', (obj) => {
	if (['brightness', 'contrast', 'mode' /*, 'ISO'*/].includes(obj.name)) {
		// These are <select> elements
		var selA = `#${obj.name} option[selected=selected]`
		var selB = `#${obj.name} option[value=${obj.display}]`
		var selP = `#${obj.name}`
		$(selA).removeAttr('selected')
		$(selB).attr('selected', 'selected')
		$(selP).val(obj.value)
	}
	else if (['height', 'width', 'timeout'].includes(obj.name)) {
		// These are text input elements
		$(`#${obj.name}`).val(obj.display)
	}
	else if (obj.name == 'vflip') {
		// These are checkbox inputs
		$(`#${obj.name}`).prop('checked', obj.value == 'true')
	}
})

// Update the DOM with existing camera settings
socket.on('camera-status', (obj) => {
	// De-select all options for all select elements, and uncheck checkboxes
	$('option[selected=selected]').removeAttr('selected')
	$('input[type=checkbox]').prop('checked', false)
	
	// select elements ...
	_selectOptionByValue('#brightness', obj.brightness)
	_selectOptionByValue('#contrast', obj.contrast)
	_selectOptionByValue('#mode', obj.exposure_mode)
	//_selectOptionByValue('#ISO', obj.ISO)
	
	// input elements
	$('#width').val(obj.width)
	$('#height').val(obj.height)
	$('#timeout').val(obj.timeout / 1000)

	// checkbox	elements
	$('#vflip').prop('checked', obj.vflip == 'true')
	
	// Surveillance status
	$('input#surveillance').prop('checked', obj.surveillance)
})

const _selectOptionByValue = (selector, value) => {
	$(`${selector} option[value="${value}"]`).attr('selected', 'selected')
}

var _deleteDialog = null

var _deselectButtons = () => {
	$('.delete-button[title=selected]').removeAttr('title');
}

const UP_ARROW_CLASS = 'fa-arrow-up'
const DOWN_ARROW_CLASS = 'fa-arrow-down'

// After page is fully loaded ...
$(function() {
	// This will load the files table into the DOM, and apply behaviour to the UI elements
	socket.emit('table-request')
	
	// This will load the current camera settings into the DOM
	socket.emit('camera-status-request')
  
	$('button#button-photo').click(function() {
		socket.emit('photo')
	});
	
	$('input#surveillance').click(function() {
		socket.emit('toggle-surveillance')
	});
	
	$('#width, #height, #brightness, #contrast, #mode, #timeout').change(function() {
		var ele = $(this)
		var name = ele.attr('id')
		var display = ele.val()
		var value = name != 'timeout' ? display : display * 1000
		
    	socket.emit('camera-setting-request', {
    		name: name,
    		value: value,
    		display: display,
    	})
	})
	
	$('#vflip').click(function() {
		var ele = $(this)
		var value = ele.is(':checked') ? 'true' : 'false'
		
    	socket.emit('camera-setting-request', {
    		name: ele.attr('id'),
    		value: value,
    		display: value,
    	})
	})
	
	_deleteDialog = $('#delete-dialog').dialog({
	    autoOpen: false,
	    height: 'auto',
	    width: 400,
	    modal: true,
	    title: 'Delete file?',
		buttons: {
			"Delete file": function() {
				let filename = $('i.filename-placeholder').html()
				socket.emit('delete', filename)
				_deleteDialog.dialog('close')
			},
			Cancel: function() {
				_deleteDialog.dialog('close')
				_deselectButtons()
			}
		},
	    close: function() {
			_deselectButtons()
		}
	})
	
	$('i#camera-settings-switch').click(function() {
		var ele = $(this)
		if (ele.hasClass(UP_ARROW_CLASS)) {
			ele.removeClass(UP_ARROW_CLASS)
			ele.addClass(DOWN_ARROW_CLASS)
			$('div#controls-wrapper').hide()
		}
		else {
			ele.removeClass(DOWN_ARROW_CLASS)
			ele.addClass(UP_ARROW_CLASS)
			$('div#controls-wrapper').show()
		}
	})
	
});

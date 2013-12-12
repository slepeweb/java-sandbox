var PSEdit = {
	attachEvents: function() {
		Event.observe(document, "keyup", this.onKeyUp);
	},
	
	detachEvents: function() {
		Event.stopObserving(document, "keyup", this.onKeyUp);
	},
	
	copyDelegate: null,
	
	stateData: new Hash(),
	
	undoables: new Array(),
	
	redoables: new Array(),	
	
	undoable: function(action, undo)  {
		var undoable = {
			"redo": action,
			"undo": undo
		};
		
		//We only need to update the client if the redoable stack wasn't already empty.
		if(PSEdit.redoables.length != 0) {
			PSEdit.notifyRedo(false);
		}
		PSEdit.redoables = new Array();
		//We only need to update the client if the undoable stack was empty.
		if(PSEdit.undoables.push(undoable) == 1) {
			PSEdit.notifyUndo(true);
		}
		
		action(); //perform the action
	},
	
	undo: function() {
		if(PSEdit.undoables.length > 0) {
			var undoable = PSEdit.undoables.pop();
			PSEdit.redoables.push(undoable);
			this.doAction(undoable.undo);
			if(PSEdit.undoables.length == 0) {
				PSEdit.notifyUndo(false);
			}
			if(PSEdit.redoables.length == 1) {
				PSEdit.notifyRedo(true);
			}
		}
	},
	
	notifyUndo: function(available) {
		if(PageStudio.contributor == "MSC")
		{
			PSEdit.notifyUndo = function(isAvailable) {
				external.NotifyPageStudioUndo(isAvailable);			
			};
			PSEdit.notifyUndo(available);
		}
		else if(PageStudio.contributor == "MWC")
		{
			PSEdit.notifyUndo = function(isAvailable) {
				var editor = window.top.PageStudioEditor;
				editor.setCommandState(editor.COMMAND_ID.UNDO, isAvailable);				
			};
			PSEdit.notifyUndo(available);
		}
		else {
			return;
		}
				
	},
	
	redo: function() {
		if(PSEdit.redoables.length > 0) {
			var undoable = PSEdit.redoables.pop();
			PSEdit.undoables.push(undoable);
			this.doAction(undoable.redo);
			if(PSEdit.redoables.length == 0) {
				PSEdit.notifyRedo(false);
			}
			if(PSEdit.undoables.length == 1) {
				PSEdit.notifyUndo(true);
			}
		}
	},
	
	notifyRedo: function(available) {
		if(PageStudio.contributor == "MSC")
		{
			PSEdit.notifyRedo = function(isAvailable) {
				external.NotifyPageStudioRedo(isAvailable);
			};
		}
		else if(PageStudio.contributor == "MWC")
		{
			PSEdit.notifyRedo = function(isAvailable) {
				var editor = window.top.PageStudioEditor;
				editor.setCommandState(editor.COMMAND_ID.REDO, isAvailable);
			};
		}
		else {
			return;
		}
		
		PSEdit.notifyRedo(available);
	},
	
	notifyCopy: function(available)
	{
		if(PageStudio.contributor == "MSC")
		{
			PSEdit.notifyCopy = function(isAvailable) {
				external.NotifyPageStudioCopy(isAvailable);
			};
		}
		else if(PageStudio.contributor == "MWC")
		{
			PSEdit.notifyCopy = function(isAvailable) {
				var editor = window.top.PageStudioEditor;
				editor.setCommandState(editor.COMMAND_ID.COPY, isAvailable);
			};
		}
		else {
			return;
		}
		
		PSEdit.notifyCopy(available);
	},
	
	notifyPaste: function(available)
	{
		if(PageStudio.contributor == "MSC")
		{
			PSEdit.notifyPaste = function(isAvailable) {
				external.NotifyPageStudioPaste(isAvailable);
			};
		}
		else if(PageStudio.contributor == "MWC")
		{
			PSEdit.notifyPaste = function(isAvailable) {
				var editor = window.top.PageStudioEditor;
				editor.setCommandState(editor.COMMAND_ID.PASTE, isAvailable);
			};
		}
		else {
			return;
		}
		
		PSEdit.notifyPaste(available);
	},
	
	copy: function()
	{
		var area = PageStudio.selectedArea;
		if(area)
		{
			var item = area.selectedItem;
			if(item)
			{
				PSEdit.copyDelegate = item.copy();
				PSEdit.notifyPaste(true);
			}
		}
	},
	
	paste: function()
	{
		if(PSEdit.copyDelegate)
		{
			PSEdit.copyDelegate(PageStudio.selectedArea);
		}
	},
	
	doAction: function(action) {
		//stop listening for undo/redo commands until the action completes
		this.detachEvents();
		try {
			action();
		}
		catch(ex) { //if there is an error, reset the stacks.
			PSEdit.stateData = new Hash();
			PSEdit.undoables = new Array();
			PSEdit.redobales = new Array();
		}
		this.attachEvents();
	},
	
	hasUndoable: function() {
		return PSEdit.undoables.length > 0;
	},
	
	hasRedoable: function() {
		return PSEdit.redoables.length > 0;
	},
	
	onKeyUp: function(e) {
		if (!document.activeElement || (document.activeElement.tagName != "INPUT" && document.activeElement.tagName != "TEXTAREA")) {
			if(e.ctrlKey) {
				if(e.keyCode == 90 || e.keyCode == 122) {
					PSEdit.undo();
				}
				else if(e.keyCode == 89 || e.keyCode == 121) {
					PSEdit.redo();
				}
			}
		}
	}
};
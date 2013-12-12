var PSDialogTracker = Class.create({
	initialize: function(parentElement) {
		this.parentElement = parentElement;
		this.createListeners();
		this.createElement();
		this.attachEvents();
		this.dialogs = new Hash();
		this.buttons = new Hash();
	},
	
	createListeners: function() {
		this.listeners = {
			"onDialogShow": this.onDialogShow.bindAsEventListener(this),
			"showDialog": this.showDialog.bindAsEventListener(this),
			"onDialogRepositioned": this.onDialogRepositioned.bindAsEventListener(this)
		};
	},
	
	createElement: function() {
		this.buttonContainer = new Element("div", { "fadehandled": true, "class": "ps_dialog_tracker ps_toolbar" });
		PageStudio.preventContributorFade(this.buttonContainer);
		this.parentElement.insert({"top": this.buttonContainer, "bottom": this.trackBottom});
	},
	
	attachEvents: function() {
		Event.observe(window, "scroll", this.trackAll.bindAsEventListener(this));
		Event.observe(document, PSDialog.EVENT.SHOW_DIALOG_CLICKED, this.listeners.showDialog);
		Event.observe(document, PSDialog.EVENT.REPOSITIONED, this.listeners.onDialogRepositioned);
		this.parentElement.observe(PSDialog.EVENT.OPENED, this.add.bindAsEventListener(this));
		this.parentElement.observe(PSDialog.EVENT.CLOSED, this.remove.bindAsEventListener(this));
	},
	
	add: function(e) {
		var dialog = e.memo.dialog;
		this.dialogs.set(dialog.element.identify(), dialog);
	},
	
	remove: function(e) {
		this.dialogs.unset(e.memo.dialog.element.identify());
	},
	
	trackAll: function(e) {
		this.dialogs.values().each(this.track.bind(this));	
	},
	
	track: function(dialog) {
		var vDim = document.viewport.getDimensions();
		var vOff = dialog.element.viewportOffset();
		var eDim = dialog.element.getDimensions();
		
		if (vOff.top + eDim.height < 0) {
			this.addButton(dialog, vDim, vOff, eDim, true);
		}
		else if(vOff.top > vDim.height) {
			this.addButton(dialog, vDim, vOff, eDim, false);
		}
		else {
			this.removeButton(dialog);
		}
	},
	
	addButton: function(dialog, vDim, vOff, eDim, above) {
		var id = dialog.element.identify();
		if (!this.buttons.keys().member(id)) {
			var className = "ps_dialog_tracker_button_bottom";
			if (above) {
				className = "ps_dialog_tracker_button_top"; 
			}
			var button = new PSButton({ "className": id+"_tracker_button " + className, "action": PSDialog.EVENT.SHOW_DIALOG_CLICKED, "tooltip": dialog.label, "memo": { "dialog": dialog }});
			this.buttons.set(id, button);
			this.buttonContainer.insert(button.element);
			button.element.setStyle({ "left": this.calculateOffset(button.element.getWidth(), vDim, vOff, eDim) +"px" });
			dialog.element.observe(PSDialog.EVENT.OPENED, this.listeners.onDialogShow);
		}
	},
	
	calculateOffset: function(width, vDim, vOff, eDim) {
		var offset = vOff.left + (eDim.width / 2) - (width/2);
		if (offset < 0) {
			offset = 0;
		}
		while (!this.checkOffset(offset, width)) {
			offset += width + 5;
		}
		if (offset > vDim.width) {
			offset = vDim.width - width - 10;
		}
		return offset;
	},
	
	checkOffset: function(offset, width) {
		var buttons = this.buttons.values();
		for (var i = 0; i < buttons.size(); i++) {
			var button = buttons[i].element;
			var left = parseInt(button.getStyle("left"));
			var buttonWidth = button.getWidth();
			if ((offset >= left && offset <= left + buttonWidth) || (offset + width >= left && offset + width <= left + buttonWidth)) {
				return false;
			}
		}
		return true;
	},
	
	removeButton: function(dialog) {
		var button = this.buttons.unset(dialog.element.identify());
		if (button != null) {
			button.remove();
		}
	},
	
	showDialog: function(e) {
		var dialog = e.memo.dialog;
		dialog.show(true);	
	},
	
	onDialogShow: function(e) {
		var dialog = e.memo.dialog;
		dialog.element.stopObserving(PSDialog.EVENT.OPENED, this.listeners.onDialogShow);
		this.removeButton(dialog);	
	},
	
	onDialogRepositioned: function(e) {
		this.track(e.memo.dialog);
	}
});

PSDialogTracker.EVENT = {
	SHOW_DIALOG_CLICKED: "pagestudio:trackerShowDialogClicked"
};
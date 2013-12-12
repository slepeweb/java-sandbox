/**
 ***********************************************************************
 ***********************************************************************
Copyright (C) 1996 - 2011 Alterian Technology Ltd. All rights reserved.

Alterian Technology Ltd
Alterian plc
The Spectrum Building
Bond Street
Bristol BS1 3LG, UK
+44 (0) 117 970 3200 +44 (0) 117 970 3201

http://www.alterian.com
info@alterian.com
 ***********************************************************************
File Information
================

%PID% %PRT% %PO%
%PM% - %PD%

 ***********************************************************************
 ***********************************************************************
 */

var PSButtonGroup = Class.create({
	initialize: function(selectionChangedHandler) {
		this.buttons = new Array;
		this.selected = null;
		this.selectionChangedHandler = selectionChangedHandler;
	},
	
	add: function(button) {
		button.group = this;
		this.buttons.push(button);
	},
	
	remove: function(button)
	{
		button.group = null;
		this.setSelected(null);
		this.buttons = this.buttons.without(button);
	},
	
	setSelected: function(button) {
		if(this.selected != button)
		{
			if (this.selected) {
				this.selected.deselect(true);
			}
			
			this.selected = button;
			
			if(button != null) {
	     button.element.select();
	    }
			
			if(this.selectionChangedHandler)
			{
			 this.selectionChangedHandler(button);
			}
		}
	},
	
	getAt: function(index) {	
		var button = null;
		if (index != null && index > -1 && index < this.buttons.length) {
			button = this.buttons[index];
		}
		return button;
	},
	
	getSelected: function() {
		return this.selected;
	},
	
	contains: function(button) {
		return this.buttons.member(button);
	},
	
	clear: function() {
		this.buttons.invoke("remove");
		this.buttons = new Array();
	}
});
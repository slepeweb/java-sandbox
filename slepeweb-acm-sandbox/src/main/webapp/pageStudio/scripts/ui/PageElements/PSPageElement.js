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

var PSPageElement = Class.create(PSElement, {
	initialize: function($super, params) {
		//if the object is not being created for an element that already has a PS object,
		//continue
		if (params.element && params.element.PSObj) {
			params.element.PSObj.refresh();
		}
		else {
			$super(params);
			if (this.element.parentNode) {
				var parent = this.element.up();
				if(parent.fire) {
					parent.fire("pagestudio:"+this.elementName+"Added", { object: this });
				}
			}
		}
	},
	
	afterInitialize: function($super) {
		$super();
		this.select();
	},
	
	createElement: function($super, parent) {
		if (!this.element && parent) {
			$super(parent);
			//We need to set style to 100% here so the cell properties tool can read the width of new cells.
			this.element.setStyle({ "width": "100%" });
		}
		else {
			$super(parent);
		}
	},
	
	select: function($super) {
		$super();
		this.toolbar.enable();
	},
	
	deselect: function($super) {
		$super();
		this.toolbar.disable();
	},
	
	remove: function($super, undoable) {
		var action = function($super) {
			this.toolbar.remove();
			$super(undoable);
		}.bind(this, $super);
		if (undoable) {
			var undo = this.undoRemove.bind(this, this.element.up(), this.element.next(), this.element.previous());
			PSEdit.undoable(action, undo);
		}
		else {
			action();
		}	
	},
	
	undoRemove: function(parentNode, next, previous) {
		if(next) {
			next.insert({ "before": this.element }); 
		}
		else if (previous) {
			previous.insert({ "after": this.element });
		}
		else {
			parentNode.insert(this.element);
		}
		this.createToolbar();
		this.attachEvents();
		this.select();
	},
	
	copy: function()
	{
		return function(area)
		{
			return area.copyItem(this);
		}.bind(this);
	}
});
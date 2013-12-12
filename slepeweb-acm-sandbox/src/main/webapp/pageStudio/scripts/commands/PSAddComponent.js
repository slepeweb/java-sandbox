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

var PSAddComponent = Class.create({
	initialize: function(params) {
		
		//If we are copying another item, get the component.
		if(params.item)
		{
			params.component = params.item.component;
		}
		
		this.cell = params.cell;
		if (this.cell == null) {
			var previous, next;
			var action = function(params, previous, next) {
				if (this.cell) {
					this.cell.undoRemove(params.body, previous, next);
				}
				else {
					this.cell = new PSCell({ "parent": params.body, "event": params.event });
					params.cell = this.cell;
					previous = this.cell.element.previous();
					next = this.cell.element.next();
				}
				
				this.addCellItem(params);
			}.bind(this, params, previous, next);
			
			var undo = function() {
				this.component.remove();
				this.cell.remove();
			}.bind(this);
			
			PSEdit.undoable(action, undo);
		}
		else {
			var action = function(params) {			
				if (this.component) {
					this.component.undoRemove(this.cell.wrapper);	
				}
				else {
					this.addCellItem(params);
					params.element = this.component.element;
				}
			}.bind(this, params);
		
			if (params.undoable) {
				var undo = function() {
					this.component.remove();
				}.bind(this);
				PSEdit.undoable(action, undo);
			}
			else {
				action();
			}
		}
		this.scrollToCellItem();
	},
	
	scrollToCellItem: function() {
		var cOff = this.component.element.viewportOffset();
		var vDim = document.viewport.getDimensions();
		//var sOff = document.viewport.getScrollOffsets();
		
		if(cOff.top > vDim.height || cOff.top < 0) {
			this.component.element.scrollTo();
			if(this.component.dialog) this.component.dialog.position();
		}
	},
	
	addCellItem: function(params) {
		if (params.component.type == "Field") {
			this.component = this.addField(params.component);
		}
		else if (params.component.type == "Image") {
			this.component = this.addImage(params.component);
		}
		else {
			this.component = this.addComponent(params.component, params.item);
		}
	},
	
	addImage: function(component) {
		return new PSImage({ "parent": this.cell.wrapper, "component": component });
	},
	
	addField: function(component) {
		if (component.value == null) {
			component.value = "";
		}
		return new PSField({ "parent": this.cell.wrapper, "component": component });
		
	},
	
	addComponent: function(component, item) {
		return new PSComponent({ "parent": this.cell.wrapper, "component": component, "item": item });
	}
});

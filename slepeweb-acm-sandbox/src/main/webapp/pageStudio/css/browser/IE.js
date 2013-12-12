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

var IEStyle = {
	listeners: new Hash(),
		
	detectDoctype: function(){
		if(document.doctype != null) return true;
		else if(typeof document.namespaces != "undefined") {
			if(document.all[0].nodeType==8) return true;
		}
		return false;
	},
	
	resizeHeight: function(element, pos) {
		var parent = element.getOffsetParent();
		parentInfo = {
			height: parent.getHeight(),
			border: {
				top: parseInt(parent.getStyle("border-top-width")),
				bottom: parseInt(parent.getStyle("border-bottom-width"))
			}
		};
		var height = parentInfo.height - (parentInfo.border.top + parentInfo.border.bottom + pos.top + pos.bottom);
		if(height > 0) {
			element.setStyle({ "height": height + "px"});
		}
	},
	
	resizeWidth: function(element, pos) {
		var parent = element.getOffsetParent();
		parentInfo = {
			width: parent.getWidth(),
			border: {
				left: parseInt(parent.getStyle("border-left-width")),
				right: parseInt(parent.getStyle("border-right-width"))
			}
		};
		if (!IEStyle.hasDocType) {
			parentInfo.border.left += parseInt(element.getStyle("padding-left")) + parseInt(element.getStyle("margin-left"));
			parentInfo.border.right += parseInt(element.getStyle("padding-right")) + parseInt(element.getStyle("margin-left"));
		}
		var width =  parentInfo.width - (parentInfo.border.left + parentInfo.border.right + pos.left + pos.right);
		if(width > 0) {
			element.setStyle({ "width": width + "px" });
		}
	},
	
	validPos: function(number) {
		if (number != null && typeof(number) != "undefined" && !isNaN(number)) {
			return true;
		}
		return false;
	},
	
	doPosition: function(element) {
		var position = element.getStyle("position");
		if (position == "absolute" || position == "fixed") {
			var pos = {
				top: parseInt(element.getStyle("top")),
				bottom: parseInt(element.getStyle("bottom")),
				left: parseInt(element.getStyle("left")),
				right: parseInt(element.getStyle("right"))
			};
			if (IEStyle.validPos(pos.top) && IEStyle.validPos(pos.bottom)) {
				IEStyle.resizeHeight(element, pos);
			}
			if (IEStyle.validPos(pos.left) && IEStyle.validPos(pos.right)) {
				IEStyle.resizeWidth(element, pos);
			}
		}
	},
	
	doRestyle: function(e) {
		IEStyle.restyleElement(e.element());
	},
	
	restyleChildren: function(element) {
		element.descendants().each(IEStyle.doPosition);
	},
	
	onResize: function(e) {
		var element = e.element();
		var listener = IEStyle.getListener(element);
		element.stopObserving(PageStudio.EVENT.RESIZE, listener);
		IEStyle.restyleChildren(element);
		element.observe(PageStudio.EVENT.RESIZE, listener);
	},
	
	getListener: function(element) {
		var listener = IEStyle.listeners.get(element.identify());
		if (!listener) {
			listener = IEStyle.onResize.bindAsEventListener(this);
			IEStyle.listeners.set(element.identify(), listener);
		}
		return listener;
	},
	
	restyleElement: function(element) {
		IEStyle.doPosition(element);
		IEStyle.restyleChildren(element);
		
		var listener = IEStyle.getListener(element);
		element.stopObserving(PageStudio.EVENT.RESIZE, listener);
		element.observe(PageStudio.EVENT.RESIZE, listener);
		element.stopObserving(PageStudio.EVENT.RESIZE_END, listener);
		element.observe(PageStudio.EVENT.RESIZE_END, listener);
	}
};

IEStyle.hasDocType = IEStyle.detectDoctype();

//Unfortunately we need to disable this in IE due to a bug in the way IE calculates the viewport.
PSDialog.prototype.ensureVisibility = Prototype.emptyFunction;
if (typeof(psIE7)) {
	PSErrorDialog.DIALOG_PADDING = 56;
}

function doCSSFix() {
	//Modifies the button mouse events to add a specific class to the button for each state so they can be styled for IE6/Quirksmode.
	
	PSButton.prototype.onMouseEnter = PSButton.prototype.onMouseEnter.wrap(function(proceed, e) {
		proceed(e);
		this.element.addClassName("ps_button_on_mouse_enter_" + this.className);
	});
	
	PSButton.prototype.onMouseDown = PSButton.prototype.onMouseDown.wrap(function(proceed, e) {
		proceed(e);
		this.element.addClassName("ps_button_on_mouse_down_" + this.className);
	});
	
	PSButton.prototype.onMouseLeave = PSButton.prototype.onMouseLeave.wrap(function(proceed, e) {
		proceed(e);
		this.element.removeClassName("ps_button_on_mouse_enter_" + this.className);
	});
	
	PSButton.prototype.onMouseUp = PSButton.prototype.onMouseUp.wrap(function(proceed, e) {
		proceed(e);
		this.element.removeClassName("ps_button_on_mouse_down_" + this.className);
	});
}

function doQuirksStyling() {
	doCSSFix();
	//Change config form resize method
	PSConfigureComponentDialog.prototype.doResize = function() {
		this.element.setStyle({ "width": "0" });
		this.element.setStyle({ "width": this.element.getWidth() - 4 + "px" });
	};
	//--
	Event.observe(document, PSDialog.EVENT.OPENED, IEStyle.doRestyle);
	Event.observe(document, PageStudio.EVENT.BROWSER_LOADED, IEStyle.doRestyle);
	//disable tooltips
	PSToolTipHelper.add = Prototype.emptyFunction;
	PSDialogTracker.prototype.initialize = function() {};
	//IE6 hack to make the add component buttons resize correctly.
	PSComponentBrowser.prototype.addItem = PSComponentBrowser.prototype.addItem.wrap(
		function(proceed, component) {
			proceed(component);
			var item = this.items.get(component.id);
			if (item) {
				var buttons = item.select(".ps_component_list_item_button");
				if (buttons.length > 0) {
					buttons[0].writeAttribute(
						"style", "padding: 0; margin: 0; top: 0; "
						+ "height: expression(document.getElementById('" + item.identify() + "').getHeight()-2)");
				}
			}
		}
	);
	
	PSDialog.prototype.makeDraggable = function() {
		//hack for IE6, no idea what the problem is but IE6 needs quiet to be set to true
		//or the dialog will disappear
		this.draggable = new Draggable(this.element, {
			"handle": this.handle,
			"zindex": "10000",
			"starteffect": null,
			"endeffect": null,
			"onEnd": this.onDragEnd.bindAsEventListener(this),
			"quiet": true });
	};
}

//if this is not IE7 or if there is no doctype, add the styling javascript
if(typeof(psIE7) == "undefined" || !IEStyle.hasDocType) {
	doQuirksStyling();
}
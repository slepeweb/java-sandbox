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

var PSResizableHelper = Class.create({
	initialize: function(element, params) {
		this.element = element;
		this.listeners = {
			onMouseOver: this.onMouseOver.bindAsEventListener(this),
			onMouseMove: this.onMouseMove.bindAsEventListener(this),
			onMouseOut: this.onMouseOut.bindAsEventListener(this),
			onMouseDown: this.onMouseDown.bindAsEventListener(this),
			onMouseUp: this.onMouseUp.bindAsEventListener(this)
		};
		this.initParams(params);
		this.makeResizable();
	},
	
	initParams: function(params, element) {
		this.grip = params.grip ? params.grip : false;
		this.top = (params.handle.indexOf("top") != -1);
		this.bottom = (params.handle.indexOf("bottom") != -1);
		this.left = (params.handle.indexOf("left") != -1);
		this.right = (params.handle.indexOf("right") != -1);
		this.minWidth = params.minWidth ? params.minWidth : this.element.getWidth() ;
		this.minHeight = params.minHeight ? params.minHeight : this.element.getHeight() ;
	},
	
	makeResizable: function() {
		if(this.element.getStyle("position") != "relative" && this.element.getStyle("position") != "fixed") {
			this.element.setStyle({ position: "absolute" });
		}
		
		if(this.element.getStyle("width") == null) {
			this.element.setStyle({ width: this.element.getWidth() + "px" });
		}
		
		if (this.grip) {
			this.addResizeGrip(this.element);
		}
		
		this.resizable = null;
		this.cursor = this.element.getStyle("cursor");
		this.bodyCursor = document.body.style.cursor;
		this.element.observe("mouseover", this.listeners.onMouseOver);
	},
	
	checkCursorPosition: function(ex, ey) {
		var offset = this.element.viewportOffset();
		var scrollOffsets = document.viewport.getScrollOffsets();
		var dimensions = this.element.getDimensions();
		if (this.right && this.cursorOnRight(ex, ey, offset, scrollOffsets, dimensions.width)) {
			if (this.bottom && this.cursorOnBottom(ey, offset, scrollOffsets, dimensions.height)) {
				this.create({
					cursor: "nw-resize"
				});
			}
			else {
				this.create({ 
					constraint: "horizontal", 
					cursor: "e-resize"
				});
			}
		}
		else if (this.bottom && this.cursorOnBottom(ey, offset, scrollOffsets, dimensions.height)) {
			this.create({ 
				constraint: "vertical", 
				cursor: "n-resize"
			});
		}
		else {
			this.destroy();
		}
	},
	
	cursorOnRight: function(x, y, offset, scrollOffsets, width) {
		return (x >= scrollOffsets.left + offset.left + width - 5 && y >= offset.top + scrollOffsets.top);
	},
	
	cursorOnBottom: function(y, offset, scrollOffsets, height) {
		return (y >= scrollOffsets.top + offset.top + height - 5);
	},
	
	create: function(params) {
		if(this.resizable == null) {
			params = Object.extend(params, {
				onStart: this.onStart.bindAsEventListener(this, this.element, params.cursor),
				onResize: this.onResize.bindAsEventListener(this),
				onEnd: this.onEnd.bindAsEventListener(this),
				minWidth: this.minWidth
			});
			document.observe("mouseover", this.listeners.onMouseOut);
			
			this.element.setStyle({ cursor: params.cursor });
			
			this.resizable = new Resizable(this.element, params);
		}
	},
	
	destroy: function() {
		if(this.resizable != null) {
			document.stopObserving("mouseout", this.listeners.onMouseOut);
			
			this.element.setStyle({ cursor: this.cursor });
			
			document.body.setStyle({cursor: this.bodyCursor });
			this.resizable.destroy();
			this.resizable = null;
		}
	},
	
	addResizeGrip: function() {		
		var grip = new Element("div", { id: PSIDHelper.getId("resize_handle"), className: "ps_resize_grip" });
		this.element.insert(grip, { position: "bottom" });
		var constraint = "";
		if (!(this.top || this.bottom) && (this.left || this.right)) {
			constraint = "horizontal";
		}
		else if ((this.top || this.bottom) && !(this.left || this.right)) {
			constraint = "vertical";
		}
		new Resizable(this.element, {
			constraint: constraint,
			handle: grip.id,
			onStart: this.onStart.bindAsEventListener(this, this.element, "se-resize"),
			onResize: this.onResize.bindAsEventListener(this, this.element),
			onEnd: this.onEnd.bindAsEventListener(this, this.element, "se-resize"),
			minHeight: this.minHeight,
			minWidth: this.minWidth
		});
	},
	
	setResizeCursor: function(resize, style) {
		document.body.style.cursor = style;
	},
	
	resetResizeCursor: function() {
		document.body.style.cursor = this.bodyCursor;
	},
	
	onMouseOut: function(e) {
		if (e.element() != this.element && !e.element().descendantOf(this.element)) {
			this.destroy();
			this.element.stopObserving("mousemove", this.listeners.onMouseMove);
			document.stopObserving("mouseout", this.listeners.onMouseOut);
		}
	},
	
	onMouseOver: function(e) {
		this.element.observe("mousedown", this.listeners.onMouseDown);
		this.element.observe("mousemove", this.listeners.onMouseMove);
	},
	
	onMouseMove: function(e) {
		this.checkCursorPosition(e.pointerX(), e.pointerY());
	},
	
	onMouseDown: function(e) {
		this.element.stopObserving("mousemove", this.listeners.onMouseMove);
		Element.stopObserving(document, "mouseout", this.listeners.onMouseOut);
	},
	
	onMouseUp: function(e) {
		this.element.observe("mousemove", this.listeners.onMouseMove);
		Element.observe(document, "mouseout", this.listeners.onMouseOut);
	},
	
	onStart: function(resizeObj, cursor) {
		var dimensions = { "width": this.element.measure("width"), "height": this.element.measure("height") };
		this.element.fire(PageStudio.EVENT.RESIZE_START, { "dimensions": dimensions, "style": { "width": this.element.getStyle("width"), "height": this.element.getStyle("height") } });
		this.element.setStyle({ "width": dimensions.width +"px", "height": dimensions.height +"px" });
		this.setResizeCursor(resizeObj, cursor);
	},
	
	onResize: function(resizeObj) {
		this.element.fire(PageStudio.EVENT.RESIZE);
	},
	
	onEnd: function(resizeObj) {
		this.resetResizeCursor();
		this.element.fire(PageStudio.EVENT.RESIZE_END);
	}
});

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
 
var PSScrollable = {
	VerticalBars: Class.create({
		initialize: function(params) {
			this.child = { element : $(params.element) };
			this.parent = null;
			this.resetVars();
			this.attachEvents();	
		},
		
		addScrollParent: function() {
			if (this.parent == null) {
				this.child.style = {
					top: this.child.element.getStyle("top")
				};
				
				this.parent = new Element("div", { className: "scroll_parent" });
				this.child.element.setStyle({ bottom: "auto" });
				this.child.element.wrap(this.parent);
				var onMouseWheel = this.onMouseWheel.bindAsEventListener(this);
				if (PageStudio.eventSupported("onmousewheel", this.parent)) {
					this.parent.observe("mousewheel", onMouseWheel);
				}
				else if (PageStudio.eventSupported("onmousewheel", document)){
					
					this.parent.observe("mouseenter", function(e, handler) {
						Event.observe(document, "mousewheel", handler);
					}.bindAsEventListener(this, onMouseWheel));
					
					this.parent.observe("mouseleave", function(e) {
						document.stopObserving("mousewheel", onMouseWheel);
					}.bindAsEventListener(this));					
				}
				else {
					this.parent.observe("DOMMouseScroll", onMouseWheel);
				}
			}
		},
		
		addScrollButtons: function() {
			this.scrollTop = new Element("div", { className: "scroll_button scroll_button_top" });
			this.scrollBottom = new Element("div", { className: "scroll_button scroll_button_bottom" });
			this.parent.insert({ top: this.scrollTop, bottom: this.scrollBottom });
			this.wrapper = new Element("div", { className: "scroll_wrapper" });
			this.wrapper.setStyle({ position: "absolute", left: "0", right: "0", top: this.scrollTop.getHeight()+"px", bottom: this.scrollBottom.getHeight()+"px" });
			this.child.element.wrap(this.wrapper);
			this.scrollTop.observe("mouseover", this.listeners.scrollUpOver);
			this.scrollBottom.observe("mouseover", this.listeners.scrollDownOver);
		
			this.calcScrollDiff();	
		},
		
		attachEvents: function() {
			document.observe(PSDialog.EVENT.OPENED, this.listeners.onDialogOpened);
		},
		
		enable: function() {
			document.observe("resize", this.listeners.onResize);
			document.observe(PageStudio.EVENT.RESIZE_END, this.listeners.onResize);
			Event.observe(window, "resize", this.listeners.onResize);
		},
		
		disable: function() {
			document.stopObserving("resize", this.listeners.onResize);
			document.stopObserving(PageStudio.EVENT.RESIZE_END, this.listeners.onResize);
			Event.stopObserving(window, "resize", this.listeners.onResize);
		},
		
		reset: function() {
			this.scrollTop.remove();
			this.scrollBottom.remove();
			this.child.element.setStyle(this.child.style);
			this.child.element.setStyle({ "bottom": "auto" });
			this.parent.insert(this.child.element);
			this.wrapper.remove();
			
			this.resetVars();
		},
		
		resetVars: function() {
			this.pe = null;
			this.wrapper = null;
			this.diff = 0;
			this.scrollDiff = 0;
			this.mouseDiff = 0;
			this.test = 0;
			this.remainder = 0;
			this.createListeners();
			this.scrollTop = null;
			this.scrollBottom = null;
		},
		
		createListeners: function() {
			this.listeners = {
				scrollUpOver: this.scrollUpOver.bindAsEventListener(this),
				scrollDownOver: this.scrollDownOver.bindAsEventListener(this),
				stopScroll: this.stopScroll.bindAsEventListener(this),
				onResize: this.onResize.bindAsEventListener(this),
				onDialogOpened: this.onDialogOpened.bindAsEventListener(this),
				onDialogClosed: this.onDialogClosed.bindAsEventListener(this)
			};
		},
		
		calcDiff: function() {
			this.diff = this.child.element.getHeight() - this.parent.getHeight();
		},
		
		calcScrollDiff: function() {
			if (this.wrapper != null) {
				this.scrollDiff = this.child.element.getHeight() - this.wrapper.getHeight();	
			}
		},
		
		scrollCheck: function() {
			this.calcDiff();
			if (this.diff > 0) {
				if(this.wrapper == null) {
					this.addScrollButtons();
				}
			}
			else {
				if (this.scrollTop && this.scrollBottom) {
					this.reset();
				}
			}
		},
		
		scrollUpOver: function(e) {
			this.scrollTop.addClassName("scroll_button_hover");
			var element = e.element();
			element.observe("mouseout", this.listeners.stopScroll);
			element.observe("mouseup", this.listeners.stopScroll);
			if(this.pe == null) {
				this.pe = new PeriodicalExecuter(this.doScroll.bind(this, -0.2), 0.01);
			}
		},
		
		scrollDownOver: function(e) {
			this.scrollBottom.addClassName("scroll_button_hover");
			var element = e.element();
			element.observe("mouseout", this.listeners.stopScroll);
			element.observe("mouseup", this.listeners.stopScroll);
			if(this.pe == null) {
				this.pe = new PeriodicalExecuter(this.doScroll.bind(this, 0.2), 0.01);
			}
		},
		
		doScroll: function(speed) {
			if (this.time == null) {
				this.time = new Date();
			}
			this.calcScrollDiff();
			var newTime = new Date();
			var step = speed * (newTime - this.time);
			this.time = newTime;
			var newTop = parseInt(this.child.element.getStyle("top")) - step;
			this.child.element.setStyle({ top: newTop + "px" });
			this.limitScroll();
		},
		
		limitScroll: function() {
			var top = parseInt(this.child.element.getStyle("top"));
			if (top > 0) { top = 0; } 
			else if (top.abs()  > this.scrollDiff) { top = -this.scrollDiff; }
			this.child.element.setStyle({ "top": top + "px" });
		},
		
		stopScroll: function(e) {
			this.scrollTop.removeClassName("scroll_button_hover");
			this.scrollBottom.removeClassName("scroll_button_hover");
			var element = e.element();
			element.stopObserving("mouseout", this.listeners.stopScroll);
			if (this.pe != null) {
				this.pe.stop();
				this.pe = null;
				this.time = null;
			}
		},
		
		scrollToElement: function(element) {
			if(this.wrapper) {
				var offset = element.positionedOffset();
				var height = element.getHeight();
				var cOffset = this.child.element.positionedOffset();
				var pHeight = this.wrapper.getHeight();
				var diff = offset.top + height + cOffset.top;
				if (diff >= pHeight) {
					this.child.element.setStyle({ "top": pHeight - (offset.top + height) + "px" });
				}
				else if(diff <= height) {
					this.child.element.setStyle({ "top": -offset.top + "px" });
				}
			}
		},
		
		onResize: function(e) {
			this.scrollCheck();
			if (this.wrapper) {
				this.calcScrollDiff();
				this.limitScroll();
			}
		},
		
		onMouseWheel: function(e) {
			if(e.wheelDeltaY) {
				this.doScroll(-e.wheelDeltaY/400);
			}
			else if(e.detail) {
				this.doScroll(e.detail/10);
			}
			else if(e.wheelDelta) {
				this.doScroll(-e.wheelDelta/400);
			}
			Event.stop(e);
		},
		
		onDialogOpened: function(e) {
			if (this.child.element.ancestors().member(e.element())) {
				document.stopObserving(PSDialog.EVENT.OPENED, this.listeners.onDialogOpened);
				document.observe(PSDialog.EVENT.CLOSED, this.listeners.onDialogClosed);
				this.addScrollParent();
				this.enable();
				this.scrollCheck();
			}
		},
		
		onDialogClosed: function(e) {
			if (this.child.element.ancestors().member(e.element())) {
				document.observe(PSDialog.EVENT.OPENED, this.listeners.onDialogOpened);
				this.disable();
			}
		}
	})
};

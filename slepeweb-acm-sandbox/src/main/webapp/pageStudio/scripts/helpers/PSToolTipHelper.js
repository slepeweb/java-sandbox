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

var PSToolTipHelper = {
	init: function() {
		PSToolTipHelper.tooltip.addClassName(PageStudio.currentTheme);
		Element.insert(document.body, PSToolTipHelper.tooltip.insert({ "top": PSToolTipHelper.title, "bottom": PSToolTipHelper.text }));
		if (PageStudio.contributor == "MWC") {
			//this stops the MWC from fading out the tooltips
			if (window.top.markAsFade)	window.top.markAsFade(PSToolTipHelper.tooltip);
		}
	},
	
	pe: null,
	
	title: new Element("div", { className: "ps_tool_tip_title" }),
	
	text: new Element("div", { className: "ps_tool_tip_text" }).hide(),
	
	tooltip: new Element("div", { className: "ps_reset ps_tool_tip", style: "position: fixed; z-index: 10000;"}).hide(),
	
	add: function(element, tip, positionOnMouse) {
		var context = element.retrieve("tooltip");
		if (context) {
			context.title = tip.title ? tip.title : tip;
			context.text = tip.text ? tip.text : null;
			context.positionOnMouse = (positionOnMouse);
		}
		else {
			/*	
			 * The tooltip may have been sent as either a title or an object
			 * containing both title and text.
			*/
			
			//create a context object to pass between the static methods.
			context = { 
				"element": element,
				"title": tip.title ? tip.title : tip,
				"text": tip.text ? tip.text : null,
				"positionOnMouse": (positionOnMouse)
			};
			
			context.listeners = { 
				"onElementRemoved": PSToolTipHelper.onElementRemoved.bindAsEventListener(context),
				"onDOMNodeRemoved": PSToolTipHelper.onDOMNodeRemoved.bindAsEventListener(context),
				"onMouseEnter": PSToolTipHelper.onMouseEnter.bindAsEventListener(context),
				"onMouseLeave": PSToolTipHelper.onMouseLeave.bindAsEventListener(context)
			};
			
			element.observe("mouseenter", context.listeners.onMouseEnter);
			element.observe("mouseleave", context.listeners.onMouseLeave);
			element.store("tooltip", context);
		}
	},
	
	remove: function(element) {
		var context = element.retrieve("tooltip");
    if (context) {
    	PSToolTipHelper.hide.bind(context)();
			element.stopObserving("mouseenter", context.listeners.onMouseEnter);
	    element.stopObserving("mouseleave", context.listeners.onMouseLeave);
	    element.getStorage().unset("tooltip");
    }
	},
	
	reposition: function() {
		var tip = {
			dimensions: PSToolTipHelper.tooltip.getDimensions()
		};
		
		var element = {
			dimensions: this.element.getDimensions()
		};
		
		//If the element has become hidden then the dimensions will be 0 and we can
		//hide the tooltip.
		if(element.dimensions.height == 0) {
			PSToolTipHelper.hide.bind(this)();
		}
		else {
			var viewport = document.viewport.getDimensions();
			
			var posX, posY, top, left;
			
			function doPosition() {
				//ensure the tooltip will be visible
				if (left + tip.dimensions.width > viewport.width) {
					left = posX - tip.dimensions.width;
				}
				if (top + tip.dimensions.height > viewport.height) {
					top = posY - tip.dimensions.height;
				}
				PSToolTipHelper.tooltip.setStyle({
					"top": top + "px",
					"left": left + "px"
				});
			}
			
			if(this.positionOnMouse) {
				var updatePos = function(e) {
					this.listener = updatePos;
					if(e) this.pointer = e.pointer();
					var scrollOffset = document.viewport.getScrollOffsets();
					posX = this.pointer.x - scrollOffset.left;
					posY = this.pointer.y - scrollOffset.top;
					top =  8 + posY;
					left = 15 + posX;
					doPosition();
				}.bindAsEventListener(this);
				updatePos();
				this.element.observe("mousemove", updatePos);
			}
			else {
				if (this.element.getStyle("position") == "fixed") {
					var top = 0;
					if(this.element.getStyle("top")) {
						top = parseInt(this.element.getStyle("top"));
					}
					else {
						var bottom = this.element.getStyle("top") ? parseInt(this.element.getStyle("top")) : 0;
						top = viewport.height - bottom;
					}
					element.offset = {
						"top": top,
						"left": this.element.getStyle("left") ? parseInt(this.element.getStyle("left")) : 0
					};
				}
				else {
					element.offset = this.element.viewportOffset();
				}
				
				posY = element.offset.top;
				posX = element.offset.left;
				top =  8 + element.offset.top + element.dimensions.height;
				left = 15 + element.offset.left + element.dimensions.width;
				doPosition();
			}
		}
	},
	
	display: function(pe) {
		//if the element has been removed, don't display the tooltip
		if(this.element && this.element.ancestors().member(document.body)) {
			//if the element is removed while the tooltip is displayed we need to remove it.
			pe.stop();
			PSToolTipHelper.tooltip.show();
			PSToolTipHelper.title.update(this.title);
			if (this.text) {
				PSToolTipHelper.tooltip.addClassName("ps_text_tool_tip");
				PSToolTipHelper.text.show().update(this.text);
			}
			else {
				PSToolTipHelper.text.hide().update().removeClassName("ps_text_tool_tip");
			}
			PSToolTipHelper.reposition.bind(this)();
		}
		else {
			this.element = null;
		}
	},
	
	hide: function(remove) {
		this.element.stopObserving("mousemove", this.listener);
		//we can stop listening for node removal now.
		this.element.stopObserving(PageStudio.EVENT.REMOVED, this.listeners.onElementRemoved);
		this.element.stopObserving("DOMNodeRemoved", this.listeners.onDOMNodeRemoved);
		
		if (remove) {	
			this.element.stopObserving();
		}
		PSToolTipHelper.tooltip.hide();
	},
	
	onMouseEnter: function(e) {
		if (PSToolTipHelper.pe) {
			PSToolTipHelper.pe.stop();
		}
		this.element.observe(PageStudio.EVENT.REMOVED, this.listeners.onElementRemoved);
		this.element.observe("DOMNodeRemoved", this.listeners.onDOMNodeRemoved);
		this.pointer = e.pointer();
		PSToolTipHelper.pe = new PeriodicalExecuter(PSToolTipHelper.display.bind(this), 0.5);
	},
	
	onMouseLeave: function(e) {
		if (PSToolTipHelper.pe) {
			PSToolTipHelper.pe.stop();
			PSToolTipHelper.pe = null;
			this.element.stopObserving("DOMNodeRemoved", this.listeners.onDOMNodeRemoved);
			this.element.stopObserving(PageStudio.EVENT.REMOVED, this.listeners.onElementRemoved);
		}
		PSToolTipHelper.hide.bind(this)();
	},
	
	onDOMNodeRemoved: function(e)
	{
		if(!this.element.parentNode) {
			this.onElementRemoved(e);
		}
	},
	
	onElementRemoved: function(e) {		
		if (PSToolTipHelper.pe) {
			PSToolTipHelper.pe.stop();
		}
		PSToolTipHelper.hide.bind(this)(true);		
	}
};
Event.observe(window, "load", PSToolTipHelper.init);

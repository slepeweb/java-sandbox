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

var PSTool = Class.create({
	initialize: function(params) {
		this.createElement();
	},
	
	createElement: function() {
		this.element = new Element("div", { className: "ps_tool" });
		this.wrapper = new Element("div", { className: "ps_tool_wrapper"});
		this.element.insert(this.wrapper);
	},
	
	close: function() {
		this.element.fire(PageStudio.EVENT.TOOL_CLOSED);
	},
	
	insert: function(element) {
		this.wrapper.insert(element);
		return this;
	},
	
	show: function() {
		this.wrapper.show();
		this.element.setStyle({ "zIndex": "100" });
	},
	
	hide: function() {
		this.wrapper.hide();
		this.element.setStyle({ "zIndex": "-100" }); //need to drop z-index so visible tools are not blocked
	}	
});

PSTool.getOverlay = function(target)
{
  var overlay = new Element("div", { "className": "ps_loading ps_processing_overlay" });
  
  var layout = Element.getLayout(target);
  overlay.setStyle({ "top": layout.get("top") + "px", "left": layout.get("left") + "px", "height": layout.get("padding-box-height") + "px", "width": layout.get("padding-box-width") + 1 + "px" });
  
  target.insert({ "after": overlay });
  
  return overlay;
};
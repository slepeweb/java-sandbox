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

var PSUnselectableHelper = {	
	makeUnselectable: function(element) {
		//Firefox, Webkit & CSS3 Standard
		element.setStyle({ MozUserSelect: "-moz-none", WebkitUserSelect: "none", userSelect: "none" }); 
		//IE
		element.observe("selectstart", this.onSelectStart);
	},
	
	makeSelectable: function(element) {
		//Firefox, Webkit & CSS3 Standard
		element.setStyle({ MozUserSelect: "text", WebkitUserSelect: "text", userSelect: "text" }); 
		//IE
		element.stopObserving("selectstart", this.onSelectStart);
	},
	
	onSelectStart: function(e) {
		var src = e.findElement();
		var selectable = ["INPUT", "TEXTAREA"];
		if (!selectable.member(src.tagName)) {
			e.stop();
		}
	}
};
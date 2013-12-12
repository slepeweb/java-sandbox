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

var PSOverflowDragHelper = {
	startDrag: function(draggable, e) {
		var ele = draggable.element;
		draggable._ps = {
			next: ele.next(),
			parent: ele.parentNode
		}; 
		
		ele.addClassName(PageStudio.CLASS.DRAG_WRAPPER);
		ele.addClassName(PageStudio.CLASS.RESET);
		
		var offset = ele.viewportOffset();		
		document.body.appendChild(draggable.element);
		var newOffset = ele.viewportOffset();
		var position = {
			top: parseInt(ele.getStyle("top")) + (offset.top - newOffset.top) + "px",
			left: parseInt(ele.getStyle("left")) + (offset.left - newOffset.left) + "px"		
		};
		ele.setStyle(position);
	},
	
	endDrag: function(draggable, e) {
		draggable.element.removeClassName(PageStudio.CLASS.DRAG_WRAPPER);
		draggable.element.removeClassName(PageStudio.CLASS.RESET);
		if (draggable._ps.next != null && typeof(draggable._ps.next) != "undefined") {
			draggable._ps.next.insert({ "before": draggable.element });
		}
		else if (draggable._ps.parent != null && typeof(draggable._ps.parent) != "undefined") {
			draggable._ps.parent.insert(draggable.element);
		}
	},
	
	revert: function(element, top_offset, left_offset) {
		element.setStyle({ "left": 0, "top": 0, "position": "relative" });
	}
};
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

var PSSelectionHelper = {   
   selectNode: function(node) {
	   if(document.body.createTextRange) {
		   var range = document.body.createTextRange();
		   range.moveToElementText(node);
		   try {
			   range.select();
		   }
		   catch(e) {
			   
		   }
	   }
	   else {
		   var selection = PSSelectionHelper.getSelection();
		   var range = PSSelectionHelper.getRange(selection);
		   range.selectNode(node);
		   selection.removeAllRanges();
		   selection.addRange(range);
	   }
   },
   
   getRangeData: function() {
	   var range = PSSelectionHelper.getRange();
	   var data;
	   if(range.startContainer) {
		   data = {
			   "startContainer": range.startContainer,
			   "endContainer": range.endContainer,
			   "startOffset": range.startOffset,
			   "endOffset": range.endOffset
		   };
	   }
	   //Safari
	   else if(range.anchorNode) {
		   data = {
			   "startContainer": range.anchorNode,
			   "endContainer": range.focusNode,
			   "startOffset": range.anchorOffset,
			   "endOffset": range.focusOffset
		   };
	   }
	   //IE
	   else if (range.duplicate){
		   data = {
			   "ieRange": range
		   };
	   }
	   return data;
   },
   
   createRange: function(rangeData) {
	   var range;
	   if (rangeData.ieRange) {   
		   range = rangeData.ieRange;
	   }
	   else if(document.createRange) {
		   range = document.createRange();
		   range.setStart(rangeData.startContainer, rangeData.startOffset);
		   range.setEnd(rangeData.endContainer, rangeData.endOffset);
	   }
	   else {
		   range = rangeData.startContainer.createTextRange();
		   range.moveStart(rangeData.startOffset);
		   range.moveEnd(rangeData.endOffset);
		   range.select();
	   }
	   return range;
   },
   
   getSelection: function() {
	   if(window.getSelection) {
		   return window.getSelection();
	   }
	   else if(document.getSelection) {
		   return document.getSelection();
	   }
	   else if(document.selection) {
		   return document.selection;
	   }
	   return null;
   },
   
   getRange: function(selection) {
	   if(selection == undefined) {
		   selection = PSSelectionHelper.getSelection();
	   }
	   if(selection.rangeCount && selection.rangeCount > 0) {
		   return selection.getRangeAt(0);
	   }
	   else if(selection.createRange) {
		   var range = selection.createRange();
		   if(range) {
			   if(range.duplicate) return range.duplicate();
			   else return range;
		   }
	   }
	   else if(document.createRange) {
		   return document.createRange();
	   }
	   return null;
   },
   
   getRangeParent: (function() {
	   if(window.TextRange && TextRange.prototype.parentElement) {
		   return function(range) {
			   return range.parentElement();
		   };
	   }
	   else {
		   return function(range) {
			   return range.endContainer;
		   };
	   }
   })(),
   
   insertIntoSelection: function(selection, content) {
	   if(selection) {
		   if(selection.type == "None") {
			   return false;
		   }
		   else {
			   var range = PSSelectionHelper.getRange(selection);
			   return PSSelectionHelper.insertIntoRange(range, content);
		   }
	   }
	   else {
		   return false;
	   }
   },
   
   //Collapses a range to within the boundaries of a single node.
   //Returns false if the node is not within the given range.
   collapseRangeToNode: (function() {
	   if(document.selection) {
		   return function(range, node) {
			   var selection = document.selection;
			   var nodeRange = selection.createRange();
			   nodeRange.moveToElementText(node);
			   if(range.compareEndPoints("EndToEnd", nodeRange) == 1) {
				   range.setEndPoint("EndToEnd", nodeRange);				  
			   }
			   if(range.compareEndPoints("StartToStart", nodeRange) < 1) {
				   range.setEndPoint("StartToStart", nodeRange);				   
			   }
			   range.select();
		   };
	   }
	   else if(Range && Range.prototype.isPointInRange) {
		   return function(range, node) {
			   var nodeRange = document.createRange();
			   nodeRange.selectNodeContents(node);
			   if(!nodeRange.isPointInRange(range.startContainer, range.startOffset)) {
					range.setStart(node, 0);
				}
			   if(!nodeRange.isPointInRange(range.endContainer, range.endOffset)) {
				   var container = range.startContainer;
				   var start = range.startOffset;
				   range.selectNodeContents(node);
				   range.setStart(container, start);
			   }
		   };
	   }
	   else {
		   return Prototype.emptyFunction;
	   }
   })(),
   
   insertIntoRange: (function() {
	   	if(document.selection) {
			return function(range, node) {
			   range.pasteHTML(node.outerHTML);
			   return true;
			};
		}
		else if (Range.prototype.insertNode) {
			return function(range, node) {
				if(range) {
				   range.deleteContents();
				   range.insertNode(node);
				   return true;
				}
			};
		}
		else { return function() { return false; }; }
	})(),
   
   insert: function(content) {
	   var selection = PSSelectionHelper.getSelection();
	   return PSSelectionHelper.insertIntoSelection(selection);
   }
};
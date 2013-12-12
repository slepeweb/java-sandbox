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

var PSSelectComponentDialog = Class.create(PSDialog, {	
	createElement: function($super) {
		$super();
		this.element.id = "ps_select_component_dialog";
		this.element.addClassName("ps_select_component_dialog");
		
		this.tool = new PSSelectComponentTool();		
		this.body.insert(this.tool.element);
		
		new PSResizableHelper(this.element, { handle: "right bottom", grip: true });
	},
	
	onSelect: function($super, e) {
		$super(e);
	},
	
	onDeselect: function($super, e) {
		$super(e);
	},
	
	onBlur: function($super, e) {
		$super(e);
		if(e.memo.object == this) {
			this.tool.detachFocusEvents();
		}
	},
	
	onFocus: function($super, e) {
		$super(e);
		if(e.memo.object == this) {
			this.tool.attachFocusEvents();
		}
	}
});
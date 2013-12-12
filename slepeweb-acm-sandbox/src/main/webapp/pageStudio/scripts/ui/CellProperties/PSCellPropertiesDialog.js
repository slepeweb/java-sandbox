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

var PSCellPropertiesDialog = Class.create(PSDialog, {	
	createElement: function($super) {
		$super();
		this.tool = new PSCellPropertiesTool();
		this.body.insert(this.tool.element);
		
		this.element.id = "ps_dialog_cell_properties";
		this.element.addClassName("ps_dialog_cell_properties");
		new PSResizableHelper(this.element, { grip: true, handle: "right bottom" });
	},
	
	show: function($super, center) {
		$super(center);
		this.tool.doAfterDialogOpened();
	},
	
	//Positions the cell properties dialog in the middle of the page.
	position: function() {
		this.center();
	}
});
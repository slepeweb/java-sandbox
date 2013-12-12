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

var PSChangeLayoutDialog = Class.create(PSModalDialog, {	
	createElement: function($super) {
		$super();
		this.tool = new PSChangeLayoutTool();		
		this.body.insert(this.tool.element);
		
		this.element.id = "ps_dialog_change_layout";
		this.element.addClassName("ps_dialog_change_layout");
		new PSResizableHelper(this.element, { grip: true, handle: "right bottom" });
	},
	
	attachEvents: function($super) {
		$super();
		this.element.observe(PSChangeLayoutTool.EVENT.CREATE_LAYOUT_CLICKED, this.onCreateLayoutClicked.bindAsEventListener(this));
		this.element.observe(PSChangeLayoutTool.EVENT.EDIT_LAYOUT_CLICKED, this.onEditLayoutClicked.bindAsEventListener(this));
		this.element.observe(PSCreateLayoutTool.EVENT.LAYOUT_EDITED, this.onLayoutActionComplete.bindAsEventListener(this));
		this.element.observe(PSCreateLayoutTool.EVENT.LAYOUT_CREATED, this.onLayoutActionComplete.bindAsEventListener(this));
		this.element.observe(PSCreateLayoutTool.EVENT.CANCEL_SAVE_CLICKED, this.onCancelSaveClicked.bindAsEventListener(this));
		this.element.observe(PSChangeLayoutTool.EVENT.CANCEL_CLICKED, this.onCancelClicked.bindAsEventListener(this));
	},
	
	show: function($super) {
		$super();
		if(this.createLayoutTool) this.createLayoutTool.hide();
		this.tool.show();
	},
	
	createCreateLayoutTool: function() {
		if (!this.createLayoutTool) {
			this.createLayoutTool = new PSCreateLayoutTool();
			this.body.insert(this.createLayoutTool.element);
		}
	},
	
	onCreateLayoutClicked: function(e) {
		this.tool.hide();
		this.createCreateLayoutTool();
		this.createLayoutTool.show();
	},
	
	onEditLayoutClicked: function(e) {
		this.tool.hide();
		this.createCreateLayoutTool();
    this.createLayoutTool.show();
    this.createLayoutTool.editLayout(this.tool.selectedLayout);
	},
	
	onLayoutActionComplete: function(e) {
		this.createLayoutTool.hide();
		this.tool.show();
	},
	
	onCancelSaveClicked: function(e) {
		this.createLayoutTool.hide();
		this.tool.show();
	},
	
	onCancelClicked: function(e) {
    this.close();
	}
});
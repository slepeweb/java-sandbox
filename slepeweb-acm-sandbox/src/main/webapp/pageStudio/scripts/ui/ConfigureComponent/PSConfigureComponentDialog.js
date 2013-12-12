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

var PSConfigureComponentDialog = new Class.create(PSModalDialog, {
	initParams: function($super, params) {
		$super(params);
		this.component = params.component;
	},
	
	attachEvents: function($super) {
		$super();
		this.element.observe(PageStudio.EVENT.CONFIGURATION_LOADED, this.onConfigurationLoaded.bindAsEventListener(this));
	},
	
	createElement: function($super) {
		$super();
		this.element.id = "ps_configure_component_dialog";
		this.element.addClassName("ps_configure_component_dialog");
		this.tool = new PSConfigureComponentTool({ "component": this.component });
		this.body.insert(this.tool.element);
		this.element.setStyle({ "height": "240px", "width": "320px" });
		this.body.addClassName(PageStudio.CLASS.LOADING);
	},
	
	doResizeFrame: function(iframe) {
		this.tool.configControls.hide();
		var iWindow = iframe.contentWindow ? iframe.contentWindow : iframe.contentDocument.defaultView;
		var width = 0;
		var height = 0;

		if (iWindow.document.body.scrollHeight) {
			width = iWindow.document.body.scrollWidth;
			if (iWindow.scrollMaxX != undefined && (iWindow.document.body.parentNode.clientHeight != 0)) {
				height = iWindow.document.body.parentNode.clientHeight;  // FF
			}
			else if (iWindow.document.forms[0]) {
				height = iWindow.document.forms[0].offsetHeight;
			}
			else {
				height = iWindow.document.body.scrollHeight;
			}
			
			//now some styles to make the iframe look nice in IE
			iWindow.document.body.style.overflow = "hidden";
			iWindow.document.body.style.border = "none";
			iWindow.document.body.style.backgroundColor = "transparent";
		}
		else if (document.body && document.body.clientWidth) {
			width = iWindow.document.body.clientWidth;
			height = iWindow.document.body.clientHeight;
		}
		
		if (width != 0) {
			iframe.setStyle({ "height": height + "px", "width": width + "px", "overflow": "hidden" });
		}
		else {
			//if the styles have not loaded yet we may need to keep trying.
			setTimeout(this.doResizeFrame.bind(this, iframe), 100);
		}
		//FIX IE SELECTION BUG.
		this.selectFirstInput(iWindow.document, iWindow.document.body);
	},
	
	//FIX IE SELECTION BUG: Prevents IE bug where IE sometimes fails to detect selection
	//of input fields by setting then removing selection.
	selectFirstInput: function(doc, parent)
	{
		if(document.all) {
			try {
				var input = $(parent).select("input[type='text']")[0];
				input.focus();
				input.select();
				doc.selection.empty();
			}
			catch(e) {}
		}
	},
	
	doResize: function(iframe) {
		this.body.removeClassName(PageStudio.CLASS.LOADING);
		var width = this.tool.configBody.scrollWidth;
		var height = this.tool.configBody.scrollHeight;
		var handleHeight = this.handle.getDimensions().height;
		
		if(iframe != null) {
			this.doResizeFrame(iframe);
			var iframeDimensions = iframe.getDimensions();
			
			if (iframeDimensions.width != 0) {
				width = iframeDimensions.width;
			}
			if (iframeDimensions.height != 0) {
				height = iframeDimensions.height;
			}
		}
		else {
			height += this.tool.configControls.getDimensions().height; // Ok/Cancel
			
			//FIX IE SELECTION BUG.
			this.selectFirstInput(document, this.tool.element);
		}
		if (width != 0) {
			this.element.setStyle({ "height": (height + handleHeight + 20) + "px", "width": (width + 20) + "px" });
		}
		new PSResizableHelper(this.element, { handle: "right bottom", grip: true });
		this.element.addClassName("ps_resized");
	},
	
	resizeForForm: function(memo) {
		if(memo) {
			this.doResize(memo.iframe);
		}
		else {
			this.doResize();
		}
	},
	
	setVisualStyle: function(memo) {
		if(memo && memo.iframe) {
			var iContent = memo.iframe.contentWindow ? memo.iframe.contentWindow : memo.iframe.contentDocument.defaultView;
			var iBody = iContent.window.document.body;
			iBody.className += (iBody.className ? ' ' : '') + PageStudio.currentTheme;
		}
	},
	
	onConfigurationLoaded: function(e) {
		this.resizeForForm(e.memo);
		this.setVisualStyle(e.memo);
		this.center();
	}
});

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

var PSLoadLayouts = Class.create({
	initialize: function() {
		document.fire(PageStudio.EVENT.LAYOUT_JSON_LOADING);
		new PageStudio.Request(PageStudio.URL.LAYOUT_DESIGNER, {
			evalJSON: true, method: "get", parameters: PageStudio.getRequestItemParams({ "action": PageStudio.ACTION.GET_LAYOUTS_LIST }),
			onSuccess: this.loadLayoutsJSON.bindAsEventListener(this),
			onFailure: this.onFailure.bindAsEventListener(this)
		});
	},
	
	onFailure: function(t) {
		PageStudio.displayExceptionAlert(PageStudio.locale.get("ps_load_layouts_exception"), t);
	},
	
	loadLayoutsJSON: function(t) {
		var layouts = $H(t.responseJSON);
		var sorted = layouts.sortBy(this.alphabetise);
		PageStudio.layouts = new Hash();
		for (var i = 0; i < sorted.length; i++) {
			var layout = sorted[i];
			PageStudio.layouts.set(layout[0], layout[1]);
		}
		document.fire(PageStudio.EVENT.LAYOUT_JSON_LOADED);
	},
	
	alphabetise: function(layout) {
		return layout[1].name;
	}
});
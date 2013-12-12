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

var PSLoadComponents = Class.create({
	initialize: function() {
		this.complete = $H({
			components: false,
			images: false
		});
		document.fire(PageStudio.EVENT.COMPONENT_JSON_LOADING);

		new PageStudio.Request(PageStudio.URL.COMPONENT_FACTORY, {
			evalJSON: true, method: "get", parameters: PageStudio.getRequestItemParams({"action": PageStudio.ACTION.GET_COMPONENT_LIST }),
			onSuccess: this.loadComponentJSON.bindAsEventListener(this), onFailure: this.onComponentError.bindAsEventListener(this), onComplete: this.completeLoad.bind(this, "components")
		});
		new PageStudio.Request(PageStudio.URL.COMPONENT_FACTORY, {
			evalJSON: true, method: "get", parameters: PageStudio.getRequestItemParams({ "action": PageStudio.ACTION.GET_IMAGE_LIST }),
			onSuccess: this.loadImageJSON.bindAsEventListener(this), onFailure: this.onImageError.bindAsEventListener(this), onComplete: this.completeLoad.bind(this, "images")
		});
	},
	
	loadComponentJSON: function(t) {
		PageStudio.components = $H(t.responseJSON);
	},
	
	onComponentError: function(t) {
		PageStudio.components = new Hash();
		PageStudio.displayExceptionAlert(PageStudio.locale.get("ps_load_components_exception"), t);
	},
	
	onImageError: function(t) {
		PageStudio.images = new Hash();
		PageStudio.displayExceptionAlert(PageStudio.locale.get("ps_load_images_exception"), t);
	},
	
	loadImageJSON: function(t) {
		PageStudio.images = $H(t.responseJSON);
		PageStudio.images.values().each(PSImageInfoHelper.loadInfo);
	},
	
	checkLoad: function() {
		if (this.complete.values().all()) {
			if(PageStudio.contributor == "MWC" && window.top.updatePageStudio) {
				//if PS is being reloaded by the MWC after switching views it will need to be updated.
				window.top.updatePageStudio(window, PageStudio);
			}
			else if(PageStudio.contributor == "MSC" && external.UpdatePageStudioComponents) {
				//if PS is being used in the Smart Client, and the default view is forms view, PS may need to update after switching views.
				external.UpdatePageStudioComponents();
			}
			document.fire(PageStudio.EVENT.COMPONENT_JSON_LOADED);
		}
	},
	
	completeLoad: function(name) {
		this.complete.set(name, true);
		this.checkLoad();
	}
});
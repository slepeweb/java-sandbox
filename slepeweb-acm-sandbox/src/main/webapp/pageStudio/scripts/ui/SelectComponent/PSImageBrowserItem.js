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

var PSImageBrowserItem = Class.create(PSBrowserItem, {
	initialize: function($super, component, buttonTitle) {
		this.buttonTitle = buttonTitle;
		$super(component);
	},
	
	createElement: function($super) {
		var imageTitle = unescape(this.component.name);
		if(this.buttonTitle) {
			this.tooltip = new Template(PageStudio.locale.get("add_image_tooltip_template")).evaluate({ name: imageTitle });
		}
		$super();
		this.element.addClassName("ps_image_browser_item");
				
		if (imageTitle.length > 17) {
			PSToolTipHelper.add(this.title, imageTitle);
			imageTitle = imageTitle.substring(0, 14) + "...";			
		}
		if(this.buttonTitle) {
			this.button.element.update(imageTitle);
			this.title.hide();
		}
		
		if(this.component.src) {
			var imageContainer = new Element("div", { className: "ps_image_browser_image_container" });				
			var imageElement = new Element("img", { alt: this.component.alt, src: this.component.src, className: "ps_image_thumbnail" });
			
			imageContainer.insert(imageElement);
			this.element.insert(imageContainer);
		}
	},
	
	onFocus: function($super, e) {
		$super(e);
		this.element.addClassName("ps_focused");
		this.element.fire(PSImageBrowserItem.EVENT.FOCUSED, { "component": this.component });
		//Deselect this image if the user cancels the selection by clicking a blank space
		//in the parent container.
		var observer = function(e) {
			if(e.element() == this.element.parentNode) {
				this.deselect();
				this.element.parentNode.stopObserving("click", observer);
			}
		}.bindAsEventListener(this);
		this.element.parentNode.observe("click", observer);
	}
});

PSImageBrowserItem.EVENT = { 
		FOCUSED: "pagestudio:imageItemFocused"
};
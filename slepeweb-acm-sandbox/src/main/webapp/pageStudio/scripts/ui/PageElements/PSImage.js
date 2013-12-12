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

var PSImage = Class.create(PSCellItem, {	
	getComponent: function() {
		if (this.element) {
			var name = this.element.readAttribute("name");
			if (name) {
				this.component = PageStudio.images.get(name);
			}
		}
	},
	
	createElement: function($super, parent) {
		this.elementName = "image";
		$super(parent);
		if (!this.component) {
			this.remove();
		}
		else {
			if (this.component.src){
				var imgElements = this.element.select("img");
				//if the image is already present on the page...
				if (imgElements.length > 0) {
					this.imageElement = imgElements[0];
					this.doAfterImageLoaded();
				}
				//...else create the image
				else {
					this.imageElement = new Element("img", { "alt": this.component.alt });
					//we need to make sure the image position is relative or the resize helper will set it to absolute.
					this.imageElement.setStyle({"position": "relative" });
					//We need to wait until the image has finished loading before we make it resizable or it can cause problems.
					this.imageElement.observe("load", this.onImageLoaded.bindAsEventListener(this));
					//We need to set the src after setting the observer or IE will not catch the onload event.
					this.imageElement.writeAttribute("src", this.component.src);
					this.element.insert(this.imageElement);
				}
				
			}
			this.element.writeAttribute({ "name": this.component.id });
			this.element.addClassName(PageStudio.CLASS.IMAGE);
		}
	},
	
	onImageLoaded: function(e) {
		this.doAfterImageLoaded();
	},
	
	doAfterImageLoaded: function() {
		this.makeResizable();
		this.setSize();
	},
	
	setSize: function() {
		//if the image hasn't had it's dimensions set, set them.
		if (this.imageElement.getStyle("width") == null) {
			var width = this.component.width;
			//Fix for lack of IE max-width support: ensure the width is not greater than the parent width.
			if (this.element.parentNode) {
				var parentWidth = this.element.up().getWidth();
				if(width > parentWidth) width = parentWidth - 4;
			}
			this.imageElement.setStyle({ "width": width + "px", "height": this.component.height + "px" });
		}
	},
	
	makeResizable: function() {
		new PSResizableHelper(this.imageElement, { "minWidth": 1, "minHeight": 1, "handle": "top left bottom right" });
	},
	
	onResizeStart: function(e) {
		PageStudio.setDirty(true);
	},
	
	onResizeEnd: function(e) {
		PageStudio.setDirty(true);
	},
	
	copy: function($super) {
		var dims = this.imageElement.getDimensions();
		return function(area)
		{
			var item = area.copyItem(this);
			item.component.imageElement.setStyle({ "width": dims.width + "px", "height": dims.height + "px" });
		}.bind(this);
	}
});

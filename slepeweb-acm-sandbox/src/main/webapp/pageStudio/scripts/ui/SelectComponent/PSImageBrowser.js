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

var PSImageBrowser = Class.create(PSBrowser, {
	initialize: function($super, images) {
		$super(images);
		this.element.addClassName("ps_image_browser");
	},
	
	attachEvents: function($super) {
		$super();
		document.observe(PageStudio.EVENT.ADD_IMAGE_CLICKED, this.onAddImageClicked.bindAsEventListener(this));
		this.element.observe(PSImageBrowserItem.EVENT.FOCUSED, this.onItemFocused.bindAsEventListener(this));
		this.element.observe("click", this.onClick.bindAsEventListener(this));
	},
	
	createElement: function($super) {
		$super();
		this.createInfoBox();
		if(this.canAddImages()) {
			var control = new Element("div", { className: "ps_image_browser_control"});
			this.addImageButton = new PSNativeButton({
				"label": PageStudio.locale.get("Add Image"),
				"tooltip": PageStudio.locale.get("Add Image"),
				"className": "ps_add_image_button ps_standard_button",
				"action": PageStudio.EVENT.ADD_IMAGE_CLICKED
			});
			control.insert(this.addImageButton.element);
			
			this.element.insert(control);
		}
		else {
			this.element.addClassName("ps_no_controls");
		}
	},
	
	addInfoSpan: function(label) {
		var infoSpan = new Element("span");
		this.details.insert(new Element("span", { "className": "ps_info_span" }).update(new Element("strong").update(label)).insert(infoSpan));
		this.imageInfo.push(infoSpan);
	},
	
	createInfoBox: function() {
		var infoBox = new Element("div", { "className": "ps_add_image_info"});
		this.instructions = new Element("div", { "className": "ps_instructions"}).update(PageStudio.locale.get("add_image_info"));
		this.details = new Element("div").hide();
		this.imageInfo = new Array();
		this.thumbnailInfo = new Element("div");	
		this.nameInfo = new Element("span");
		this.details.insert(this.thumbnailInfo);
		
		this.addInfoSpan(PageStudio.locale.get("Dimensions"));
		this.addInfoSpan(PageStudio.locale.get("Name"));
		this.addInfoSpan(PageStudio.locale.get("Type"));
		
		this.element.insert(infoBox.insert(this.instructions).insert(this.details));
	},
	
	updateInfoBox: function(component) {
		if(component != null) {
			this.instructions.hide();
			this.thumbnailInfo.update(new Element("img", { "src": component.src }));
			this.imageInfo[0].update(component.width + " x " + component.height);
			this.imageInfo[1].update(decodeURIComponent(component.name));
			this.imageInfo[2].update(component.mediatype);
			this.details.show();
		}
		else {
			this.instructions.show();
			this.details.hide();
		}
	},
	
	createItem: function(image) {
		return new PSImageBrowserItem(image, true);
	},
	
	canAddImages: function() {
		try {
			if (PageStudio.contributor == "MSC"){
				PSImageBrowser.prototype.openAddImageDialog = function() {
					var newImages = $A(eval(external.LaunchInlineImageBrowser()));
					var existed = null;
					newImages.each(function(newImage) {
						if (newImage != null) {
							if (!this.category.pluck("src").member(newImage.src)) {
								PageStudio.addNewInline(newImage);
							}
							else {
								if(!existed) existed = "";
								existed += ("\n" + escape(newImage.name));
							}
						}
					}, this);
					if(existed !=  null) {
						alert(PageStudio.locale.get("image_already_exists" + existed));
					}
				};
				return true;
			}
			else if(PageStudio.contributor == "MWC" && window.top.inLineImage) {
				PSImageBrowser.prototype.openAddImageDialog = function() {
					var chooseImageUrl = window.location.protocol + "//" + window.location.hostname + ":" + window.location.port + '/mwc/app?service=external/chooseImageDialogue&sp=S' + encodeURI("PS Images");
					window.open(chooseImageUrl ,  'inlinesBrowser' , 'width=600,height=400,resizable=yes', false);
				};
				return true;
			}
		}
		catch(e){}
		return false;
	},
	
	openAddImageDialog: Prototype.emptyFunction,
	
	onAddImageClicked: function(e) {
		this.openAddImageDialog();
	},
	
	onItemFocused: function(e) {
		this.updateInfoBox(e.memo.component);
	},
	
	onClick: function(e) {
		if(e.element() == this.list) this.updateInfoBox();
	}
});

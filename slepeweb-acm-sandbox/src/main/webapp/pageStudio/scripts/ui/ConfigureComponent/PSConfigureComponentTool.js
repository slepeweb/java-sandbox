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

var PSConfigureComponentTool = new Class.create(PSTool, {
	initialize: function($super, params) {
		$super(params);
		this.component = params.component;
		this.attachEvents();
		this.retrieveConfigForm();
	},
	
	attachEvents: function($super) {
		this.element.observe(PageStudio.EVENT.SUBMIT_CONFIGURATION_CLICKED_IFRAME, this.onConfigurationOKClickedIFrame.bindAsEventListener(this));
		this.element.observe(PageStudio.EVENT.SUBMIT_CONFIGURATION_CLICKED, this.onConfigurationOKClicked.bindAsEventListener(this));
		document.observe(PageStudio.EVENT.CANCEL_CONFIGURATION_CLICKED, this.onConfigurationCancelClicked.bindAsEventListener(this));
	},
	
	createElement: function($super) {
		$super();
		this.element.hide();
		this.config = new Element("div", { className: "ps_config_form" });
		
		this.configControls = new Element("div", { className: "ps_config_form_controls" });
		this.configOKButton = new PSNativeButton({
			"label": PageStudio.locale.get("OK"),
			"action": PageStudio.EVENT.SUBMIT_CONFIGURATION_CLICKED
		});
		this.configCancelButton = new PSNativeButton({
			"label": PageStudio.locale.get("Cancel"),
			"action": PageStudio.EVENT.CANCEL_CONFIGURATION_CLICKED
		});
		this.configControls.insert({
			"top": this.configOKButton.element,
			"bottom": this.configCancelButton.element
		});
		this.config.insert({ "bottom": this.configControls });
		this.wrapper.insert(this.config);
	},
	
	addConfigForm: function(t) {
		if(t.responseJSON) {
			this.addConfigFormFromJSON(t.responseJSON);
		}
		else {
			this.addConfigFormFromHTML(t.responseText);
		}
		this.configBody.removeClassName(PageStudio.CLASS.ERROR);
	},
	
	addConfigFormError: function(error, t) {
		PageStudio.displayExceptionAlert(error, t);
		this.close();
	},
	
	createFormBody: function(form) {
		if(!form) {
			form = new Element("form", { className: "ps_config_form_body" });
		}
		this.configBody = form;
		this.config.insert({ "top": this.configBody });
	},
	
	addConfigFormFromHTML: function(html) {
		this.createFormBody();
		//Wrap the incoming html in a block level element to prevent errors in IE.
		var contents = new Element("div", { "className": "ps_config_body_contents" });
		this.configBody.update(contents.update(html));
		this.doAfterConfigLoad();
	},
	
	addConfigFormFromJSON: function(json) {
		if (json.configURL) {
			this.addConfigFormIframe(json);
		}
		else {
			this.constructComponentConfigurationForm(json);
		}
	},
	
	constructComponentConfigurationForm: function(json) {
		var component = this.component.component;
		
		var title = new Element("div", { "className": PSConfigureComponentTool.CLASS.TITLE }).update(component.name);
		if (component.icon && component.icon != "null") {
			var icon = new Element("img", { "className": "ps_config_icon", "src": component.icon });
			title.insert({ "top": icon });
		}
		var description = new Element("div", { "className": PSConfigureComponentTool.CLASS.DESCRIPTION }).update(component.description);
		
		this.autoForm = new PSConfigureComponentForm(json, this.component.component);
		//insert the generated table in to the config form.
		this.createFormBody(this.autoForm.element);
		
		this.configBody.insert({ "top": description });
		this.configBody.insert({ "top": title });
		
		this.doAfterConfigLoad();
	},
	
	addConfigFormIframe: function(json) {
		this.createFormBody();
		var src = PageStudio.proxifyURL(json.configURL);
		this.iframe = new Element("iframe", { "id": json.id, "frameborder": "0", "src": src });
		this.iframe.allowTransparency = true;
		this.configBody.insert(this.iframe);
		this.iframe.observe("load", this.doAfterConfigLoad.bind(this));
	},
	
	doAfterConfigLoad: function() {
		this.element.show();
		this.element.fire(PageStudio.EVENT.CONFIGURATION_LOADED, { "iframe": this.iframe });
	},
		
	submitConfiguration: function() {
		if(this.autoForm) {
			this.autoForm.submit(this.component.element.id, this.onSubmissionSuccess.bind(this), this.onSubmissionFailure.bind(this));
		}
		else {
			var params = PageStudio.getRequestItemParams(this.configBody.serialize().toQueryParams());
			Object.extend(params, {
				"action": PageStudio.ACTION.GET_COMPONENT,
				"configuration": PageStudio.ACTION.SAVE_COMPONENT_CONFIGURATION,
				"cid": this.component.component.id,
				"cInstId": this.component.element.id
			});
			new PageStudio.Request(PageStudio.URL.COMPONENT_FACTORY, { 
				method: "get",
				parameters: params,
				evalScripts: true,
				onSuccess: this.onSubmissionSuccess.bind(this),
				onFailure: this.onSubmissionFailure.bind(this)
			});
		}
	},
	
	retrieveConfigForm: function() {
		var params = PageStudio.getRequestItemParams({
			"action": PageStudio.ACTION.GET_COMPONENT,
			"configuration": PageStudio.ACTION.GET_CONFIGURATOR_DETAILS,
			"cid": this.component.component.id,
			"cInstId": this.component.element.id
		});
		
		new PageStudio.Request(PageStudio.URL.COMPONENT_FACTORY, { 
			method: "get",
			parameters: params,
			evalScripts: true,
			onSuccess: this.addConfigForm.bind(this),
			onFailure: this.addConfigFormError.bind(this, PageStudio.locale.get("There was a problem retrieving the component configuration form."))
		});
	},
	
	getConfigFormDimensions: function() {
		var position = this.configBody.getStyle("position");
		this.configBody.setStyle({ "position": "relative" });
		var dimensions = this.configBody.getDimensions();
		this.configBody.setStyle({ "position": position });
		return dimensions;
	},
	
	onSubmissionSuccess: function(t) {
		this.status = "configured";
		this.close();
		this.component.display();
		PageStudio.setDirty(true);
	},
	
	onSubmissionFailure: function(t) {
		this.component.display();
	},
	
	onConfigurationOKClickedIFrame: function(e) {
		this.onSubmissionSuccess();
	},
	
	onConfigurationOKClicked: function(e) {
		this.submitConfiguration();
	},
	
	onConfigurationCancelClicked: function(e) {
		this.close();
	},
	
	close: function() {
		this.element.fire(PSDialog.EVENT.CLOSE_CLICKED, { "status": this.status });
	}
});

PSConfigureComponentTool.CLASS = {
	TITLE: "ps_configuration_form_title",
	DESCRIPTION: "ps_configuration_form_description",
	ERROR_DETAILS: "ps_error_details",
	PROPERTIES_FIELDSET: "ps_properties_fieldset"
};

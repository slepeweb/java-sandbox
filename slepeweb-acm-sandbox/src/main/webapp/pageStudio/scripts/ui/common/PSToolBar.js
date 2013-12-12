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

var PSToolBar = Class.create({
	initialize: function(params) {
		this.initParams(params);
		this.buttons = new Array();
		this.effect = null;
		this.visible = false;
		this.shown = false;
		this.enable();
		this.createElement();
	},
	
	initParams: function(params) {
		this.target = $(params.target);
		this.transition = params.transition ? params.transition : "appear";
	},
	
	addButton: function(button, before) {
		this.buttons.push(button);
		if (before) {
			before.element.insert({ "before": button.element });
		}
		else {
			this.element.insert(button.element);
		}
	},
	
	createElement: function() {
		this.element = new Element("div", { className: "ps_toolbar ps_reset" });
		this.target.insert(this.element);
		PageStudio.preventContributorFade(this.element);
		this.element.hide();
	},
	
	show: function() {
		this.shown = true;
		if(this.effect == null && this.target && (this.visible == null || !this.visible)) {
			this.effect = this.doShowTransition();
		}
	},
	
	hide: function(disable) {
		this.shown = false;
		if(this.effect == null && this.visible && this.target) {
			this.effect = this.doHideTransition(disable);
		}
	},
	
	doShowTransition: function() {
		var effect = null;
		if (this.transition == "slidedown") {
			effect = new Effect.SlideDown(this.element, { duration: 0.2, afterFinish: this.doAfterTransition.bind(this, false, true) });
		}
		else if (this.transition == "appear") {
			effect = new Effect.Appear(this.element, { duration: 0.1, afterFinish: this.doAfterTransition.bind(this, false, true) });
		}
		return effect;
	},
	
	doHideTransition: function(disable) {
		var effect = null;
		if (this.transition == "slidedown") {
			effect = new Effect.SlideUp(this.element, { duration: 0.2, afterFinish: this.doAfterTransition.bind(this, disable, false) });
		}
		else if (this.transition == "appear") {
			effect = new Effect.Fade(this.element, { duration: 0.1, afterFinish: this.doAfterTransition.bind(this, disable, false) });
		}
		return effect;
	},
	
	doAfterTransition: function(disable, visible) {
		this.effect = null;
		this.visible = visible;
		//double check the toolbar is visible
		if (this.shown) {
			this.element.show();
			this.visible = true;
		}
		else {
			this.element.hide();
			this.visible = false;
		}
	},
	
	enable: function() {
		this.enabled = true;
	},
	
	disable: function() {
		if(this.target) {
			if(this.visible && this.effect == null) {
				this.hide(true);
			}
		}
		this.enabled = false;
	},
	
	remove: function() {
		if(this.target) {
			this.disable();
			this.buttons.invoke("remove");
			this.element.remove();
		}
	}
});

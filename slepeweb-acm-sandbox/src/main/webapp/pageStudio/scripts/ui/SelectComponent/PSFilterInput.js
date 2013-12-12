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

var PSFilterInput = Class.create({
	initialize: function(params) {
		this.filter = params.filter;
		this.label = params.label;
		this.createElement();
		this.attachEvents();
	},
	
	createElement: function() {
		this.element = new Element("div", { className: "ps_filter_form" });
		
		this.input = new Element("input", { className: "ps_filter_input ps_input" });
		this.addLabel();
		
		this.element.insert(this.input);
	},
	
	attachEvents: function() {
		this.element.observe("submit", this.onSubmit );
		this.input.observe("focus", this.onGetFocus.bindAsEventListener(this) );
		this.input.observe("blur", this.onLoseFocus.bindAsEventListener(this) );
		this.input.observe("keyup", this.onKeyUp.bindAsEventListener(this) );
	},
	
	onSubmit: function(e) {
		e.stop();
	},
	
	onGetFocus: function(e) {
		this.removeLabel();
	},
	
	onLoseFocus: function(e) {
		this.addLabel();
	},
	
	onKeyUp: function(e) {
		var input = e.element();
		this.filter(this.input.getValue());
	},
	
	addLabel: function() {
		if (!this.input.hasClassName("ps_filter_input_blank") && this.input.getValue().empty()) {
			this.input.addClassName("ps_filter_input_blank");
			this.input.setValue(this.label);
		}
	},
	
	getValue: function()
	{
		if (!this.input.hasClassName("ps_filter_input_blank")) {
		  return this.input.getValue();
		}
		return "";
	},
	
	removeLabel: function() {
		if (this.input.hasClassName("ps_filter_input_blank")) {
			this.input.removeClassName("ps_filter_input_blank");
			this.input.clear();
		}
	}
});
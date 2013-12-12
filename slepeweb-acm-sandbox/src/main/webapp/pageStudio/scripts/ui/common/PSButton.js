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

var PSButton = Class.create(PSElement, {
	initialize: function($super, params) {
		if (params.group) {
			this.addToGroup(params.group);
		}
		this.enabled = true;
		$super(params);
		this.addTooltip();
		this.attachEvents();
	},
	
	initParams: function($super, params) {
		this.className = params.className;
		this.label =  {
			"length": 0,
			"text": null
		};
		if (params.label) {
			if(typeof(params.label) == "string") {
				params.label = { 
					"length": 0,
					"text": params.label
				};
			}
			this.label = {
				"length": params.label.length ? params.label.length : 0,
				"text": params.label.text ? params.label.text : null
			};
		}
		this.tooltip = params.tooltip;
		this.action = params.action;
		this.memo = params.memo;
		this.icon = params.icon;
	},
	
	addToGroup: function(group) {
		if(group) {
			group.add(this);
		}
	},
	
	createElement: function($super) {
		this.elementName = "button";
		$super();
		this.element.addClassName("ps_button " + this.className);
		this.createIcon();
		if(this.label.text) {
			this.labelSpan = new Element("span", { "className": "ps_button_label" });
			this.element.insert(this.labelSpan);
			this.addLabelText(this.labelSpan);
		}
	},
	
	addTooltip: function() {
		if (this.tooltip) {
			PSToolTipHelper.add(this.element, this.tooltip);
		}
	},
	
	createIcon: function() {
		if (this.icon) {
			this.element.addClassName("ps_button_with_icon");
			this.element.insert(
				new Element("img", { "className": "ps_button_icon", "src": this.icon })
			);
		}
	},
	
	addLabelText: function(element) {
		var text = this.label.text;
		if (this.label.length != 0 && text.length > this.label.length) {
			text = text.substring(0, this.label.length - 3) + "...";
			if (!this.tooltip) {
				PSToolTipHelper.add(this.element, this.label.text);
			}
		}
		element.insert(text);
	},	
	
	disable: function() {
		if(this.enabled) {
			this.deselect();
			this.detachEvents();
			this.enabled = false;
			this.element.addClassName(PSButton.CLASS.DISABLED);
			PSToolTipHelper.tooltip.hide();
		}
	},
	
	enable: function() {
		if(!this.enabled) {
			this.attachEvents();
			this.enabled = true;
			this.element.removeClassName(PSButton.CLASS.DISABLED);
		}
	},
	
	remove: function() {
		this.element.fire(PageStudio.EVENT.REMOVED);
		this.element.stopObserving();
		this.element.remove();
	},
	
	select: function($super) {
		$super();
		if (this.group) {
			this.group.setSelected(this);
		}
	},
	
	//byGroup param indicates wether or not the button is being deselected programmatically from the group.
	deselect: function($super, byGroup)
	{
		$super();
		if(!byGroup && this.group && this.group.getSelected() == this)
		{
			this.group.setSelected(null);
		}
	},
	
	fire: function() {
		this.element.fire(this.action, this.memo);
	},
	
	onDeselect: function($super, e) {
		if (this.group && this.group.contains(e.memo.object)) {
			$super(e);
		}
	},
	
	onClick: function($super, e) {
		$super(e);
		e.stop();
	},
	
	onMouseDown: function($super, e) {
		$super(e);
		this.clickStarted = true;
		this.element.addClassName("ps_button_on_mouse_down");
		e.stop();
	},
	
	onMouseUp: function($super, e) {
		$super(e);
		if (this.clickStarted) {
			this.clickStarted = false;
			this.fire();
		}
		this.element.removeClassName("ps_button_on_mouse_down");
		e.stop();
	},
	
	onMouseEnter: function($super, e) {
		$super(e);
		this.element.addClassName("ps_button_on_mouse_over");
	},
	
	onMouseLeave: function($super, e) {
		$super(e);
		this.clickStarted = false;
		this.element.removeClassName("ps_button_on_mouse_down");
		this.element.removeClassName("ps_button_on_mouse_over");
	},
	
	onContextMenu: function($super, e) {
		e.stop();
	}
});

PSButton.CLASS = {
		DISABLED: "ps_disabled"
};

var PSNativeButton = Class.create(PSButton, {
	initialize: function($super, params) {
		$super(params);
	},
	
	createElement: function($super) {
		this.element = new Element("button");
		this.element.addClassName("ps_native_button " + this.className);
		
		this.createIcon();
		if(this.label.text) {
			this.addLabelText(this.element);
		}
	},
	
	attachEvents: function($super) {
		this.element.observe("click", this.listeners.onClick);
		this.element.observe("mouseenter", this.onMouseEnter.bindAsEventListener(this));
		this.element.observe("mouseleave", this.onMouseLeave.bindAsEventListener(this));
		this.element.observe("contextmenu", this.listeners.onContextMenu);
	},
	
	createIcon: function()
	{
		if (this.icon) {
			this.element.addClassName("ps_button_with_icon");
			this.iconElement = new Element("img", { "className": "ps_button_icon", "src": this.icon.enabled }); 
			this.element.insert(this.iconElement);
		}
	},
	
	onClick: function($super, e) {
		$super(e);
		this.fire();
	},
	
	onMouseEnter: function(e) {
		if (this.icon && !this.element.readAttribute("disabled")) {
			this.iconElement.writeAttribute("src", this.icon.hot);
		}
	},
	
	onMouseLeave: function(e) {
		if (this.icon && !this.element.readAttribute("disabled")) {
			this.iconElement.writeAttribute("src", this.icon.enabled);
		}
	},
	
	enable: function($super, e) {
		$super(e);
		this.element.removeAttribute("disabled");
		if (this.icon) {
			this.iconElement.writeAttribute("src", this.icon.enabled);
		}
	},
	
	disable: function($super, e) {
		$super(e);
		this.element.writeAttribute("disabled", "true");
		if (this.icon) {
			this.iconElement.writeAttribute("src", this.icon.disabled);
		}
	}
});

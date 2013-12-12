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

PSBrowserItem = Class.create(PSElement, {
	initialize: function($super, component) {
		this.elementName = "browserItem";
		this.component = component;
		this.tooltip = PageStudio.locale.get("Add component");
		$super({});
	},
	
	createElement: function($super) {
		$super();
		this.element.addClassName("ps_browser_item");
		this.element.addClassName("ps_category_" + this.component.type);
		this.title = new Element("div", { "className": "ps_title" });
		this.title.update(unescape(this.component.name));
		this.element.insert(this.title);
		this.button = new PSButton({ 
			"className": "ps_browser_item_button",
			"tooltip": this.tooltip,
			"action": PageStudio.EVENT.ADD_COMPONENT_CLICKED,
			"memo": { component: this.component },
			"group": this.buttonGroup
		});
		this.element.insert(this.button.element);
	},
	
	attachEvents: function($super) {
		$super();
		PSDraggableMessenger.create(this.element, this.component, { "scroll": window, "ghosting": true, "revert": true, "zindex": "999999", "reverteffect": PSOverflowDragHelper.revert, "onStart": PSOverflowDragHelper.startDrag, "onEnd": PSOverflowDragHelper.endDrag });
	},
	
	onMouseEnter: function($super, e) {
		$super(e);
		this.element.addClassName("ps_browser_item_hover");
	},
	
	onMouseLeave: function($super, e) {
		$super(e);
		this.element.removeClassName("ps_browser_item_hover");
	},
	
	onMouseDown: function($super, e) {
		$super(e);
		this.element.addClassName("ps_component_list_item_mouse_down");
		this.element.style.cursor="url('/pageStudio/images/ui/cursors/closedhand.cur'), auto";
	},
	
	onMouseUp: function($super, e) {
		$super(e);
		this.element.removeClassName("ps_component_list_item_mouse_down"); 
		this.element.style.cursor="url('/pageStudio/images/ui/cursors/openhand.cur'), auto";
	},
	
	onDblClick: function($super, e) {
		$super(e);
		Effect.Pulsate(this.element, { pulses: 1, duration: 0.3 });
		this.element.fire("pagestudio:addComponentClicked", { component: this.component });
	}
});
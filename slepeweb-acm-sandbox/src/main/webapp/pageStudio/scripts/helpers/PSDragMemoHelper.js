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

var PSDraggableMessenger = {
	create: function(element, object, options) {
		element.observe("messenger:caught", PSDraggableMessenger.doOnDrop.bindAsEventListener(object));
		return new Draggable(element, options);
	},
	
	doOnDrop: function(e) {
		var memo = e.memo;
		memo.object.draggable = this;
		memo.element.droppable.fire("messenger:dropped", memo);
	}
};

var PSDroppableReceiver = {
	add: function(element, object, options) {
		if(options) {
			if(options.onDrop) {
				options.onDrop = options.onDrop.wrap(PSDroppableReceiver.onDropWrapper.bind(object));
			}
			else {
				options.onDrop = PSDroppableReceiver.doAfterDrop.bind(object);
			}
			if (options.onReceive) {
				element.observe("messenger:dropped", options.onReceive);
			}
		}
		else {
			options = { onDrop: PSDroppableReceiver.doAfterDrop.bind(object) };
		}
		Droppables.add(element, options);
	},
	
	onDropWrapper: function(onDrop, draggable, droppable, event) {
		onDrop(draggable, droppable, event);
		PSDroppableReceiver.doAfterDrop(draggable, droppable, event).bind(this); 
	},
	
	doAfterDrop: function(draggable, droppable, event) {
		var object = this == window ? null : this;
		draggable.fire("messenger:caught", { "element": { "draggable": draggable, "droppable": droppable }, "object": { "event": event, "droppable": object } });
	}
};
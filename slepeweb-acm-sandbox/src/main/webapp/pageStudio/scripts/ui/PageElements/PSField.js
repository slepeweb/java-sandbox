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
var PSField = Class.create(PSCellItem, {
	initialize: function($super, params) {
		this.isEditing = false;
		this.allowEditing = true;
		this.loadingComponents = 0;
		$super(params);
		this.loadMarkupComponents();
	},
	
	initParams : function($super, params) {
		if(params.component) {
			this.getFieldProperties(params.component.id);
			$super(params);			
		}
		else $super(params);
		
	},
	
	getFieldProperties: function(id) {
		this.fieldID = id;
		var field = PageStudio.Data.getField(id);
		var markup = field.markup;
		this.isMarkup = (markup && markup.toLowerCase() == "true");
		this.fieldName = field.name;
	},
	
	getComponent : function() {
		if (this.element) {
			var id = this.element.readAttribute("name");
			if (id) {
				this.component = PageStudio.Data.getField(id);
				this.getFieldProperties(id);
			}
		}
	},
	
	attachEvents: function($super) {
		$super();
		this.wrapper.observe(PSComponent.EVENT.MARKUP_COMPONENT_LOADING, this.listeners.onComponentLoading);
		this.wrapper.observe(PSComponent.EVENT.MARKUP_COMPONENT_LOADED, this.listeners.onComponentLoaded);
		this.wrapper.observe(PSComponent.EVENT.MARKUP_COMPONENT_LOAD_ERROR, this.listeners.onComponentLoaded);
	},
	
	createListeners: function($super) {
		$super({
			onComponentLoading: this.onComponentLoading.bindAsEventListener(this),
			onComponentLoaded: this.onComponentLoaded.bindAsEventListener(this)
		});
	},
	
	createElement: function($super, parent) {
		this.elementName = "field";
		$super(parent);
		this.addWrapper();
		this.element.writeAttribute( { "name" : this.fieldID });
		this.element.addClassName(PageStudio.CLASS.FIELD);
		PageStudio.Data.subscribe(this.wrapper, this.fieldID);
		this.makeEditable();//must be called AFTER the innerHTML has been set.
	},
	
	loadMarkupComponents: function() {
		var mcs = this.wrapper.select(".ps_markup_component_wrapper");
		mcs.each(function(mc) { new PSMarkupComponent(mc); });
	},
	
	addWrapper : function() {
		this.wrapper = this.element.down("span.page-studio-insitu-field");
		if (this.wrapper == null) {
			var attributes = {
				className : "page-studio-insitu-field",
				itemid : PageStudio.ITEM_KEY,
				fieldname : this.fieldName.escapeHTML(),
				style : 'border: 1px dashed blue;'
			};
			if(this.component.fieldType != 3) {
				attributes.mstagname = 'itemfield';
			}
			this.wrapper = new Element("span", attributes);
			this.element.insert(this.wrapper);
		}
	},

	makeEditable : function() {
		if (this.wrapper != null && this.fieldID != null && this.component.fieldType != 3) {
			this.wrapper.contentEditable = true;
			// If we are in the MSC we can call the external
			// MakeFieldEditable method
			// otherwise we need to add the field to the MWC
			// editable element list.
			if (PageStudio.contributor == "MSC") {
				try {
					external.MakeFieldEditable(this.element.id);
				}
				catch(e) {}
			}
			else if (PageStudio.contributor == "MWC") {
				var itemid = PageStudio.ITEM_KEY;
				if (itemid != null && typeof (itemid) != "undefined" && itemid != "undefined" && parent.getMsElementForField) {
					parent.currentItemId = itemid;
					var msEle = parent.getMsElementForField(this.wrapper.getAttribute("fieldname"));
					if (msEle) {
						msEle.incontextEle = this.wrapper;
						parent.makeFieldEditable(this.wrapper, "incontext", msEle);
					}
				}
			}
		}
	},
	
	createToolbar: function($super) {
		if(this.isMarkup) {
			this.toolbar = new PSMarkupFieldTab({ "target": this.element, "transition": "appear", "item": this });
			this.toolbar.disable();
		}
		else {
			$super();
		}
	},
	
	enableEditing : function() {
		// if we aren't already editing, start editing.
		if (this.component.fieldType != 3 && !this.isEditing && this.allowEditing) {
			PSEdit.detachEvents(); //prevent PS undo/redo while text editing
			// most modern browsers will support content editable. Otherwise, use design mode.
			if (typeof (this.wrapper.contentEditable) != "undefined") {
				this.wrapper.contentEditable = true;
			}
			else if (typeof (document.designMode) != "undefined" && document.designMode == "off") {
				document.designMode = "on";
				// we can control which part of the page is
				// selectable in firefox with MozUserSelect
				// this should help prevent the user editing
				// anything other than the editable field.
				document.body.setStyle( { "MozUserSelect" : "-moz-none" });
				this.wrapper.setStyle( { "MozUserSelect" : "text" });
			}			

			this.isEditing = true;
			this.wrapper.fire(PSField.EVENT.BEGIN_EDITING);
		}
	},

	disableEditing : function() {
		if (this.component.fieldType != 3 && this.isEditing) {
			PSEdit.attachEvents();
			if (typeof (this.wrapper.contentEditable) != "undefined") {
				this.wrapper.contentEditable = false;
			}
			else if (typeof (document.designMode) != "undefined" && document.designMode == "on") {
				document.designMode = "off";
				document.body.setStyle({ "MozUserSelect" : "text" });
			}

			this.isEditing = false;
			this.wrapper.fire(PSField.EVENT.END_EDITING);
		}
	},

	remove : function($super, undoable) {
		PageStudio.Data.unsubscribe(this.wrapper);
		$super(undoable);
		//Call makeEditable on one of the remaining instances in case the one we have removed was the
		//one that the contributor was watching.
		var others = $$("."+PageStudio.CLASS.FIELD+"[name='"+this.fieldID+"']");
		if(others.length > 0 && others[0].PSObj) {
			others[0].PSObj.makeEditable();
		}
	},
	
	insertIntoCurrentSelection: function(node) {
		var range = null;
		if(this.rangeData) {
			//Get a range object using the range data stored on the Field item.
			range = PSSelectionHelper.createRange(this.rangeData);
		}
		else {
			range = PSSelectionHelper.getRange();
		}
		if (range) {
			PSSelectionHelper.collapseRangeToNode(range, this.wrapper);
			PSSelectionHelper.insertIntoRange(range, node);
			//For IE, we need to add a space after the component so the user can select and type outside of the MC body.
			if(document.selection){
				var iT = this.wrapper.innerText;
				var i = iT.indexOf(node.innerText);
				//We only need to add the space if the node has been inserted at the end of the wrapper.
				if(i != -1 && i + node.innerText.length == this.wrapper.innerText.length) {
					this.wrapper.appendChild(document.createTextNode(" "));
				}				
			}
		}
		else {
			//If we don't have any range data, insert the content at the end of the field.
			this.wrapper.insert(node);
		}
		this.rangeData = null;
		//Load any new mark-up components.
		this.loadMarkupComponents();
	},
	
	replaceElement: function(element, replacement) {
		element.replace(replacement);
		this.loadMarkupComponents();
	},
	
	onMouseDown: function($super, e) {
		$super(e);
		this.wrapper.fire(PSField.EVENT.MOUSE_DOWN);
		this.enableEditing();
	},
	
	onMouseUp: function($super, e) {
		$super(e);
		this.rangeData = PSSelectionHelper.getRangeData();
	},
	
	onBlur: function($super, e) {
		$super(e);
		this.disableEditing();
	},
	
	onDeselect: function($super, e) {
		$super(e);
		this.disableEditing();
	},
	
	onComponentLoading: function(e) {
		this.allowEditing = false;
		this.wasEditing = this.isEditing;
		if(this.wasEditing) {
			 this.disableEditing();
		}		
		this.loadingComponents++;
	},
	
	onComponentLoaded: function(e) {
		this.loadingComponents--;
		if(this.loadingComponents == 0) {
			this.allowEditing = true;
			if(this.wasEditing) {
				this.enableEditing();
			}
		}
	}
});

PSField.EVENT = {
		BEGIN_EDITING: "pagestudio:beginEditing",
		END_EDITING: "pagestudio:endEditing",
		INSTANCE_UPDATED: "pagestudio:instanceUpdated",
		MOUSE_DOWN: "pagestudio:fieldMouseDown"
};

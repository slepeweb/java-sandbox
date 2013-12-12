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

var PSSavePage = Class.create({
	initialize: function(areas, saveAction, callback) {
		this.callback = callback;
		this.doSave(saveAction, areas.pluck("element"));
	},	
	
	doSave: function(saveAction, areas) {
		var parser = new PSLayoutParser(true);

		//We must enabled the styles preview during the save so the layout parser can pick up the cell styles.
		this.enableStylesPreview();

		var params = PageStudio.getRequestItemParams({ "xml": parser.createXML(areas), "action": "save" });
		
		switch(saveAction)
		{
			case PSSavePage.SAVE.VIEW:
				params.method = PageStudio.ACTION.SAVE_BY_VIEW;				
				break;
			case PSSavePage.SAVE.TYPE:
				params.method = PageStudio.ACTION.SAVE_BY_TYPE;
				break;
		}
		
		new PageStudio.Request(PageStudio.URL.SAVE_LAYOUT, { 
			method: "post", parameters: params,
			onSuccess: this.onSuccess.bindAsEventListener(this),
			onFailure: this.onFailure.bindAsEventListener(this),
			onComplete: this.onComplete.bindAsEventListener(this)
		});
	},
	
	enableStylesPreview: function() {
		this.previewingStyles = PageStudio.getPreviewCellStyles();
		if(!this.previewingStyles) {
			PageStudio.setPreviewCellStyles(true);
		}
	},
	
	onSuccess: function(t) {	
		PageStudio.setDirty(false);
		if(this.callback) this.callback(true);
	},
	
	onFailure: function(t) {
		PageStudio.displayExceptionAlert(PageStudio.locale.get("ps_save_exception"), t);
		if(this.callback) this.callback(false);
	},
	
	onComplete: function(t) {
		if(!this.previewingStyles) {
			PageStudio.setPreviewCellStyles(false);
		}		
	}
});

/*
 * Converts HTML DOM to Page Studio layout XML.
 */
var PSLayoutParser = Class.create({
	//Complete parameter determines whether or not to include cell items
	initialize: function(complete, stripIDs) {
		this.complete = complete;
		this.stripIDs = stripIDs == true;
	},
	/*
	 * creates a string template for an XML element in the format
	 * <tagName param1key="param1value" ... paramXkey="paramXvalue">#{children}</tagName>
	 * the string can then be interpolated with it's children or a blank object
	 */
	createXMLElement: function(tagName, attributes) {
		var attributeArray = new Array();
		$H(attributes).each(function(param) {
			attributeArray.push("#{key}='#{value}'".interpolate({ "key": param.key, "value": param.value }));
		});
		return "<#{tagName} #{attributes}>\\#{children}</#{tagName}>".interpolate({ "tagName": tagName, "attributes": attributeArray.join(" ") });
	},
	
	createXML: function(areas) {
		//return this.areas.collect(this.createAreaXML, this).join("");
		return this.createXMLElement("page").interpolate({
			"children": areas.collect(this.createAreaXML, this).join("")
		});
	},
	
	createAreaXML: function(area) {
		var cells = area.select("."+PageStudio.CLASS.CELL);
		var attributes = this.complete ? { "id": area.id } : {};
		return this.createXMLElement("area", attributes).interpolate({
			"children": cells.collect(this.createCellXML, this).join("")
		});
	},
	
	createCellXML: function(cell) {
		var style = "width: #{width};".interpolate({ "width": cell.getStyle("width") });
		if (style == null) {
			style = "";
		}
				
		var attributes = {
			"id": this.stripIDs ? "" : cell.readAttribute("id"),
			"style": style,
			//Get any class that has been added but ignore PageStudio classes.
			"class": cell.PSObj ? cell.PSObj.classes.join(" ") : ""
		};
		
		var cellItems = cell.select("."+PageStudio.CLASS.CELL_ITEM);
		return this.createXMLElement("overlay", attributes).interpolate({
			"children": this.complete ? this.createXMLElement("fields", {}).interpolate({
				"children": cellItems.collect(this.createCellItemXML, this).join(" ")
			}) : ""
		});
	},
	
	createCellItemXML: function(cellItem) {
		var attributes = {
			"id": cellItem.readAttribute("id"),
			"type": "Field"			
		};
		//if it is a component field, add the component id attribute
		var cid = cellItem.readAttribute("cid");
		if(cid) {
			attributes.type="Component";
			var component = PageStudio.components.get(cid);
			attributes.cid = cid;
			attributes.location = component.location;
		}
		else {
			attributes.name = cellItem.readAttribute("name");
			//if it's an image field, add the style attribute
			if (cellItem.hasClassName(PageStudio.CLASS.IMAGE)) {
				var img = cellItem.select("img")[0];
				if (img) {
					var style = img.readAttribute("style");
					attributes.imgStyle = style != null ? style : "";
				}
			}
		}
		return this.createXMLElement("field", attributes).interpolate({ "children": "" });
	}
});

PSSavePage.SAVE = {
	VIEW: 0,
	TYPE: 1
};

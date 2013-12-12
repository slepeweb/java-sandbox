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

var PSLoadPage = Class.create({
	initialize: function(body) {
		this.body = body;
		//The components may not have been loaded yet so we check and if they are not ready, 
		//we wait for the component loaded event.
		if (PageStudio.components) {
			this.doLoad();
		}
		else {
			this.onComponentsLoadedListener = this.onComponentsLoaded.bindAsEventListener(this);
			document.observe(PageStudio.EVENT.COMPONENT_JSON_LOADED, this.onComponentsLoadedListener);
		}
	},
	
	onComponentsLoaded: function(e) {
		document.stopObserving(PageStudio.EVENT.COMPONENT_JSON_LOADED, this.onComponentsLoadedListener);
		this.doLoad();
	},
	
	doLoad: function() {
		var cells = this.body.select("." + PageStudio.CLASS.CELL);
		if(cells.length > 0) {
		  cells.each(this.createCell.bind(this));
		  cells[0].PSObj.select();
		}
		
		//Subscribe any other fields on the page so they are updated
		//if modified by the PS user.
		$$("span[mstagname='itemfield']").each(function(instance) {
			if (!instance.hasClassName("page-studio-insitu-field")) {
				var name = instance.readAttribute("fieldname");
				PageStudio.Data.subscribeByName(instance, name);
			}
		});
		
		this.body.fire(PageStudio.EVENT.PAGE_LOAD_COMPLETED);
	},
	
	createCell: function(cell) {
		new PSCell({ element: cell });
		var cellItems = cell.select("." + PageStudio.CLASS.CELL_ITEM);
		cellItems.each(this.createCellItem);
	},
	
	createCellItem: function(item) {
		//If the item already existed on the page, we can just refresh it.
		if(item.PSObj)
		{
			item.PSObj.refresh();
		}
		else {
			if (item.hasClassName(PageStudio.CLASS.IMAGE)) {
				new PSImage({ element: item });
			}		
			else if (item.hasClassName(PageStudio.CLASS.FIELD)) {
				new PSField({ element: item });
			}
			else if (item.hasClassName(PageStudio.CLASS.COMPONENT)){
				new PSComponent({ element: item });
			}
		}
	}
});

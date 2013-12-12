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

var PageStudio = {
	ROOT: "/",
	
	PROXY_PARAMS: {},
	
	URL: {
		SAVE_LAYOUT: "ps-save-overlay",
		COMPONENT_FACTORY: "ps-component-factory",
		LAYOUT_DESIGNER: "ps-layout-designer"
	},
	
	ACTION: {
		COPY_COMPONENT: "copyComponent",
		GET_EXTERNAL_SCRIPT: "getExternalScript",
		GET_COMPONENT: "getComponent",
		GET_COMPONENT_LIST: "getComponentListJSON",
		GET_CONFIGURATOR_DETAILS: "getConfiguratorJSON",
		GET_IMAGE_LIST: "getImageListJSON",
		GET_STYLE_LIST: "getStyleListJSON",
		GET_LAYOUTS_LIST: "getLayoutsListJSON",
		SAVE_BY_TYPE: "save-by-type",
		SAVE_BY_VIEW: "save-by-view",
		SAVE_COMPONENT_CONFIGURATION: "storeProperties",
		SAVE_LAYOUT: "addSiteLayout",
		REMOVE_LAYOUT: "removeSiteLayout",
		EDIT_LAYOUT: "editSiteLayout"
	},
	
	CLASS: {
		BODY: "ps_body",
		CELL: "ps_cell",
		CELL_ITEM: "ps_cell_item",
		CELL_HANDLE: "ps_cell_handle",
		FIELD: "ps_field",
		FIELD_HANDLE: "ps_field_handle",
		IMAGE: "ps_image",
		COMPONENT: "ps_component",
		WRAPPER: "ps_wrapper",
		DRAG_WRAPPER: "ps_drag_wrapper",
		RESET: "ps_reset",
		HIDE: "ps_hide",
		LOADING: "ps_loading",
		ERROR: "ps_error",
		INVALID: "ps_invalid",
		TOOLBAR: "ps_toolbar",
		UI_CONTAINER: "ps_ui_container"
	},
	
	EVENT: {
		ADD_COMPONENT_CLICKED: "pagestudio:addComponentClicked",
		ADD_IMAGE_CLICKED: "pagestudio:addImageClicked",
		BROWSER_LOADED: "pagestudio:browserLoaded",
		CANCEL_CONFIGURATION_CLICKED: "pagestudio:cancelConfigurationClicked",
		CELL_ADDED: "pagestudio:cellAdded",
		CELL_ITEM_ADDED: "pagestudio:cellItemAdded",
		CELL_SELECTED: "pagestudio:cellSelected",
		CELL_ITEM_SELECTED: "pagestudio:cellItemSelected",
		CONFIGURATION_LOADED: "pagestudio:configurationLoaded",
		COMPONENTS_UPDATED: "pagestudio:componentsUpdated",
		COMPONENT_JSON_LOADING: "pagestudio:componentJSONLoading",
		COMPONENT_JSON_LOADED: "pagestudio:componentJSONLoaded",
		DELETE_CELL_CLICKED: "pagestudio:deleteCellClicked",
		DELETE_CELL_ITEM_CLICKED: "pagestudio:deleteCellItemClicked",
		TOOL_CLOSED: "pagestudio:toolClosed",
		DISPLAY_CELL_PROPERTIES_CLICKED: "pagestudio:displayCellPropertiesClicked",
		DISPLAY_CHANGE_LAYOUT_CLICKED: "pagestudio:displayChangeLayoutClicked",
		DISPLAY_SELECT_COMPONENT_CLICKED: "pagestudio:displaySelectComponentClicked",
		DISPLAY_INSERT_MARKUP_COMPONENT_CLICKED: "pagestudio:displayInsertMarkupComponentClicked",
		LOADED: "pagestudio:loaded",
		LAYOUT_CHANGED: "pagestudio:layoutChanged",
		LAYOUT_JSON_LOADING: "pagestudio:layoutJSONLoading",
		LAYOUT_JSON_LOADED: "pagestudio:layoutJSONLoaded",
		LAYOUT_ADDED: "pagestudio:layoutAdded",
		LAYOUT_REMOVED: "pagestudio:layoutRemoved",
		MODAL_DIALOG_OPENED: "pagestudio:modalDialogOpened",
		MODAL_DIALOG_CLOSED: "pagestudio:modalDialogClosed",
		PAGE_LOAD_COMPLETED: "pagestudio:pageLoadCompleted",
		REMOVED: "pagestudio:removed",
		RESIZE: "pagestudio:resize",
		RESIZE_START: "pagestudio:resizeStart",
		RESIZE_END: "pagestudio:resizeEnd",
		SORTABILITY_STARTED: "pagestudio:sortabilityStarted",
		SUBMIT_CONFIGURATION_CLICKED_IFRAME: "pagestudio:submitConfigurationClickedIFrame",
		SUBMIT_CONFIGURATION_CLICKED: "pagestudio:submitConfigurationClicked",
		FOCUS: "pagestudio:focus",
		BLUR: "pagestudio:blur"
	},

	//Sniffs for contributor client and sets PageStudio.contributor to 'MSC' or 'MWC'.
	contributor: (function() {
		var isMSC;
		try {
			isMSC = (typeof(external.RegisterPageStudioEditor) != "undefined");
		}
		catch(e) {
			isMSC = false;
		}
		if (isMSC) {
			return "MSC";
		}
		else if(window.top.PageStudioEditor) {
			return "MWC";
		}
		else return "Unknown";
	})(),

	currentTheme: "",

	Data: new (function() {
		var fields = new Hash();
		var ids = new Hash();
		
		this.addField = function(field) {
			fields.set(field.id, field);
			ids.set(field.name, field.id);
			Event.fire(document, "pagestudio:"+field.id+"FieldUpdated", { "updater": null });
		};
		
		this.getField = function(fieldID) {
			return fields.get(fieldID);
		};
		
		this.getFieldId = function(name) {
			return ids.get(name);
		};
		
		this.getFieldNames = function() {
			return fields.keys();
		};
		
		function getFieldValue(fieldID) {
			var field = fields.get(fieldID);
			return field ? field.value : "";
		}
		
		function setFieldValue(fieldID, value, reference) {
			var field = fields.get(fieldID);
			if(field) {
				field.value = value;
				fields.set(fieldID, field);
				Event.fire(document, "pagestudio:"+fieldID+"FieldUpdated", { "updater": reference });
			}
		}
		
		var updater = function(target, id) {
			var element = target;
			var fieldID = id;
			var updaterID = id + Math.random();
			
			function updateComponents() {
				
			}
			
			function update() {
				detachAllEvents();
				element.update(getFieldValue(fieldID));
				updateComponents();
				attachAllEvents();
			}
			
			function onFieldUpdated(e) {
				if(e.memo.updater != updaterID) {
					update();
				}
				else {
					attachEvents();
				}
			}
			
			function onElementEdited(e) {
				detachEvents();
				setFieldValue(fieldID, element.innerHTML, updaterID);
			}
			
			var attachEvents, detachEvents;
			if(PageStudio.mutationEventsSupported) {
				attachEvents = function() {
					element.observe("DOMSubtreeModified", onElementEdited);
					element.observe(PSComponent.EVENT.MARKUP_COMPONENT_LOADED, onElementEdited);
				};
				detachEvents = function() {
					element.stopObserving("DOMSubtreeModified", onElementEdited);
					element.stopObserving(PSComponent.EVENT.MARKUP_COMPONENT_LOADED, onElementEdited);
				};
			}
			else {
				attachEvents = function() {
					element.observe("keyup", onElementEdited);
					element.observe("paste", onElementEdited);
					element.observe("dragend", onElementEdited);
					element.observe(PSComponent.EVENT.MARKUP_COMPONENT_LOADED, onElementEdited);
				};
				detachEvents = function() {
					element.stopObserving("keyup", onElementEdited);
					element.stopObserving("paste", onElementEdited);
					element.stopObserving("dragend", onElementEdited);
					element.stopObserving(PSComponent.EVENT.MARKUP_COMPONENT_LOADED, onElementEdited);
				};
			}
			
			function attachAllEvents() {
				attachEvents();
				document.observe("pagestudio:"+fieldID+"FieldUpdated", onFieldUpdated);
				document.observe(PageStudio.EVENT.COMPONENTS_UPDATED, updateComponents);
			};
			
			function detachAllEvents() {
				detachEvents();
				document.stopObserving("pagestudio:"+fieldID+"FieldUpdated", onFieldUpdated);
				document.stopObserving(PageStudio.EVENT.COMPONENTS_UPDATED, updateComponents);
			};
			
			this.remove = function() {
				detachAllEvents();
			};
			
			update();
		};
		
		this.getFieldValue = getFieldValue;
		this.setFieldValue = setFieldValue;
		this.subscribe = function(element, id) {
			if (!element.retrieve("updater")) {
				element.store("updater", new updater(element, id));
			}
		};
		
		this.unsubscribe = function(element) {
			var updater = element.retrieve("updater");
			if (updater) {
				updater.remove();
				element.getStorage().unset("updater");
			}
		};
		
		this.subscribeByName = function(element, fieldName) {
			var id = ids.get(fieldName);
			PageStudio.Data.subscribe(element, id);
		};
	})(),
	
	isDirty: false,
	
	instance: null,
	
	Create: function(params) {
		if(PageStudio.instance == null) PageStudio.instance = new PageStudio.App(params);
		else PageStudio.instance.addArea(params.id);
		return PageStudio.instance;
	},
	
	App: Class.create({
		initialize: function(params) {
			PageStudio.loaded = false;
			PageStudio.previewCellStyles = true;
			
			if(params.fields) {
				//if we have just arrived from forms view,
				//the fields may have already been populated (with more
				//up to date data).
				if(PageStudio.Data.getFieldNames.length == 0) {
					params.fields.values().each(function(entry){
						PageStudio.Data.addField(entry);
					});
				}
			}
			this.areas = new Array();
			this.initParams(params);
			this.registerPageStudioEditor();
			this.documentLoadListener = this.loadPageStudio.bind(this);
			Event.observe(document, "dom:loaded", this.documentLoadListener);
		},
		
		initParams: function(params) {
			PageStudio.locale = new PSLocale(params.language);
			this.addArea(params.id);
		},
		
		addArea: function(id) {
			this.areas.push(new PSArea({ "element": $(id) }));
		},
		
		loadPageStudio: function() {
			Event.stopObserving(document, "dom:loaded", this.loadListener);
			
			if(PageStudio.contributor == "MSC") {
				external.RegisterPageStudioEditor(PageStudio.LAYOUTS_FOLDER);
				var styleInfo = external.GetVisualStyleInformation();
				if(styleInfo) {
					PageStudio.setVisualStyle(styleInfo.evalJSON());
				}
			}
			
			this.initConstants();
			
			this.createListeners();
			this.loadUI();
			this.attachEvents();
			this.loadComponents();
			this.loadLayouts();
		},
		
		initConstants: function() {
			//Prepend the root to each servlet URL.
			var updateURLCollection = function(collection) {
				for (var url in collection) {
					collection[url] = PageStudio.ROOT + collection[url];
				}
			};
			updateURLCollection(PageStudio.URL);
			updateURLCollection(PSSelectComponentTool.ICON);
		},
		
		loadUI: function() {
			PageStudio.uiContainer = new Element("div", { "id": PageStudio.CLASS.UI_CONTAINER, "className": PageStudio.CLASS.RESET });
			PageStudio.uiContainer.addClassName(PageStudio.currentTheme);
			this.areas[0].element.insert({ "before": PageStudio.uiContainer });
			this.ui = {
				dialogTracker: new PSDialogTracker(PageStudio.uiContainer),
				selectComponent: new PSSelectComponentDialog({ "parent": PageStudio.uiContainer, "label": PageStudio.locale.get("Select Component"), "width": "514px" }),
				mainTab: new PSMainTab({ "target": PageStudio.uiContainer }),
				cellProperties: new PSCellPropertiesDialog({ "parent": PageStudio.uiContainer, "label": PageStudio.locale.get("Cell Properties"), "width": "356px" }),
				changeLayout: new PSChangeLayoutDialog({ "parent": PageStudio.uiContainer, "label": PageStudio.locale.get("Change Layout"), "width": "453px" }),
				insertMarkupComponent: new PSInsertMarkupComponentDialog({ "parent": PageStudio.uiContainer, "label": PageStudio.locale.get("Insert Mark-up Component"), "width": "657px" })
			};
			document.observe(PageStudio.EVENT.DISPLAY_SELECT_COMPONENT_CLICKED, this.listeners.onDisplaySelectComponentClicked);
			document.observe(PageStudio.EVENT.DISPLAY_CELL_PROPERTIES_CLICKED, this.listeners.onDisplayCellPropertiesClicked);
			document.observe(PageStudio.EVENT.DISPLAY_INSERT_MARKUP_COMPONENT_CLICKED, this.listeners.onDisplayInsertMarkupComponentClicked);
			document.observe(PageStudio.EVENT.DISPLAY_CHANGE_LAYOUT_CLICKED, this.listeners.onDisplayChangeLayoutClicked);
			PSEdit.attachEvents();
		},
		
		loadComponents: function() {
			new PSLoadComponents();
		},
		
		loadLayouts: function() {
			new PSLoadLayouts();
		},
		
		//registers page studio with the contributor
		registerPageStudioEditor: function() {
			//TODO: is config open
			window.isConfigOpen = Prototype.emptyFunction;
			//TODO: add field to overlay 
			window.addFieldToOverlay = Prototype.emptyFunction;
			window.updateComponents = this.updateComponents.bind(this);
			window.hidePageStudio = this.hide.bind(this);
			window.showPageStudio = this.show.bind(this);
			window.addInline = this.addInline.bind(this);
			window.showInsertMarkup = this.onDisplayInsertMarkupComponentClicked.bindAsEventListener(this);
			
			if(PageStudio.contributor == "MSC") {
				external.RegisterPageStudioEditor(PageStudio.LAYOUTS_FOLDER);
			}
			else if(PageStudio.contributor == "MWC") {
				window.top.PageStudioEditor.register(PageStudio.LAYOUTS_FOLDER);
			}
			
			window.resetPrototype = function() {
			};
		},
		
		//event listeners are stored so they can be disabled later.
		createListeners: function() {
			this.listeners = {
				onDisplaySelectComponentClicked: this.onDisplaySelectComponentClicked.bindAsEventListener(this),
				onDisplayCellPropertiesClicked: this.onDisplayCellPropertiesClicked.bindAsEventListener(this),
				onDisplayChangeLayoutClicked: this.onDisplayChangeLayoutClicked.bindAsEventListener(this),
				onDisplayInsertMarkupComponentClicked: this.onDisplayInsertMarkupComponentClicked.bindAsEventListener(this)
			};
		},
		
		attachEvents: function() {
			document.observe(PSArea.EVENT.PAGE_AREA_LOADED, this.onPageAreaLoaded.bindAsEventListener(this));
			document.observe(PSArea.EVENT.PAGE_AREA_SELECTED, this.onPageAreaSelected.bindAsEventListener(this));
		},
		
		onPageAreaLoaded: function(e) {
			//If all the Page Studio areas are loaded, fire the areas loaded event.
			if(this.areas.all(function(area) { return area.loaded; })) {
				this.areas[0].select();//select the first area by default;
				PageStudio.loaded = true;
				document.fire(PageStudio.EVENT.LOADED);
			}
		},
		
		onPageAreaSelected: function(e) {
			PageStudio.selectedArea = e.memo.area;
		},
		
		updateComponents: function(fields, images) {
			this.updateFields(fields);
			this.updateInlineImages(images);
			document.fire(PageStudio.EVENT.COMPONENTS_UPDATED);
		},
		
		updateFields: function(fields) {
			$H(fields).values().each(function(field) {
				PageStudio.Data.setFieldValue(field.id, field.value);
			});
		},
		
		updateInlineImages: function(images) {
			function doUpdate(e) {
				Event.stopObserving(document, PageStudio.EVENT.LOADED, doUpdate);
				images = $H(images);
				//add images if they aren't already present
				//we don't want to overwrite because we'd have to reload the image info.
				images.values().each(function(image) {
					PageStudio.addNewInline(image);
				});
				//remove images
				PageStudio.images.values().each(function(image) {
					if(!images.keys().member(image.id)) {
						PageStudio.images.unset(image.id);
					}
				});
			}
			//Check the images have been loaded before attempting to update them.
			if(PageStudio.images) {
				doUpdate();
			}
			else {
				Event.observe(document, PageStudio.EVENT.COMPONENT_JSON_LOADED, doUpdate);
			}
		},
		
		addInline: function(itemKey, typeKey, name, url, mediatype, alt) {
			var imageJSON = { "id":  itemKey , "name": name, "src": url, "type": "Image", "mediatype": mediatype, "alt": alt};
			PageStudio.images.set(itemKey,imageJSON);
			PSImageInfoHelper.loadInfo(imageJSON);
			if(typeof(parent.incontextView) != "undefined" && parent.incontextView == "incontext"){
				window.top.inLineImage(itemKey, typeKey, name, url);
				window.top.addUnsavedInline(itemKey, typeKey, name, url);
			}
			document.fire(PageStudio.EVENT.COMPONENTS_UPDATED);
		},	
		
		getSaveCallback: function(internalCallback)
		{
			var callback;
			var doNotify;
			if(PageStudio.contributor == "MSC") {
				doNotify = function(success) {
					try {
						external.NotifyPageStudioSaveComplete(success);
					}
					catch(e) { }
				};
			}
			else if(PageStudio.contributor == "MWC") {
				doNotify = function(success) {
					try {
						window.top.PageStudioEditor.notifySaveComplete(success);
					}
					catch(e) { }
				};
			}
			
			if(doNotify) {
				if(internalCallback == null) {
					callback = doNotify;
				}
				else {
					callback = function(success) {
						internalCallback(success);
						doNotify(success);
					};
				}
			}
			else callback = internalCallback;
			
			return callback;
		},
		
		
		//gets a list of overlays on the page, then a list of fields for each overlay and builds an xml document
		//callback is a function that will be called when the save completes.
		saveView: function(internalCallback) {
			var callback = this.getSaveCallback(internalCallback);
			new PSSavePage(this.areas, PSSavePage.SAVE.VIEW, callback);
		},
		
		saveType: function(internalCallback) {
			var callback = this.getSaveCallback(internalCallback);
			new PSSavePage(this.areas, PSSavePage.SAVE.TYPE, callback);
		},
		
		clearLayout: function() {
			this.areas.invoke("clear");
			try {
				if(PageStudio.contributor == "MSC") {
					external.NotifyPageStudioClearComplete();
				}
				else {
					window.top.PageStudioEditor.notifyClearComplete();
				}
			}
			catch(e)
			{}
		},
		
		hide: function() {
			this.areas.invoke("hide");
		},
		
		show: function() {
			this.areas.invoke("show");
		},
		
		onDisplaySelectComponentClicked: function(e) {
			this.ui.selectComponent.show();
		},
		
		onDisplayCellPropertiesClicked: function(e) {
			this.ui.cellProperties.show();
		},
		
		onDisplayChangeLayoutClicked: function(e) {
			this.ui.changeLayout.show();
		},
		
		onDisplayInsertMarkupComponentClicked: function(e) {
			this.ui.insertMarkupComponent.show();
		}
	}),
	
	
	/* Add a layout to the Page Studio layouts collection and fire an event to notify listeners of the new layout. */
	addLayout: function(name, html, isSiteLayout) {
		var layout = {
			"name": name,
			"html": encodeURIComponent(html),
			"siteLayout": isSiteLayout
		};
		PageStudio.layouts.set(name, layout);
		Event.fire(document, PageStudio.EVENT.LAYOUT_ADDED, layout);
	},
	
	removeLayout: function(name)
	{
		var layout = PageStudio.layouts.unset(name);
		Event.fire(document, PageStudio.EVENT.LAYOUT_REMOVED, layout);
	},
	
	addNewInline: function(image) {
		if (!PageStudio.images.keys().member(image.id)) {
			image.name = decodeURIComponent(image.name);
			PageStudio.images.set(image.id, image);
			PSImageInfoHelper.loadInfo(image);
			document.fire(PageStudio.EVENT.COMPONENTS_UPDATED);
		}
	},
	
	/*
	 * Prevent the contributor from fading a given element.
	 * 
	 * If the madeAsFade function does not exist or fails, we can just replace this function
	 * with the Prototype empty function.
	 */
	preventContributorFade: (function() {
		try {
			if (window.top.markAsFade) {
				return function(element) {
					window.top.markAsFade(element);
				};
			}
			else {
				return Prototype.emptyFunction;
			}
		}
		catch(e){
			return Prototype.emptyFunction;
		}
	})(),
	
	setPreviewCellStyles: function(preview) {
		PageStudio.previewCellStyles = preview;
		Event.fire(document, "pagestudio:previewCellStylesChanged", { "preview": preview });
	},
	
	getPreviewCellStyles: function() {
		return PageStudio.previewCellStyles;
	},
	
	//Sends a message to the current contributor, requesting a data update.
	updateData: function(){
		if(PageStudio.contributor == "MWC" && window.top.updatePageStudio) {
			PageStudio.updateData =  function() {
				//if PS is being reloaded by the MWC after switching views it will need to be updated.
				window.top.updatePageStudio(window, PageStudio);
			};
		}
		else if(PageStudio.contributor == "MSC" && external.UpdatePageStudioComponents) {
			PageStudio.updateData = function() {
				//if PS is being used in the Smart Client, and the default view is forms view, PS may need to update after switching views.
				external.UpdatePageStudioComponents();
			};
		}
		else {
			PageStudio.updateData = Prototype.emptyFunction;
		}
		return PageStudio.updateData();
	},
	
	/* 
	 * Updates a given params JSON to contain the requested item parameters so that any PageStudio
	 * servlets can determine the current requested item if not available from the ms:defineObjects tag
	 */
	getRequestItemParams: function(otherParams) {
		// we need to identify the currently requested item
		var params = {
			"itemKey": PageStudio.ITEM_KEY,
			"viewKey": PageStudio.VIEW_KEY
		};
		if(otherParams) {
			Object.extend(params, otherParams);
		}
		return params;
	},
	
	proxifyURL: function(url) {
		if(PageStudio.PROXY) {
			var index = url.indexOf("/");
			if(index != -1) url = url.substring(index);
			return PageStudio.PROXY + "?url=" + encodeURIComponent(PageStudio.HOST + "/" + url); 
		}
		return url;
	},
	
	reserveLibrary: function() {
		if (jQuery && jQuery.noConflict) {
			jQuery.noConflict();
		}
	},
	
	unreserveLibrary: function() {
		if (jQuery && jQuery.noConflict) {
			$ = jQuery.noConflict();
		}
	},
	
	/*
	 * Removes classes with the Page Studio prefix.
	 */
	stripPageStudioClasses: function(classes) {
		return $w(classes).reject(function(className) {
			return (className.indexOf("ps_") == 0);
		});
	},
	
	getExternalEventTarget: function(event) {
		var target = null;
		if (event.srcElement) {
			target = event.srcElement.ownerDocument.parentWindow.frameElement;
		}
		else if (event.view) {
			target = event.view.frameElement;
		}
		return target;
	},
	
	//Normalises a failed AJAX response.
	getExceptionReport: function(t) {
		if(t.headerJSON && t.headerJSON.exception) {
			return t.headerJSON;
		}
		else {
			return {
				"type": -1,
				"exception": "unknown",
				"message": t.responseText
			};
		}
	},
	
	getExceptionTypeName: function(type) {
		return PageStudio.locale.get(type+"_exception");
	},
	
	displayExceptionAlert: function(message, t) {
		var report = PageStudio.getExceptionReport(t);
		var d = new PSErrorDialog({ "label": "Error", "width": "400px", "report": report, "message": message  });
		d.show();
	},
	
	mutationEventsSupported: (function(){
		var div = document.createElement("div");
		if(div.addEventListener) {
			var handler = function() {
				div.removeEventListener("DOMAttrModified", handler, false);
				div.id = "x";
			};
			div.addEventListener("DOMAttrModified", handler, false);
			div.id = "z";
			return div.id == "x"; 
		}
		else return false;
	})(),
	
	//attempts to detect if an event type is supported by a browser - does not work for mutation events, use mutationEventsSupported instead.
	eventSupported: function(eventName, target) {
		var elementName = null;
		if(Object.isString(target)) {
			elementName = target;
		}
		else if(target.tagName) {
			elementName = target.tagName;
		}
		else {
			elementName = "document";
		}
		
		var result= false;
		
		if(!PageStudio.checkedEvents) PageStudio.checkedEvents = new Hash();
		var eventHash = PageStudio.checkedEvents.get(eventName);
		if(!eventHash) {
			eventHash = new Hash();
			PageStudio.checkedEvents.set(eventName, eventHash);
		}
		
		result = eventHash.get(elementName);
		
		if (result == null) {
			result = false;
			var element;
			if (elementName != "document") {
				element = new Element(elementName);
			}
			else {
				element = document;
			}
			result = (eventName in element);
			if (!result && element.writeAttribute) {
				element.writeAttribute(eventName, "return;");
				result = typeof element[eventName] == "function";
			}
			if (elementName) eventHash.set(elementName, result);
		}
		return result;
	},
	
	setDirty: function(isDirty) {
		PageStudio.isDirty = isDirty;
		var action = null;
		if(isDirty) {
			action = function() {
				return PageStudio.locale.get("confirm_leave_page");
			};
		}
		window.onbeforeunload = action;
	},
	
	setVisualStyle: function(styleData) {
		var setTheme = function(theme) {
			var container = $('ps_ui_container');
			var bodies = $$('.ps_body');
			var tooltips = $$('.ps_reset.ps_tool_tip');
			
			bodies.invoke('removeClassName', PageStudio.currentTheme);
			bodies.invoke('addClassName', theme);
			
			if(container) {
				container.removeClassName(PageStudio.currentTheme);
				container.addClassName(theme);
			}
			if(tooltips.length > 0) {
				tooltips.invoke('removeClassName', PageStudio.currentTheme);
				tooltips.invoke('addClassName', theme);
			}
			
			var iframes = $$('iframe');
			if (iframes.length > 0) {
				iframes.each(function (iframe)
				{
					var iContent = iframe.contentWindow ? iframe.contentWindow : iframe.contentDocument.defaultView;
					var iBody = iContent.window.document.body;
					iBody.className = iBody.className
						.replace(new RegExp("(^|\\s+)" + PageStudio.currentTheme + "(\\s+|$)"), ' ')
						.replace(/^\s+/, '').replace(/\s+$/, '');
					iBody.className += (iBody.className ? ' ' : '') + theme;
				});
			}
			PageStudio.currentTheme = theme;
		};

		switch(styleData.themeId) {
			case 2: setTheme("ps_theme_silver");
				break;
			case 3: setTheme("ps_theme_black");
				break;
			default: setTheme("");
				break;
		}
	},
	
	openItemPicker: (function() {
		if(window.top.openItemPicker) {
			return function(acceptableTypes, callback) {
				window.top.openItemPicker(acceptableTypes, callback);
			};
		}
		else if (typeof(external.RegisterPageStudioEditor) != "undefined") {
			return function(acceptableTypes, callback) {
				var itemData = external.LaunchRepositoryBrowser(acceptableTypes);
				if(itemData != null) itemData = eval(itemData.stripScripts());
				callback(itemData);
			};
		}
		else return function() { alert("The repository browser is not currently available."); return null; };
	})(),
	
	external: {
		saveView: function(callback) {
			PageStudio.instance.saveView(callback);
		},

		saveType: function(callback){
			PageStudio.instance.saveType(callback);
		}, 
		
		undo: function()
		{
			PSEdit.undo();
		},
		
		redo: function()
		{
			PSEdit.redo();
		},
		
		copy: function()
		{
			PSEdit.copy();
		},
		
		paste: function()
		{
			PSEdit.paste();
		},
		
		clearLayout: function()
		{
			PageStudio.instance.clearLayout();
		},
		
		submitConfiguration: function(window) {
			var target = window.frameElement;
			if (target != null) {
				$(target).fire(PageStudio.EVENT.SUBMIT_CONFIGURATION_CLICKED_IFRAME);
			}
		},
		
		cancelConfiguration: function(e) {
			Element.fire(document, PageStudio.EVENT.CANCEL_CONFIGURATION_CLICKED);
		},
		
		setVisualStyle: function(styleData) {
			PageStudio.setVisualStyle(styleData);
		}
	}
};

PageStudio.Request = Class.create(Ajax.Request, {
	request: function($super, url) {
		if(PageStudio.PROXY) { 
			Ajax.getTransport = function() {
				return Try.these(
						function() {return new ActiveXObject('Msxml2.XMLHTTP');},
						function() {return new ActiveXObject('Microsoft.XMLHTTP');},
						function() {return new XMLHttpRequest();}
						) || false;
			};
			if(PageStudio.CTX) this.options.parameters.ctx = PageStudio.CTX;
			if(this.options.method == 'get') {
				var params = Object.clone(this.options.parameters);
				if (params = Object.toQueryString(params)) {
					url += "?" + params;
				}
				this.options.parameters = { "url": PageStudio.HOST + url };
			}
			else if(this.options.method == 'post') {
				this.options.parameters.url = PageStudio.HOST + url;
			}
			
			url = PageStudio.PROXY;
		}
		$super(url);
	}
});

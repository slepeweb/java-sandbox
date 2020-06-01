_cms.links = {
	behaviour: {},
	refresh: {},
	addlink_dialog: {
		obj: null
	},
	addlink_warning_dialog: {
		obj: null
	},
	define: {},
	selrel: {
		LINKTYPE_SELECT: "select[name='linktype']",
		LINKNAME_SELECT: "select[name='linkname']",
		LINKDATA_INPUT: "input[name='linkdata']",
		STATE_INPUT: "input[name='state']",
		CHILDID_INPUT: "input[name='childId']",
		LINKID_INPUT: "input[name='linkId']",
	},
	sel: {
		ADD_LINK_CONTAINER: "#addlinkdiv",
		ADD_LINK_BUTTON: "#addlink-button",
		SAVE_LINKS_BUTTON: "#savelinks-button",
		USE_LINK_BUTTON: "#use-link-button",
		CANCEL_LINK_BUTTON: "#cancel-use-link-button",
		ITEM_PICKER: "#addlinkdiv i.itempicker",
		ALL_SELECTS: "#addlinkdiv select",
		SORTABLE_LINKS_CONTAINER: "#sortable-links",
		EDIT_LINK_BUTTONS: ".edit-link",
		REMOVE_LINK_BUTTONS: ".remove-link",
		LINKTO_BUTTONS: ".link-linker",
		LINK_TARGET_IDENTIFIER: "#link-target-identifier",
		INCOMPLETE_LINK_DIALOG: "#dialog-choose-linktype",
	}
};

_cms.links.sel.LINKTYPE_SELECT = _cms.links.sel.ADD_LINK_CONTAINER + " " + _cms.links.selrel.LINKTYPE_SELECT;
_cms.links.sel.LINKNAME_SELECT = _cms.links.sel.ADD_LINK_CONTAINER + " " + _cms.links.selrel.LINKNAME_SELECT;
_cms.links.sel.CHILDID_INPUT = _cms.links.sel.ADD_LINK_CONTAINER + " " + _cms.links.selrel.CHILDID_INPUT;

_cms.links.show_addlink_form = function(data) {
	if (data) {
		_cms.links.setLinkForm(data);
	}	
	_cms.links.openDialog(_cms.links.addlink_dialog);
}

_cms.links.behaviour.itempicker = function() {
	$(_cms.links.sel.ITEM_PICKER).click(function() {
		_cms.leftnav.mode = "link";
		_cms.links.openDialog(_cms.leftnav.dialog);
	});
}

_cms.links.use_form_data = function() {
	// TODO: state property is redundant!
	
	var div = $(_cms.links.sel.ADD_LINK_CONTAINER);
	var formData = {};
	formData.type = div.find(_cms.links.selrel.LINKTYPE_SELECT).val();
	formData.name = div.find(_cms.links.selrel.LINKNAME_SELECT).val();
	formData.data = div.find(_cms.links.selrel.LINKDATA_INPUT).val();
	formData.state = div.find(_cms.links.selrel.STATE_INPUT).val();
	formData.childId = div.find(_cms.links.selrel.CHILDID_INPUT).val();
	var linkIdStr = div.find(_cms.links.selrel.LINKID_INPUT).val();
	formData.ordering = parseInt(linkIdStr, 10);
	
	/*
	 * We should only have got here if: 
	 * formData.type != 'unknown' && formData.name != 'unknown' && formData.childId > 0
	*/
	_cms.links.useLink(formData);
	_cms.links.activateSaveButton(true);
}

_cms.links.behaviour.changetype = function() {
	// Re-populate linkname options when link type is selected
	$(_cms.links.sel.LINKTYPE_SELECT).change(function(e) {
		_cms.links.repopulateLinkNameDropdown($(this).val());
	});
}

_cms.links.behaviour.add = function() {
	// Show link addition form when 'Add link' button is clicked
	$(_cms.links.sel.ADD_LINK_BUTTON).click(function(e) {
		_cms.links.show_addlink_form(["-1", "unknown", "unknown", "", "-1", "0"]);
	});
}

_cms.links.behaviour.save = function(nodeKey) {
	// Add behaviour to 'Save links' button 
	$(_cms.links.sel.SAVE_LINKS_BUTTON).click(function(e) {
		var links = _cms.links.identifyHiddenLinkDataList($(_cms.sortableLinksSelector));
		
		// Remove the 'span' property from each object
		for (var i = 0; i < links.length; i++) {
			delete links[i].span;
			links[i].data = encodeURIComponent(links[i].data);
		}
		
		// Ajax call to save links to db 
		$.ajax(_cms.ctx + "/rest/links/" + nodeKey + "/save", {
			type: "POST",
			cache: false,
			data: JSON.stringify(links), 
			contentType: "application/json; charset=utf-8",
			dataType: "json",
			processData: false,
			success: function(obj, status, z) {
				_cms.support.flashMessage(obj);
				_cms.links.refresh.tab(nodeKey);
				_cms.links.activateSaveButton(false);

				
				if (! obj.error && obj.data) {
					// Need to refresh the FancyTree,
					// since one or more shortcuts have been added/removed
					_cms.leftnav.refreshShortcuts("" + _cms.editingItemId, obj.data);
				}
			},
			error: function(obj, status, z) {
				_cms.support.serverError();
			},
		});
	});
}

_cms.links.behaviour.remove = function() {
	// Add behaviour to 'Remove links' button 
	$(_cms.links.sel.REMOVE_LINK_BUTTONS).off().click(function(e) {
		$(this).parent().parent().remove();
		_cms.links.activateSaveButton(true);
	});
}

_cms.links.behaviour.edit = function() {
	// Open form to edit an existing link
	$(_cms.links.sel.EDIT_LINK_BUTTONS).off().click(function() {
		var parent = $(this).parent();
		var span = parent.find("span.hide");
		var dataparts = span.html().split("\|");
		
		// Append an element to the array identifying the name of the child link.
		// This data is only available in the array for editing existing links, and NOT
		// for adding new ones.
		var grandparent = parent.parent();
		dataparts.push(grandparent.find("span.link-identifier").html());

		_cms.links.repopulateLinkNameDropdown(dataparts[1], dataparts[2]);
		_cms.links.show_addlink_form(dataparts);
	});
}

_cms.links.behaviour.navigate = function() {
	// Add behaviour for when a link (in the links editor) is clicked 
	$(_cms.links.sel.LINKTO_BUTTONS).off().click(function(e) {
		var key = $(this).attr("data-id");
		var node = _cms.leftnav.tree.getNodeByKey(key);
		
		if (node) {
			// This attribute setting changes the active tab for when node activation completes
			$("li.ui-tabs-active").attr("aria-controls", "core-tab");
			_cms.leftnav.tree.activateKey(node.key);
		}
		else {
			// This node hasn't been loaded yet - ask the server for the breadcrumb trail
			var fn = function() {
				// This attribute setting changes the active tab for when node activation completes
				$("li.ui-tabs-active").attr("aria-controls", "core-tab");
			}
			
			if (! _cms.leftnav.loadBreadcrumbs(key, fn)) {
				_cms.support.flashMessage(_cms.support.toStatus(false, "Failed to retrieve breadcrumb trail"));
			}
		}
	});
}

_cms.sortableLinksSelector = _cms.links.sel.SORTABLE_LINKS_CONTAINER + " div.sortable-link";

_cms.links.identifyHiddenLinkDataList = function(sortableLinks) {
	var links = [];
	var span;
	
	sortableLinks.find("div.right span").each(function(index, span) {
		links.push(_cms.links.identifyHiddenLinkData4Span(span));
	});
	
	return links;
};

_cms.links.identifyHiddenLinkData4Span = function(span) {
	var	parts = span.innerHTML.split("\|");
	var	obj = {
		span: span,
		childId: parts[0],
		type: parts[1],
		name: parts[2],
		data: decodeURIComponent(parts[3]),
		ordering: parseInt(parts[4], 10),
		state: parts[5]
	};
	
	return obj;
};

_cms.links.formatHiddenLinkData = function(d) {
	var dl = "|";
	return d.childId + dl + d.type + dl + d.name + dl + d.data + dl + d.ordering + dl + d.state;
};

// Use the link form data to add a new link for saving, or edit an existing link
_cms.links.useLink = function(formData) {
	
	var sortableLinksContainer = $("div" + _cms.links.sel.SORTABLE_LINKS_CONTAINER);
	var sortableLinks = $(_cms.sortableLinksSelector);

	// Is this link already in the list?
	var target = null;	
	var links = _cms.links.identifyHiddenLinkDataList(sortableLinks);
	
	for (var i = 0; i < links.length; i++) {
		if (links[i].childId == formData.childId) {
			target = links[i];
			break;
		}
	}
	
	if (target) {
		var span = target.span;
		span.innerHTML = _cms.links.formatHiddenLinkData(formData);
		
		var identifier = $(span).parent().parent().find(".link-identifier");
		
		_cms.links.getItemNameAnd(formData.childId, _cms.links.updateLink, 
				span, identifier, formData);
	}
	else {
		// This link is not in the list - clone the template, and append the clone to the end of the list
		var linkHtml = $("#link-template>div").clone(true);
		
		// Need an ajax call to identify the name of the child item, for list display purposes only
		_cms.links.getItemNameAnd(formData.childId, _cms.links.insertClonedLink, sortableLinksContainer, 
				linkHtml, formData);		
	}
};

_cms.links.getItemNameAnd = function(childId, fn, param1, param2, param3, param4) {
	$.ajax(_cms.ctx + "/rest/item/" + childId + "/name", {
		type: "POST",
		cache: false,
		dataType: "text",
		success: function(itemName, status, z) {
			fn(itemName, param1, param2, param3, param4);
		}
	});
}

_cms.links.insertClonedLink = function(itemName, sortableLinksContainer, jqe, formData) {
	jqe.find("div.left span.link-identifier").html(_cms.links.formatLinkIdentifier(formData, itemName));
	jqe.find("div.right button.link-linker").attr("data-id", formData.childId);
	jqe.find("div.right span.hide").html(_cms.links.formatHiddenLinkData(formData));				
	jqe.appendTo(sortableLinksContainer);					
}



_cms.links.updateLink = function(itemName, span, identifier, formData) {
	span.innerHTML = _cms.links.formatHiddenLinkData(formData);
	identifier.html(_cms.links.formatLinkIdentifier(formData, itemName));
}

_cms.links.formatLinkIdentifier = function(formData, itemName) {
	return formData.type + " (" + formData.name + "): " + itemName;
}

_cms.links.setLinkForm = function(data) {
	var form = $(_cms.links.sel.ADD_LINK_CONTAINER);	
	form.find(_cms.links.selrel.CHILDID_INPUT).val(data[0]);
	form.find(_cms.links.selrel.LINKTYPE_SELECT).val(data[1]);
	form.find(_cms.links.selrel.LINKNAME_SELECT).val(data[2]);
	form.find(_cms.links.selrel.LINKDATA_INPUT).val(data[3]);
	form.find(_cms.links.selrel.LINKID_INPUT).val(data[4]);
	form.find(_cms.links.selrel.STATE_INPUT).val(data[5]);	
	
	if (data[0] == "-1") {
		$(_cms.links.sel.LINK_TARGET_IDENTIFIER).empty();
	}
	else if (data.length == 7) {
		var idx = data[6].indexOf("&gt;");
		if (idx > -1) {
			var name = data[6].substring(idx + 5);
			$(_cms.links.sel.LINK_TARGET_IDENTIFIER).html("'" + name + "'");
		}
	}
};

_cms.links.repopulateLinkNameDropdown = function(linkType, currentLinkname) {
	var selector = $(_cms.links.sel.LINKNAME_SELECT);
	
	$.ajax(_cms.ctx + "/rest/linknames/" + _cms.siteId + "/" + linkType, {
		type: "POST",
		cache: false,
		dataType: "json",
		success: function(result, status, z) {
			selector.empty();
			selector.append("<option value='unknown'>Choose ...</option>");
			for (var i = 0; i < result.length; i++) {
				selector.append("<option value='" + result[i] + "'>" + result[i] + "</option>");
			}
			
			if (currentLinkname) {
				selector.val(currentLinkname);
			}
		}
	});
};

_cms.links.behaviour.sortable = function() { 
	$(_cms.links.sel.SORTABLE_LINKS_CONTAINER).sortable();
	$(_cms.links.sel.SORTABLE_LINKS_CONTAINER).disableSelection();
}

_cms.links.activateSaveButton = function(activate) {
	if (activate) {
		_cms.support.enable(_cms.links.sel.SAVE_LINKS_BUTTON);
	}
	else {
		_cms.support.disable(_cms.links.sel.SAVE_LINKS_BUTTON);
	}
}

_cms.links.refresh.tab = function(nodeKey) {
	_cms.support.refreshtab("links", nodeKey, _cms.links.onrefresh);
}

_cms.links.check_for_use = function() {
	return $(_cms.links.sel.LINKTYPE_SELECT).val() != 'unknown' &&
			$(_cms.links.sel.LINKNAME_SELECT).val() != 'unknown' && 
			$(_cms.links.sel.CHILDID_INPUT).val() > -1;
}

_cms.links.openDialog = function(d) {
	d.obj.dialog("open");
}

_cms.links.closeDialog = function(d) {
	d.obj.dialog("close");
}

_cms.links.define.addlink_dialog = function() {
	_cms.links.addlink_dialog.obj = $(_cms.links.sel.ADD_LINK_CONTAINER).dialog({
		  autoOpen: false,
		  minHeight: 250,
		  minWidth: 300,
		  modal: true,
		  title: "Add/edit a link",
		  buttons: {
			  Use: function() {
				  if (_cms.links.check_for_use()) {
					  _cms.links.use_form_data();
					  _cms.links.closeDialog(_cms.links.addlink_dialog);
				  }
				  else {
					  _cms.links.openDialog(_cms.links.addlink_warning_dialog);
				  }
			  },
			  Cancel: function() {
				  _cms.links.closeDialog(_cms.links.addlink_dialog);
			  }
		  },
		  close: function() {
			  _cms.links.closeDialog(_cms.links.addlink_dialog);
		  }
	});
}

_cms.links.define.addlink_warning_dialog = function() {
	_cms.links.addlink_warning_dialog.obj = $(_cms.links.sel.INCOMPLETE_LINK_DIALOG).dialog({
		  autoOpen: false,
		  minHeight: 200,
		  minWidth: 250,
		  modal: true,
		  title: "Incomplete link data",
		  buttons: {
			  Close: function() {
				  _cms.links.closeDialog(_cms.links.addlink_warning_dialog);
			  }
		  },
		  close: function() {
			  _cms.links.closeDialog(_cms.links.addlink_warning_dialog);
		  }
	});
}

// Things to do once-only on page load
_cms.links.onpageload = function() {
	_cms.links.define.addlink_dialog();
	_cms.links.define.addlink_warning_dialog();
	_cms.links.behaviour.changetype();
	_cms.links.behaviour.itempicker();
}

// Behaviours to apply once html is loaded/reloaded
_cms.links.onrefresh = function(nodeKey) {
	_cms.links.behaviour.sortable(); 
	_cms.links.behaviour.add();
	_cms.links.behaviour.save(nodeKey);
	_cms.links.behaviour.remove();
	_cms.links.behaviour.edit();
	_cms.links.behaviour.navigate();	
}


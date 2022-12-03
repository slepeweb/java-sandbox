/*
 * Hidden span data format:
 * Example:
 * 	1226|relation|std|some data|-1|0
 * 
 * Format:
 * 	childId|linktype|linkname|site-specific data string|linkId|state
 * 
 * where:
 * 		state has binary values: 0 = new link, 1 = existing link
 * 		linkId uniquely identifies links in the list
 * 
 */

/*
 * Index of functions:
 *		_cms.links.behaviour.add
 *		_cms.links.behaviour.changetype					Triggered when user selects a new link type (eg. relation)
 *		_cms.links.behaviour.edit
 *		_cms.links.behaviour.itempicker
 *		_cms.links.behaviour.linkguidanceicon
 *		_cms.links.behaviour.linknamechange				Triggered when user selects a different linkname (eg. std)
 *		_cms.links.behaviour.navigate
 *		_cms.links.behaviour.remove
 *		_cms.links.behaviour.reset
 *		_cms.links.behaviour.save
 *		_cms.links.behaviour.sortable
 *		_cms.links.activateSaveButton
 *		_cms.links.check_duplicate_link
 *		_cms.links.check_for_use
 *		_cms.links.check_not_binding
 *		_cms.links.formatHiddenLinkData
 *		_cms.links.formatLinkIdentifier
 *		_cms.links.getCurrentGuidance
 *		_cms.links.getItemNameAnd
 *		_cms.links.getLinkData
 *		_cms.links.getLinkGuidance
 *		_cms.links.getNextSortableLinkId
 *		_cms.links.identifyHiddenLinkData4Span
 *		_cms.links.identifyHiddenLinkDataList
 *		_cms.links.insertClonedLink
 *		_cms.links.onpageload
 *		_cms.links.onrefresh
 *		_cms.links.refresh.tab
 *		_cms.links.repopulateGuidance					Re-populates guidance dialog given a new linkname selected by the user
 *		_cms.links.repopulateLinkNameDropdown
 *		_cms.links.setLinkForm
 *		_cms.links.shortcut.settings
 *		_cms.links.show_addlink_form
 *		_cms.links.updateLink
 *		_cms.links.use_form_data
 *		_cms.links.useLink
*/

_cms.links = {
	behaviour: {},
	refresh: {},
	selrel: {
		LINKTYPE_SELECT: "select[name='linktype']",
		LINKNAME_SELECT: "select[name='linkname']",
		LINKDATA_INPUT: "input[name='linkdata']",
		STATE_INPUT: "input[name='state']",
		CHILDID_INPUT: "input[name='childId']",
		LINKID_INPUT: "input[name='linkId']",
	},
	sel: {
		LINKS_TAB: "#links-tab",
		ADD_LINK_CONTAINER: "#addlinkdiv",
		ITEM_PICKER: "#addlinkdiv i.itempicker",
		ALL_SELECTS: "#addlinkdiv select",
		SORTABLE_LINKS_CONTAINER: "#sortable-links",
		EDIT_LINK_BUTTONS: ".edit-link",
		REMOVE_LINK_BUTTONS: ".remove-link",
		LINKTO_BUTTONS: ".link-linker",
		LINK_TARGET_IDENTIFIER: "#link-target-identifier",
	},
	shortcut: {},
};

_cms.links.sel.LINKTYPE_SELECT = _cms.links.sel.ADD_LINK_CONTAINER + " " + _cms.links.selrel.LINKTYPE_SELECT;
_cms.links.sel.LINKNAME_SELECT = _cms.links.sel.ADD_LINK_CONTAINER + " " + _cms.links.selrel.LINKNAME_SELECT;
_cms.links.sel.LINKDATA_INPUT = _cms.links.sel.ADD_LINK_CONTAINER + " " + _cms.links.selrel.LINKDATA_INPUT;
_cms.links.sel.CHILDID_INPUT = _cms.links.sel.ADD_LINK_CONTAINER + " " + _cms.links.selrel.CHILDID_INPUT;
_cms.links.sel.STATE_INPUT = _cms.links.sel.ADD_LINK_CONTAINER + " " + _cms.links.selrel.STATE_INPUT;
_cms.links.sel.LINKID_INPUT = _cms.links.sel.ADD_LINK_CONTAINER + " " + _cms.links.selrel.LINKID_INPUT;
_cms.links.sel.SORTABLE_LINKS = _cms.links.sel.SORTABLE_LINKS_CONTAINER + " div.sortable-link";

_cms.links.sel.ADD_LINK_BUTTON = _cms.links.sel.LINKS_TAB + " button.action.add",
_cms.links.sel.SAVE_LINKS_BUTTON = _cms.links.sel.LINKS_TAB + " button.action.save",
_cms.links.sel.RESET_BUTTON = _cms.links.sel.LINKS_TAB + " button.reset",

_cms.support.setTabIds(_cms.links, "links");

_cms.links.show_addlink_form = function(data) {
	if (data) {
		_cms.links.setLinkForm(data);
	}	
	_cms.dialog.open(_cms.dialog.addLink);
}

_cms.links.behaviour.itempicker = function() {
	$(_cms.links.sel.ITEM_PICKER).click(function() {
		_cms.leftnav.mode = "link";
		_cms.dialog.open(_cms.leftnav.dialog);
	});
}

_cms.links.use_form_data = function() {
	var div = $(_cms.links.sel.ADD_LINK_CONTAINER);
	var formData = {};
	formData.type = div.find(_cms.links.selrel.LINKTYPE_SELECT).val();
	formData.name = div.find(_cms.links.selrel.LINKNAME_SELECT).val();
	formData.data = div.find(_cms.links.selrel.LINKDATA_INPUT).val();
	formData.state = div.find(_cms.links.selrel.STATE_INPUT).val();
	formData.childId = div.find(_cms.links.selrel.CHILDID_INPUT).val();
	var linkIdStr = div.find(_cms.links.selrel.LINKID_INPUT).val();
	formData.linkId = parseInt(linkIdStr, 10);
	
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
		var linkType = "unknown";
		var linkSubtype = "unknown";
		if (_cms.editingItemIsShortcut) {
			linkType = "shortcut";
			linkSubtype = "std";
		}
		_cms.links.show_addlink_form(["-1", linkType, linkSubtype, "", "-1", "0"]);
	});
}

_cms.links.behaviour.save = function(nodeKey) {
	// Add behaviour to 'Save links' button 
	$(_cms.links.sel.SAVE_LINKS_BUTTON).click(function(e) {
		var links = _cms.links.identifyHiddenLinkDataList($(_cms.links.sel.SORTABLE_LINKS));
		
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
			success: function(resp, status, z) {
				_cms.support.flashMessage(resp);
				_cms.links.refresh.tab(nodeKey);
				_cms.links.activateSaveButton(false);
				_cms.undoRedo.displayAll(resp.data[3]);				
				
				if (! resp.error && resp.data) {
					// Need to refresh the FancyTree,
					// since one or more shortcuts have been added/removed
					//_cms.leftnav.refreshShortcuts("" + _cms.editingItemId, resp.data[0]);
					_cms.leftnav.refreshShortcut("" + nodeKey, resp.data[1], resp.data[2]);
				}
			},
			error: function(obj, status, z) {
				_cms.support.serverError();
			},
		});
	});
}

_cms.links.behaviour.reset = function(nodeKey) {
	$(_cms.links.sel.RESET_BUTTON).click(function(e) {
		_cms.support.resetForm(_cms.links.refresh.tab, nodeKey, e);
	});
}

_cms.links.behaviour.remove = function() {
	// Add behaviour to 'Remove links' button 
	$(_cms.links.sel.REMOVE_LINK_BUTTONS).off().click(function(e) {
		$(this).parent().parent().remove();
		_cms.links.activateSaveButton(true);
		_cms.links.shortcut.settings();
		
		if ( $(_cms.links.sel.SORTABLE_LINKS).length == 0) {
			$(_cms.links.sel.SORTABLE_LINKS_CONTAINER).html("<p>None</p>")
		}
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
		var fn = function() {
			// This attribute setting changes the active tab for when node activation completes
			$("li.ui-tabs-active").attr("aria-controls", "core-tab");
		}

		// Make corresponding node current in leftnav
		_cms.leftnav.activateKey(key, fn);
		
		// Render forms for linked item, and show the core tab
		_cms.support.renderItemForms(key, "core-tab");
	});
}

_cms.links.behaviour.linkguidanceicon = function() {
	$("#link-guidance-icon").click(function(e){
		_cms.dialog.open(_cms.dialog.linkGuidance);
	});
}

_cms.links.behaviour.linknamechange = function() {
	$(_cms.links.selrel.LINKNAME_SELECT).change(function(e){
		_cms.links.repopulateGuidance($(this).val());
	});
}

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
		linkId: parseInt(parts[4], 10),
		state: parts[5]
	};
	
	return obj;
};

_cms.links.formatHiddenLinkData = function(d) {
	var dl = "|";
	return d.childId + dl + d.type + dl + d.name + dl + d.data + dl + d.linkId + dl + d.state;
};

// Use the link form data to add a new link for saving, or edit an existing link
_cms.links.useLink = function(formData) {
	
	var sortableLinksContainer = $("div" + _cms.links.sel.SORTABLE_LINKS_CONTAINER);
	var sortableLinks = $(_cms.links.sel.SORTABLE_LINKS);

	// Is this link already in the list?
	var target = null;	
	var links = _cms.links.identifyHiddenLinkDataList(sortableLinks);
	
	for (var i = 0; i < links.length; i++) {
		if (links[i].linkId == formData.linkId) {
			target = links[i];
			break;
		}
	}
	
	if (target) {
		// This link is already in the list (target)
		var span = target.span;
		span.innerHTML = _cms.links.formatHiddenLinkData(formData);
		
		var identifier = $(span).parent().parent().find(".link-identifier");
		
		_cms.links.getItemNameAnd(formData.childId, _cms.links.updateLink, 
				span, identifier, formData);
	}
	else {
		// This link is not in the list - it is a new link.
		// Clone the template, and append the clone to the end of the list
		var div = $("#link-template>div").clone(true);
		
		// Assign a linkId to the link - find the largest id and increment by 1
		formData.linkId = _cms.links.getNextSortableLinkId(links);
		
		// Need an ajax call to identify the name of the child item, for list display purposes only
		_cms.links.getItemNameAnd(formData.childId, _cms.links.insertClonedLink, sortableLinksContainer, 
				div, formData);		
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

_cms.links.insertClonedLink = function(itemName, sortableLinksContainer, div, formData) {
	if ( $(_cms.links.sel.SORTABLE_LINKS).length == 0) {
		$(_cms.links.sel.SORTABLE_LINKS_CONTAINER).empty();
	}

	div.find("div.left span.link-identifier").html(_cms.links.formatLinkIdentifier(formData, itemName));
	div.find("div.right button.link-linker").attr("data-id", formData.childId);
	div.find("div.right span.hide").html(_cms.links.formatHiddenLinkData(formData));				
	div.appendTo(sortableLinksContainer);					
}



_cms.links.updateLink = function(itemName, span, identifier, formData) {
	span.innerHTML = _cms.links.formatHiddenLinkData(formData);
	identifier.html(_cms.links.formatLinkIdentifier(formData, itemName));
	$(span).parent().parent().addClass("changed-link");
}

_cms.links.formatLinkIdentifier = function(formData, itemName) {
	return formData.type + " (" + formData.name + "): " + itemName;
}

_cms.links.getNextSortableLinkId = function(sortableLinks) {
	var maxId = -1;
	for (var i = 0; i < sortableLinks.length; i++) {
		if (sortableLinks[i].linkId > maxId) {
			maxId = sortableLinks[i].linkId;
		}
	}
	return maxId + 1;
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
	
	if (data.length == 7) {
		var idx = data[6].indexOf(":");
		var name = "n/a";
		if (idx > -1) {
			name = data[6].substring(idx + 2);
		}
		$(_cms.links.sel.LINK_TARGET_IDENTIFIER).html("'" + name + "'");
	}
};

_cms.links.behaviour.sortable = function() { 
	$(_cms.links.sel.SORTABLE_LINKS_CONTAINER).sortable();
	$(_cms.links.sel.SORTABLE_LINKS_CONTAINER).disableSelection();
}

_cms.links.getLinkGuidance = function(linkname) {
	return $(`#link-guidance-${linkname}`).html();
}

_cms.links.getCurrentGuidance = function() {
	return $('#link-guidance').html();
}

_cms.links.getLinkData = function() {
	return $(_cms.links.selrel.LINKDATA_INPUT).val();
}

_cms.links.repopulateGuidance = function(linkName) {
	var linkDataInput = $(_cms.links.selrel.LINKDATA_INPUT);
	var guidance = _cms.links.getLinkGuidance(linkName);
	
	if (guidance) {
		linkDataInput.removeAttr("disabled");
	}
	else {
		linkDataInput.attr("disabled", "disabled");
		guidance = "Link data ignored for this link type/subtype";
	}
	
	$("#link-guidance").html(guidance);
}

_cms.links.activateSaveButton = function(activate) {
	if (activate) {
		_cms.support.enable(_cms.links.sel.SAVE_LINKS_BUTTON);
		_cms.support.enable(_cms.links.sel.RESET_BUTTON);
	}
	else {
		_cms.support.disable(_cms.links.sel.SAVE_LINKS_BUTTON);
		_cms.support.disable(_cms.links.sel.RESET_BUTTON);
	}
}

_cms.links.refresh.tab = function(nodeKey) {
	_cms.support.refreshtab("links", nodeKey, _cms.links.onrefresh);
}

_cms.links.check_for_use = function() {
	var childId = $(_cms.links.sel.CHILDID_INPUT).val();
	var error = false;
	var linkType = $(_cms.links.sel.LINKTYPE_SELECT).val();
	var linkName = $(_cms.links.sel.LINKNAME_SELECT).val();
	var linkData = $(_cms.links.sel.LINKDATA_INPUT).val();
	var linkDataRegexpStr = $("#link-guidance p.regexp").html();
	var linkId = $(_cms.links.sel.LINKID_INPUT).val();
	
	if (linkType == 'unknown' || linkName == 'unknown' || childId == -1) {
		_cms.dialog.open(_cms.dialog.linkNotDefined);
		error = true;
	}

	// Only check this link for duplicate target if this link is new to the list
	if (! error && linkId == -1 && ! _cms.links.check_duplicate_link(childId)) {
		_cms.dialog.open(_cms.dialog.duplicateTarget);
		error = true;
	}

	if (! error && ! _cms.links.check_not_binding(childId)) {
		_cms.dialog.open(_cms.dialog.illegalTarget);
		error = true;
	}
	
			
	if (! error) {
		// Check link data is formatted correctly
		var linkData = _cms.links.getLinkData();
		
		if (linkDataRegexpStr && linkData) {
			var regexp = new RegExp(linkDataRegexpStr, "i");
			if (! regexp.test(linkData)) {
				_cms.dialog.open(_cms.dialog.badLinkDataFormat);
				error = true;
			}
		}
	}
	
	return ! error;
}

_cms.links.check_duplicate_link = function(childId) {
	var ok = true;
	var usedLinks = _cms.links.identifyHiddenLinkDataList($(_cms.links.sel.SORTABLE_LINKS));

	for (var i = 0; i < usedLinks.length; i++) {
		if (usedLinks[i].childId == childId) {
			ok = false;
			break;
		}
	}	
	
	return ok;
}

_cms.links.check_not_binding = function(childId) {
	var node = _cms.leftnav.tree.getNodeByKey(childId);
	if (node) {
		var parentNode = node.getParent();
		if (parentNode) {
			return parentNode.key != _cms.editingItemId;
		}
	}
	
	// Otherwise, let it through, and let the back-end deal with it.
	return true;
}


_cms.links.shortcut.settings = function() {
	if (_cms.editingItemIsShortcut) {
		if ( $(_cms.links.sel.SORTABLE_LINKS).length > 0) {
			_cms.support.disable(_cms.links.sel.ADD_LINK_BUTTON);
		}
		else {
			_cms.support.enable(_cms.links.sel.ADD_LINK_BUTTON);
			$(_cms.links.sel.LINKTYPE_SELECT).empty().html('<option value="shortcut">shortcut</option>');
		}
		
	}
	else {
		_cms.support.enable(_cms.links.sel.ADD_LINK_BUTTON);
		$(_cms.links.sel.LINKTYPE_SELECT).empty().html(
				'<option value="unknown">Choose ...</option>' + 
				'<option value="relation">relation</option>' + 
				'<option value="inline">inline</option>' + 
				'<option value="component">component</option>');
	}
}

_cms.links.repopulateLinkNameDropdown = function(linkType, currentLinkname) {

	$.ajax(_cms.ctx + "/rest/linknames/" + _cms.siteId + "/" + linkType, {
		type: "POST",
		cache: false,
		dataType: "html",
		mimeType: "text/html",
		success: function(html, status, z) {
			var divs = $(html).children();
			var selector = $(_cms.links.sel.LINKNAME_SELECT);
			var guidanceListDiv = $("#link-guidance-list");
	
			// currentLinkname is not set for new links
			if (! currentLinkname) {
				currentLinkname = "std";
			}
				
			selector.html($(divs[0]).html());
			guidanceListDiv.html($(divs[1]).html());
								
			selector.val(currentLinkname);
			_cms.links.repopulateGuidance(currentLinkname);
		}
	});
};


// Things to do once-only on page load
_cms.links.onpageload = function() {
	_cms.links.behaviour.changetype();
	_cms.links.behaviour.itempicker();
	_cms.links.behaviour.linkguidanceicon();
	_cms.links.behaviour.linknamechange();
}

// Behaviours to apply once html is loaded/reloaded
_cms.links.onrefresh = function(nodeKey) {
	_cms.links.behaviour.sortable(); 
	_cms.links.behaviour.add();
	_cms.links.behaviour.save(nodeKey);
	_cms.links.behaviour.reset(nodeKey);
	_cms.links.behaviour.remove();
	_cms.links.behaviour.edit();
	_cms.links.behaviour.navigate();
	
	_cms.links.shortcut.settings();
}


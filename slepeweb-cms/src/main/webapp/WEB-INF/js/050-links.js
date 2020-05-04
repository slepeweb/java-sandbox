_cms.links = {
	behaviour: {},
	refresh: {},
	dialog: {
		obj: null
	},
	tree: null,
};

_cms.links.behaviour.fancytree = function(rebuild) {
	if (rebuild) {
		// Add a fancytree to identify child links 
		$("#linknav").fancytree({
			source: {
				url: _cms.ctx + "/rest/leftnav/lazy/thread",
				data: _cms.queryParams, /* nodeKey and siteId */
				cache: false,
				checkbox: true,
				complete: function() {
					// On completion of loading the tree, activate the node for the current item
					_cms.links.tree = $("#linknav").fancytree("getTree");
				}
			},
			lazyLoad: function(event, data) {
				var node = data.node;
				data.result = {
					url: _cms.ctx + "/rest/leftnav/lazy/one",
					data: {key: node.key}
				};
			},
			activate: function(event, data) {
				// Do nothing
			}	
		});
	}
}
   
_cms.links.behaviour.changetype = function() {
	// Re-populate linkname options when link type is selected
	$("#addlinkdiv select[name='linktype']").off().change(function(e) {
		_cms.links.repopulateLinkNameDropdown($(this).val());
	});
}

_cms.links.behaviour.add = function() {
	// Show link addition form when 'Add link' button is clicked
	$("#addlink-button").click(function(e) {
		_cms.links.dialog.open();
	});
}

_cms.links.behaviour.save = function(nodeKey) {
	// Add behaviour to 'Save links' button 
	$("#savelinks-button").click(function(e) {
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
	$(".remove-link").click(function(e) {
		$(this).parent().parent().remove();
		_cms.links.activateSaveButton(true);
	});
}

_cms.links.behaviour.edit = function() {
	// Open form to edit an existing link
	$(".edit-link").click(function() {
		var span = $(this).parent().find("span.hide");
		var dataparts = span.html().split("\|");
		
		// Ignore first element
		dataparts.shift();
		
		_cms.links.repopulateLinkNameDropdown(dataparts[0], dataparts[1]);
		_cms.links.setLinkForm(dataparts, "1");
		
		// Hide the tree for link editing (as opposed to link creation)
		$("#linknav-container").css("display", "none");
		
		// Open the dialog
		_cms.links.dialog.open();
	});
}

_cms.links.behaviour.navigate = function() {
	// Add behaviour for when a link (in the links editor) is clicked 
	$(".link-linker").click(function(e) {
		var key = $(this).attr("data-id");
		var node = _cms.leftnav.tree.getNodeByKey(key);
		
		if (node) {
			// This attribute setting changes the active tab for when node activation completes
			$("li.ui-tabs-active").attr("aria-controls", "core-tab");
			_cms.leftnav.tree.activateKey(node.key);
		}
		else {
			// This node hasn't been loaded yet - ask the server for the breadcrumb trail
			$.ajax(_cms.ctx + "/rest/breadcrumbs/" + key, {
				cache: false,
				dataType: "json",
				mimeType: "application/json",
				success: function(json, status, z) {
					// This attribute setting changes the active tab for when node activation completes
					$("li.ui-tabs-active").attr("aria-controls", "core-tab");
					_cms.leftnav.tree.loadKeyPath(json, function(node, stats) {
						if (stats === "ok") {
						    node.setActive();
						}
					});
				},
				error: function(json, status, z) {
					_cms.support.flashMessage(_cms.support.toStatus(false, "Failed to retrieve breadcrumb trail"));
				}
			});
		}
	});
}

_cms.sortableLinksSelector = "#sortable-links div.sortable-link";

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
_cms.links.useLink = function() {
	// Collect form data
	var div = $("#addlinkdiv");
	var formData = {};
	formData.type = div.find("select[name='linktype']").val();
	formData.name = div.find("select[name='linkname']").val();
	formData.data = div.find("input[name='linkdata']").val();
	formData.state = div.find("input[name='state']").val();
	var linkIdStr = div.find("input[name='linkId']").val();
	formData.ordering = parseInt(linkIdStr, 10);
	
	if (formData.type != 'unknown' && formData.name != 'unknown') {
		var sortableLinksContainer = $("div#sortable-links");
		var sortableLinks = $(_cms.sortableLinksSelector);
	
		if (formData.state == "1") {
			// We need to replace an existing link
			var linkHtml = null;
			var target = null;
			
			var links = _cms.links.identifyHiddenLinkDataList(sortableLinks);
			for (var i = 0; i < links.length; i++) {
				if (links[i].ordering == formData.ordering) {
					target = links[i];
					break;
				}
			}
			
			if (target) {
				var span = target.span;
				formData.childId = target.childId;
				span.innerHTML = _cms.links.formatHiddenLinkData(formData);
			}
		}
		else {
			// This is a new link - append it to the end of any existing links
			linkHtml = $("#link-template>div").clone(true);
			var links = _cms.links.identifyHiddenLinkDataList(sortableLinks);
			var nextId = -1;
			
			if (links) {
				for (var i = 0; i < links.length; i++) {
					if (links[i].ordering > nextId) {
						nextId = links[i].ordering;
					}
				};
				
				nextId += 1;
			}
			else {
				// This is the first link
				nextId = 0;				
			}
			
			if (_cms.links.tree) {
				formData.childId = _cms.links.tree.activeNode.key;
				
				if (formData.childId) {
					 
					$.ajax(_cms.ctx + "/rest/item/" + formData.childId + "/name", {
						type: "POST",
						cache: false,
						dataType: "text",
						success: function(itemName, status, z) {
							linkHtml.find("div.left span.link-identifier").html(formData.type + " (" + formData.name + "): " + itemName);
							linkHtml.find("div.right button.link-linker").attr("data-id", formData.childId);
							linkHtml.find("div.right span.hide").html(_cms.links.formatHiddenLinkData(formData));				
							linkHtml.appendTo(sortableLinksContainer);					
						}
					});
				}
			}
		}
		
		_cms.links.activateSaveButton(true);
	}
	else {
		_cms.support.showDialog("dialog-choose-linktype");
	}
};

_cms.links.dialog.open = function() {
	
	if (_cms.links.dialog.obj) {
		_cms.links.dialog.obj.dialog("destroy");
	}
	
	// New dialog containing form to add/edit links
	_cms.links.dialog.obj = $("#addlinkdiv").dialog({
	  autoOpen: false,
	  minHeight: 250,
	  maxWidth: 350,
	  modal: true,
	  title: "Add/Edit a link",
	  buttons: {
	    Use: function() {
	    	_cms.links.useLink();
	    	_cms.links.dialog.close();
	    },
	    Cancel: function() {
	    	_cms.links.dialog.close();
	    }
	  },
	  close: function() {
	  }
	});
	
	_cms.links.dialog.obj.dialog("open");
}

_cms.links.dialog.close = function() {
	_cms.links.dialog.obj.dialog("close");
	$("#linknav-container").css("display", "block");
	_cms.links.setLinkForm(["unknown","unknown","","-1"], "0");
};

_cms.links.setLinkForm = function(data, state) {
	var form = $("#addlinkdiv");	
	form.find("select[name='linktype']").val(data[0]);
	form.find("select[name='linkname']").val(data[1]);
	form.find("input[name='linkdata']").val(data[2]);
	form.find("input[name='linkId']").val(data[3]);
	form.find("input[name='state']").val(state);	
};

_cms.links.repopulateLinkNameDropdown = function(linkType, value) {
	var selector = $("#addlinkdiv select[name='linkname']");
	selector.empty();
	
	$.ajax(_cms.ctx + "/rest/linknames/" + _cms.siteId + "/" + linkType, {
		type: "POST",
		cache: false,
		dataType: "json",
		success: function(result, status, z) {
			selector.append("<option value='unknown'>Choose ...</option>");
			for (var i = 0; i < result.length; i++) {
				selector.append("<option value='" + result[i] + "'>" + result[i] + "</option>");
			}
			
			if (value) {
				selector.val(value);
			}
		}
	});
};

_cms.links.behaviour.sortable = function() { 
	$( "#sortable-links" ).sortable();
	$( "#sortable-links" ).disableSelection();
}

_cms.links.activateSaveButton = function(activate) {
	var button = $("#savelinks-button");
	if (activate) {
		button.removeAttr("disabled");
	}
	else {
		button.attr("disabled", "disabled");
	}
}

_cms.links.refresh.tab = function(nodeKey) {
	_cms.support.refreshtab("links", nodeKey, _cms.links.behaviour.all);
}

// Behaviours to apply once html is loaded/reloaded
_cms.links.behaviour.all = function(nodeKey, rebuildTree) {
	_cms.links.behaviour.fancytree(rebuildTree);
	_cms.links.behaviour.sortable(); 
	_cms.links.behaviour.changetype();
	_cms.links.behaviour.add();
	_cms.links.behaviour.save(nodeKey);
	_cms.links.behaviour.remove();
	_cms.links.behaviour.edit();
	_cms.links.behaviour.navigate();	
}


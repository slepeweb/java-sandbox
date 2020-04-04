<%@ tag %><%@ 
    taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%><%@ 
    taglib prefix="cms" tagdir="/WEB-INF/tags/cms"%><%@ 
    taglib prefix="cmsjs" tagdir="/WEB-INF/tags/cms/js"%>
        
<cms:debug>/* tags/cms/js/links.tag */</cms:debug>

// Add a fancytree to identify child links 
$("#linknav").fancytree({
	source: {
		url: _ctx + "/rest/leftnav/lazy/thread",
		data: queryParams, /* TODO: Not sure this is right */
		cache: false,
		checkbox: true,
		complete: function() {
			// On completion of loading the tree, activate the node for the current item
			_linkerTree = $("#linknav").fancytree("getTree");
		}
	},
	lazyLoad: function(event, data) {
		var node = data.node;
		data.result = {
			url: _ctx + "/rest/leftnav/lazy/one",
			data: {key: node.key}
		};
	},
	activate: function(event, data) {
		// Do nothing
	}	
});
   
// Re-populate linkname options when link type is selected
$("#addlinkdiv select[name='linktype']").change(function(e) {
	_repopulateLinkNameDropdown($(this).val());
});

// Show link addition form when 'Add link' button is clicked
$("#addlink-button").click(function(e) {
	linkDialog.dialog("open");
});

// Add behaviour to 'Save links' button 
$("#savelinks-button").click(function(e) {
	var selector = $("#sortable-links");
	var links = _identifyHiddenLinkDataList(selector);
	
	// Remove the 'span' property from each object
	for (var i = 0; i < links.length; i++) {
		delete links[i].span;
	}
	
	// Ajax call to save links to db 
	$.ajax(_ctx + "/rest/links/" + _editingItemId + "/save", {
		type: "POST",
		cache: false,
		data: JSON.stringify(links), 
		contentType: "application/json; charset=utf-8",
		dataType: "json",
		processData: false,
		success: function(obj, status, z) {
			flashMessage(obj);
			
			if (! obj.error && obj.data) {
				// Need to refresh the page, to update the FancyTree,
				// since one or more shortcuts have been added/removed
				_refreshShortcuts("" + _editingItemId, obj.data);
			}
		},
		error: function(obj, status, z) {
			serverError();
		},
	});
});

// Add behaviour to 'Remove links' button 
$(".remove-link").click(function(e) {
	$(this).parent().remove();
});

// New dialog containing form to add/edit links
if (linkDialog) {
	linkDialog.dialog("destroy");
}

linkDialog = $("#addlinkdiv").dialog({
  autoOpen: false,
  minHeight: 250,
  minWidth: 400,
  modal: true,
  title: "Add/Edit a link",
  buttons: {
    Use: function() {
    	_useLink();
  	  _closeLinkForm();
    },
    Cancel: function() {
  	  _closeLinkForm();
    }
  },
  close: function() {
  }
});

// Open form to edit an existing link
$(".edit-link").click(function() {
	var span = $(this).parent().find("span.hide");
	var dataparts = $(this).parent().find("span.hide").html().split("\|");
	
	// Ignore first element
	dataparts.shift();
	
	_repopulateLinkNameDropdown(dataparts[0], dataparts[1]);
	_setLinkForm(dataparts, "1");
	
	// Hide the tree for link editing (as opposed to link creation)
	$("#linknav").css("display", "none");
	
	// Open the dialog
	linkDialog.dialog("open");
});

// Add behaviour for when a link (in the links editor) is clicked 
$(".link-linker").click(function(e) {
	var key = $(this).attr("data-id");
	var node = _tree.getNodeByKey(key);
	
	if (node) {
		// This attribute setting changes the active tab for when node activation completes
		$("li.ui-tabs-active").attr("aria-controls", "core-tab");
		_tree.activateKey(node.key);
	}
	else {
		// This node hasn't been loaded yet - ask the server for the breadcrumb trail
		$.ajax(_ctx + "/rest/breadcrumbs/" + key, {
			cache: false,
			dataType: "json",
			mimeType: "application/json",
			success: function(json, status, z) {
				// This attribute setting changes the active tab for when node activation completes
				$("li.ui-tabs-active").attr("aria-controls", "core-tab");
				_tree.loadKeyPath(json, function(node, stats) {
					if (stats === "ok") {
					    node.setActive();
					}
				});
			},
			error: function(json, status, z) {
				flashMessage(toStatus(false, "Failed to retrieve breadcrumb trail"));
			}
		});
	}
});


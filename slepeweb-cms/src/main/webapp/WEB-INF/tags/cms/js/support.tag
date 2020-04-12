<%@ tag %><%@ 
    taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%><%@ 
    taglib prefix="cms" tagdir="/WEB-INF/tags/cms"%><%@ 
    taglib prefix="cmsjs" tagdir="/WEB-INF/tags/cms/js"%>
        
<cms:debug>/* tags/cms/js/support.tag */</cms:debug>

var toStatus = function(err, msg) {
	var obj = {};
	obj.error = err;
	obj.message = msg;
	return obj;
};

/*
 * Standard error message.
 */
var serverError = function() {
	flashMessage(toStatus(true, "Server error"));
};

/*
 * Displays a flash message, in red for errors, and green for info messages.
 */
var flashMessage = function(status) {
	var clazz, msg;
	if (status) {
		clazz = status.error ? "red" : "green";
		msg = status.message;
	}
	else {
		clazz = "red";
		msg = "";
	}
	
	$("#status-block").removeClass("red").removeClass("green").addClass(clazz).append(msg);
	var audio = $("#bell");
	if (audio && audio.get(0)) {
		audio.get(0).play();
	}
};

var pageEditorUrlPrefix = _ctx + "/page/editor/";

var displayCommerceElements = function(target) {
	var ele = target.find("option:selected");
	var flag = ele.attr("data-isproduct");

	if (flag == 1) {
		$("#core-commerce").css("display", "block");
	}
	else {
		$("#core-commerce").css("display", "none");
	}
};

/*
 * Tells the browser to get the item editor page for a given item.
 */
var fetchItemEditor = function(nodeKey, status, tabName) {
	var url = pageEditorUrlPrefix + nodeKey;
	
	if (status) {
		var param = status.error ? "error" : "success";
		url += ("?status=" + param + "&msg=" + status.messageEncoded);	
	}
	
	if (tabName) {
		url += "&tab=" + tabName;
	}
	
	window.location = url; 
};
	
// Get form field names and values for forms on item-editor 
var getFieldsFormInputData = function() {
	var result = {};
	var language = $("#field-language-selector select").val();
	if (! language) {
		language = _siteDefaultLanguage;
	}
	
	result["language"] = language;
	var selector = "#form-fields-lang input, #form-fields-lang textarea, #form-fields-lang select";
	selector = selector.replace(/lang/g, language);
	
	$(selector).each(function(i, obj) {
		var ctrl = $(obj);
		var type = ctrl.attr("type");
		var param = ctrl.attr("name");
		var str;
		if (type == "radio") {
			if (ctrl.is(':checked')) {
				result[param] = ctrl.val();
			}
		}
		else if (type == "checkbox") {
			if (ctrl.is(':checked')) {
				str = result[param];
				if (! str) {
					str = "";
				}
				if (str.length > 0) {
					str += "|";
				}
				str += ctrl.val();
				result[param] = str;
			}
		}
		else {
			result[param] = ctrl.val();
		}
	});
	return result;
};

/*
 * Shows a modal giving the user a chance to confirm his selection.
 */
var showDialog = function(id, relocate) {
	$("#" + id).dialog({
		modal: true,
		buttons: {
			Ok: function() {
				$(this).dialog("close");
				if (relocate) {
					window.location = relocate;
				}
			}
		}
	});
};

/* Nodes representing shortcuts in the FancyTree have '.s' appended to their standard key value,
 * so that they are distinguishable from the node representing the 'real' item. This method
 * identifies the numeric part preceding the '.s' suffix.
*/
var removeShortcutMarker = function(key) {
	var cursor = key.indexOf(".s");
	if (cursor > -1) {
		return key.substring(0, cursor);
	}
	return key;
};

var addHistoryBehaviour = function(selector) {
	selector.unbind();
	selector.change(function() {	
		fetchItemEditor(selector.val());
	});
};

var refreshHistory = function(siteId) {
		$.ajax(_ctx + "/rest/item/history/" + siteId, {
			type: "POST",
			cache: false,
			//data: {key: nodeKey}, 
			dataType: "json",
			mimeType: "application/json",
			
			// On successful loading of forms 
			success: function(list, status, z) {
					var selector = $("#history-selector");
					selector.empty();
					for (var i = 0; i < list.length; i++) {
						selector.append("<option value='" + list[i].itemId + "'>" + list[i].name + "</option>");
					}

					addHistoryBehaviour(selector);					
			},
			error: function(list, status, z) {
				console.log("Error: " + list);
			}
		});
};

var _sortableLinksSelector = "#sortable-links div.sortable-link";

var _identifyHiddenLinkDataList = function(sortableLinks) {
	var links = [];
	var span;
	
	sortableLinks.find("div.right span").each(function(index, span) {
		links.push(_identifyHiddenLinkData4Span(span));
	});
	
	return links;
};

var _identifyHiddenLinkData4Span = function(span) {
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

var _formatHiddenLinkData = function(d) {
	var dl = "|";
	return d.childId + dl + d.type + dl + d.name + dl + d.data + dl + d.ordering + dl + d.state;
};

// Use the link form data to add a new link for saving, or edit an existing link
var _useLink = function() {
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
		var sortableLinks = $(_sortableLinksSelector);
	
		if (formData.state == "1") {
			// We need to replace an existing link
			var linkHtml = null;
			var target = null;
			
			var links = _identifyHiddenLinkDataList(sortableLinks);
			for (var i = 0; i < links.length; i++) {
				if (links[i].ordering == formData.ordering) {
					target = links[i];
					break;
				}
			}
			
			if (target) {
				var span = target.span;
				formData.childId = target.childId;
				span.innerHTML = _formatHiddenLinkData(formData);
			}
		}
		else {
			// This is a new link - append it to the end of any existing links
			linkHtml = $("#link-template>div").clone(true);
			var links = _identifyHiddenLinkDataList(sortableLinks);
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
			
			if (_linkerTree) {
				formData.childId = _linkerTree.activeNode.key;
				
				if (formData.childId) {
					 
					$.ajax(_ctx + "/rest/item/" + formData.childId + "/name", {
						type: "POST",
						cache: false,
						dataType: "text",
						success: function(itemName, status, z) {
							linkHtml.find("div.left span.link-identifier").html(formData.type + " (" + formData.name + "): " + itemName);
							linkHtml.find("div.right button.link-linker").attr("data-id", formData.childId);
							linkHtml.find("div.right span.hide").html(_formatHiddenLinkData(formData));				
							linkHtml.appendTo(sortableLinksContainer);					
						}
					});
				}
			}
		}	   	  	
	}
	else {
		showDialog("dialog-choose-linktype");
	}
};

var _closeLinkForm = function() {
	linkDialog.dialog("close");
	$("#linknav-container").css("display", "block");
	_setLinkForm(["unknown","unknown","","-1"], "0");
};

var _setLinkForm = function(data, state) {
	var form = $("#addlinkdiv");	
	form.find("select[name='linktype']").val(data[0]);
	form.find("select[name='linkname']").val(data[1]);
	form.find("input[name='linkdata']").val(data[2]);
	form.find("input[name='linkId']").val(data[3]);
	form.find("input[name='state']").val(state);	
};

var _repopulateLinkNameDropdown = function(linkType, value) {
	var selector = $("#addlinkdiv select[name='linkname']");
	selector.empty();
	
	$.ajax(_ctx + "/rest/linknames/" + _siteId + "/" + linkType, {
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

/* We are filtering the left array, by comparing it's elements with the right array.
 * All links that are NOT shortcuts will be filtered out. So will all MATCHING shortcuts.
 * leftIsFancytreeNode = true indicates the left array contains fancytree nodes, and the right array contains form data.
 * leftIsFancytreeNode = false indicates the left array contains form data, and the right array contains fancytree nodes.
 */
var _filterShortcuts = function(left, leftIsFancytreeNode, right) {
	return left.filter(function(node, index, fullArray) {
	
		var isShortcut = leftIsFancytreeNode ? node.data.shortcut : node.shortcut;	
		if (! isShortcut) {
			return false;
		}
		
		for (var j = 0; j < right.length; j++) {
			isShortcut = leftIsFancytreeNode ? right[j].shortcut : right[j].data.shortcut
			if (isShortcut && right[j].key == node.key) {
				return false;
			}
		}
		
		return true;
	});
}

var _refreshShortcuts = function(parentKey, updatedChildData) {
	var existingParentNode = _tree.getNodeByKey(parentKey);
	if (existingParentNode) {
		var existingChildren = existingParentNode.getChildren();
	
		// Remove non-shortcuts, and matching shortcuts from each array
		// First, existing shortcuts
		var filteredExisting = _filterShortcuts(existingChildren, true, updatedChildData);
		
		// Next, the updated form data
		var filteredUpdates = _filterShortcuts(updatedChildData, false, existingChildren);;
		
		// Remove any nodes remaining in the filteredExisting array
		for (var j = 0; j < filteredExisting.length; j++) {
			existingParentNode.removeChild(filteredExisting[j]);
		}
		
		// Add any nodes remaining in the filteredUpdates array
		for (var i = 0; i < filteredUpdates.length; i++) {
			existingParentNode.addNode(filteredUpdates[i]);
		}		
	}
};

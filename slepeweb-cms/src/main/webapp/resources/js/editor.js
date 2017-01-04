var messages = [
  "Failed to update item",
  "Field data successfully updated",
  "Item successfully updated",
  "New item successfully created",
  "Failed to create new item",
  "Item(s) successfully trashed",
  "Failed to trash the item(s)",
  "Item successfully moved",
  "Failed to move the item",
  "Links successfully updated",
  "Failed to update links",
  "Failed to retrieve breadcrumb trail"
];

var flashError = function(id) {
	flashMessage(messages[id], "red");
};

var flashSuccess = function(id) {
	flashMessage(messages[id], "green");
};

var flashMessage = function(msg, clazz) {
	$("#status-block").addClass(clazz).append(msg);
	$("#bell").get(0).play();
};
	
// Get form field names and values for forms on item-editor 
var getFieldsFormInputData = function() {
	var result = {};
	$("#field-form input, #field-form textarea, #field-form select").each(function(i, obj) {
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

var pageEditorUrlPrefix = _ctx + "/page/editor/";

var gotoPage = function(suffix, code, status) {
	var url = pageEditorUrlPrefix + suffix;
	if (code > -1) {
		var param = status == 1 ? "msg" : "err";
		url += ("?" + param + "=" + code);
	}
	
	window.location = url; 
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

// (Re-)render the forms
var renderItemForms = function(nodeKey, activeTab) {
	$.ajax(_ctx + "/rest/item/editor", {
		cache: false,
		data: {key: nodeKey}, 
		dataType: "html",
		mimeType: "text/html",
		
		// On successful loading of forms 
		success: function(html, status, z) {
			var tabsdiv = $("#item-editor");
			tabsdiv.empty().append(html);
			
			// Re-build tabs 
			if (tabsdiv.hasClass("ui-tabs")) {
				$("#item-editor").tabs("destroy");
			}
			
			// Focus on same tab as previous render
			var tabName;
			var tabNum = 0;
			var activeTabId = 0;
			
			if (activeTab) {
				$("#editor-tabs a").each(function() {
					tabName = $(this).attr("href").substring(1);
					if (activeTab == tabName) {
						activeTabId = tabNum;
					}
					tabNum++;
				});
			}

			$("#item-editor").tabs({active: activeTabId});
			
			// Identify tooltips
			$("input,select,textarea").tooltip({
				position: {
					my: "center bottom-20",
					at: "center top",
					using: function( position, feedback ) {
						$( this ).css( position );
						$( "<div>" )
						.addClass( "arrow" )
						.addClass( feedback.vertical )
						.addClass( feedback.horizontal )
						.appendTo( this );
					}
				}
			});
			
			// Add behaviour to submit core item updates 
			$("#core-button").click(function () {
				$.ajax(_ctx + "/rest/item/" + nodeKey + "/update/core", {
					type: "POST",
					cache: false,
					data: {
						name: $("#core-tab input[name='name']").val(),
						simplename: $("#core-tab input[name='simplename']").val(),
						template: $("#core-tab select[name='template']").val(),
						published: $("#core-tab input[name='published']").is(':checked'),
						tags: $("#core-tab input[name='tags']").val()
					}, 
					dataType: "json",
					success: function(json, status, z) {
						gotoPage(nodeKey, 2, 1);
					},
					error: function(json, status, z) {
						flashError(0);
					},
				});
			});
			
			// Add behaviour to submit item field updates 
			$("#field-button").click(function () {
				var theDialog = $("#dialog-fields-confirm");
				theDialog.dialog({
					resizable: false,
					height:200,
					modal: true,
					buttons: {
						"Update field values": function() {
							$.ajax(_ctx + "/rest/item/" + nodeKey + "/update/fields", {
								type: "POST",
								cache: false,
								data: getFieldsFormInputData(), 
								dataType: "json",
								success: function(json, status, z) {
									theDialog.dialog("close");
									flashSuccess(1);
								},
								error: function(json, status, z) {
									theDialog.dialog("close");
									flashError(0);
								},
							});
						},
						Cancel: function() {
							$(this).dialog("close");
						}
					}
				});
			});
			
			// Add behaviour to add new item 
			$("#add-button").click(function () {
				$.ajax(_ctx + "/rest/item/" + nodeKey + "/add", {
					type: "POST",
					cache: false,
					data: {
						template: $("#add-tab select[name='template']").val(),
						itemtype: $("#add-tab select[name='itemtype']").val(),
						name: $("#add-tab input[name='name']").val(),
						simplename: $("#add-tab input[name='simplename']").val()
					}, 
					dataType: "json",
					success: function(json, status, z) {
						gotoPage(json, 3, 1);
					},
					error: function(json, status, z) {
						gotoPage(json, 4, 0);
					},
				});
			});
			
			// Add behaviour to copy an item 
			$("#copy-button").click(function () {
				$.ajax(_ctx + "/rest/item/" + nodeKey + "/copy", {
					type: "POST",
					cache: false,
					data: {
						name: $("#copy-tab input[name='name']").val(),
						simplename: $("#copy-tab input[name='simplename']").val()
					}, 
					dataType: "json",
					success: function(json, status, z) {
						gotoPage(json, 3, 1);
					},
					error: function(json, status, z) {
						gotoPage(json, 4, 0);
					},
				});
			});
			
			// Add behaviour to trash an item 
			$("#trash-button").click(function () {
				var theDialog = $("#dialog-trash-confirm");
				theDialog.dialog({
					resizable: false,
					height:200,
					modal: true,
					buttons: {
						"Delete all items": function() {
							$.ajax(_ctx + "/rest/item/" + nodeKey + "/trash", {
								type: "POST",
								cache: false,
								data: {key: nodeKey}, 
								dataType: "json",
								success: function(json, status, z) {
									theDialog.dialog("close");
									gotoPage(json, 5, 1);
								},
								error: function(json, status, z) {
									theDialog.dialog("close");
									gotoPage(json, 6, 0);
								}
							});
						},
						Cancel: function() {
							$(this).dialog("close");
						}
					}
				});
			});
			
			// Add behaviour to template & itemtype selectors 
			$("#add-tab select[name='template']").change(function (e) {
				var typeSelector = $("#add-tab select[name='itemtype']");
				if ($(e.target).val() != "0") {
					typeSelector.val("0");
					typeSelector.attr("disabled", "true");
				}
				else {
					typeSelector.removeAttr("disabled");
				}
			});
	  		
			// Add a fancytree to identify child links 
			$("#linknav").fancytree({
				source: {
					url: _ctx + "/rest/leftnav/lazy/thread",
					data: queryParams, /* TODO: Not sure this is right */
					cache: false,
					checkbox: true
				},
				lazyLoad: function(event, data) {
					var node = data.node;
					data.result = {
						url: _ctx + "/rest/leftnav/lazy/one",
						data: {key: node.key}
					};
				},
				activate: function(event, data) {
					var linkType = $("#addlinkdiv select[name='linktype']").val();
					var linkName = $("#addlinkdiv select[name='linkname']").val();
					var parentId = nodeKey;
					var childId = data.node.key;
 	   			
					if (linkType != 'unknown' && linkName != 'unknown') {
						var selector = $("#sortable-links");
						var copy = $("#link-template li").clone(true);
	 	 	   	  	
						$.ajax(_ctx + "/rest/item/" + childId + "/name", {
							type: "POST",
							cache: false,
							dataType: "text",
							success: function(itemName, status, z) {
								copy.find("a").attr("href", pageEditorUrlPrefix + childId).html(linkType + " (" + linkName + "): " + itemName);
								copy.find("span.hide").html(parentId + "," + childId + "," + linkType + "," + linkName);
								copy.appendTo(selector);
							}
						});
					}
					else {
						showDialog("dialog-choose-linktype");
					}
				}
			});
 	    
			// Re-populate linkname options when link type is selected
			$("#addlinkdiv select[name='linktype']").change(function(e) {
				var selector = $("#addlinkdiv select[name='linkname']");
				selector.empty();
				var linkType = $(this).val();
				
				$.ajax(_ctx + "/rest/linknames/" + nodeKey + "/" + linkType, {
					type: "POST",
					cache: false,
					dataType: "json",
					success: function(result, status, z) {
						selector.append("<option value='unknown'>Choose ...</option>");
						for (var i=0; i<result.length; i++) {
							selector.append("<option value='" + result[i] + "'>" + result[i] + "</option>");
						}
					}
				});
			});
			
			// Show link addition form when 'Add link' button is clicked
			$("#addlink-button").click(function(e) {
				$("#addlinkdiv").css("visibility", "visible");
			});
			
			// Add behaviour to 'Save links' button 
			$("#savelinks-button").click(function(e) {
				var links = [];
				var parts, obj;
				
				$("#sortable-links li").each(function(index, li) {
					parts = $(li).find("span.hide").html().split(",");
					obj = {
						parentId: parts[0],
						childId: parts[1],
						type: parts[2],
						name: parts[3],
						ordering: index
					};
					links.push(obj);
				});
				
				// Ajax call to save links to db 
				$.ajax(_ctx + "/rest/links/" + nodeKey + "/save", {
					type: "POST",
					cache: false,
					data: JSON.stringify(links), 
					contentType: "application/json; charset=utf-8",
					dataType: "text",
					processData: false,
					success: function(obj, status, z) {
						// Need to refresh the page, to update the FancyTree,
						// in case a shortcut link was added/removed
						gotoPage(nodeKey, 9, 1);
					},
					error: function(obj, status, z) {
						gotoPage(nodeKey, 10, 0);
					},
				});
			});
			
			// Add behaviour to 'Remove links' button 
			$(".remove-link").click(function(e) {
				$(this).parent().remove();
			});
			
			// Add behaviour to update media content 
			function progressHandlingFunction(e){
			    if(e.lengthComputable){
			        $("progress").attr({value:e.loaded,max:e.total});
			    }
			}
			
			$("#media-button").click(function () {
				var formData = new FormData($("#media-form")[0]);
			    $.ajax({
			        url: _ctx + "/rest/item/" + nodeKey + "/update/media",
			        type: "POST",
			        xhr: function() {
			            var myXhr = $.ajaxSettings.xhr();
			            if(myXhr.upload) {
			                myXhr.upload.addEventListener("progress",progressHandlingFunction, false);
			            }
			            return myXhr;
			        },
			        success: function() {
						flashSuccess(2);
			        },
			        error: function() {
						flashError(0);
			        },
			        data: formData,
			        cache: false,
			        contentType: false,
			        processData: false
			    });
				
//				$.ajax(_ctx + "/rest/item/" + nodeKey + "/update/media", {
//					type: "POST",
//					cache: false,
//					contentType: "multipart/form-data",
//					data: {
//						media: $("#media-tab input[name='choose-media']").val(),
//					}, 
//					dataType: "json",
//					success: function(json, status, z) {
//						showDialog("dialog-update-success");
//					},
//					error: function(json, status, z) {
//						showDialog("dialog-update-error");
//					},
//				});
			});				
			
			// Initialise sortable links 
			$( "#sortable-links" ).sortable();
			$( "#sortable-links" ).disableSelection();
			
		}
	});
	
};

// Left navigation
var queryParams = {site: _siteId};
if (_editingItemId) {
	queryParams = {
		key: _editingItemId,
		site: _siteId
	};
}
	
// All the things that can only be executed once the page has been fully loaded ...
// For development purposes, expose a handle to the main FancyTree
var _tree;

$(function() {
	$("body").click(function() {
		$("#status-block").empty();
	});

	$("#leftnav").fancytree({
		extensions: ["dnd"],
		source: {
			url: _ctx + "/rest/leftnav/lazy/thread",
			data: queryParams,
			cache: false,
			checkbox: true,
			complete: function() {
				// On completion of loading the tree, activate the node for the current item
				_tree = $("#leftnav").fancytree("getTree");
				var key = "" + _editingItemId;
				var node = _tree.getNodeByKey(key);
				_tree.activateKey(node.key);
			}
		},
		lazyLoad: function(event, data) {
			var node = data.node;
			data.result = {
				url: _ctx + "/rest/leftnav/lazy/one",
				data: {key: node.key}
			};
		},
		dnd: {
			autoExpandMS: 400,
			focusOnClick: true,
			preventVoidMoves: true, // Prevent dropping nodes 'before self', etc.
			preventRecursiveMoves: true, // Prevent dropping nodes on own descendants
			dragStart: function(node, data) {
				return true;
			},
			dragEnter: function(node, data) {
				return true;
			},
			dragDrop: function(node, data) {
				var theDialog = $("#dialog-move-confirm");
				theDialog.dialog({
					resizable: false,
					height:200,
					modal: true,
					buttons: {
						"Move item": function() {
							$.ajax(_ctx + "/rest/item/" + removeShortcutMarker(data.otherNode.key) + "/move", {
								type: "POST",
								cache: false,
								data: {
									targetId: node.key,
									parentId: data.otherNode.parent.key,
									shortcut: data.otherNode.data.shortcut,
									mode: data.hitMode
								}, 
								dataType: "json",
								success: function(json, status, z) {
									theDialog.dialog("close");
									gotoPage(json, 7, 1);
								},
								error: function(json, status, z) {
									theDialog.dialog("close");
									gotoPage(json, 8, 0);
								}
							});
						},
						Cancel: function() {
							$(this).dialog("close");
						}
					}
				});
			}
		},
		activate: function(event, data) {
			if (! data.node.data.shortcut) {
				// Update the item forms
				var tabName = $("li.ui-tabs-active").attr("aria-controls");
				renderItemForms(data.node.key, tabName);
			}
			else {
				// Do not allow the user to work with the shortcut item - automatically
				// navigate to the real item
				var key = removeShortcutMarker(data.node.key);
				var node = _tree.getNodeByKey(key);
				
				if (node) {
					_tree.activateKey(node.key);
				}
				else {
					// The 'real' item hasn't been loaded yet - ask the server for the breadcrumb trail
					$.ajax(_ctx + "/rest/breadcrumbs/" + key, {
						cache: false,
						dataType: "json",
						mimeType: "application/json",
						success: function(json, status, z) {
							_tree.loadKeyPath(json, function(node, stats) {
								if (stats === "ok") {
								    node.setActive();
								}
							});
						},
						error: function(json, status, z) {
							flashError(11);
						}
					});
				}
			}
		}
	});	
	
	$("#site-selector").change(function(e){
		window.location = _ctx + "/page/site/select/" + $(this).val();
	});

	// Render item management forms when page is first loaded
	if (_editingItemId) {
		renderItemForms(_editingItemId);
	}
	
	// Render flash message when page is first loaded
	if (_flashMessageCode) {
		flashSuccess(_flashMessageCode);
	}
	else if (_flashErrorCode) {
		flashError(_flashErrorCode);
	}
});

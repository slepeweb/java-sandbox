$(function() {
	// Get form field names and values for forms on item-editor 
	var getFieldsFormInputData = function() {
		var result = {};
		$("#field-form input, #field-form textarea").each(function(i, obj) {
			result[$(obj).attr("name")] = $(obj).val();
		});
		return result;
	};
	
	var showDialog = function(id) {
		$("#" + id).dialog({
			modal: true,
			buttons: {
				Ok: function() {
					$(this).dialog("close");
				}
			}
		});
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
				$("#item-editor").tabs({active: activeTab});
				
				// Add behaviour to submit core item updates 
				$("#core-button").click(function () {
					$.ajax(_ctx + "/rest/item/" + nodeKey + "/update/core", {
						type: "POST",
						cache: false,
						data: {
							name: $("#core-tab input[name='name']").val(),
							simplename: $("#core-tab input[name='simplename']").val(),
							published: $("#core-tab input[name='published']").is(':checked')
						}, 
						dataType: "json",
						success: function(json, status, z) {
							showDialog("#dialog-update-success");
						},
						error: function(json, status, z) {
							showDialog("#dialog-update-error");
						},
					});
				});
				
				// Add behaviour to submit item field updates 
				$("#field-button").click(function () {
					$.ajax(_ctx + "/rest/item/" + nodeKey + "/update/fields", {
						type: "POST",
						cache: false,
						data: getFieldsFormInputData(), 
						dataType: "json",
						success: function(json, status, z) {
							showDialog("#dialog-update-success");
						},
						error: function(json, status, z) {
							showDialog("#dialog-update-error");
						},
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
							showDialog("#dialog-add-success");
							window.location = _ctx + "/page/editor/" + json;
						},
						error: function(json, status, z) {
							showDialog("#dialog-add-error");
							window.location = _ctx + "/page/editor/" + json;
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
										window.location = _ctx + "/page/editor/" + json;
									},
									error: function(json, status, z) {
										theDialog.dialog("close");
										window.location = _ctx + "/page/editor/" + json;
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
						var linkName = $("#addlinkdiv input[name='linkname']").val();
						var parentId = nodeKey;
						var childId = data.node.key;
	 	   			
						if (linkType != 'unknown') {
							var selector = $("#sortable-links");
							var copy = $("#link-template li").clone(true);
		 	 	   	  	
							$.ajax(_ctx + "/rest/item/" + childId + "/name", {
								type: "POST",
								cache: false,
								dataType: "text",
								success: function(itemName, status, z) {
									copy.find("a").attr("href", _ctx + "/page/editor/" + childId).html(linkType + " (" + linkName + "): " + itemName);
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
							showDialog("dialog-update-success");
						},
						error: function(obj, status, z) {
							showDialog("dialog-update-error");
						},
					});
				});
				
				// Add behaviour to 'Remove links' button 
				$(".remove-link").click(function(e) {
					$(this).parent().remove();
				});
				
				// Initialise sortable links 
				$( "#sortable-links" ).sortable();
				$( "#sortable-links" ).disableSelection();
			}
		});
	};
  
	// Left navigation
	var queryParams = {};
	if (_editingItemId) {
		queryParams = {key: _editingItemId};
	}
		
	$("#leftnav").fancytree({
		extensions: ["dnd"],
		source: {
			url: _ctx + "/rest/leftnav/lazy/thread",
			data: queryParams,
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
							$.ajax(_ctx + "/rest/item/" + data.otherNode.key + "/move", {
								type: "POST",
								cache: false,
								data: {
									targetId: node.key,
									mode: data.hitMode
								}, 
								dataType: "json",
								success: function(json, status, z) {
									theDialog.dialog("close");
									window.location = _ctx + "/page/editor/" + json;
								},
								error: function(json, status, z) {
									theDialog.dialog("close");
									window.location = _ctx + "/page/editor/" + json;
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
			var tabName = $("li.ui-tabs-active").attr("aria-controls");
			var tabNum = 0;
			if (tabName == 'field-tab') {tabNum = 1;}
			else if (tabName == 'links-tab') {tabNum = 2;}
			else if (tabName == 'add-tab') {tabNum = 3;}
			renderItemForms(data.node.key, tabNum);
		}
	});		

	if (_editingItemId) {
		renderItemForms(_editingItemId);
	}
});

<%@ tag %><%@ 
	taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %><%@ 
	taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %><%@ 
	taglib prefix="cms" tagdir="/WEB-INF/tags/cms"%>

<title>slepe web solutions | </title>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<meta name="description" content="" />
<meta name="keywords" content="" />
<link rel="stylesheet" href="/resources/css/main.css" type="text/css">

<link rel="stylesheet" href="//code.jquery.com/ui/1.11.0/themes/start/jquery-ui.css">
<script src="//ajax.googleapis.com/ajax/libs/jquery/1/jquery.min.js" type="text/javascript"></script>
<script src="//ajax.googleapis.com/ajax/libs/jqueryui/1/jquery-ui.min.js" type="text/javascript"></script>

<!-- Include Fancytree skin and library -->
<link href="/resources/fancytree/skin-win8/ui.fancytree.min.css" rel="stylesheet" type="text/css">
<script src="/resources/fancytree/jquery.fancytree.min.js" type="text/javascript"></script>
<script src="/resources/fancytree/jquery.fancytree.dnd.js" type="text/javascript"></script>
<!-- Initialize the tree when page is loaded -->
<script type="text/javascript">

  $(function(){
	  // Get form field names and values for forms on item-editor 
	  var getFieldsFormInputData = function() {
		  var result = {};
		  $("#field-form input, #field-form textarea").each(function(i, obj) {
			  result[$(obj).attr("name")] = $(obj).val();
		  });
		  return result;
	  };
	  
	  // (Re-)render the forms
	  var renderItemForms = function(nodeKey) {
  		$.ajax("/rest/cms/item-editor", {
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
					$("#item-editor").tabs();
					
					// Add behaviour to submit core item updates 
					$("#core-button").click(function () {
						$.ajax("/rest/cms/item/" + nodeKey + "/update/core", {
								type: "POST",
			    			cache: false,
			    			data: {
			    				name: $("#core-tab input[name='name']").val(),
			    				simplename: $("#core-tab input[name='simplename']").val()
			    			}, 
			    			dataType: "json",
			    			success: function(json, status, z) {
			    				window.alert("Success");
			    			}
						});
					});
					
					// Add behaviour to submit item field updates 
					$("#field-button").click(function () {
						$.ajax("/rest/cms/item/" + nodeKey + "/update/fields", {
								type: "POST",
			    			cache: false,
			    			data: getFieldsFormInputData(), 
			    			dataType: "json",
			    			success: function(json, status, z) {
			    				window.alert("Success");
			    			}
						});
					});
					
					// Add behaviour to add new item 
					$("#add-button").click(function () {
						$.ajax("/rest/cms/item/" + nodeKey + "/add", {
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
			    				window.alert("Success");
			    				window.location = "/cms/editor/" + json;
			    			}
						});
					});
					
					// Add behaviour to trash an item 
					$("#trash-button").click(function () {
						if (window.confirm("This will delete the current item PLUS ALL descendant items. Are you sure you want to do this?")) {
							$.ajax("/rest/cms/item/" + nodeKey + "/trash", {
									type: "POST",
				    			cache: false,
									data: {key: nodeKey}, 
				    			dataType: "json",
				    			success: function(json, status, z) {
				    				window.alert("Success");
				    				window.location = "/cms/editor/" + json;
				    			}
							});
						}
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
		 	    		url: "/rest/cms/leftnav/lazy/thread",
		 	    		data: queryParams,
		 	    		cache: false,
		 	    		checkbox: true
		 	    	},
		 	    	lazyLoad: function(event, data) {
		 	    		var node = data.node;
		 	    		data.result = {
		 	    			url: "/rest/cms/leftnav/lazy/one",
		 	    			data: {key: node.key}
		 	    		};
		 	    	},
		 	   		activate: function(event, data) {
		 	   			var linkType = $("#addlinkdiv select[name='linktype']").val();
		 	   			var linkName = $("#addlinkdiv input[name='linkname']").val();
		 	   			var parentId = nodeKey;
		 	   			var childId = data.node.key;
		 	   			
			 	 		  if (linkType != 'unknown') {
			 	 	   	  	var selector = $("#sortable");
			 	 	   	 		var copy = $("#link-template li").clone(true);
			 	 	   	  	
			 	 					$.ajax("/rest/cms/item/" + childId + "/name", {
			 	 						type: "POST",
			 	 						cache: false,
			 	 						dataType: "text",
			 	 						success: function(itemName, status, z) {
			 	 			   	  	copy.find("a").attr("href", "/cms/editor/" + childId).html(linkType + " (" + linkName + "): " + itemName);
			 	 			   	  	copy.find("span.hide").html(parentId + "," + childId + "," + linkType + "," + linkName);
			 	 			   	  	copy.appendTo(selector);
			 	 						}
			 	 					});
			 	 			  }
			 	 			  else {
			 	 				  window.alert("Please choose a link type");
			 	 			  }
		 	   			}
		 	    });
		 	    
					// Show link tools when 'Add link' button is clicked
		 	    $("#addlink-button").click(function(e) {
		 	    	$("#addlinkdiv").css("visibility", "visible");
		 	    });
					
					// Add behaviour to 'Save links' button 
					$("#savelinks-button").click(function(e) {
						var links = [];
						var parts, obj;
						
						$("#sortable li").each(function(index, li) {
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
						
		  			$.ajax("/rest/cms/links/" + nodeKey + "/save", {
							type: "POST",
							cache: false,
							data: JSON.stringify(links), 
							contentType: "application/json; charset=utf-8",
							dataType: "text",
							processData: false,
							success: function(obj, status, z) {
								// TODO: add dialog 
								window.alert("Success");
							},
							error: function(obj, status, z) {
								window.alert("Failure");
							},

						});
					});
					
					// Add behaviour to 'Remove links' button 
					$(".remove-link").click(function(e) {
						$(this).parent().remove();
					});
					
		 		  // Initialise sortable links 
		 	    $( "#sortable" ).sortable();
		 	    $( "#sortable" ).disableSelection();
				}
			});
	  };
	  
		// Left navigation
		var queryParams = {};
 		<c:if test="${not empty editingItem}">
 			queryParams = {key: "${editingItem.id}"};
		</c:if>
 			
    $("#leftnav").fancytree({
    	extensions: ["dnd"],
    	source: {
    		url: "/rest/cms/leftnav/lazy/thread",
    		data: queryParams,
    		cache: false,
    		checkbox: true
    	},
    	lazyLoad: function(event, data) {
    		var node = data.node;
    		data.result = {
    			url: "/rest/cms/leftnav/lazy/one",
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
        	// hitMode can be one of: 'after', 'before', 'over' 
//					TODO: WINDOW.CONFIRM isn't working here ...      	
//         	if (window.confirm("Are you sure you want to move the selected item?")) {
						$.ajax("/rest/cms/item/" + data.otherNode.key + "/move", {
							type: "POST",
							cache: false,
							data: {
								targetId: node.key,
								mode: data.hitMode
							}, 
							dataType: "json",
							success: function(json, status, z) {
								window.alert("Success");
								window.location = "/cms/editor/" + json;
							}
						});
				        
					  // data.otherNode.moveTo(node, data.hitMode);
// 	        }
        }
      },
   		activate: function(event, data) {
    		renderItemForms(data.node.key);
    	}
    });		
    
		<c:if test="${not empty editingItem}">
			renderItemForms("${editingItem.id}");
		</c:if>
		
	});
		
</script>

<cms:extraCSS />
<cms:extraJS />

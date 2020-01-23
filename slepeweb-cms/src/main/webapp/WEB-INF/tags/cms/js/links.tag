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
		var linkData = encodeURIComponent($("#addlinkdiv input[name='linkdata']").val());
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
					copy.find("span.hide").html(parentId + "," + childId + "," + linkType + "," + linkName + "," + linkData);
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
	$("#addlinkdiv").css("display", "block");
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
			data: decodeURIComponent(parts[4]),
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
			fetchItemEditor(nodeKey, obj);
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

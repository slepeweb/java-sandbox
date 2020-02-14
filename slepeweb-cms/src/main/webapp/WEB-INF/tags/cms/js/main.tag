<%@ tag %><%@ 
    taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%><%@ 
    taglib prefix="cms" tagdir="/WEB-INF/tags/cms"%><%@ 
    taglib prefix="cmsjs" tagdir="/WEB-INF/tags/cms/js"%>
        
<cms:debug><!-- tags/cms/js/main.tag --></cms:debug>

<script type="text/javascript">
	<cmsjs:support />
	
	// (Re-)render the forms
	var renderItemForms = function(nodeKey, activeTab) {
		$.ajax(_ctx + "/rest/item/editor", {
			cache: false,
			data: {key: nodeKey}, 
			dataType: "html",
			mimeType: "text/html",
			
			// On successful loading of forms 
			success: function(html, status, z) {
				<cmsjs:init />
				<cmsjs:core />
				<cmsjs:fields />
				<cmsjs:addnew />
				<cmsjs:copy />
				<cmsjs:version />
				<cmsjs:trash />
				<cmsjs:links />
				<cmsjs:media />
					
				// Initialise sortable links 
				$( "#sortable-links" ).sortable();
				$( "#sortable-links" ).disableSelection();
				
				// Refresh history selector
				refreshHistory(_siteId);
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
	var _tree, _linkerTree;
	
	$(function() {
		$("body").click(function() {
			$("#status-block").empty();
		});
	
		<cmsjs:leftnav />
	
		$("#site-selector").change(function(e){
			window.location = _ctx + "/page/site/select/" + $(this).val();
		});
	
		// Render item management forms when page is first loaded
		if (_editingItemId) {
			// On first call to renderItemForms, we use _editingItemId.
			// On subsequent ajax calls driven by selecting items on the left nav, we use nodeKey.
			renderItemForms(_editingItemId, _activeTab);
		}
		
		// Render flash message when page is first loaded
		flashMessage(_flashMessage);
	});
</script>


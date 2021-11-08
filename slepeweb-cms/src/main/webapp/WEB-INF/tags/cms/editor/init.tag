<%@ tag %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>
        
<cms:debug><!-- tags/cms/editor/mainjs.tag --></cms:debug>

<script type="text/javascript">

	/*
		What happens in this file happens once per page load. Subsequent refreshes of the item
		editor do not execute these instructions again.
	*/
	_cms.ctx = "${applicationContextPath}";
	_cms.pageEditorUrlPrefix = _cms.ctx + "/page/editor/";
	_cms.siteId = <c:choose><c:when test="${not empty site}">${site.id}</c:when><c:otherwise>0</c:otherwise></c:choose>;
	_cms.editingItemId = null;
	_cms.siteDefaultLanguage = "en";
	_cms.editingItemIsShortcut = false;
	
	<c:if test="${not empty editingItem}">
		_cms.editingItemId = ${editingItem.origId};
		_cms.siteId = ${editingItem.site.id};
		_cms.siteDefaultLanguage = "${editingItem.site.language}";
		_cms.siteShortname = "${editingItem.site.shortname}";
		_cms.editingItemIsShortcut = ${editingItem.shortcut};
		_cms.editingItemIsWriteable = ${editingItem.accessible};
	</c:if>
	
	// Flash messages passed through when window.location is set 
	_cms.flashMessage = null;
	<c:if test="${not empty _flashMessage}">
		_cms.flashMessage = {};
		_cms.flashMessage.error = ${_flashMessage.error};
		_cms.flashMessage.message = "${_flashMessage.message}";
	</c:if>
	
	_cms.productTypeId = "${_productTypeId}";
	_cms.activeTab = "core-tab";
	_cms.queryParams = {site: _cms.siteId};
	
	if (_cms.editingItemId) {
		_cms.queryParams = {
			key: _cms.editingItemId,
			site: _cms.siteId
		};
	}
	

		
	$(function() {
		$("body").click(function() {
			$("#status-block").empty();
		});
	
		$("#site-selector").change(function(e){
			window.location = _cms.ctx + "/page/site/select/" + $(this).val();
		});
	
		/* 
			The leftnav is built once only per page request. Subsequent UI actions
			that refresh tabs on the item editor will NOT rebuild the tree.
		*/
		_cms.leftnav.define.dialog();
		_cms.leftnav.define.fancytree();
		
		// Certain behaviours need only be defined on page load
		_cms.links.onpageload();
		_cms.dialog.onpageload();
		
		// Render item management forms when page is first loaded
		if (_cms.editingItemId) {
			// On first call to renderItemForms, we use _editingItemId.
			// On subsequent ajax calls driven by selecting items on the left nav, we use nodeKey.
			_cms.support.renderItemForms(_cms.editingItemId, _cms.activeTab);
		}
		
		// Render flash message when page is first loaded
		_cms.support.flashMessage(_cms.flashMessage);
	});
</script>


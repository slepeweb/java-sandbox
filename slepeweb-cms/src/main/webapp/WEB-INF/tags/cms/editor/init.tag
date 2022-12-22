<%@ tag %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>
        
<cms:debug><!-- tags/cms/editor/init.tag --></cms:debug>

<script type="text/javascript">
	/*
	What happens in this file happens once per page load. Subsequent refreshes of the item
	editor do not execute these instructions again.
	*/
	
	var _cms = {
		support: {
			dialog: {}
		},
		leftnav: {
			behaviour: {}
		}
	}

	_cms.init = function(nodeKey, html, activeTab) {
		// Re-build the item editor tabs, and select the required tab
		_cms.support.refreshAllTabs(html, activeTab);
	}
	
	_cms.ctx = "${applicationContextPath}";
	_cms.pageEditorUrlPrefix = _cms.ctx + "/page/editor/";
	_cms.siteId = ${editingItem.site.id};
	_cms.siteDefaultLanguage = "${editingItem.site.language}";
	_cms.siteShortname = "${editingItem.site.shortname}";
	_cms.rootItemOrigId = ${rootItem.origId};

	/*
		These variables are item-specific. They get updated by ajax calls
		when the user picks a new item to navigate to.
	*/
	_cms.editingItemId = ${editingItem.origId};
	_cms.editingItemIsShortcut = ${editingItem.shortcut};
	_cms.editingItemIsWriteable = ${editingItem.accessible};
	
	_cms.undoRedo = {
			status: ${_undoRedoStatus}
	};
	
	_cms.currentItemName = 'none';
	_cms.currentItemFlagged = 'no';
	_cms.numDeletableItems= 0;
	_cms.editingItemIsShortcut = false;
	_cms.editingItemIsWriteable = false;
	_cms.rightNavKey = 0;
	_cms.leftNavKey = 0;
	_cms.upNavKey = 0;
	_cms.downNavKey = 0;

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
	
		/* 
			The leftnav is built once only per page request. Subsequent UI actions
			that refresh tabs on the item editor will NOT rebuild the tree.
		*/
		_cms.leftnav.define.dialog();
		_cms.leftnav.define.fancytree();
		
		// Certain behaviours need only be defined on page load
		_cms.links.onpageload();
		_cms.dialog.onpageload();
		
		// Load the editors
		_cms.support.renderItemForms(_cms.editingItemId, _cms.activeTab);
		
		// Render flash message when page is first loaded
		_cms.support.flashMessage(_cms.flashMessage);

		// Display the undo/redo/clear buttons
		_cms.undoRedo.displayAll(_cms.undoRedo.status);
		
		// Set the behaviour of the undo/redo/clear buttons, once per page load
		_cms.undoRedo.behaviour('div#undo-icon', 'undo');
		_cms.undoRedo.behaviour('div#redo-icon', 'redo');
		_cms.undoRedo.behaviour('div#kill-icon', 'clear', true);
		
	});</script>


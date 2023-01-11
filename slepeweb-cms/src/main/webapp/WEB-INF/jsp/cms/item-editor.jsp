<%@ 
	page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false"%><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>

<script type="text/javascript">
	_cms.currentItemName = `${editingItem.name}`; // back-ticks are deliberate!
	_cms.currentItemFlagged = '${_itemIsFlagged ? 'yes' : 'no'}';
	_cms.numDeletableItems = ${_numItemsInSection};
	_cms.editingItemIsShortcut = ${editingItem.shortcut};
	_cms.editingItemIsWriteable = ${editingItem.accessible};
	_cms.rightNavKey = ${_navkeys.right};
	_cms.leftNavKey = ${_navkeys.left};
	_cms.upNavKey = ${_navkeys.up};
	_cms.downNavKey = ${_navkeys.down};
</script>

<c:set var="_resetHelp" scope="request">title="Cancel changes and reset form"</c:set>

<ul id="editor-tabs">
	<li><a href="#core-tab">Core</a></li>
	<c:if test="${not editingItem.shortcut}"><li><a href="#field-tab">Fields</a></li></c:if>
	<li><a href="#links-tab">Links</a></li>
	<c:if test="${not editingItem.shortcut and editingItem.type.media}">
		<li><a href="#media-tab">Media</a></li>
	</c:if>
	<li><a href="#add-tab">Add new</a></li>
	
	<c:if test="${editingItem.path ne '/'}">
		<c:if test="${not editingItem.shortcut}"><li><a href="#copy-tab">Copy</a></li></c:if>
		<li><a href="#move-tab">Move</a></li>
	</c:if>
	
	<c:if test="${not editingItem.shortcut}"><li><a href="#version-tab">Version</a></li></c:if>
	<li><a href="#misc-tab">Misc</a></li>
</ul>

<div id="core-tab"><edit:core /></div>
<c:if test="${not editingItem.shortcut}"><div id="field-tab"><edit:field /></div></c:if>
<div id="links-tab"><edit:links /></div>

<c:if test="${not editingItem.shortcut and editingItem.type.media}">
	<div id="media-tab"><edit:media /></div>
</c:if>

<div id="add-tab"><edit:addnew /></div>

<c:if test="${editingItem.path ne '/'}">
	<c:if test="${not editingItem.shortcut}"><div id="copy-tab"><edit:copy /></div></c:if>
	<div id="move-tab"><edit:move /></div>
</c:if>

<c:if test="${not editingItem.shortcut}"><div id="version-tab"><edit:version /></div></c:if>
<div id="misc-tab"><edit:misc /></div>
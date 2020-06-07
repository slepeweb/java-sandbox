<%@ 
	page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false"%><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>

<div id="current-item-name" class="hide">${editingItem.name}</div>

<ul id="editor-tabs">
	<li><a href="#core-tab">Core</a></li>
	<li><a href="#field-tab">Fields</a></li>
	<li><a href="#links-tab">Links</a></li>
	<c:if test="${editingItem.type.media}">
		<li><a href="#media-tab">Media</a></li>
	</c:if>
	<li><a href="#add-tab">Add new</a></li>
	
	<c:if test="${editingItem.path ne '/'}">
		<li><a href="#copy-tab">Copy</a></li>
		<li><a href="#move-tab">Move</a></li>
	</c:if>
	
	<li><a href="#version-tab">Version</a></li>
	<li><a href="#misc-tab">Misc</a></li>
</ul>

<div id="core-tab"><edit:core /></div>
<div id="field-tab"><edit:field /></div>
<div id="links-tab"><edit:links /></div>

<c:if test="${editingItem.type.media}">
	<div id="media-tab"><edit:media /></div>
</c:if>

<div id="add-tab"><edit:addnew /></div>

<c:if test="${editingItem.path ne '/'}">
	<%-- Avoid calling the getCopyDetails() method more than once per request --%>
	<c:set var="_copyDetails" value="${editingItem.copyDetails}" scope="request" />
	<div id="copy-tab"><edit:copy /></div>
	<div id="move-tab"><edit:move /></div>
</c:if>

<div id="version-tab"><edit:version /></div>
<div id="misc-tab"><edit:misc /></div>
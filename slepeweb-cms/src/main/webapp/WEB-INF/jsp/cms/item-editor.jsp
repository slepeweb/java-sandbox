<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false"%><%@ 
    taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%><%@ 
    taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%><%@ 
    taglib prefix="cms" tagdir="/WEB-INF/tags/cms"%><%@ 
    taglib prefix="edit" tagdir="/WEB-INF/tags/cms/editor"%>

<c:set var="_showVersionTab" 
	value="${editingItem.type.name ne 'ContentFolder' and (editingItem.published or editingItem.version > 1)}"
	scope="request" />

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
	</c:if>
	<c:if test="${_showVersionTab}">
		<li><a href="#version-tab">Version</a></li>
	</c:if>
		<li><a href="#misc-tab">Misc</a></li>
</ul>

<edit:core />
<edit:fields />
<edit:links />
<edit:media />
<edit:addnew />
<edit:copy />
<edit:version />

<div id="misc-tab">
	<div>
		<button id="reindex-button" type="button">Re-index</button>
	</div>

	<div>
		<button id="trash-show-button" type="button">Show bin</button>
		<div id="trash-container"></div>
	</div>
</div>

<%@ tag %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>
        
<cms:debug><!-- tags/cms/editor/version.tag --></cms:debug>
	
<table>
	<tr>
		<th>Version</th>
		<th>Id</th>
		<th>Published</th>
		<th>Editable</th>
		<th>Date last updated</th>
	</tr>
	
	<c:forEach items="${allVersions}" var="_i">
		<tr>
			<td>${_i.version}</td>
			<td>${_i.id}</td>
			<td><c:if test="${_i.published}">X</c:if></td>
			<td><c:if test="${_i.editable}">X<c:set var="editableVersion" value="${_i.version}" /></c:if></td>
			<td>${_i.dateUpdated}</td>
		</tr>
	</c:forEach>
</table>

<div id="version-buttons">
	<c:choose><c:when test="${editingItem.published}">
		<p>Create a new version (${editableVersion + 1})</p>
		<button id="version-button" type="button">Version</button>
	</c:when><c:otherwise>
		<p>(You cannot create a new version until this version is Published.)</p>
		<button id="version-button-disabled" disabled="disabled" type="button">Version</button>
	</c:otherwise></c:choose>
	
	<c:if test="${editingItem.version > 1}">
		<p>Revert to previous version (${editableVersion - 1})</p>
		<button id="revert-button" type="button">Revert</button>
	</c:if>
</div>
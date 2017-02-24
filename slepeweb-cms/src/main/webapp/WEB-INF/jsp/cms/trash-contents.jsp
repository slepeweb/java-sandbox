<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false"%><%@ 
    taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%><%@ 
    taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%><%@ 
    taglib prefix="cms" tagdir="/WEB-INF/tags/cms"%>

<c:choose><c:when test="${fn:length(_trashContents) > 0}">
	<table id="trash-table">
		<tr><th id="select-all-trash">Select</th><th>Path</th><th>Version</th></tr>
		<c:forEach items="${_trashContents}" var="_item">
			<tr>
				<td><input type="checkbox" value="${_item.id}" /></td>
				<td>${_item.path}</td>
				<td>${_item.version}</td>
			</tr>
		</c:forEach>
	</table>
	
	<div id="trash-action">
		<button id="trash-empty-button" type="button">Empty</button>
		<div class="radio-set-container">
			<div class="radio-set"><input type="radio" name="trash-item-scope" value="selected" checked /> <span>Selected</span></div>
			<div class="radio-set"><input type="radio" name="trash-item-scope" value="all" /> <span>All</span></div>
		</div>
		<button id="trash-restore-button" type="button">Restore</button>
	</div>
	
</c:when><c:otherwise>
	<p>The bin is empty.</p>
</c:otherwise></c:choose>

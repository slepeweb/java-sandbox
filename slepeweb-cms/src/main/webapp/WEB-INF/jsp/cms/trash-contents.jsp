<%@ include file="/WEB-INF/jsp/tagDirectives.jsp" %>

<c:choose><c:when test="${fn:length(_trashContents) > 0}">
	<p><span class="in-your-face-text">NOTE</span> that trashed items are displayed in path order. Should you restore an item whose 
		parent is also in the bin, then you won't see that item in the content structure until the parent has also been restored.</p>

	<p><span class="in-your-face-text">NOTE ESPECIALLY</span> that once you empty items from the bin, they will be destroyed
		for ever, and not retrievable through this app.</p>
		
	<table id="trash-table">
		<tr><th id="select-all-trash">Select</th><th>Name</th><th>Path</th><th>Version</th></tr>
		<c:forEach items="${_trashContents}" var="_item">
			<tr>
				<td><input type="checkbox" value="${_item.origId}" /></td>
				<td>${_item.name}</td>
				<td>${_item.path}</td>
				<td>${_item.version}</td>
			</tr>
		</c:forEach>
	</table>
	
	<div id="trash-action">
		<button id="trash-restore-button" type="button">Restore</button>
		<div class="radio-set-container">
			<div class="radio-set"><input type="radio" name="trash-item-scope" value="selected" checked /> <span>Selected</span></div>
			<div class="radio-set"><input type="radio" name="trash-item-scope" value="all" /> <span>All</span></div>
		</div>
		<button id="trash-empty-button" type="button">Empty</button>
	</div>
	
</c:when><c:otherwise>
	<p>The bin is empty.</p>
</c:otherwise></c:choose>

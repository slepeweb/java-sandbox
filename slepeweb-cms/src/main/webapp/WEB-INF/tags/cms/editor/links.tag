<%@ tag %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>
        
<cms:debug><!-- tags/cms/editor/links.tag --></cms:debug>
	
<h2>Linked to</h2>

<div id="sortable-links">
	<c:forEach items="${editingItem.allLinksShortcutsLast}" var="link" varStatus="_stat">
		<div class="sortable-link ui-state-default">
			<div class="left">
				<span class="ui-icon ui-icon-arrowthick-2-n-s"></span>
				<span class="link-identifier">${link}</span>
			</div>
			
			<div class="right">
				<button class="link-linker" data-id="${link.child.id}" title="Navigate to this item"><i class="fas fa-location-arrow"></i></button>
				<button class="edit-link" title="Edit link"><i class="far fa-edit"></i></button>
				<button class="remove-link" title="Remove link"><i class="far fa-trash-alt"></i></button>
				<span class="hide">${link.child.id}|${link.type}|${link.name}|${link.data}|${_stat.index}|1</span>
			</div>
		</div>
	</c:forEach>
</div>

<c:if test="${empty editingItem.allLinksShortcutsLast}">
	<p>None</p>
</c:if>

<div class="spacer20">
	<button id="addlink-button" type="button">Add link</button>
	<button id="savelinks-button" type="button" disabled="disabled">Save changes</button>
</div>

<div class="spacer3em">
	<h2>Linked from</h2>
	<c:choose><c:when test="${not empty editingItem.parentLinks}">
		<table>
			<tr>
				<th align="left">Link type</th>
				<th align="left">Link name</th>
				<th align="left">From</th>
			</tr>
			<c:forEach items="${editingItem.parentLinks}" var="_link">
				<tr>
					<td width="20%">${_link.type}</td>
					<td width="20%">${_link.name}</td>
					<td><span class="link-linker" data-id="${_link.child.origId}">${_link.child.name}</span></td>
				</tr>
			</c:forEach>
		</table>
	</c:when><c:otherwise>
		<p>None</p>
	</c:otherwise></c:choose>
</div>

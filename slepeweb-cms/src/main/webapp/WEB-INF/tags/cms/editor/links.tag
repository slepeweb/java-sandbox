<%@ tag %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>
        
<cms:debug><!-- tags/cms/editor/links.tag --></cms:debug>
	
<h2>Inlines, relations & shortcuts</h2>

<div id="sortable-links">
	<c:forEach items="${editingItem.nonOrthogonalLinks}" var="link" varStatus="_stat">
		<div class="sortable-link ui-state-default">
			<div class="left">
				<span class="ui-icon ui-icon-arrowthick-2-n-s"></span>
					<span class="link-identifier">${link.type}&nbsp;&nbsp;|&nbsp;&nbsp;${link.name}&nbsp;&nbsp;|&nbsp;&nbsp;${link.child.name}</span>
			</div>
			
			<div class="right">
				<button class="link-linker" data-id="${link.child.id}" title="Navigate to this item"><i class="fas fa-location-arrow"></i></button>
				<button class="edit-link" title="Edit link"><i class="far fa-edit"></i></button>
				<button class="remove-link" title="Remove link"><i class="far fa-trash-alt"></i></button>
				<span class="hide">${link.child.id}|${link.type}|${link.name}|${link.data}|${_stat.index}|1|${link.child.name}</span>
			</div>
		</div>
	</c:forEach>

	<c:if test="${empty editingItem.nonOrthogonalLinks}">
		<p>None</p>
	</c:if>
</div>

<form>
	<div class="button-set spacer20">
		<button class="action add" type="button">Add link</button>
		<div class="action-reset-combo">
			<button class="reset" type="button" disabled="disabled"  ${_resetHelp}>Reset form</button>
			<button class="action save" type="button" disabled="disabled">Save changes</button>
		</div>
	</div>
</form>



<div class="spacer3em">
	<h2>Parent links</h2>
	
	<div id="parent-links" class="spacer3m">
		<c:forEach items="${editingItem.parentLinksIncludingBindings}" var="link" varStatus="_stat">
			<div class="sortable-link ui-state-default">
				<div class="left">
					<span class="ui-icon"></span>
					<span class="link-identifier">${link.type}&nbsp;&nbsp;|&nbsp;&nbsp;${link.name}&nbsp;&nbsp;|&nbsp;&nbsp;${link.child.name}</span>
				</div>
				
				<div class="right">
					<button class="link-linker" data-id="${link.child.id}" title="Navigate to this item"><i class="fas fa-location-arrow"></i></button>
					<c:if test="${link.type eq 'component' or link.type eq 'binding'}">
						<button id="edit-binding-link" title="Edit link"><i class="far fa-edit"></i></button>
					</c:if>
					<span class="hide">${link.child.id}|${link.type}|${link.name}|${link.data}|${_stat.index}|1</span>
				</div>
			</div>
		</c:forEach>
	</div>
</div>

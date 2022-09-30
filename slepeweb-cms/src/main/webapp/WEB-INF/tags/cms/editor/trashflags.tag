<%@ tag %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>
        
<cms:debug><!-- tags/cms/editor/trashflags.tag --></cms:debug>

<h2>Items flagged for trash</h2>
<div id="trashflags-message" class="hide">${_trashFlagsMessage}</div>

<div>
	<p>You have flagged ${fn:length(_trashFlagList)} item(s) for the trash bin.</p>
	
	<c:if test="${fn:length(_trashFlagList) > 0}">
		<ul>
			<c:forEach items="${_trashFlagList}" var="_gist">
				<li><a href="#" data-id="${_gist.itemId}">${_gist.name}</a> (${_gist.path})</li>
			</c:forEach>
		</ul>
		
		<p>NOTE that some of these may have child/descendant items, in which case they will also trashed, so BEWARE. If you are
		uncertain about the next step, it might be wise to unflag all the items.</p>
		
		<div id="trash-flagged-button-wrapper">
			<div><button id="unflag-button" type="button">Unflag ALL</button></div>
			<div><button id="trash-button" type="button">Trash ALL</button></div>
		</div>
	</c:if>
	
</div>

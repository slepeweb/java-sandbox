<%@ tag %><%@ include file="/WEB-INF/jsp/tagDirectives.jsp" %>

<div class="in-this-section">
	<c:if test="${fn:length(_inThisSection.root.children) gt 0}">
		<h4>${_inThisSection.root.tag}</h4>
				
		<c:forEach items="${_inThisSection.root.children}" var="sib_target">
			<c:set var="_clazz">${sib_target.selected ? 'class="selected"' : ''}</c:set>
			<c:set var="_spacer">${sib_target.selected ? '&nbsp;' : ''}</c:set>
			
			<div ${_clazz}>${_spacer}${sib_target.tag}
				<c:if test="${sib_target.selected}">
					<c:forEach items="${sib_target.children}" var="c_target">
						<div class="selected-child">${c_target.tag}</div>
					</c:forEach>
				</c:if>
			</div>
		</c:forEach>
	</c:if>
</div>

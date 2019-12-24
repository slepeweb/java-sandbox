<%@ tag %><%@ include file="/WEB-INF/jsp/common/tagDirectives.jsp" %>

<gen:debug><!-- tags/anc/personSubMenu.tag --></gen:debug>

<ul id="sub-menu">
	<c:forEach items="${_subMenu}" var="_link">
		<li <c:if test="${_link.selected}">class="selected"</c:if>>
				
			<c:if test="${not _link.selected}"><a href="${_link.href}"></c:if>
			${_link.title}
			<c:if test="${not _link.selected}"></a></c:if>
		</li>
	</c:forEach>
</ul>
		
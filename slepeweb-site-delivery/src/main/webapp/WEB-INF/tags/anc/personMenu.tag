<%@ tag %><%@ include file="/WEB-INF/jsp/common/tagDirectives.jsp" %>

<gen:debug><!-- tags/anc/personMenu.tag --></gen:debug>

<ul id="menu">
	<c:forEach items="${_menu}" var="_link">
		<c:set var="_anchorise" value="${_link.enabled and not _link.selected}" />
		<li <c:if test="${not anchorise}">class=" <c:if 
			test="${not _link.enabled}">disabled </c:if><c:if 
				test="${_link.selected}">selected</c:if>"></c:if>
				
			<c:if test="${_anchorise}"><a href="${_link.href}"></c:if>
			${_link.title}
			<c:if test="${_anchorise}"></a></c:if>
		</li>
	</c:forEach>
</ul>
		
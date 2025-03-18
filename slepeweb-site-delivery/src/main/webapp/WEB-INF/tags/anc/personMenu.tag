<%@ tag %><%@ include file="/WEB-INF/jsp/tagDirectives.jsp" %>

<gen:debug><!-- tags/anc/personMenu.tag --></gen:debug>

<ul id="menu">
	<c:forEach items="${_menu}" var="_link">
			<c:set var="_anchorise" value="${_link.enabled and not _link.selected}" />
			
			<c:set var="_class" value="" />
			<c:if test="${not _anchorise}">
				<c:if test="${not _link.enabled}"><c:set var="_class" value="disabled" /></c:if>
				<c:if test="${_link.selected}"><c:set var="_class" value="selected" /></c:if>
			</c:if>
		
		<li class="${_class}">				
			<c:choose><c:when test="${_anchorise}">
				<a href="${_link.href}">${_link.title}</a>
			</c:when><c:otherwise>
				${_link.title}
			</c:otherwise></c:choose>
		</li>
		
	</c:forEach>
</ul>
		
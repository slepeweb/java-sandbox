<%@ tag %><%@ include file="/WEB-INF/jsp/common/tagDirectives.jsp" %>

<gen:debug><!-- tags/anc/personBreadcrumbs.tag --></gen:debug>

<c:forEach items="${_page.header.breadcrumbItems}" var="_item" varStatus="_stat">
	<c:if test="${_item.path != '/'}">
		<a href="${_item.url}">${_item.name}</a>
		<c:if test="${not _stat.last}"><span><i class="fas fa-angle-double-right"></i></span></c:if>
	</c:if>
</c:forEach>
		
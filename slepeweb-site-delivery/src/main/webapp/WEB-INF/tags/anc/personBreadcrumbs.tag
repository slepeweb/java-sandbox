<%@ tag %><%@ include file="/WEB-INF/jsp/common/tagDirectives.jsp" %>

<gen:debug><!-- tags/anc/personBreadcrumbs.tag --></gen:debug>

<c:forEach items="${_breadcrumbs}" var="_person" varStatus="_stat">
	<a href="${_person.item.url}">${_person.firstName}</a>
	<c:if test="${not _stat.last}"><span><i class="fas fa-angle-double-right"></i></span></c:if>
</c:forEach>
		
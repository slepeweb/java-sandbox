<%@ tag %><%@ include file="/WEB-INF/jsp/tagDirectives.jsp" %>

<gen:debug><!-- tags/anc/personBreadcrumbs.tag --></gen:debug>

<div id="breadcrumbs">
	<c:forEach items="${_breadcrumbs}" var="_person" varStatus="_stat">
		<a href="${_person.item.url}">${_person.firstName}</a>
		<c:if test="${not _stat.last}"><span><i class="fas fa-angle-double-right"></i></span></c:if>
	</c:forEach>
</div>
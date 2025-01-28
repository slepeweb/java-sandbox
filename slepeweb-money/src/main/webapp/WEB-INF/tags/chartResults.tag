<%@ tag %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>

<!-- chartResults.tag -->

<c:choose><c:when test="${not empty noCategoriesSpecified}">
	<p>No categories specified - please <a href="${_ctxPath}/chart/by/categories${queryString}">try again</a>.</p>
</c:when><c:otherwise>
	<c:if test="${not empty _chartSVG}">
		${_chartSVG}
	</c:if>
</c:otherwise></c:choose>
	
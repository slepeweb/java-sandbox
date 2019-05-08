<%@ 
	include file="/WEB-INF/jsp/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>
	
<mny:standardLayout>
	<h2>Chart</h2>
	<div class="right"><a href="${_ctxPath}/chart/by/categories?repeat">Revise chart input</a></div>
	
	<c:choose><c:when test="${not empty noCategoriesSpecified}">
		<p>No categories specified - please <a href="${_ctxPath}/chart/by/categories${queryString}">try again</a>.</p>
	</c:when><c:otherwise>
		${_chartSVG}
	</c:otherwise></c:choose>
</mny:standardLayout>
	
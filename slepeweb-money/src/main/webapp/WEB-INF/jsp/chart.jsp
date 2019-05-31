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
		
		<table>
			<tr><th>Label</th><th>Categories</th></tr>
			<c:forEach items="${_chartProps.groups}" var="_group">
				<tr>
					<td>${_group.label}</td><td>${_group.filterStr}</td>
				</tr>
			</c:forEach>
		</table>
	</c:otherwise></c:choose>
</mny:standardLayout>
	
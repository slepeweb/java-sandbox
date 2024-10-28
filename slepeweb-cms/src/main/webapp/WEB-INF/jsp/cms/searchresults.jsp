<%@ include file="/WEB-INF/jsp/tagDirectives.jsp" %>

<c:choose><c:when test="${not empty _response.results}">
	<ul>
		<c:forEach items="${_response.results}" var="_result">
			<li>
				<div class="cms-icon cms-icon-${fn:toLowerCase(_result.type)}"></div>
				<a class="navigate" href="${_result.id}">${_result.title}</a>
			</li>
		</c:forEach>
	</ul>
</c:when><c:otherwise>
	<p>No results for these search terms</p>
</c:otherwise></c:choose>
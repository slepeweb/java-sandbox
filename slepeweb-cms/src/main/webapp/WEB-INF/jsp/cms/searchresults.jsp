<%@ include file="/WEB-INF/jsp/tagDirectives.jsp" %>

<p <c:if test="${_response.error}">class="error-message"</c:if>>${_response.message}</p>
<ul>
	<c:forEach items="${_response.results}" var="_result">
		<li>
			<div class="cms-icon cms-icon-${fn:toLowerCase(_result.type)}"></div>
			<a class="navigate" href="${_result.id}">${_result.title}</a>
		</li>
	</c:forEach>
</ul>

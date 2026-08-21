<%@ include file="/WEB-INF/jsp/tagDirectives.jsp" %>

<p <c:if test="${_response[0]}">class="error-message"</c:if>>${_response[2]}</p>

<c:if test="${not _response[0]}">
	<ul>
		<li>
			<div class="cms-icon cms-icon-${fn:toLowerCase(_response[3].type)}"></div>
			<a class="xnavigate" href="${_response[3].id}">${_response[3].title}</a>
		</li>
	</ul>
</c:if>
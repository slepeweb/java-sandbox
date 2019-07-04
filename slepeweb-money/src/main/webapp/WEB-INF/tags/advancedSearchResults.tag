<%@ tag %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>

<c:set var="_urlPrefix">${_ctxPath}/search/action/${_ss.id}</c:set>

<c:if test="${not empty _response}">
	<c:choose><c:when test="${_response.error}">
		<p><strong>${_response.message}</strong></p>
	</c:when><c:otherwise>
	
		<mny:pager urlPrefix="${_ctxPath}/search/get/${_ss.id}" pager="${_response.pager}" params="" />
		
		<c:if test="${not empty _response.pager.results}">
			<mny:flatTransactionTable pager="${_response.pager}" />
		</c:if>
			
	</c:otherwise></c:choose>
</c:if>
	
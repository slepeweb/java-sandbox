<%@ tag %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>

<c:set var="_urlPrefix">${_ctxPath}/search</c:set>

<c:if test="${not empty _response}">
	<c:choose><c:when test="${_response.error}">
		<p><strong>${_response.message}</strong></p>
	</c:when><c:otherwise>
		<mny:pager urlPrefix="${_urlPrefix}" pager="${_response.pager}" params="${_response.params.urlParameters}" />
		
		<c:choose><c:when test="${not empty _response.pager.results}">
			<mny:flatTransactionTable pager="${_response.pager}" />
		</c:when><c:otherwise>
			<p><strong>No transactions found matching these search criteria</strong></p>
		</c:otherwise></c:choose>
	</c:otherwise></c:choose>
</c:if>
	
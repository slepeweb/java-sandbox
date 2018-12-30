<%@ 
	include file="/WEB-INF/jsp/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>
	
<c:set var="_urlPrefix">${_ctxPath}/transaction/list/by/payee/${_payee.id}</c:set>
<c:set var="_pager" value="${_response.pager}" />
	
<mny:standardLayout>

	<h2>Transactions for payee '${_payee}'</h2>	
	
	<p>
		<strong>Total no. of transactions = ${_response.totalHits}</strong>
		<c:if test="${_response.totalHits eq _limit}">
			<button id="lift-limit">Lift limit</button>
		</c:if>
	</p>
	
	<mny:pager urlPrefix="${_urlPrefix}" pager="${_pager}" />

	<c:choose><c:when test="${not empty _pager.results}">
		<mny:flatTransactionTable pager="${_pager}" disable="payee" />
	</c:when><c:otherwise>
		<p><strong>No transactions for this payee</strong></p>
	</c:otherwise></c:choose>

</mny:standardLayout>

<mny:menuActionDialog target="none" />

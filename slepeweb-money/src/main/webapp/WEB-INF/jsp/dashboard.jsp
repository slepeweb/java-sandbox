<%@ 
	include file="/WEB-INF/jsp/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>

<c:set var="_extraCss" scope="request">
	table.trn_listing hr {
		margin: 0;
		background-color: black;
	}
</c:set>

<mny:standardLayout>
	<div class="right">
		<a href="${_ctxPath}/index/by/dates" title="Re-index ALL transactions (for searching)">Re-index</a><br />
		<a href="${_ctxPath}/asset/history" title="Calculate growth/decline of all assets over complete data set">Asset history</a><br />
	</div>
	
	<h2>Dashboard</h2>
					
	<c:choose><c:when test="${not empty _accounts}">
		<table class="trn_listing">
		
			<c:set var="_lastType" value="none" />
			<c:forEach items="${_accounts}" var="_a">
			
				<c:if test="${_lastType ne 'none' and _a.type ne _lastType}">
					<tr><td colspan="3"><hr /></td></tr>
				</c:if>
				
				<tr>
					<td class="name"><a href="${_ctxPath}/transaction/list/${_a.id}"
						title="List transactions for this account">${_a.name}</a></td>
					<td class="type">${_a.type}</td>
					<td class="currency amount">${mon:displayAmountWS(_a.balance)}</td>
				</tr>
				
				<c:set var="_lastType" value="${_a.type}" />
				
			</c:forEach>
		
			<tr><td colspan="3"><hr /></td></tr>
			<tr><td colspan="3">&nbsp;</td></tr>
			<c:forEach items="${_summary}" var="_pair" varStatus="_status">
				<tr>
					<td><c:if test="${_status.first}"><strong>Summary</strong></c:if></td>
					<td class="type">${_pair.left}</td>
					<td class="currency amount">${mon:displayAmountWS(_pair.right)}</td>
				</tr>
			</c:forEach>
			
			<tr><td colspan="3">&nbsp;</td></tr>
			<tr>
				<td></td>
				<td><h3>Total</h3></td>
				<td class="currency amount">${mon:displayAmountWS(_grandTotal)}</td>
			</tr>
		</table>
		
	</c:when><c:otherwise>
		<p><strong>No accounts defined</strong></p>
	</c:otherwise></c:choose>
</mny:standardLayout>
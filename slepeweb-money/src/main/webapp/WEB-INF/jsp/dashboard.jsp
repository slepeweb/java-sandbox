<%@ 
	include file="/WEB-INF/jsp/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>
	
<mny:standardLayout>
	<div class="right">
		<a href="/money/index/by/dates">Re-index</a><br />
	</div>
	
	<h2>Dashboard</h2>
					
	<c:choose><c:when test="${not empty _accounts}">
		<table class="trn_listing">
			<c:forEach items="${_accounts}" var="_a">
				<tr>
					<td class="name"><a href="${_ctxPath}/transaction/list/${_a.id}">${_a.name}</a></td>
					<td class="type">${_a.type}</td>
					<td class="currency amount">&pound;${_a.balanceStr}</td>
				</tr>
			</c:forEach>
		
			<tr><td colspan="3">&nbsp;</td></tr>
			<c:forEach items="${_summary}" var="_pair" varStatus="_status">
				<tr>
					<td><c:if test="${_status.first}"><strong>Summary</strong></c:if></td>
					<td class="type">${_pair.left}</td>
					<td class="currency amount">&pound;${mon:formatPounds(_pair.right)}</td>
				</tr>
			</c:forEach>
			
			<tr><td colspan="3">&nbsp;</td></tr>
			<tr>
				<td></td>
				<td><h3>Total</h3></td>
				<td class="currency amount">&pound;${mon:formatPounds(_grandTotal)}</td>
			</tr>
		</table>
		
	</c:when><c:otherwise>
		<p><strong>No accounts defined</strong></p>
	</c:otherwise></c:choose>
</mny:standardLayout>
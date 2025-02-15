<%@ 
	include file="/WEB-INF/jsp/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>

<!-- dashboard.jsp -->

<mny:standardLayout>
	<mny:pageHeading heading="Dashboard">
		<ul>
			<li><a href="${_ctxPath}/index/by/dates" title="Re-index ALL transactions (for searching)">Re-index</a></li>
			<li><a href="${_ctxPath}/asset/history" title="Calculate growth/decline of all assets over complete data set">Asset history</a></li>	
			<li><a href="${_ctxPath}/login?logout" title="Logout">Logout</a></li>	
		</ul>
	</mny:pageHeading>			
					
	<c:choose><c:when test="${not empty _dash.groups}">
		
		<c:forEach items="${_dash.groups}" var="_group">
			<div class="dashboard-group ${_group.type}">
				<table>
					<c:forEach items="${_group.accounts}" var="_a">
						<tr>
							<td class="name"><a href="${_ctxPath}/transaction/list/${_a.id}"
								title="List transactions for this account">${_a.name}</a></td>
							<td class="type ${_a.type}">${_a.type}</td>
							<td class="currency amount">${mon:displayAmountWS(_a.balance)}</td>
						</tr>
					</c:forEach>
				</table>
			</div>
		</c:forEach>
		
		<div class="dashboard-group summary">
			<table>
				<c:forEach items="${_dash.groups}" var="_group">
					<tr>
						<td></td>
						<td class="type ${_group.type}">${_group.type}</td>
						<td class="currency amount">${mon:displayAmountWS(_group.total)}</td>
					</tr>
				</c:forEach>
				
				<tr>
					<td><h3>Summary</h3></td>
					<td><h3>Total</h3></td>
					<td class="currency amount"><strong>${mon:displayAmountWS(_dash.total)}</strong></td>
				</tr>
			</table>
		</div>
		
	</c:when><c:otherwise>
		<p><strong>No accounts defined</strong></p>
	</c:otherwise></c:choose>
</mny:standardLayout>
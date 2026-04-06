<%@ 
	include file="/WEB-INF/jsp/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>

<mny:standardLayout>

	<mny:pageHeading heading="Savings accounts">
		<ul>
			<li><a href="../list" title="List all accounts">List ALL accounts</a></li>
			<li><a href="../add" title="Create a new account">New account</a></li>
		</ul>
	</mny:pageHeading>
	
	<c:choose><c:when test="${not empty _savings}">
		<p>Accounts are listed in order of maturity date.</p>
		
		<table>
			<tr><th>Name</th><th>Accessibility</th><th>Balance</th><th>Matures</th><th>Notes</th></tr>
			<c:forEach items="${_savings}" var="_a">
				<tr>
					<td class="name"><a href="${_ctxPath}/account/form/${_a.id}" 
						title="Update details of this account">${_a.name}</a></td>
					<td>${_a.access}</td>
					<td>${mon:displayAmountWS(_a.balance)}</td>
					<td>${mon:formatTimestamp(_a.matures)}</td>
					<td>${_a.note}</td>
				</tr>
			</c:forEach>
		</table>
		
	</c:when><c:otherwise>
		<p><strong>No savings accounts defined</strong></p>
	</c:otherwise></c:choose>
</mny:standardLayout>

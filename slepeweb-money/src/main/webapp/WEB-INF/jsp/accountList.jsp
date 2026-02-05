<%@ 
	include file="/WEB-INF/jsp/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>

<mny:standardLayout>

	<mny:pageHeading heading="Accounts">
		<ul>
			<li><a href="add" title="Create a new account">New account</a></li>
			<li><a href="balancer" title="Re-calculate balances for all open accounts">Reset balances</a></li>
		</ul>
	</mny:pageHeading>
	
	<p><strong>${fn:length(_openAccounts)} Open accounts, ${fn:length(_closedAccounts)} Closed accounts</strong></p>

	<c:choose><c:when test="${not empty _openAccounts or not empty _closedAccounts}">
		<div id="accordion-accounts">
			<mny:accountList list="${_openAccounts}" heading="Open accounts" />
			<mny:accountList list="${_closedAccounts}" heading="Closed accounts" />
		</div>
	</c:when><c:otherwise>
		<p><strong>No accounts defined</strong></p>
	</c:otherwise></c:choose>
</mny:standardLayout>

<%@ tag %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %><%@ 
	attribute name="pager" type="com.slepeweb.money.bean.solr.SolrPager" required="true" rtexprvalue="true" %>

<table class="trn_listing">
	<tr>
		<th>Date</th>
		<th>Account</th>
		<th>Payee</th>
		<th>Category</th>
		<th>Amount</th>
		<th>Memo</th>
	</tr>
	<c:forEach items="${pager.results}" var="_trn">
		<tr>
			<td class="date"><a href="${_ctxPath}/transaction/form/${_trn.id}">${_trn.enteredStr}</a></td>
			<td class="account">${_trn.account}</td>			
			<td class="payee">${_trn.payee}</td>
			<td class="category"><c:choose><c:when test="${_trn.type eq '1'}">[Split transaction]</c:when><c:otherwise>${_trn.category}</c:otherwise></c:choose></td>			
			<td class="currency amount">${mon:displayAmountNS(_trn.amount)}</td>
			<td class="memo">${_trn.memo}</td>				
		</tr>
	</c:forEach>
	
	<tr class="table-summary">
		<td></td>
		<td></td>
		<td></td>
		<td>Total on <strong>this</strong> page</td>
		<td class="currency amount">${mon:displayAmountNS(_totalCredit)}</td>
		<td></td>
	</tr>
</table>
	
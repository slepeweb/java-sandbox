<%@ tag %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %><%@ 
	attribute name="disable" required="false" rtexprvalue="true" %><%@ 
	attribute name="pager" type="com.slepeweb.money.bean.solr.SolrPager" required="true" rtexprvalue="true" %>

<table class="trn_listing">
	<tr>
		<th>Account</th>
		<th>Date</th>
		<c:if test="${not fn:contains(disable, 'payee')}"><th>Payee</th></c:if>
		<c:if test="${not fn:contains(disable, 'category')}"><th>Category</th></c:if>
		<th>Amount</th>
		<th>Memo</th>
	</tr>
	<c:forEach items="${pager.results}" var="_trn">
		<tr>
			<td class="date">${_trn.enteredStr}</td>
			<td class="account">${_trn.account}</td>
			
			<c:if test="${not fn:contains(disable, 'payee')}">
				<td class="payee">${_trn.payee}</td>
			</c:if>
			
			<c:if test="${not fn:contains(disable, 'category')}">
				<td class="category"><c:choose><c:when test="${_trn.type eq '1'}">[Split transaction]</c:when><c:otherwise>${_trn.category}</c:otherwise></c:choose></td>
			</c:if>
			
			<td class="currency amount">${_trn.amountInPounds}</td>
			<td class="memo">${_trn.memo}</td>				
			<td class="menu-icon" data-id="${_trn.id}"><i class="fas fa-bars"></i></td>
		</tr>
	</c:forEach>
</table>
	
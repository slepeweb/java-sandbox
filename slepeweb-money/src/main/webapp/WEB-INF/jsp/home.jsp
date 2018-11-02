<%@ 
	include file="/WEB-INF/jsp/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>
	
<mny:standardLayout>
	<form>
		<label>Select account:</label>
		<select id="account-selector">
			<c:forEach items="${_accounts}" var="_account">
				<option value="${_account.id}"<c:if test="${_account.id eq _tl.account.id}"> selected</c:if>>${_account.name}</option>
			</c:forEach>
		</select>
	</form>
	
	<p>
		<c:if test="${_tl.pager.previous}"><span class="pager arrow"><a href="./${_tl.pager.previousMonth.index}"><i 
			class="fas fa-angle-double-left" title="Previous"></i> Previous</a></span></c:if>
		<c:forEach items="${_tl.pager.navigation}" var="_option">
			<span class="pager <c:if test="${_option.selected}">selected</c:if>"><a href="./${_option.value}">${_option.name}</a></span>
		</c:forEach>
		<c:if test="${_tl.pager.next}"><span class="pager arrow"><a href="./${_tl.pager.nextMonth.index}">Next <i 
			class="fas fa-angle-double-right" title="Previous"></i></a></span></c:if>
	</p>
	
	<c:choose><c:when test="${not empty _tl.runningBalances}">
	<table id="trn_listing">
		<tr>
			<th>Date</th>
			<th>Payee</th>
			<th>Category</th>
			<th>Amount</th>
			<th>Memo</th>
			<th>Balance</th>
		</tr>
		<c:forEach items="${_tl.runningBalances}" var="_trn">
			<tr>
				<td class="date">${_trn.enteredStr}</td>
				<td class="payee">${_trn.payee.name}</td>
				<td class="payee">${_trn.split ? 'Total' : _trn.category}</td>
				<td class="currency amount">${_trn.amountInPounds}</td>
				<td class="memo">${_trn.memo}</td>
				<td class="currency amount">${_trn.balance}</td>
				
				<c:if test="${_trn.split}">
						<c:forEach items="${_trn.splits}" var="_split">
							<tr>
								<td></td>
								<td></td>
								<td>${_split.category}</td>
								<td>${_split.amountInPounds}</td>
								<td>${_split.memo}</td>
								<td></td>
							</tr>
						</c:forEach>
				</c:if>
			</tr>
		</c:forEach>
	</table>
	</c:when><c:otherwise>
		<h2>No results available</h2>
	</c:otherwise></c:choose>
</mny:standardLayout>
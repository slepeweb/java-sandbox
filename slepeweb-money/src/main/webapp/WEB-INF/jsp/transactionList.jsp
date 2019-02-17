<%@ 
	include file="/WEB-INF/jsp/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>
	
<c:set var="_urlPrefix">${_ctxPath}/transaction/list/${_accountId}</c:set>
	
<mny:standardLayout>
	<div class="right">
		<a href="add/${_tl.account.id}">New transaction</a><br />
		<a href="../../index/${_tl.account.id}">Re-index</a><br />
	</div>
	
	<div class="inline-block">
		<form>
			<label>Select account:</label>
			<select id="account-selector">
				<c:forEach items="${_accounts}" var="_account">
					<option value="${_account.id}"<c:if test="${_account.id eq _tl.account.id}"> selected</c:if>>${_account.name}</option>
				</c:forEach>
			</select>
		</form>
	</div>
	
	<div class="inline-block">
		<p><strong>Today's balance: <span class="scale_3-2">&pound;${_tl.balanceStr}</span></strong></p>
	</div>
	
	<c:if test="${fn:length(_yearSelector) gt 1}">
		<form>
			<label>Select year: </label>
			<select id="year-selector">
				<c:forEach items="${_yearSelector}" var="_o">
					<option value="${_o.value}"<c:if test="${_o.selected}"> selected</c:if>>${_o.name}</option>
				</c:forEach>
			</select>
		</form>
	</c:if>
		
	<p>
		<c:if test="${_tl.pager.previous}"><span 
				class="pager arrow left"><a href="${_urlPrefix}/${_tl.pager.previousBlock.index}"><i 
					class="fas fa-angle-double-left" title="Jump left"></i></a></span><span 
				class="pager arrow left"><a href="${_urlPrefix}/${_tl.pager.previousMonth.index}"><i 
					class="fas fa-angle-left" title="Previous"></i>Previous</a></span></c:if>
					
		<c:forEach items="${_tl.pager.navigation}" var="_option">
			<span class="pager <c:if test="${_option.selected}">selected</c:if>"><a href="${_urlPrefix}/${_option.value}">${_option.name}</a></span>
		</c:forEach>
		
		<c:if test="${_tl.pager.next}"><span 
			class="pager arrow right"><a href="${_urlPrefix}/${_tl.pager.nextMonth.index}">Next<i 
				class="fas fa-angle-right" title="Next"></i></a></span><span 
			class="pager arrow right"><a href="${_urlPrefix}/${_tl.pager.nextBlock.index}"><i 
				class="fas fa-angle-double-right" title="Jump right"></i></a></span></c:if>
	</p>
	
	<c:choose><c:when test="${not empty _tl.runningBalances}">
	<table class="trn_listing">
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
				<td class="date"><a href="${_ctxPath}/transaction/form/${_trn.id}">${_trn.enteredStr}</a></td>
				<td class="payee">${_trn.payee.name}</td>
				
				<c:choose><c:when test="${not _trn.split and not _trn.transfer}">
					<td class="category">${_trn.category}</td>
					<td class="currency amount">${_trn.amountInPounds}</td>
					<td class="memo">${_trn.memo}</td>
				</c:when><c:when test="${_trn.transfer}">
					<td class="category">Transfer</td>
					<td class="currency amount">${_trn.amountInPounds}</td>
					<td class="memo">${_trn.memo}</td>
				</c:when><c:when test="${_trn.split}">
					<td>(Split transaction) Total:<br />
						<c:forEach items="${_trn.splits}" var="_split">
								${_split.category}<br />
						</c:forEach>						
					</td>
					<td class="currency amount">${_trn.amountInPounds}<br />
						<c:forEach items="${_trn.splits}" var="_split">
								${_split.amountInPounds}<br />
						</c:forEach>						
					</td>
					<td> <br />
						<c:forEach items="${_trn.splits}" var="_split">
								${_split.memo}<br />
						</c:forEach>						
					</td>
				</c:when></c:choose>
				
				<td class="currency amount">${_trn.balance}</td>
			</tr>
		</c:forEach>
	</table>
	</c:when><c:otherwise>
		<p><strong>No transactions this month</strong></p>
	</c:otherwise></c:choose>
</mny:standardLayout>

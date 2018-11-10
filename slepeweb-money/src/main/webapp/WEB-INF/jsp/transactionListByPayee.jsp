<%@ 
	include file="/WEB-INF/jsp/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>
	
<c:set var="_urlPrefix">${_ctxPath}/transaction/list/by/payee/${_payee.id}</c:set>
	
<mny:standardLayout>

	<h2>Transactions for payee '${_payee}'</h2>	
	
	<p>
		<strong>Total no. of transactions = ${_pager.totalHits}</strong>
		<c:if test="${_pager.totalHits eq _limit}">
			<button id="lift-limit">Lift limit</button>
		</c:if>
	</p>
	
	<p>
		<c:if test="${_pager.previous}"><span 
				class="pager arrow left"><a href="${_urlPrefix}/${_pager.previousBlock}"><i 
					class="fas fa-angle-double-left" title="Jump left"></i></a></span><span 
				class="pager arrow left"><a href="${_urlPrefix}/${_pager.previousSelection}"><i 
					class="fas fa-angle-left" title="Previous"></i>Previous</a></span></c:if>
					
		<c:forEach items="${_pager.navigation}" var="_option">
			<span class="pager <c:if test="${_option.selected}">selected</c:if>"><a href="${_urlPrefix}/${_option.value}">${_option.name}</a></span>
		</c:forEach>
		
		<c:if test="${_pager.next}"><span 
			class="pager arrow right"><a href="${_urlPrefix}/${_pager.nextSelection}">Next<i 
				class="fas fa-angle-right" title="Next"></i></a></span><span 
			class="pager arrow right"><a href="${_urlPrefix}/${_pager.nextBlock}"><i 
				class="fas fa-angle-double-right" title="Jump right"></i></a></span></c:if>
	</p>

	<c:choose><c:when test="${not empty _pager.results}">
		<table id="trn_listing">
			<tr>
				<th>Date</th>
				<th>Category</th>
				<th>Amount</th>
				<th>Memo</th>
			</tr>
			<c:forEach items="${_pager.page}" var="_trn">
				<tr>
					<td class="date">${_trn.enteredStr}</td>
					<td class="category">${_trn.category}</td>
					<td class="currency amount">${_trn.amountInPounds}</td>
					<td class="memo">${_trn.memo}</td>				
				</tr>
			</c:forEach>
		</table>
	</c:when><c:otherwise>
		<p><strong>No transactions for this payee</strong></p>
	</c:otherwise></c:choose>

</mny:standardLayout>
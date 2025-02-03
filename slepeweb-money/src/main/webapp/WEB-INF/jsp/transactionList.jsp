<%@ 
	include file="/WEB-INF/jsp/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>
	
<!-- transactionList.jsp -->

<c:set var="_urlPrefix">${_ctxPath}/transaction/list/${_accountId}</c:set>

<c:set var="_extraInPageCss" scope="request">
	.total-amount {
		border-top: 1px solid black;
	}
	
	.transaction-list-header p, .transaction-list-header form {
		margin: 0 0 1em 0;
	}
</c:set>

	
<mny:standardLayout>
	<div class="transaction-list-header">
		<mny:pageHeading heading="Transactions by account">
			<ul>
				<li><a href="${_ctxPath}/transaction/add/${_tl.account.id}" title="Create a new transaction record">New transaction</a></li>
				<li><a href="${_ctxPath}/index/by/account/${_tl.account.id}" title="Re-index all transactions for this account (for searching)">Re-index</a></li>
			</ul>
		</mny:pageHeading>			
		
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
			<p><strong>Today's balance: <span class="scale_3-2">${mon:displayAmountWS(_tl.balance)}</span></strong></p>
		</div>
	</div>
	
	<div class="inline-block year-selector-wrapper">
		<form>
			<label>Select year: </label>
			<select id="year-selector">
				<c:forEach items="${_yearSelector}" var="_o">
					<option value="${_o.value}"<c:if test="${_o.selected}"> selected</c:if>>${_o.name}</option>
				</c:forEach>
			</select>
		</form>
	</div>
		
	<div class="inline-block pager-wrapper">
		<p>
			<c:if test="${_tl.pager.previous}"><span 
					class="pager arrow left"><a href="${_urlPrefix}/${_tl.pager.previousBlock.index}"><i 
						class="fas fa-angle-double-left" title="Jump ${_tl.pager.blocksize} months earlier"></i></a></span><span 
					class="pager arrow left"><a href="${_urlPrefix}/${_tl.pager.previousMonth.index}" 
						title="Step 1 month earlier"><i class="fas fa-angle-left"></i>Previous</a></span>
			</c:if>
						
			<c:forEach items="${_tl.pager.navigation}" var="_option">
				<span class="pager <c:if test="${_option.selected}">selected</c:if>"><a href="${_urlPrefix}/${_option.value}">${_option.name}</a></span>
			</c:forEach>
			
			<c:if test="${_tl.pager.next}"><span 
				class="pager arrow"><a href="${_urlPrefix}/${_tl.pager.nextMonth.index}"
					title="Step 1 month later">Next<i class="fas fa-angle-right"></i></a></span><span 
				class="pager arrow"><a href="${_urlPrefix}/${_tl.pager.nextBlock.index}"><i 
					class="fas fa-angle-double-right" title="Jump ${_tl.pager.blocksize} months later"></i></a></span></c:if>
		</p>
	</div>
	
	<c:choose><c:when test="${not empty _tl.runningBalances}">
	<table class="trn_listing">
		<thead>
			<tr>
				<th>Date <i class="fas fa-angle-double-down date-sorter"></i></th>
				<th>Payee</th>
				<th>Category</th>
				<th>Amount</th>
				<th class="memo">Memo</th>
				<th class="balance">Balance</th>
			</tr>
		</thead>
		
		<tbody>
			<c:forEach items="${_tl.runningBalances}" var="_trn">
				<c:set var="_payee" value="${_trn.payee.name}" />
				<c:if test="${_trn.transfer}">
					<c:set var="_payee">${_trn.credit ? 'From' : 'To'}: ${_trn.mirror.name}</c:set>
				</c:if>
				
				<tr>
					<td class="date"><a href="${_ctxPath}/transaction/form/${_trn.id}"
						title="Update this transaction">${_trn.enteredStr}</a></td>
					<td class="payee">${_payee}</td>
					
					<c:choose><c:when test="${not _trn.split and not _trn.transfer}">
						<td class="category">${_trn.category}</td>
						<td class="currency amount">${mon:displayAmountNS(_trn.amount)}</td>
						<td class="memo">${_trn.memo}</td>
					</c:when><c:when test="${_trn.transfer}">
						<td class="category">Transfer</td>
						<td class="currency amount">${mon:displayAmountNS(_trn.amount)}</td>
						<td class="memo">${_trn.memo}</td>
					</c:when><c:when test="${_trn.split}">
						<td>
							<c:forEach items="${_trn.splits}" var="_split">
									${_split.category}<br />
							</c:forEach>
						</td>
						<td class="currency amount">
							<c:forEach items="${_trn.splits}" var="_split">
									${mon:displayAmountNS(_split.amount)}<br />
							</c:forEach>	
							<span class="total-amount">${mon:displayAmountNS(_trn.amount)}</span><br />					
						</td>
						<td> 
							<c:forEach items="${_trn.splits}" var="_split">
									${_split.memo}<br />
							</c:forEach>						
							Total<br />
						</td>
					</c:when></c:choose>
					
					<td class="currency amount balance">${mon:displayAmountNS(_trn.balance)}</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	</c:when><c:otherwise>
		<p><strong>No transactions this month</strong></p>
	</c:otherwise></c:choose>
</mny:standardLayout>

<script>
	$(function(){
		$("i.date-sorter").click(function(){
			// Create a temporary div
			$(".trn_listing").append("<div id='tmp'></div>");
			
			var _tmp = $("#tmp");
			var _tbody = $(".trn_listing tbody");
			
			// Move all table rows to the temp div
			_tbody.find("tr").appendTo(_tmp);
			
			// Store all tables rows in an array for re-sorting
			var _arr = [];
			$("#tmp").children().each(function(){
				_arr.push($(this));
			});
			
			// Move table rows back into original tbody, in reverse order
			var _len = _arr.length;
			for (var i = _len - 1; i >= 0; i--) {
				_arr[i].appendTo(_tbody);
			}
			
			// Discard the temporary div
			_tmp.remove();
			
			// Change arrow to indicate sort direction
			var arrows = $(this);
			var down = "fa-angle-double-down";
			var up = "fa-angle-double-up";
			
			if (arrows.hasClass(down)) {
				arrows.removeClass(down).addClass(up);
			}
			else {
				arrows.removeClass(up).addClass(down);
			}
		});
	});
</script>

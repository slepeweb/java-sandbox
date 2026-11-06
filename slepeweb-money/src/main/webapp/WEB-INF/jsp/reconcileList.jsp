<%@ 
	include file="/WEB-INF/jsp/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>
	
<!-- reconcileList.jsp -->

<c:set var="_extraJs" scope="request" value="reconcile.js" />
<c:set var="_extraCss" scope="request" value="reconcile.css" />
	
<mny:standardLayout>
	<div id="reconcile-list" data-accountid="${_account.id}">
		<mny:pageHeading heading="Reconcile: ${_account.name}">
			<ul>
				<li><a href="../../list/${_account.id}">List transactions</a></li>
			</ul>
		</mny:pageHeading>
		
		<input type="hidden" name="paused" value="${_paused ? 'true' : 'false'}" />
		
		<div id="reconcile-dashboard">
			<p>Balance b/f: £<span id="balance-bf"
				<c:if test="${_account.reconciled < 0}">class="debit-amount"</c:if>>
					${mon:formatPounds(_account.reconciled)}</span></p>
			
			<c:set var="balanceNow" value="${empty pausedReconciliations ? 0 : pausedReconciliations.target}" />
			<p>Balance now: £<input id="balance-now" autocomplete="off" value="${mon:formatPounds(balanceNow)}" /></p>

			<p>To reconcile: £<span id="reconcile-target">${mon:formatPounds(balanceNow - _account.reconciled)}</span></p>
			<p>Reconciled: £<span id="amount-reconciled">0.00</span></p>
			<p>Outstanding: £<span id="amount-outstanding">0.00</span></p>

			<button id="trigger-action">Start</button>
		</div>
		
		<form id="reconcile-form" method="post" action="${_ctxPath}/transaction/reconcile/submit/${_account.id}">
			<input type="hidden" name="transactionIds" value="" />
			<input type="hidden" name="reconciledAmount" value="" />			
		</form>
		
		<div id="menu-icons">
			<span id="undo" title="Undo reconciliation"><i class="fa-solid fa-arrow-rotate-left"></i></span>
			<span id="pause" title="Pause reconciliation"><i class="fa-solid fa-pause"></i></span>
			<button id="commit" class="dimmed">Commit</button>
		</div>
		
	</div>
		
	<c:choose><c:when test="${not empty _tl}">
	<table class="trn_listing lolight">
		<thead>
			<tr>
				<th>Date</th>
				<th>Payee</th>
				<th>Category</th>
				<th>Amount</th>
				<th class="memo">Memo</th>
			</tr>
		</thead>
		
		<tbody>
			<c:forEach items="${_tl}" var="_trn">
				<c:set var="_payee" value="${_trn.payee.name}" />
				<c:if test="${_trn.transfer}">
					<c:set var="_payee">${_trn.credit ? 'From' : 'To'}: ${_trn.mirrorAccount.name}</c:set>
				</c:if>
				
				<tr data-id="${_trn.id}" data-pence="${_trn.amount}" data-provisional="${_trn.provisionallyReconciled ? 'yes' : 'no'}">
					<td class="date">${_trn.enteredStr}</td>
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
					
					<c:if test="${not _trn.reconciled}"><td class="reconcile-switch"><span class="asterisk">*</span></td></c:if>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	</c:when><c:otherwise>
		<p><strong>No un-reconciled transactions for this account</strong></p>
	</c:otherwise></c:choose>
</mny:standardLayout>

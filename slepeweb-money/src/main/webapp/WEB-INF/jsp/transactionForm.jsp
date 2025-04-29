<%@ 
	include file="/WEB-INF/jsp/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>
	
<!-- transactionForm.jsp -->

<c:set var="_extraInPageJs" scope="request">
	_money.context = 'transaction';
	_money.transaction.accountid = ${_transaction.account.id};
	_money.transaction.reconciled = ${_transaction.partReconciled ? 'true' : 'false'};
	
	$(function(){
		$('input#payee').focus();
	});
</c:set>

<c:set var="_extraJs" scope="request" value="transaction.js,transandsched.js,minorcats.js" />
<c:if test="${not _transaction.partReconciled}"><c:set var="_extraJs" value="${_extraJs},datepicker.js" scope="request" /></c:if>

<c:set var="_extraInPageCss" scope="request">
	.ui-autocomplete-loading {
		background: white url("${_ctxPath}/resources/images/progress-indicator.gif") right center no-repeat;
	}
</c:set>

<c:set var="_readonlyAttr" value="${_transaction.partReconciled ? 'readonly' : ''}" scope="request" />
<c:set var="_readonlyClass" value="${_readonlyAttr}" scope="request" />
<c:set var="_hiddenDivClass" value="${_transaction.partReconciled ? 'hidden' : ''}" scope="request" />

<mny:standardLayout>

	<tsf:labels entityName="transaction" /> <%-- Defines variables _buttonLabel and _pageHeading --%>
	
	<mny:pageHeading heading="${_pageHeading}" 
			intro="${_transaction.partReconciled ? 'Updates/deletions of partly/wholly reconciled transactions are restricted' : ''}">
			
		<ul>
			<c:if test="${_formMode eq 'update'}">
				<li><a href="../add/${_transaction.account.id}">New transaction</a></li>
				<li><a href="../copy/${_transaction.id}">Copy this transaction</a><li>
			</c:if>
			<li><a href="../list/${_transaction.account.id}">List transactions</a></li>
		</ul>
	</mny:pageHeading>
	
	<form id="transaction-form" method="post" action="${_ctxPath}/transaction/update">
	    <table id="trn-form">
	    
		    <tsf:ids entity="${_transaction}" />
		    
	    	<mny:tableRow heading="Date">
		    	<input id="entered" class="datepicker ${_readonlyClass}" type="text"name="entered" ${_readonlyAttr}
		        	placeholder="Enter transaction date" value="${mon:formatTimestamp(_transaction.entered)}" />
		    </mny:tableRow>

		    <tsf:account accountId="${_transaction.account.id}" />
		    <tsf:paymentType entity="${_transaction}" />
		    <tsf:transfer istransfer="${_transaction.transfer}" mirror="${_transaction.mirrorAccount}" />
		    <tsf:payee payeeName="${_transaction.payee.name}" />
		    <tsf:category entity="${_transaction}" />
		    <tsf:subCategory entity="${_transaction}" />
				<mny:categoryList heading="Splits" categories="${_transactionSplits}" />
		    <tsf:notes memo="${_transaction.memo}" />
		    <tsf:amount value="${_transaction.amountValue}" isdebit="${_transaction.debit}" />

		</table> 
		
		<tsf:tail entity="${_transaction}" label="Delete transaction?" />	       
	  <input type="hidden" name="origxferid" value="${_transaction.transferId}" />   
	</form>		  	
		
	<tsf:dialogs entity="${_transaction}" />
	
</mny:standardLayout>

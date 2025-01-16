<%@ 
	include file="/WEB-INF/jsp/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>
	
<c:set var="_extraInPageJs" scope="request">
	_money.context = 'transaction';
	_money.transaction.accountid = ${_transaction.account.id};
</c:set>

<c:set var="_extraJs" scope="request" value="transaction.js,transandsched.js,minorcats.js,datepicker.js" />

<c:set var="_extraInPageCss" scope="request">
	.ui-autocomplete-loading {
		background: white url("${_ctxPath}/resources/images/progress-indicator.gif") right center no-repeat;
	}
</c:set>

<mny:flash />
	
<mny:standardLayout>

	<mny:tsformLabels entityName="transaction" /> <%-- Defines variables _buttonLabel and _pageHeading --%>
	
	<div class="right">
		<c:if test="${_formMode eq 'update'}">
			<a href="../add/${_transaction.account.id}">New transaction</a><br />
			<a href="../copy/${_transaction.id}">Copy this transaction</a><br />
		</c:if>
		<a href="../list/${_transaction.account.id}">List transactions</a>
	</div>
	
	<h2>${_pageHeading} <c:if test="${not empty param.flash}"><span 
		class="flash ${_flashType}">${_flashMessage}</span></c:if></h2>	
	
	<form id="transaction-form" method="post" action="${_ctxPath}/transaction/update">	  
	    <table id="trn-form">
	    
		    <mny:tsformIds entity="${_transaction}" />
		    
		    <tr>
		        <td class="heading"><label for="entered">Date</label></td>
		        <td><input id="entered" type="text" class="datepicker" name="entered" 
		        	placeholder="Enter transaction date" value="${mon:formatTimestamp(_transaction.entered)}" /></td>
		    </tr>

		    <mny:tsformAccount entity="${_transaction}" />
		    <mny:tsformPaymentType entity="${_transaction}" />
		    <mny:tsformTransfer istransfer="${_transaction.transfer}" mirror="${_transaction.mirrorAccount}" />
		    <mny:tsformPayee entity="${_transaction}" />
		    <mny:tsformCategory entity="${_transaction}" />
		    <mny:tsformSubCategory entity="${_transaction}" />
		    <mny:tsformSplits />
		    <mny:tsformNotesAndAmount entity="${_transaction}" />

		</table> 
		
		<mny:tsformTail entity="${_transaction}" label="Delete scheduled transaction?" />	       
	    <input type="hidden" name="origxferid" value="${_transaction.transferId}" />   
	</form>		  	
		
	<mny:tsformDialogs entity="${_transaction}" />
	
</mny:standardLayout>

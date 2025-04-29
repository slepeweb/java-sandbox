<%@ tag %><%@ include file="/WEB-INF/jsp/tagDirectives.jsp" %><%@ 
	attribute name="value" required="true" rtexprvalue="true" type="java.lang.Long" %><%@ 
	attribute name="isdebit" required="true" rtexprvalue="true" type="java.lang.Boolean" %>

<!-- transandschedform/amount.tag -->

<mny:tableRow heading="Total amount">
	<input id="amount" class="${_readonlyClass}" type="text" name="amount" placeholder="Enter amount" ${_readonlyAttr} 
 		value="${mon:formatPounds(value)}" /><i class="fa-solid fa-eraser"></i><span class="spacer2"></span>
 		
 	<c:choose><c:when test="${not _transaction.partReconciled}">
 	
	 	<span class="radio-horiz"><input id="debit" type="radio" name="debitorcredit" value="debit"
	 		${mon:tertiaryOp(_formMode eq 'add' or isdebit, 'checked=checked', '')} /> Debit</span>
	 	<span class="radio-horiz"><input id="credit" type="radio" name="debitorcredit" value="credit"
	 		${mon:tertiaryOp(not isdebit, 'checked=checked', '')} /> Credit</span>
	 		
 	</c:when><c:otherwise> 	
		<input type="hidden" name="debitorcredit" value="${isdebit ? 'debit' : 'credit'}" />
		<input type="text" name="dummy" class="readonly" value="${isdebit ? 'Debit' : 'Credit'}" style="display: inline; width: auto;" readonly />
	</c:otherwise></c:choose>
 	
</mny:tableRow>
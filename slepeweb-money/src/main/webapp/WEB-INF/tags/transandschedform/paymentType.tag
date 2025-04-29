<%@ tag %><%@ include file="/WEB-INF/jsp/tagDirectives.jsp" %><%@ 
	attribute name="entity" required="true" rtexprvalue="true" type="com.slepeweb.money.bean.Transaction" %>

<!-- transandschedform/paymentType.tag -->

<mny:tableRow heading="Payment type">
	<div class="${_hiddenDivClass}">
		<span class="radio-horiz"><input id="standard" type="radio" name="paymenttype" value="standard" 
	 		${mon:tertiaryOp(_formMode eq 'add' or (not entity.split and not entity.transfer), 'checked=checked', '')} /> Standard</span>
	 	<span class="radio-horiz"><input id="split" type="radio" name="paymenttype" value="split"  
	 		${mon:tertiaryOp(entity.split, 'checked=checked', '')} /> Split</span>
	 	<span class="radio-horiz"><input id="transfer" type="radio" name="paymenttype" value="transfer"  
	 		${mon:tertiaryOp(entity.transfer, 'checked=checked', '')} /> Transfer</span>
	</div>
	 		
	<c:if test="${_transaction.partReconciled}">
 		<c:set var="typevalue" value="standard" /><c:set var="typename" value="Standard" />
 		<c:if test="${entity.split}"><c:set var="typevalue" value="split" /><c:set var="typename" value="Split" /></c:if>
 		<c:if test="${entity.transfer}"><c:set var="typevalue" value="transfer" /><c:set var="typename" value="Transfer" /></c:if>
 		<input type="text" name="dummy1" class="readonly" value="${typename}" readonly />
 	</c:if>
</mny:tableRow>
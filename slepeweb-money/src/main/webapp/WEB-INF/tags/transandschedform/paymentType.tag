<%@ tag %><%@ include file="/WEB-INF/jsp/tagDirectives.jsp" %><%@ 
	attribute name="entity" required="true" rtexprvalue="true" type="com.slepeweb.money.bean.Transaction" %>

<!-- transandschedform/paymentType.tag -->

<mny:tableRow heading="Payment type">
	<span class="radio-horiz"><input id="standard" type="radio" name="paymenttype" value="standard" 
 		${mon:tertiaryOp(_formMode eq 'add' or (not entity.split and not entity.transfer), 'checked=checked', '')} /> Standard</span>
 	<span class="radio-horiz"><input id="split" type="radio" name="paymenttype" value="split" 
 		${mon:tertiaryOp(entity.split, 'checked=checked', '')} /> Split</span>
 	<span class="radio-horiz"><input id="transfer" type="radio" name="paymenttype" value="transfer" 
 		${mon:tertiaryOp(entity.transfer, 'checked=checked', '')} /> Transfer</span>
</mny:tableRow>
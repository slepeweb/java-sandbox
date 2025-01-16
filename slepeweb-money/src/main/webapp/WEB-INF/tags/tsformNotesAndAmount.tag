<%@ tag %><%@ include file="/WEB-INF/jsp/tagDirectives.jsp" %><%@ 
	attribute name="entity" required="true" rtexprvalue="true" type="com.slepeweb.money.bean.Transaction" %>

<tr>
    <td class="heading"><label for="memo">Notes</label></td>
    <td><input id="memo" type="text" name="memo" placeholder="Enter any relevant notes" value="${entity.memo}" /></td>
</tr>

<tr>
    <td class="heading"><label for="amount">Total amount</label></td>
    <td>
    	<span class="inline-block radio-horiz"><input id="amount" type="text" name="amount" placeholder="Enter amount" 
    		value="${mon:formatPounds(entity.amountValue)}" /></span>
    	<span class="radio-horiz"><input id="debit" type="radio" name="debitorcredit" value="debit" 
    		${mon:tertiaryOp(_formMode eq 'add' or entity.debit, 'checked=checked', '')} /> Debit</span>
    	<span class="radio-horiz"><input id="credit" type="radio" name="debitorcredit" value="credit" 
    		${mon:tertiaryOp(not entity.debit, 'checked=checked', '')} /> Credit</span>
    </td>
</tr>

<%@ tag %><%@ include file="/WEB-INF/jsp/tagDirectives.jsp" %><%@ 
	attribute name="value" required="true" rtexprvalue="true" type="java.lang.Long" %><%@ 
	attribute name="isdebit" required="true" rtexprvalue="true" type="java.lang.Boolean" %>

<tr>
    <td class="heading"><label for="amount">Total amount</label></td>
    <td>
    	<span class="inline-block radio-horiz"><input id="amount" type="text" name="amount" placeholder="Enter amount" 
    		value="${mon:formatPounds(value)}" /></span>
    	<span class="radio-horiz"><input id="debit" type="radio" name="debitorcredit" value="debit" 
    		${mon:tertiaryOp(_formMode eq 'add' or isdebit, 'checked=checked', '')} /> Debit</span>
    	<span class="radio-horiz"><input id="credit" type="radio" name="debitorcredit" value="credit" 
    		${mon:tertiaryOp(not isdebit, 'checked=checked', '')} /> Credit</span>
    </td>
</tr>

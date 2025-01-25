<%@ tag %><%@ include file="/WEB-INF/jsp/tagDirectives.jsp" %><%@ 
	attribute name="payeeName" required="true" rtexprvalue="true" %>

<tr class="payee">
    <td class="heading"><label for="payee">Payee</label></td>
    <td>
     	<input id="payee" type="text" name="payee" value="${payeeName}"
     	 	placeholder="Begin typing to reveal matching payees" />
    </td>
</tr>

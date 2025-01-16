<%@ tag %><%@ include file="/WEB-INF/jsp/tagDirectives.jsp" %><%@ 
	attribute name="entity" required="true" rtexprvalue="true" type="com.slepeweb.money.bean.Transaction" %>

<tr class="payee">
    <td class="heading"><label for="payee">Payee</label></td>
    <td>
     	<input id="payee" type="text" name="payee" value="${entity.payee.name}"
     	 	placeholder="Begin typing to reveal matching payees" />
    </td>
</tr>

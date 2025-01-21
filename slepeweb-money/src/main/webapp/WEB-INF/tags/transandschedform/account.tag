<%@ tag %><%@ include file="/WEB-INF/jsp/tagDirectives.jsp" %><%@ 
	attribute name="entity" required="true" rtexprvalue="true" type="com.slepeweb.money.bean.Transaction" %>

<tr>
    <td class="heading"><label for="account">Account</label></td>
    <td>
    	<select id="account" name="account">
     	<option value="">Choose ...</option>
     	<c:forEach items="${_allAccounts}" var="_a">
     		<option value="${_a.id}" <c:if test="${_a.id eq entity.account.id}">selected</c:if>>${_a.name}</option>
     	</c:forEach>
    	</select>
    </td>
</tr>

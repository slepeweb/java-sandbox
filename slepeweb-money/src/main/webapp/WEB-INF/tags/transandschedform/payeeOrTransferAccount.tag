<%@ tag %><%@ include file="/WEB-INF/jsp/tagDirectives.jsp" %><%@ 
	attribute name="payeeName" required="true" rtexprvalue="true" %><%@ 
	attribute name="accountId" required="true" rtexprvalue="true" %>

<!-- payeeOrTransferAccount.tag -->

<tr>
    <td class="heading"><label>Payment to/from</label></td>
    <td class="payee-or-transfer">
    	<div>
		    <span class="subheading">Payee:</span> 
		    <input id="payee" type="text" name="payee" value="${payeeName}"
				 	 	placeholder="Begin typing to reveal matching payees" />
			 	<i class="fa-solid fa-eraser"></i>
	 		</div>
	 		
	 		<div class="or-spacer">OR</div>
	 		
    	<div>
    		<span class="subheading">Transfer account:</span>
				<select id="transferAccount" name="transferAccount">
			   	<option value="">Choose ...</option>
			   	<c:forEach items="${_allAccounts}" var="_a">
			   		<option value="${_a.id}" <c:if test="${_a.id eq accountId}">selected</c:if>>${_a.name}</option>
			   	</c:forEach>
			  </select>
	 		</div>
    </td>
</tr>

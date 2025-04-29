<%@ tag %><%@ include file="/WEB-INF/jsp/tagDirectives.jsp" %><%@ 
	attribute name="accountId" required="true" rtexprvalue="true" type="java.lang.Long" %>

<!-- transandschedform/account.tag -->

<mny:tableRow heading="Account">
	<c:choose><c:when test="${not _transaction.partReconciled}">
	
	 	<select id="account" name="account">
	  	<option value="">Choose ...</option>
	  	<c:forEach items="${_allAccounts}" var="_a">
	  		<option value="${_a.id}" <c:if test="${_a.id eq accountId}">selected</c:if>>${_a.name}</option>
	  	</c:forEach>
	 	</select>
	 	
	</c:when><c:otherwise>
	
		<input type="text" class="readonly" name="accountname" value="${_transaction.account.name}" readonly />
		<input type="hidden" name="account" value="${_transaction.account.id}" />
		
	</c:otherwise></c:choose>
</mny:tableRow>
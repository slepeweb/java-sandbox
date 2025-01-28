<%@ tag %><%@ include file="/WEB-INF/jsp/tagDirectives.jsp" %><%@ 
	attribute name="accountId" required="true" rtexprvalue="true" type="java.lang.Long" %>

<!-- transandschedform/account.tag -->

<mny:tableRow heading="Account">
 	<select id="account" name="account">
  	<option value="">Choose ...</option>
  	<c:forEach items="${_allAccounts}" var="_a">
  		<option value="${_a.id}" <c:if test="${_a.id eq accountId}">selected</c:if>>${_a.name}</option>
  	</c:forEach>
 	</select>
</mny:tableRow>
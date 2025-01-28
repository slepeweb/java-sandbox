<%@ tag %><%@ include file="/WEB-INF/jsp/tagDirectives.jsp" %><%@ 
	attribute name="payeeName" required="true" rtexprvalue="true" %>

<!-- transandschedform/payee.tag -->

<mny:tableRow heading="Payee" trclass="payee">
 	<input id="payee" type="text" name="payee" value="${payeeName}"
 	 	placeholder="Begin typing to reveal matching payees" />
</mny:tableRow>
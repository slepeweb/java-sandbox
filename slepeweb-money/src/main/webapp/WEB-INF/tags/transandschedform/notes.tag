<%@ tag %><%@ include file="/WEB-INF/jsp/tagDirectives.jsp" %><%@ 
	attribute name="memo" required="true" rtexprvalue="true" %>

<!-- transandschedform/notes.tag -->

<mny:tableRow heading="Notes">
	<input id="memo" type="text" name="memo" placeholder="Enter any relevant notes" value="${memo}" />
</mny:tableRow>
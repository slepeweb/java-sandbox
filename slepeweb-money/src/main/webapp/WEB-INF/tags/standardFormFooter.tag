<%@ tag %><%@ include file="/WEB-INF/jsp/tagDirectives.jsp" %>

<input type="hidden" name="formMode" value="${_formMode}" />		
<input type="submit" id="submit-button" value="Submit" title="Submit this form" />		

<c:if test="${_formMode eq 'update'}">
	<input type="button" value="Delete search?" id="delete-button" title="Delete this search" />
</c:if>
	
<input id="cancel-button" type="button" value="Cancel" title="Return to list" />

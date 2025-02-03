<%@ tag %><%@ include file="/WEB-INF/jsp/tagDirectives.jsp" %><%@ 
	attribute name="submit" required="true" rtexprvalue="true" %><%@ 
	attribute name="cancel" required="true" rtexprvalue="true" %><%@ 
	attribute name="delete" required="true" rtexprvalue="true" %>

<!-- standardFormActionButtons.tag -->

<div class="std-form-buttons">
	<div class="left">
		<input id="submit-button" type="submit" value="${submit}" />		
		<input id="cancel-button" type="button" value="${cancel}" />
	</div>

	<c:if test="${_formMode eq 'update'}">
		<div class="right">
			<input id="delete-button" type="button" value="${delete}" />
		</div>
	</c:if>
</div>

<input type="hidden" name="formMode" value="${_formMode}" />		

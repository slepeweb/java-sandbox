<%@ tag %><%@ include file="/WEB-INF/jsp/tagDirectives.jsp" %><%@ 
	attribute name="entity" required="true" rtexprvalue="true" type="com.slepeweb.money.bean.Transaction" %><%@ 
	attribute name="label" required="true" rtexprvalue="true" %>

<input id="submit-button" type="submit" value="${_buttonLabel}" /> 
<input id="cancel-button" type="button" value="Cancel" />

<c:if test="${_formMode eq 'update'}">
  		<input type="button" value="${label}" id="delete-button" /> 
</c:if>

<input type="hidden" name="id" value="${entity.id}" />   
<input type="hidden" name="formMode" value="${_formMode}" />   

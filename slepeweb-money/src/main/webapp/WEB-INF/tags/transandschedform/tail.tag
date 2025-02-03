<%@ tag %><%@ include file="/WEB-INF/jsp/tagDirectives.jsp" %><%@ 
	attribute name="entity" required="true" rtexprvalue="true" type="com.slepeweb.money.bean.Transaction" %><%@ 
	attribute name="label" required="true" rtexprvalue="true" %>

<!-- transandschedform/tail.tag -->

<mny:standardFormActionButtons submit="${_buttonLabel}" cancel="Cancel" delete="${label}" />

<input type="hidden" name="id" value="${entity.id}" />   

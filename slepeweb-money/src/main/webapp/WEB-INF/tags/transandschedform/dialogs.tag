<%@ tag %><%@ include file="/WEB-INF/jsp/tagDirectives.jsp" %><%@ 
	attribute name="entity" required="true" rtexprvalue="true" type="com.slepeweb.money.bean.Transaction" %>

<!-- transandschedform/dialogs.tag -->

<div id="form-error-dialog" title="Form error"></div>
<div id="form-warning-dialog" title="Form error"></div>

<mny:entityDeletionDialog entity="${entity.typeIdentifier}" mode="${_formMode}" id="${entity.id}"/>

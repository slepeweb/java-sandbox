<%@ tag %><%@ include file="/WEB-INF/jsp/common/tagDirectives.jsp" %>

<gen:debug><!-- tags/sws/userForm.tag --></gen:debug>

<article class="first">
	<h2>${_item.fields.title}</h2>
	<jsp:include page="/WEB-INF/jsp/sws/template/user/flash-messages.jsp" />
	  
	<form:form method="post" action="${submitAction}" commandName="userForm">
	  
	    <table class="two-col-table">
	    <tr>
	        <td class="heading"><form:label path="userName">Name</form:label></td>
	        <td><form:input path="userName" /></td>
	        <td><form:errors path="userName" cssClass="form-field-error compact" /></td>
	    </tr>
	    <tr>
	        <td class="heading"><form:label path="alias">Alias</form:label></td>
	        <td><form:input path="alias" /></td>
	        <td><form:errors path="alias" cssClass="form-field-error compact" /></td>
	    </tr>
	    <tr>
	        <td class="heading"><form:label path="password">Password</form:label></td>
	        <td><form:password path="password" /></td>
	        <td><form:errors path="password" cssClass="form-field-error compact" /></td>
	    </tr>
	    <tr>
	        <td class="heading"><form:label path="selectedRoles">Roles</form:label></td>
	        <td><form:select path="selectedRoles" multiple="true" items="${availableRoles}" /></td>
	    </tr>
			</table> 
			<br />

	    <input class="button" type="submit" value="${submitLabel}" />
	    
	    <%-- This form is used for updates as well, in which case we need 
	    	to carry through the id and password --%>
	    <form:hidden path="userId" />
	    
	    <%-- Keep a handle on the page item rendering this form - need it when dealing with validation errors. --%>
	    <form:hidden path="userFormPageId" />
	    <form:hidden path="encryptedPassword" />
	    
	    <%-- Keep this property hidden from site visitors --%>
	    <form:hidden path="demoUser" />
	</form:form>		  	
</article>

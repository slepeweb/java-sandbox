<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<%-- Assume we're adding a new user --%>
<c:set var="submitAction" value="/sandbox/user/add" />
<c:set var="submitLabel" value="Add user" />
<c:set var="pageHeading" value="Add a new user" />

<c:if test="${not empty userForm.id}">
	<%-- Oh, we have an id, so we must be updating an existing user --%>
	<c:set var="submitAction" value="/sandbox/user/update" />
	<c:set var="submitLabel" value="Update user" />
	<c:set var="pageHeading" value="Update an existing user" />
</c:if>
			
<article class="first">
	<h2>${pageHeading}</h2>
	<jsp:include page="./flash-messages.jsp" />
	  
	<form:form method="post" action="${submitAction}" commandName="userForm">
	  
	    <table class="two-col-table">
	    <tr>
	        <td class="heading"><form:label path="name">Name</form:label></td>
	        <td><form:input path="name" /></td>
	        <td><form:errors path="name" cssClass="form-field-error compact" /></td>
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
	    <form:hidden path="id" />
	    <form:hidden path="encryptedPassword" />
	    
	    <%-- Keep this property hidden from site visitors --%>
	    <form:hidden path="demoUser" />
	</form:form>
	  	
</article>

<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
  
<c:set var="submitAction" value="/user/add" />
<c:set var="submitLabel" value="Add user" />
<c:if test="${not empty userForm.id}">
	<c:set var="submitAction" value="/user/update" />
	<c:set var="submitLabel" value="Update user" />
</c:if>
			
<article class="first">
	<h2>User management</h2>
	  
	<form:form method="post" action="${submitAction}" commandName="userForm">
	  
			<form:errors path="*" element="div" cssClass="form-error" />
			
	    <table class="two-col-table">
	    <tr>
	        <td class="heading"><form:label path="name">Name</form:label></td>
	        <td><form:input path="name" /></td>
	    </tr>
	    <tr>
	        <td class="heading"><form:label path="alias">Alias</form:label></td>
	        <td><form:input path="alias" /></td>
	    </tr>
	    <tr>
	        <td class="heading"><form:label path="password">Password</form:label></td>
	        <td><form:password path="password" /></td>
	    </tr>
	    <tr>
	        <td class="heading"><form:label path="selectedRoles">Roles</form:label></td>
	        <td><form:select path="selectedRoles" multiple="true" items="${availableRoles}" /></td>
	    </tr>
			</table> 
			<br />

	    <input class="button" type="submit" value="${submitLabel}" />
	    <form:hidden path="id" />
	    <form:hidden path="encryptedPassword" />
	</form:form>
	  	
</article>

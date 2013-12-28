<%@ 
    taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%><%@ 
    taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%><%@
    taglib uri="http://www.springframework.org/tags" prefix="spring"%><%@
    taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<article class="first">
	<h2>Please register your details</h2>
	<%-- TODO: Put this in a common folder - also used by 'user' functionality --%>
	<jsp:include page="../user/flash-messages.jsp" />
	
	<form:form method="post" commandName="customer">
	  
	    <table class="two-col-table">
	    <tr>
	        <td class="heading"><form:label path="name">Full name</form:label></td>
	        <td><form:input path="name" /></td>
	        <td><form:errors path="name" cssClass="form-field-error compact" /></td>
	    </tr>
	    <tr>
	        <td class="heading"><form:label path="address">Address</form:label></td>
	        <td><form:input path="address" /></td>
	        <td><form:errors path="address" cssClass="form-field-error compact" /></td>
	    </tr>
	    <tr>
	        <td class="heading"><form:label path="zipCode">Postcode</form:label></td>
	        <td><form:input path="zipCode" /></td>
	        <td><form:errors path="zipCode" cssClass="form-field-error compact" /></td>
	    </tr>
	    <tr>
	        <td class="heading"><form:label path="phoneNumber">Telephone</form:label></td>
	        <td><form:input path="phoneNumber" /></td>
	        <td><form:errors path="phoneNumber" cssClass="form-field-error compact" /></td>
	    </tr>
	    <tr>
	        <td class="heading"><form:label path="email">Email</form:label></td>
	        <td><form:input path="email" /></td>
	        <td><form:errors path="email" cssClass="form-field-error compact" /></td>
	    </tr>
	    <tr>
	        <td class="heading"><form:label path="confirmEmail">Confirm email</form:label></td>
	        <td><form:input path="confirmEmail" /></td>
	        <td><form:errors path="confirmEmail" cssClass="form-field-error compact" /></td>
	    </tr>
	    <tr>
	        <td class="heading"><form:label path="password">Password</form:label></td>
	        <td><form:input path="password" /></td>
	        <td><form:errors path="password" cssClass="form-field-error compact" /></td>
	    </tr>
	    <tr>
	        <td class="heading"><form:label path="confirmPassword">Confirm password</form:label></td>
	        <td><form:password path="confirmPassword" /></td>
	        <td><form:errors path="confirmPassword" cssClass="form-field-error compact" /></td>
	    </tr>
			</table> 
			<br />

	    <input class="button" type="submit" name="_eventId_register" value="Register" />	    
	</form:form>
</article>
<%@ 
    taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%><%@ 
    taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%><%@
    taglib uri="http://www.springframework.org/tags" prefix="spring"%><%@
    taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<h2>Customer Registration</h2>
<form:form commandName="customer">
	<table class="two-col-table">
		<tr>
			<td class="heading"><form:label path="name">Name</form:label></td>
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
			<td class="heading"><form:label path="phoneNumber">Phone number</form:label></td>
			<td><form:input path="phoneNumber" /></td>
			<td><form:errors path="phoneNumber" cssClass="form-field-error compact" /></td>
		</tr>
	</table>
	
	<br />
	<input type="submit" name="_eventId_submit" value="Submit" />
	<input type="submit" name="_eventId_cancel" value="Cancel" />
	<input type="hidden" name="_flowExecutionKey" value="${flowExecutionKey}" />
</form:form>

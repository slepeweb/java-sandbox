<%@ 
    taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%><%@ 
    taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%><%@
    taglib uri="http://www.springframework.org/tags" prefix="spring"%><%@
    taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<div>
	<h2>Payment</h2>
	<form:form commandName="paymentDetails">
		<b>Type: </b>
		<br />
		<form:radiobuttons path="paymentType" items="${paymentTypeList}" delimiter="<br />" />
		<br />
		<br />
		<input type="submit" class="button" name="_eventId_paymentSubmitted" value="Take payment" />
		<input type="submit" class="button" name="_eventId_cancel" value="Cancel" />
	</form:form>
</div>

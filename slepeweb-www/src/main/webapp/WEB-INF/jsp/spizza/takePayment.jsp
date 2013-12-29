<%@ 
    taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%><%@ 
    taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%><%@
    taglib uri="http://www.springframework.org/tags" prefix="spring"%><%@
    taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<article>
	<h2>Payment details</h2>
	
	<form:form method="post" commandName="paymentForm">
		<table class="two-col-table">

      <tr>
      	<td class="heading">Total amount</td>
      	<td>${order.totalFormatted}</td>
	      <td></td>
      </tr>
      
      <tr>
      	<td class="heading"><form:label path="cardType">Card type</form:label></td>
      	<td>
				<c:forEach items="${paymentForm.cardOptions}" var="option">
					<form:radiobutton path="cardType" label="${option.label}" value="${option.key}" /><br />
				</c:forEach>
      	</td>
	      <td><form:errors path="cardType" cssClass="form-field-error compact" /></td>
      </tr>
      
      <tr>
      	<td class="heading"><form:label path="cardOwner">Name on card</form:label></td>
      	<td><form:input path="cardOwner" /></td>
	      <td><form:errors path="cardOwner" cssClass="form-field-error compact" /></td>
      </tr>
      
      <tr>
      	<td class="heading"><form:label path="cardNumber">Card number</form:label></td>
      	<td><form:input path="cardNumber" /></td>
	      <td><form:errors path="cardNumber" cssClass="form-field-error compact" /></td>
      </tr>
      
      <tr>
      	<td class="heading"><form:label path="ccvCode">CCV code</form:label></td>
      	<td><form:input path="ccvCode" /></td>
	      <td><form:errors path="ccvCode" cssClass="form-field-error compact" /></td>
      </tr>
      
      <tr>
      	<td class="heading"><form:label path="expiryDate">Expiry date (MM/YYYY)</form:label></td>
      	<td><form:input path="expiryDate" /></td>
	      <td><form:errors path="expiryDate" cssClass="form-field-error compact" /></td>
      </tr>
      
	    <tr>
        <td class="buttons"><input type="submit" class="button" name="_eventId_submitPayment" value="Take payment" /></td>
        <td><input type="submit" class="button" name="_eventId_cancel" value="Cancel" /></td>
	    </tr>
		</table> 
	</form:form>
</article>

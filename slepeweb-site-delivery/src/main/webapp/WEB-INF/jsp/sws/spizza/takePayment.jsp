<%@ 
	include file="/WEB-INF/jsp/common/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/common/tagDirectives.jsp" %>
	
<sw:spizzaLayout>
	<gen:debug><!-- jsp/sws/spizza/takePayment.jsp --></gen:debug>

	<article>
		<h2>${contentMap.heading}</h2>
		
		<form:form method="post" commandName="paymentForm">
			<table>
	
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
			</table> 
	      
	    <div class="row">
	    	<div class="col-1-2">
	        <input type="submit" class="button small special" name="_eventId_submitPayment" value="Take payment" />
        </div>
	    	<div class="col-1-2">
	        <input type="submit" class="button small special" name="_eventId_cancel" value="Cancel order" />
        </div>
	    </div>
		</form:form>
	</article>

</sw:spizzaLayout>
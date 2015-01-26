<%@ 
	include file="/WEB-INF/jsp/common/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/common/tagDirectives.jsp" %>
	
<sw:spizzaLayout>
	<gen:debug><!-- jsp/sws/spizza/registrationForm.jsp --></gen:debug>

	<article class="first">
		<h2>${contentMap.heading}</h2>		
		<p>${contentMap.body}</p>
			
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
		        <td class="heading"><form:label path="password">Password</form:label></td>
		        <td><form:password path="password" /></td>
		        <td><form:errors path="password" cssClass="form-field-error compact" /></td>
		    </tr>
		    <tr>
		        <td class="heading"><form:label path="confirmPassword">Confirm password</form:label></td>
		        <td><form:password path="confirmPassword" /></td>
		        <td><form:errors path="confirmPassword" cssClass="form-field-error compact" /></td>
		    </tr>
				</table> 
				
		    <div class="row">
					<div class="6u"><input class="button small special" type="submit" name="_eventId_register" value="Register" /></div>
		      <div class="6u"><input class="button small special" type="submit" name="_eventId_cancel" value="Cancel" /></div>
		    </div>
		</form:form>
	</article>

</sw:spizzaLayout>
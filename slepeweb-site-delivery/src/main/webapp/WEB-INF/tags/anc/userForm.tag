<%@ tag %><%@ 
	attribute name="context" required="true" rtexprvalue="true" %><%@ 
	attribute name="showFormIf" required="true" rtexprvalue="true" %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>
	
<div class="main">
	<c:if test="${not empty _error_1}">
		<div class="error-message"><p>${_error_1}</p></div>
	</c:if>
	
	<h2>${_heading}</h2>
	<p>${_body}</p>

	<c:if test="${showFormIf}">
		<form id="registration-form" method="post" action="">
			<table>
				<tr>
					<td class="heading"><label>Email</label></td>
					<td><input type="email" name="email" placeholder="Your email address"
						<c:if test="${not empty _u}">value="${_u.email}"</c:if>
						<c:if test="${context eq 'update'}">readonly="true"</c:if> /></td>
				</tr>
				
				<c:if test="${context eq 'update'}">
					<tr>
						<td class="heading"><label>Current password</label></td>
						<td><input type="password" name="current" 
							placeholder="Please enter your password" /></td>
					</tr>
				</c:if>
				
				<tr>
					<td class="heading"><label>First name</label></td>
					<td><input type="text" name="firstname" placeholder="Your first name" 
						<c:if test="${not empty _u}">value="${_u.firstName}"</c:if> /></td>
				</tr>
				
				<tr>
					<td class="heading"><label>Last name</label></td>
					<td><input type="text" name="lastname" placeholder="Your last name"
						<c:if test="${not empty _u}">value="${_u.lastName}"</c:if> /></td>
				</tr>
				
				<tr>
					<td class="heading"><label>Phone</label></td>
					<td><input type="tel" name="phone" placeholder="A mobile or landline telephone number"
						<c:if test="${not empty _u}">value="${_u.phone}"</c:if> /></td>
				</tr>
			</table>
			<input id="submit-button" class="button special small" type="button" value="Submit" />
		</form>	
	</c:if>
</div>

<div id="missing-form-data-dialog" class="hide">
  <p>
    All form fields must be completed!
  </p>
</div>
	
<script>
	var _formErrorDialog = null;
	
	$(function(){
		_formErrorDialog = _simpleDialogCreator("#missing-form-data-dialog", "Missing form data");
		
		$("#registration-form #submit-button").click(function(){
			var firstName = $("#registration-form input[name=firstname]").val().trim();
			var lastName = $("#registration-form input[name=lastname]").val().trim();
			var email = $("#registration-form input[name=email]").val().trim();
			var phone = $("#registration-form input[name=phone]").val().trim();
			
			if (firstName && lastName && email && phone) {
				$("#registration-form").submit();
			}
			else {
				_formErrorDialog.dialog("open");
			}
		});
	});
</script>
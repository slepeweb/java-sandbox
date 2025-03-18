<%@ 
	include file="/WEB-INF/jsp/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>
	
<c:set var="_extraInpageJs" scope="request">
	<anc:simpleDialogScript />
</c:set>

<anc:pageLayout type="std">
	<gen:debug><!-- jsp/anc/forgotten/emailform.jsp --></gen:debug>
	
	<div class="main">
		<c:if test="${not empty _error_1}">
			<div class="error-message"><p>${_error_1}</p></div>
		</c:if>
		
		<h2>${_heading}</h2>
		<p>${_body}</p>

		<c:if test="${pageContext.request.method eq 'GET' or not empty _error_1}">
			<form id="forgotten-password-form" method="post" action="">
				<table>
					<tr>
						<td class="heading"><label>Email</label></td>
						<td><input type="email" name="email" placeholder="The email address you registered with"
							<c:if test="${not empty _email}">value="${_email}"</c:if> /></td>
					</tr>
				</table>
				<input id="submit-button" class="button special small" type="button" value="Submit" />
			</form>	
		</c:if>
	</div>
	
	<div id="simple-dialog" class="hide">
	  <p>
	    Please enter the email address you registered with!
	  </p>
	</div>
	
</anc:pageLayout>

<script>
	var _formErrorDialog = null;
	
	$(function(){
		_formErrorDialog = _simpleDialogCreator("#simple-dialog", "Missing email address");
		
		$("#forgotten-password-form #submit-button").click(function(){
			var email = $("#forgotten-password-form input[name=email]").val().trim();
			
			if (email) {
				$("#forgotten-password-form").submit();
			}
			else {
				_formErrorDialog.dialog("open");
			}
		});
	});
</script>
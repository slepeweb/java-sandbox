<%@ tag %><%@ 
	attribute name="context" required="true" rtexprvalue="true" %><%@ 
	attribute name="showFormIf" required="true" rtexprvalue="true" %><%@ 
	include file="/WEB-INF/jsp/common/tagDirectives.jsp" %>

<%-- 
	This tag file is used in3 different use cases:
	1. New registration
	2. Existing user has forgotten his password
	3. Existing user wants to change his password
	
	The 'context' attribute on this tag is used to indicate use-case 3, where context == 'change'.
	In the other two use-cases, the context attribute is not specified, ie empty.
--%>

<c:choose><c:when test="${context eq 'register'}">
	<c:set var="_labelA">Password</c:set>
	<c:set var="_labelB">Confirm password</c:set>
</c:when><c:otherwise>
	<c:set var="_labelA">New password</c:set>
	<c:set var="_labelB">Confirm new password</c:set>
</c:otherwise></c:choose>


<div class="main">
	<c:if test="${not empty _error_1}">
		<div class="error-message"><p>${_error_1}</p></div>
	</c:if>
	<c:if test="${not empty _error_2}">
		<div class="error-message"><p>${_error_2}</p></div>
	</c:if>
	
	<h2>${_heading}</h2>
	<p>${_body}</p>

	<c:if test="${showFormIf}">
		<form id="password-form" method="post" action="">
			<table>
				<c:if test="${context eq 'change'}">
					<tr>
						<td class="heading"><label>Current password</label></td>
						<td><input type="password" name="current" /></td>
					</tr>
				</c:if>
				
				<tr>
					<td class="heading"><label>${_labelA}</label></td>
					<td><input type="password" name="pwdA" /></td>
				</tr>
				
				<tr>
					<td class="heading"><label>${_labelB}</label></td>
					<td><input type="password" name="pwdB" /></td>
				</tr>
			</table>
			<input type="hidden" name="secret" value="${_secret}" />
			<input id="submit-button" class="button special small" type="button" value="Submit" />
		</form>	
	</c:if>
</div>

<div id="simple-dialog-1" class="hide"> 
  <p>
    The two passwords do not match!
  </p>
</div>
	
<c:if test="${context eq 'change'}">
	<div id="simple-dialog-2" class="hide">
	  <p>
	    Please enter your current password, as well as your new password!
	  </p>
	</div>
</c:if>
	
<script>
	var _nonMatchingDialog = null;
	var _emptyCurrentPasswordDialog = null;
	
	$(function(){
		_nonMatchingDialog = _simpleDialogCreator("#simple-dialog-1", "Non-matching passwords");
		var currentPasswordField = $("#password-form input[name=current]");
		if (currentPasswordField) {
			_emptyCurrentPasswordDialog = _simpleDialogCreator("#simple-dialog-2", "Current password not specified");
		}
		
		$("#password-form #submit-button").click(function(){
			var pwdA = $("#password-form input[name=pwdA]").val().trim();
			var pwdB = $("#password-form input[name=pwdB]").val().trim();
			var ok = true;
			
			if (currentPasswordField) {
				currentPassword = currentPasswordField.val().trim();
				if (! currentPassword) {
					ok = false;
					_emptyCurrentPasswordDialog.dialog("open");
				}
			}
			
			if (ok && pwdA == pwdB) {
				$("#password-form").submit();
			}
			else {
				_nonMatchingDialog.dialog("open");
			}
		});
	});
</script>
<%@ 
	page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false"%><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>

<c:set var="_extraCSS" scope="request">${applicationContextPath}/resources/css/user.css</c:set>

<cms:basicLayout loadjs="${false}">

	<div id="main-wrapper">
	
		<h1>Forgot Password</h1>
		
		<div class="forgot-password">
		
			<c:if test="${not empty msg and error}">
				<div class="user-update-msg error">${msg}</div>
			</c:if>	
			
			<c:choose><c:when test="${empty msg or error}">
				<p class="x1pt2em">Please enter your email address, and we'll send you a link in an email
				that will allow you to reset your password.</p>
				
				<form id="login" method="post" action="">
					<div class="ff">
						<label>Email: </label>
						<div class="inputs"><input name="email" size="128" autocomplete="off" /></div>
					</div>
								
					<button class="action" type="submit">Submit</button>
				</form>
			</c:when><c:otherwise>
			
				<p class="x1pt2em">${msg}</p>
			
			</c:otherwise></c:choose>
		</div>
	</div>
	
</cms:basicLayout>

<script>
$(function() {
	$('input[name=email]').focus();
})
</script>
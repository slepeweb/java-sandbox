<%@ 
	page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false"%><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>

<cms:basicLayout loadjs="${false}">

	<div id="main-wrapper">
		<h1>CMS Login</h1>
		
		<c:if test="${not empty error}">
			<div class="underline red"><p>${error}</p></div>
		</c:if>
		<c:if test="${not empty msg}">
			<div class="underline green"><p>${msg}</p></div>
		</c:if>	
		
		<p class="x1pt2em">Please enter your login details to gain access to the CMS:</p>
		
		<form id="login" method="post" action="">
			<div class="ff">
				<label>Alias: </label>
				<div class="inputs"><input name="alias" size="128" autocomplete="off" /></div>
			</div>
			
			<div class="ff">
				<label>Password: </label>
				<div class="inputs"><input type="password" name="password" size="32" autocomplete="off" /></div>
			</div>
			
			<button class="action" type="submit">Login</button>
		</form>	
		
		<p style="margin-top: 1em;">
			<a href="/cms/user/forgot/password" target="_blank">Forgot password?</a>
		</p>

	</div>
	
</cms:basicLayout>

<script>
$(function() {
	$('input[name=alias]').focus();
})
</script>
<%@ 
	page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false"%><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>

<cms:basicLayout loadjs="${false}">

	<div id="main-wrapper">
		<h2>CMS Login</h2>
		
		<c:if test="${not empty error}">
			<div class="underline red"><p>${error}</p></div>
		</c:if>
		<c:if test="${not empty msg}">
			<div class="underline green"><p>${msg}</p></div>
		</c:if>	
		
		<p>Please enter your login details to gain access to the CMS:</p>
		
		<form id="login" method="post" action="">
			<div>
				<label>Alias: </label><input name="alias" size="128" autocomplete="off" />
			</div>
			
			<div>
				<label>Password: </label><input type="password" name="password" size="32" autocomplete="off" />
			</div>
			
			<button class="action" type="submit">Login</button>
		</form>	
	</div>
	
</cms:basicLayout>

<script>
$(function() {
	$('input[name=alias]').focus();
})
</script>
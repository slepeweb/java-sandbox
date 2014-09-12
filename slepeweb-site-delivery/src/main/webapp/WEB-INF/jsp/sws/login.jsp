<%@ 
    taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%><%@ 
    taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
    
    
<article class="first">
	<h2>Login</h2>
	
	<c:if test="${not empty error}">
		<div class="error">${error}</div>
	</c:if>
	<c:if test="${not empty msg}">
		<div class="msg">${msg}</div>
	</c:if>	
	
	<p>
		A login account is required in order to see privileged content. 
		Please contact the <a href="mailto:admin@slepeweb.com">administrator</a>
		if you need an account.
	</p>
	
	<form method="post" action="<c:url value="/j_spring_security_check" />">
		<table class="two-col-table">
			<tr>
				<td class="heading"><label for="alias">User name</label></td>
				<td><input name="alias" size="32" /></td>
			</tr>
			<tr>
				<td class="heading"><label for="password">Password</label></td>
				<td><input type="password" name="password" size="32" /></td>
			</tr>
		</table>
		<br />
		<input class="button" type="submit" name="Submit" />
	</form>
</article>

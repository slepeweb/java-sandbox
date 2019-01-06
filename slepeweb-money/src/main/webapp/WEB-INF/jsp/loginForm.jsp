<%@ 
	include file="/WEB-INF/jsp/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>
	
<mny:standardLayout>
	<h1>Login <c:if 
		test="${not empty error}"><span class="flash failure">${error}</span></c:if><c:if 
		test="${not empty msg}"><span class="flash success">${msg}</span></c:if></h1>
	
	<p>Please enter your login details:</p>
	
	<c:if test="${not _isAuthor}">	
		<form id="login" method="post" action="<c:url value="/j_spring_security_check" />">
			<table>
				<tr>
					<td class="heading"><label for="alias">User name</label></td>
					<td><input type="text" name="alias" size="32" /></td>
				</tr>
				<tr>
					<td class="heading"><label for="password">Password</label></td>
					<td><input type="password" name="password" size="32" /></td>
				</tr>
			</table>
			<br />
			<input class="button" type="submit" name="Submit" value="Login" />
		</form>	
	</c:if>		
</mny:standardLayout>
	
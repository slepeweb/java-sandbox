<%@ tag %><%@ include file="/WEB-INF/jsp/common/tagDirectives.jsp" %>

<gen:debug><!-- jsp/sws/loginForm.tag --></gen:debug>

<article class="first">

	<c:if test="${not empty error}">
		<div class="underline red"><p>${error}</p></div>
	</c:if>
	<c:if test="${not empty msg}">
		<div class="underline green"><p>${msg}</p></div>
	</c:if>	
	
	<c:if test="${not _isGuest and not _isAdmin}">	
		<form method="post" action="<c:url value="/j_spring_security_check" />">
			<table>
				<tr>
					<td class="heading"><label for="alias">User name</label></td>
					<td><input type="text" name="alias" /></td>
				</tr>
				<tr>
					<td class="heading"><label for="password">Password</label></td>
					<td><input type="password" name="password" /></td>
				</tr>
			</table>
			<input class="button special small" type="submit" value="Login" />
<%-- 			<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" /> --%>
		</form>	
	</c:if>
	
</article>

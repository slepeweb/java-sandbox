<%@ tag %><%@ include file="/WEB-INF/jsp/common/tagDirectives.jsp" %>

<gen:debug><!-- jsp/sws/loginForm.tag --></gen:debug>

<article class="first">

	<h2>${_item.fields.title}</h2>
	
	<c:if test="${not empty error}">
		<div class="underline red"><p>${error}</p></div>
	</c:if>
	<c:if test="${not empty msg}">
		<div class="underline green"><p>${msg}</p></div>
	</c:if>	
	
	<p>${_item.fields.bodytext}</p>
	
	<c:if test="${not _isGuest and not _isAdmin}">	
		<form method="post" action="<c:url value="/j_spring_security_check" />">
			<table>
				<tr>
					<td class="heading"><label for="alias">User name</label></td>
					<td><input name="alias" size="32" /></td>
				</tr>
				<tr>
					<td class="heading"><label for="password">Password</label></td>
					<td><input type="password" name="password" size="32" /></td>
				</tr>
			</table>
			<input class="button special small" type="submit" value="Login" />
		</form>	
	</c:if>
	
</article>

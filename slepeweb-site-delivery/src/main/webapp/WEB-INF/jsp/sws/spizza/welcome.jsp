<%@ 
	include file="/WEB-INF/jsp/common/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/common/tagDirectives.jsp" %>
	
<sw:spizzaLayout>
	<gen:debug><!-- jsp/sws/spizza/welcome.jsp --></gen:debug>
	<c:set var="_replacement">${flowExecutionUrl}&_eventId=register</c:set>

	<article class="first">
		<h2>${_page.item.fields.title}</h2>
		<p>${contentMap.main}</p>

		<form:form method="post" commandName="loginForm">
			<c:set var="logonErrors">
				<form:errors path="email" cssClass="form-field-error compact" />
			</c:set>
		</form:form>

		<c:choose>
			<c:when test="${empty logonErrors}">
				<p>${fn:replace(contentMap.help_account, '[register.href]', _replacement)}</p>
			</c:when>
			<c:otherwise>
				<p class="flash-msg orange">${fn:replace(contentMap.help_login, '[register.href]', _replacement)}</p>
			</c:otherwise>
		</c:choose>

		<form:form method="post" action="${flowExecutionUrl}" commandName="loginForm">

			<table class="two-col-table">
				<tr>
					<td class="heading"><form:label path="email">Email address</form:label></td>
					<td><form:input path="email" /></td>
					<td><form:errors path="email"
							cssClass="form-field-error compact" /></td>
				</tr>
				<tr>
					<td class="heading"><form:label path="password">Password</form:label></td>
					<td><form:password path="password" /></td>
					<td><form:errors path="password"
							cssClass="form-field-error compact" /></td>
				</tr>
			</table>

			<input class="button small special" type="submit" name="_eventId_login"
				value="Login" />
		</form:form>
	</article>
</sw:spizzaLayout>
<%@ 
	include file="/WEB-INF/jsp/common/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/common/tagDirectives.jsp" %>
	
<sw:spizzaLayout>
	<gen:debug><!-- jsp/sws/spizza/welcome.jsp --></gen:debug>

	<article class="first">
		<h2>Pizza Spizza !</h2>

		<p>
			This is a Spring Webflow demonstration, based upon an example in a
			book by Craig Walls <span class="italic">(Spring in Action)</span>. A
			series of forms and user interactions are tied together by the Spring
			Webflow framework, to simulate an online pizza ordering service.
		</p>

		<form:form method="post" commandName="loginForm">
			<c:set var="logonErrors">
				<form:errors path="email" cssClass="form-field-error compact" />
			</c:set>
		</form:form>

		<c:choose>
			<c:when test="${empty logonErrors}">
				<p>
					If you have an account with us, then please use the form below to
					log on and place an order. Otherwise, please <a
						href="${flowExecutionUrl}&_eventId=register">register</a>.
				</p>
			</c:when>
			<c:otherwise>
				<p class="flash-msg orange">
					For demo purposes, you can login as 'fred@flintstone.com' /
					'rubble'. Otherwise, please <a
						href="${flowExecutionUrl}&_eventId=register">register</a> a new
					account.
				</p>
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
			<br />

			<input class="button" type="submit" name="_eventId_login"
				value="Login" />
		</form:form>
	</article>
</sw:spizzaLayout>
<%@ 
    taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%><%@ 
    taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%><%@
    taglib uri="http://www.springframework.org/tags" prefix="spring"%><%@
    taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
    
    
<article class="first">
	<h2>Login</h2>
	<p>
		Please login to access the desired page:
	</p>
	
	<form:form method="post" action="/login" commandName="loginForm">
		<form:errors path="*" element="div" cssClass="form-error" />
		<table class="two-col-table">
			<tr>
				<td class="heading"><label for="alias">User name</label></td>
				<td><form:input path="alias" size="32" /></td>
			</tr>
			<tr>
				<td class="heading"><label for="password">Password</label></td>
				<td><form:password path="password" size="32" /></td>
			</tr>
		</table>
		<form:hidden path="nextPath" />
		<br />
		<input class="button" type="submit" name="Submit" />
	</form:form>
</article>

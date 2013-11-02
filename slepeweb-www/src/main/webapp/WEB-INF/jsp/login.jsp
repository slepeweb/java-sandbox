<%@ 
    taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%><%@ 
    taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
    
<article class="first">
	<h2>Login</h2>
	<p>
		Please login to access the desired page:
	</p>
	<form method="post" action="/logon">
		<table class="two-col-table">
			<tr><td class="heading">User name</td><td><input type="text" name="username" size="32" /></td></tr>
			<tr><td class="heading">Password</td><td><input type="password" name="password" size="32" /></td></tr>
		</table>
		<input type="hidden" name="target" value="${_page.nextPath}" />
		<br />
		<input class="button" type="submit" name="Submit" />
	</form>
</article>

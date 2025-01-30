<%@ 
	include file="/WEB-INF/jsp/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>
	
<!-- loginForm.jsp -->

<mny:standardLayout>
	<h1>Login <c:if 
		test="${not empty error}"><span class="flash failure">${error}</span></c:if><c:if 
		test="${not empty msg}"><span class="flash success">${msg}</span></c:if></h1>
	
	<p>Please enter your login details:</p>
	
	<c:if test="${not _isAuthor}">	
		<form id="login" method="post" action="">
			<table>
			
	    	<mny:tableRow heading="User name">
					<input type="text" name="alias" size="32" autocomplete="off" />
				</mny:tableRow>
				
	    	<mny:tableRow heading="Password">
					<input type="password" name="password" size="32" />
				</mny:tableRow>

			</table>
			
			<br />
			<input class="button" type="submit" name="Submit" value="Login" />
		</form>	
	</c:if>		
</mny:standardLayout>
	
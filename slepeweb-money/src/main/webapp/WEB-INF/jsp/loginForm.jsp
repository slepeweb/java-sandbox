<%@ 
	include file="/WEB-INF/jsp/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>
	
<!-- loginForm.jsp -->

<mny:standardLayout>
	<mny:pageHeading heading="Login" />
	
	<p class="datetimenow">${_now}</p>
	<p>Please enter your login details:</p>
	
	<form id="login" method="post" action="">
		<table>
		
    	<mny:tableRow heading="User name">
				<input type="text" name="alias" size="32" autocomplete="off" />
			</mny:tableRow>
			
    	<mny:tableRow heading="Password">
				<input type="password" name="password" size="32" autocomplete="off" />
			</mny:tableRow>

		</table>
		
		<br />
		<input class="button" type="submit" name="Submit" value="Login" />
	</form>	
</mny:standardLayout>

<script>
$(function() {
	$('input[name=alias]').focus();
})
</script>
	
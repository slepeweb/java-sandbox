<%@ 
	include file="/WEB-INF/jsp/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>
	
<gen:debug><!-- jsp/anc/template/login.jsp --></gen:debug>

<anc:pageLayout type="std">	
	<gen:loginForm>		
		<p>
			Not registered yet? <a href="/${_item.language}/login/register?view=form">Register</a>
			<br />
			Forgotten your password? <a href="/${_item.language}/login/forgotten?view=forgotten">Reset</a>
		</p>
	</gen:loginForm>	
</anc:pageLayout>

<script>
$(function() {
	_site.support.ajax('GET', '/rest/anc/login/redirect/' + _origId, {dataType: 'json', mimeType: 'application/json'}, function(resp) {
		if (! resp.error) {
			$('input[name=redirectPath]').val(resp.data)
		}
	})
})
</script>
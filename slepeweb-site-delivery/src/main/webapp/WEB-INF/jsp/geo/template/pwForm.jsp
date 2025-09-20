<%@ 
	include file="/WEB-INF/jsp/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>
	
<gen:debug><!-- jsp/geo/template/pwForm.jsp --></gen:debug>

<script>
	let _flash = '${param.error}';
	let _focus = 'password';
</script>

<c:set var="_extraJs" scope="request">/resources/geo/js/flasher.js</c:set>

<c:set var="_extraInpageCss" scope="request">
	form#user-form {
		background: pink;
	}
</c:set>

<c:set var="_extraCss" scope="request">/resources/geo/css/pwList.css</c:set>

<geo:pageLayout type="std">

	<div class="main standard-3col">
		<geo:emptyLeftside />
		
		<div class="rightside">
			<div class="mainbody full-width">
			
				<div class="flash-error">${param.error}</div>
		
				<geo:title />
				<p>${_item.fields.body_1}</p>
				
				<form id="pwg-login-form" method="post" action="">
					<input type="hidden" name="alias" value="george" />
					
					<table>
						<tr>
							<td><label>Important accounts only?: </label></td>
							<td><input type="checkbox" name="important" checked autocomplete="off" /></td>
						</tr>
						
						<tr>
							<td><label>Root password: </label></td>
							<td><input type="password" name="password" autocomplete="off" /></td>
						</tr>
						
					</table>

					<input class="button" type="submit" value="Submit" />
					
				</form>
							
			</div>
		</div>
	</div>
	
</geo:pageLayout>

<script>
	$(function() {
		$('input[name=alias]').focus()
	})
</script>		


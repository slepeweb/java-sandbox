<%@ 
	include file="/WEB-INF/jsp/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>
	
<gen:debug><!-- jsp/geo/template/moneyForm.jsp --></gen:debug>

<script>
	let _flash = '${param.error}';
	let _focus = 'password';
</script>

<c:set var="_extraJs" scope="request">/resources/geo/js/flasher.js</c:set>

<c:set var="_extraInpageCss" scope="request">
	form#money-login-form table {
		background: red;
	}
	
	form#money-login-form input[type=submit] {
		margin-top: 1em;
	}
</c:set>

<geo:pageLayout type="std">

	<div class="main standard-3col">
		<geo:emptyLeftside />
		
		<div class="rightside">
			<div class="mainbody full-width">
			
				<div class="flash-error">${param.error}</div>
		
				<geo:title />
				<p>${_item.fields.body_1}</p>
				
				<form id="money-login-form" method="post" action="">
					<input type="hidden" name="alias" value="george" />
					
					<table>
						<tr>
							<td><label>Master password: </label></td>
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
		$('input[name=password]').focus()
	})
</script>		


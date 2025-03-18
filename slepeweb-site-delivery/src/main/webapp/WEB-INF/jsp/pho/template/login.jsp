<%@ 
	include file="/WEB-INF/jsp/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>
	
<c:set var="_extraInpageCss" scope="request">
</c:set>

<pho:pageLayout type="std">
	<gen:debug><!-- jsp/pho/template/login.jsp --></gen:debug>
	
	<div class="main">
		<c:if test="${not empty error}">
			<div class="underline red"><p>${error}</p></div>
		</c:if>
		<c:if test="${not empty msg}">
			<div class="underline green"><p>${msg}</p></div>
		</c:if>	
		
		<h2>${_item.fields.heading}</h2>
		<p>${_item.fields.bodytext}</p>
		
		<form method="post" action="">
			<table>
				<tr>
					<td class="heading"><label>Alias</label></td>
					<td><input type="text" name="alias" autocomplete="off" /></td>
				</tr>
				<tr>
					<td class="heading"><label>Password</label></td>
					<td><input type="password" name="password" autocomplete="off" /></td>
				</tr>
			</table>
			<input class="button special small" type="submit" value="Login" />
			<input type="hidden" name="originalPath" value="${_originalPath}" />
		</form>	
	</div>
	
</pho:pageLayout>

<script>
$(function() {
	$('input[name=alias]').focus();
})
</script>
<%@ 
	include file="/WEB-INF/jsp/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>
	
<gen:debug><!-- jsp/geo/template/pwList.jsp --></gen:debug>

<c:set var="_extraCss" scope="request">/resources/geo/css/pwList.css</c:set>

<geo:pageLayout type="std">

	<div class="main standard-wide">
	
		<geo:title />
		
		<p>
			<c:choose><c:when test="${_important}">
				This list contains the more important accounts. <a href="${_item.path}">Re-submit the form</a> if
				you need to see ALL accounts.
			</c:when><c:otherwise>
				This list contains ALL accounts. <a href="${_item.path}">Re-submit the form</a> if
				you need to see only the more important accounts.
			</c:otherwise></c:choose>
		</p>
				
		<c:forEach items="${_pwl.groups}" var="_group">
			<h3>${_group.category}</h3>
			
			<table class="password-list">
				<tr>
					<th>Company</th>
					<th class="hideable">Website</th>
					<th>Login</th>
				</tr>
	
				<c:forEach items="${_group.accounts}" var="_acct">
					<tr>
						<td>${_acct.company}</td>
						<td class="hideable"><a href="http://${_acct.website}" target="_blank">${_acct.website}</a></td>
						<td class="auth-details">
							<div>${_acct.login}<br />${_acct.password}</div>
							<div class="notes">${_acct.notes}</div>
						</td>
					</tr>
				</c:forEach>
			</table>
		</c:forEach>
		
	</div>
		
</geo:pageLayout>

<script>
$(function() {
	$('table.password-list td.auth-details').click(function() {
		$('table.password-list div.notes').css('visibility', 'hidden')
		$(this).find('div.notes').css('visibility', 'visible')
	})
	
	$('table.password-list div.notes').click(function(e) {
		e.stopPropagation()
		$(this).css('visibility', 'hidden')
	})

	$('input[name=filter]').change(function() {
		
	})
})
</script>
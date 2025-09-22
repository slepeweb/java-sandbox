<%@ 
	include file="/WEB-INF/jsp/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>
	
<gen:debug><!-- jsp/geo/template/pwList.jsp --></gen:debug>

<c:set var="_extraCss" scope="request">/resources/geo/css/pwList.css</c:set>

<geo:pageLayout type="std">

	<div class="main standard-wide">
	
		<geo:title />
		
		<c:choose><c:when test="${not empty _pwl.groups}">
		
			<p>
				<c:set var="_backlink"><a href="${_item.path}">Re-submit the form</a></c:set>
				<c:set var="_placeholder">__link__</c:set>
				
				<c:choose><c:when test="${_important}">
					${site:substitute(_item.fields.body_2, _placeholder, _backlink)}
				</c:when><c:otherwise>
					${site:substitute(_item.fields.body_3, _placeholder, _backlink)}
				</c:otherwise></c:choose>
			</p>
			
			${_item.fields.body_4}
					
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
		
		</c:when><c:otherwise>
		
			<c:set var="_url" value="${site:resolveConfig(_item.site.id, 'host.pwg', _siteConfigService)}" />
			${site:substitute(_item.fields.body_5, '__url__', _url)}
			
		</c:otherwise></c:choose>
		
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
})
</script>
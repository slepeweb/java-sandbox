<%@ 
	include file="/WEB-INF/jsp/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>
	
<gen:debug><!-- jsp/geo/template/moneyList.jsp --></gen:debug>

<geo:pageLayout type="std">

	<div class="main standard-wide">
	
		<geo:title />
		
		<c:choose><c:when test="${not empty _dashboard.groups}">
		
			${site:substitute(_item.fields.body_2, '__total__', _dashboard.total)}
					
			<c:forEach items="${_dashboard.groups}" var="_group">
				<h3>${_group.name}</h3>
				
				<table>
					<tr>
						<th>Account</th>
						<th>Balance</th>
						<th>Notes</th>
					</tr>
		
					<c:forEach items="${_group.accounts}" var="_acct">
						<tr>
							<td>${_acct.name}</td>
							<td>&pound;${_acct.balance}</td>
							<td>${_acct.notes}</td>
						</tr>
					</c:forEach>
					
					<c:if test="${fn:length(_group.accounts) gt 1}">
						<tr>
							<td><strong>Total</strong></td>
							<td>&pound;${_group.total}</td>
							<td></td>
						</tr>
					</c:if>
										
				</table>
			</c:forEach>
		
		</c:when><c:otherwise>
		
			<c:set var="_host" value="${site:resolveConfig(_item.site.id, 'host.money', _siteConfigService)}" />
			${site:substitute(_item.fields.body_3, '__host__', _host)}
			
		</c:otherwise></c:choose>
		
	</div>
		
</geo:pageLayout>

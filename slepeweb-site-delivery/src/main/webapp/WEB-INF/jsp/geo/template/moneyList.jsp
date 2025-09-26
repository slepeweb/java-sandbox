<%@ 
	include file="/WEB-INF/jsp/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>
	
<gen:debug><!-- jsp/geo/template/moneyList.jsp --></gen:debug>

<c:set var="_extraInpageCss" scope="request">
	th.col1 {width: 25%;}
	th.col2 {width: 20%;}
	th.col3 {width: auto;}
	td.notes {font-size: 0.9em; line-height: 1.25em;}
	td.total, span.total {font-weight: bold;}
</c:set>

<geo:pageLayout type="std">

	<div class="main standard-wide">
	
		<geo:title />
		
		<c:choose><c:when test="${not empty _dashboard.groups}">
		
			<c:set var="_replacement"><span class="total">${_dashboard.total}</span></c:set>
			${site:substitute(_item.fields.body_2, '__total__', _replacement)}
					
			<c:forEach items="${_dashboard.groups}" var="_group">
				<h3>${_group.name}</h3>
				
				<table>
					<tr>
						<th class="col1">Account</th>
						<th class="col2">Balance</th>
						<th class="col3">Notes</th>
					</tr>
		
					<c:forEach items="${_group.accounts}" var="_acct">
						<tr>
							<td>${_acct.name}</td>
							<td>&pound;${_acct.balance}</td>
							<td class="notes">${_acct.notes}</td>
						</tr>
					</c:forEach>
					
					<c:if test="${fn:length(_group.accounts) gt 1}">
						<tr>
							<td class="total">Total</td>
							<td class="total">&pound;${_group.total}</td>
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

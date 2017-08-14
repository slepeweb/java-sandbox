<%@ 
	include file="/WEB-INF/jsp/common/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/common/tagDirectives.jsp" %>
	
<sw:standardLayout>
	<gen:debug><!-- jsp/sws/commerce/basket-111.jsp --></gen:debug>
	
	<div class="col-1-1">	
		<h2>${_page.title}</h2>
		${_page.body}
		
		<c:if test="${_basket.notEmpty}">
			<table>
				<tr>
					<th></th><th>Item</th><th>Price</th><th>Quantity</th><th>Total</th>
				</tr>
				
				<c:forEach items="${_basket.items}" var="_oi">
					<c:set var="_thumb" value="${site:resolveOrderItemThumbnail(_oi.product, _oi.qualifier)}" />
					<tr>
						<td><c:if test="${not empty _thumb}"><img src="${_thumb.path}?width=100" /></c:if></td>
						<td>${_oi.product.fields.title} <c:if 
							test="${not empty _oi.product.alphaAxis}"><br /><span class="smaller">${_oi.product.alphaAxis.label}: ${_oi.variant.alphaAxisValue.value}<c:if 
								test="${not empty _oi.product.betaAxis}">, ${_oi.product.betaAxis.label}: ${_oi.variant.betaAxisValue.value}</c:if></span></c:if></td>
								
						<td>${_oi.product.priceInPoundsAsString}</td>
						<td>${_oi.quantity}</td>
						<td>${_oi.totalValueAsString}</td>
					</tr>
				</c:forEach>
				<tr>
					<td class="horiz-rule" colspan="4"></td>
					<td class="horiz-rule">${_basket.totalValueAsString}</td>
				</tr>
			</table>
		</c:if>
		
	</div>			
	
</sw:standardLayout>
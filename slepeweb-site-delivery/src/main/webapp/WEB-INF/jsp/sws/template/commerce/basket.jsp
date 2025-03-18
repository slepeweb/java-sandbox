<%@ 
	include file="/WEB-INF/jsp/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>
	
<gen:debug><!-- jsp/sws/commerce/basket.jsp --></gen:debug>
	
<c:choose><c:when test="${_basket.notEmpty}">
	<table>
		<tr>
			<th></th><th>Item</th><th>Price</th><th>Quantity</th><th>Total</th><th></th>
		</tr>
		
		<c:forEach items="${_basket.items}" var="_oi">
			<c:set var="_thumb" value="${site:resolveOrderItemThumbnail(_oi.product, _oi.qualifier)}" />
			<tr>
				<td><c:if test="${not empty _thumb}"><img src="${_thumb.path}?width=100" /></c:if></td>
				<td>${_oi.product.fields.title} <c:if 
					test="${not empty _oi.product.alphaAxis}"><br /><span class="smaller">${_oi.product.alphaAxis.label}: ${_oi.variant.alphaAxisValue.value}<c:if 
						test="${not empty _oi.product.betaAxis}">, ${_oi.product.betaAxis.label}: ${_oi.variant.betaAxisValue.value}</c:if></span></c:if></td>
						
				<td>${_oi.product.priceInPoundsAsString}</td>
				<td>
					<select class="basket-change-quantity" data-id="${_oi.product.origId}" data-q="${_oi.qualifier}">
						<c:forTokens items="1,2,3,4,5" delims="," var="_tok">
							<option value="${_tok}"<c:if test="${_tok eq _oi.quantity}">selected</c:if>>${_tok}</option>
						</c:forTokens>
					</select></td>
				<td>${_oi.totalValueAsString}</td>
				<td><button class="basket-remove" 
					data-id="${_oi.product.origId}" data-q="${_oi.qualifier}">Remove</button></td>
			</tr>
		</c:forEach>
		<tr>
			<td class="horiz-rule" colspan="4"></td>
			<td class="horiz-rule">${_basket.totalValueAsString}</td>
		</tr>
	</table>
</c:when><c:otherwise>
	<p>Your basket is empty!</p>
</c:otherwise></c:choose>

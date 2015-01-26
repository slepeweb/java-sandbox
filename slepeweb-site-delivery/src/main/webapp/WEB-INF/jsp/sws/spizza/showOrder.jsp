<%@ 
	include file="/WEB-INF/jsp/common/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/common/tagDirectives.jsp" %>
	
<sw:spizzaLayout>
	<gen:debug><!-- jsp/sws/spizza/showOrder.jsp --></gen:debug>

	<article>
		<h2>${contentMap.heading}</h2>
		
		<p><c:if test="${fn:length(order.pizzas) == 1}">${fn:replace(contentMap.response_A, '[customer.name]', order.customer.name)}</c:if>
			${fn:replace(fn:replace(contentMap.response_B, '[order.id]', order.id), '[order.total]', order.totalFormatted)}</p>
		
	
		<form:form action="${flowExecutionUrl}">
			<table>
				<tr>
					<th>Base</th>
					<th>Size</th>
					<th>Toppings</th>
					<th>Price</th>
					<th>Remove?</th>
				</tr>
				
				<c:forEach items="${order.pizzas}" var="pizza" varStatus="status">
			    <tr>
						<td>${pizza.base.label}</td>
						<td>${pizza.size.label}</td>
						<td>${pizza.toppingsAsString}</td>
						<td>${pizza.priceFormatted}</td>
						<td align="center"><a href="${flowExecutionUrl}&_eventId_remove&id=${status.count}"><i 
							class="fa fa-trash" title="Remove"></i></a></td>
					</tr>
				</c:forEach>
			</table> 
			
			<div class="row">
				<div class="4u"><input type="submit" class="button small special" name="_eventId_createPizza" value="More pizza" /></div>
				<div class="4u"><input type="submit" class="button small special" name="_eventId_checkout" value="Checkout" /></div>
				<div class="4u"><input type="submit" class="button small special" name="_eventId_cancel" value="Cancel order" /></div>
			</div>
		</form:form>
	</article>

</sw:spizzaLayout>
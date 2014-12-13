<%@ 
	include file="/WEB-INF/jsp/common/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/common/tagDirectives.jsp" %>
	
<sw:spizzaLayout>
	<gen:debug><!-- jsp/sws/spizza/showOrder.jsp --></gen:debug>

	<article>
		<h2>Your order so far</h2>
		
		<p><c:if test="${fn:length(order.pizzas) == 1}">
			Mmm - good choice, ${order.customer.name} ! <br /></c:if>
			Your order, number ${order.id}, totals: <span class="embolden">${order.totalFormatted}</span></p>
		
	
		<form:form action="${flowExecutionUrl}">
			<table id="pizza-order-table">
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
						<td><a href="${flowExecutionUrl}&_eventId_remove&id=${status.count}"><img
									src="/resources/images/delete-icon.jpg"
									alt="Remove"
									title="Remove"></a></td>
					</tr>
				</c:forEach>
				
		    <tr>
		        <td class="buttons"><input type="submit" class="button" name="_eventId_createPizza" value="More pizza" /></td>
		        <td><input type="submit" class="button" name="_eventId_checkout" value="Checkout" /></td>
		        <td><input type="submit" class="button" name="_eventId_cancel" value="Cancel" /></td>
		    </tr>
			</table> 
		</form:form>
	</article>

</sw:spizzaLayout>
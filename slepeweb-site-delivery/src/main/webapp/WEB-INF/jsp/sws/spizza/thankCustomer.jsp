<%@ 
	include file="/WEB-INF/jsp/common/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/common/tagDirectives.jsp" %>
	
<sw:spizzaLayout>
	<gen:debug><!-- jsp/sws/spizza/thankCustomer.jsp --></gen:debug>

	<article>
		<h2>${contentMap.heading}</h2>
		<p>${fn:replace(contentMap.body, '[customer.name]', order.customer.name)}</p>
		
		<table class="two-col-table">
			<tr>
				<td class="heading">Delivery ETA</td>
				<td>${order.deliveryEta} hrs</td>
			</tr>
			<tr>
				<td class="heading">Order items</td>
				<td><ul>
					<c:forEach items="${order.pizzas}" var="pizza">
						<li>${pizza}</li>
					</c:forEach>
				</ul></td>
			</tr>
			<tr>
				<td class="heading">Total amount</td>
				<td>${order.totalFormatted}</td>
			</tr>
			<tr>
				<td class="heading">Card owner</td>
				<td>${order.payment.cardOwner}</td>
			</tr>
		</table>
		
		<p><a class="button special icon fa-level-up" href="${flowExecutionUrl}&_eventId=finished">Finish</a></p>
	</article>
	
</sw:spizzaLayout>
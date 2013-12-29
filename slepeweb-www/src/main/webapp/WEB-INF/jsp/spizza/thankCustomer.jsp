<%@ 
    taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%><%@ 
    taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%><%@
    taglib uri="http://www.springframework.org/tags" prefix="spring"%><%@
    taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<article>
	<h2>Thank you</h2>
	<p>Thanks for your order, ${order.customer.name} ! Your pizza is on it's way</p>
	
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
	
	<br />
	<p><a href="${flowExecutionUrl}&_eventId=finished">Finish</a></p>
</article>
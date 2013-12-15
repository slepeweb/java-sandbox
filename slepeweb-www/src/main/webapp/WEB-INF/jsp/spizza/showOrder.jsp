<%@ 
    taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%><%@ 
    taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%><%@
    taglib uri="http://www.springframework.org/tags" prefix="spring"%><%@
    taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<div>
	<h2>Your order</h2>
	<ul>
	<c:forEach items="${order.pizzas}" var="pizza">
		<li>${pizza.size} (${pizza.toppingsAsString})</li>
	</c:forEach>
	</ul>
	<c:if test="${fn:length(order.pizzas) == 0}">
		<p>Nothing ordered yet - fill your boots!
	</c:if>
	
	<form:form>
		<input type="hidden" name="_flowExecutionKey" value="${flowExecutionKey}" />
		<input type="submit" class="button" name="_eventId_createPizza" value="Create pizza" /><br />
		<input type="submit" class="button" name="_eventId_checkout" value="Checkout" /><br />
		<input type="submit" class="button" name="_eventId_cancel" value="Cancel" />
	</form:form>
</div>

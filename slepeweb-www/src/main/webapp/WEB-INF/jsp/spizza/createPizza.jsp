<%@ 
    taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%><%@ 
    taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%><%@
    taglib uri="http://www.springframework.org/tags" prefix="spring"%><%@
    taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<article>
	<h2>Create Pizza</h2>
	<p>Build a pizza to your taste, by selecting a base, size and any number of toppings.</p>
	
	<form:form action="${flowExecutionUrl}" commandName="pizzaForm">
		<table id="pizza-options-table" class="two-col-table">
			<tr>
				<td>
				<h3>Base</h3>
				<c:forEach items="${pizzaForm.baseOptions}" var="base">
					<form:radiobutton path="base" label="${base.label}" value="${base.key}" /><br />
				</c:forEach>
				</td>

				<td>
				<h3>Size</h3>
				<c:forEach items="${pizzaForm.sizeOptions}" var="size">
					<form:radiobutton path="size" label="${size.label}" value="${size.key}" /><br />
				</c:forEach>
				</td>

				<td>
				<h3>Toppings</h3>
				<c:forEach items="${pizzaForm.toppingOptions}" var="topping">
					<form:checkbox path="toppings" label="${topping.label}" value="${topping.key}" /><br />
				</c:forEach>
				</td>
			</tr>

			<tr>
				<td class="buttons"><input type="submit" class="button" name="_eventId_addPizza" value="Continue" /></td>
				<td></td>
				<td align="right"><input type="submit" class="button" name="_eventId_cancel" value="Cancel" /></td>
			</tr>
		</table>
	</form:form>
</article>

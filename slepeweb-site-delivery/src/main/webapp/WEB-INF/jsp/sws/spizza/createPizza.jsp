<%@ 
	include file="/WEB-INF/jsp/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>
	
<sw:spizzaLayout mainCols="col-3-4">
	<gen:debug><!-- jsp/sws/spizza/createPizza.jsp --></gen:debug>

	<article>
		<h2>${contentMap.heading}</h2>
		<p>${contentMap.body}</p>
		
		<form:form action="${flowExecutionUrl}" commandName="pizzaForm">
			<table>
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
			</table>

			<div class="row">
				<div class="col-1-2"><input type="submit" class="button small special" name="_eventId_addPizza" value="Continue" /></div>
				<div class="col-1-2"><input type="submit" class="button small special" name="_eventId_cancel" value="Cancel order" /></div>
			</div>
		</form:form>
	</article>

</sw:spizzaLayout>
<%@ 
	include file="/WEB-INF/jsp/common/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/common/tagDirectives.jsp" %>
	
<sw:spizzaLayout>
	<gen:debug><!-- jsp/sws/spizza/deliveryWarning.jsp --></gen:debug>
	
	<article>
		<h2>Outside delivery area</h2>
		<p>Hello, ${customer.name}. Your address (${customer.zipCode}) is outside of our delivery area.
			Are you happy to collect it up yourself?</p>
		
		<form:form method="post" action="${flowExecutionUrl}">	  
		    <table class="two-col-table">
		    <tr>
		        <td><input class="button" type="submit" name="_eventId_accept" value="Continue" /></td>
		        <td>I'll collect the order myself</td>
		    </tr>
		    <tr>
		        <td class="buttons"><input class="button" type="submit" name="_eventId_cancel" value="Never mind" /></td>
		        <td>I can't collect today - perhaps another time!</td>
		    </tr>
				</table> 
		</form:form>
	</article>
	
</sw:spizzaLayout>
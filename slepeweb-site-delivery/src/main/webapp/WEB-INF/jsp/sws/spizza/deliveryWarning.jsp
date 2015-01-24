<%@ 
	include file="/WEB-INF/jsp/common/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/common/tagDirectives.jsp" %>
	
<sw:spizzaLayout>
	<gen:debug><!-- jsp/sws/spizza/deliveryWarning.jsp --></gen:debug>
	
	<article>
		<h2>${contentMap.heading}</h2>
		<p>${fn:replace(fn:replace(contentMap.body, '[customer.name]', customer.name), '[customer.zipCode]', customer.zipCode)}</p>
		
		<form:form method="post" action="${flowExecutionUrl}">	  
		    <table class="two-col-table">
		    <tr>
		        <td><input class="button small special" type="submit" name="_eventId_accept" value="Continue" /></td>
		        <td>${contentMap.response_A}</td>
		    </tr>
		    <tr>
		        <td class="buttons"><input class="button small special" type="submit" name="_eventId_cancel" value="Never mind" /></td>
		        <td>${contentMap.response_B}</td>
		    </tr>
				</table> 
		</form:form>
	</article>
	
</sw:spizzaLayout>
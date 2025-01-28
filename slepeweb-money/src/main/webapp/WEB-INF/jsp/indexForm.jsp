<%@ 
	include file="/WEB-INF/jsp/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>
	
<c:set var="_extraJs" scope="request" value="chart.js,search.js,minorcats.js" />

<!-- indexForm.jsp -->

<mny:standardLayout>

	<h2>Re-index by date</h2>
			
	<form method="post" action="${_ctxPath}/index/by/dates">	  
	    <table>
	    	<mny:tableRow heading="From date">
			    <input class="datepicker" id="from" type="text" name="from" value="${_selectedFrom}"
			        	placeholder="Defaults to 1970" />
	    	</mny:tableRow>

	    	<mny:tableRow heading="To date">
			    <input class="datepicker" id="to" type="text" name="to" value="${_selectedTo}"
			        	placeholder="Defaults to today" />
				</mny:tableRow>
			</table> 
			
			<br />
	    <input type="submit" value="Re-index" />    
	</form>		  	
	
</mny:standardLayout>

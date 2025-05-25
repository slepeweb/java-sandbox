<%@ 
	include file="/WEB-INF/jsp/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>
	
<c:set var="_extraJs" scope="request" value="chart.js,search.js,minorcats.js" />

<!-- indexForm.jsp -->

<mny:standardLayout>

	<h2>Re-index by date</h2>
	<p>'Search' and 'Charts' functionality retrieves transaction data using 'Solr', which
		indexes the data in the database, so that it can efficiently produce a variety of searches.
		This index should maintain itself, but should there be any doubt about the integrity of the
		index, it can be re-built at any time using this form. If no dates are specified, the 
		entire database will be re-index, but fear not, it happens remarkably fast on this server.</p>
			
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

<%@ 
	include file="/WEB-INF/jsp/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>
	
<mny:standardLayout>

	<h2>Re-index by date</h2>
			
	<form method="post" action="${_ctxPath}/index/bydates">	  
	    <table>
		    <tr>
		        <td class="heading"><label for="from">From date</label></td>
		        <td><input class="datepicker" id="from" type="text" name="from" value="${_selectedFrom}"
		        	placeholder="Enter start date" /></td>
		    </tr>
		    <tr>
		        <td class="heading"><label for="to">To date</label></td>
		        <td><input class="datepicker" id="to" type="text" name="to" value="${_selectedTo}"
		        	placeholder="Enter end date" /></td>
		    </tr>
			</table> 
			
			<br />
	    <input type="submit" value="Search" />    
	</form>		  	
	
	<script>
		$(function() {
			$(".datepicker").datepicker({
				dateFormat: "yy-mm-dd",
				changeMonth: true,
				changeYear: true
			});
		});
	</script>
	
</mny:standardLayout>

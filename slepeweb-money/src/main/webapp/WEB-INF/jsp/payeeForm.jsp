<%@ 
	include file="/WEB-INF/jsp/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>
	
<!-- payeeForm.jsp -->

<mny:standardLayout>

	<mny:standardFormPageHeading entity="payee" />
	
	<form method="post" action="${_ctxPath}/payee/update">	  
	    <table>
	    	<c:if test="${_formMode eq 'update'}">
	    		<mny:tableRow heading="Id" trclass="opaque50">
			    	<input type="text" readonly name="identifier" placeholder="Unique id" value="${_payee.id}" />
			    </mny:tableRow>
		    </c:if>
		    
	    	<mny:tableRow heading="Name">
		    	<input type="text" name="name" placeholder="Enter payee name" value="${_payee.name}" />
			   </mny:tableRow>

			</table> 
			
			<mny:cautiousFormActionButtons label="${_buttonLabel}" entity="payee" />

	    <input type="hidden" name="id" value="${_payee.id}" />   
	    <input type="hidden" name="formMode" value="${_formMode}" />   
	</form>		  	
		
</mny:standardLayout>

<script>
$(function() {
	$('input[name=name]').focus()
})
</script>

<mny:entityDeletionDialog entity="payee" mode="${_formMode}" id="${_payee.id}"/>
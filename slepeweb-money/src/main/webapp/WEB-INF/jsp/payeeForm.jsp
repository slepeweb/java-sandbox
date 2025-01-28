<%@ 
	include file="/WEB-INF/jsp/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>
	
<!-- payeeForm.jsp -->

<mny:flash />
	
<mny:standardLayout>

	<c:set var="_buttonLabel" value="Add payee" />
	<c:set var="_pageHeading" value="Add new payee" />
	<c:if test="${_formMode eq 'update'}">
		<c:set var="_buttonLabel" value="Update payee" />
	<c:set var="_pageHeading" value="Update payee" />
	</c:if>
	

	<h2>${_pageHeading} <c:if test="${not empty param.flash}"><span 
		class="flash ${_flashType}">${_flashMessage}</span></c:if></h2>	
	
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
			
	    <input type="submit" value="${_buttonLabel}" /> 
			<mny:deleteButtonEnabler entity="payee" />

	    <input type="hidden" name="id" value="${_payee.id}" />   
	    <input type="hidden" name="formMode" value="${_formMode}" />   
	</form>		  	
		
</mny:standardLayout>

<mny:entityDeletionDialog entity="payee" mode="${_formMode}" id="${_payee.id}"/>
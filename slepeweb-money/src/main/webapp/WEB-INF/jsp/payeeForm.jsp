<%@ 
	include file="/WEB-INF/jsp/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>
	
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
			    <tr class="opaque50">
			        <td class="heading"><label for="identifier">Id</label></td>
			        <td><input type="text" readonly name="identifier" placeholder="Unique id" value="${_payee.id}" /></td>
			    </tr>
		    </c:if>
		    
		    <tr>
		        <td class="heading"><label for="name">Name</label></td>
		        <td><input type="text" name="name" placeholder="Enter payee name" value="${_payee.name}" /></td>
		    </tr>
			</table> 
			
	    <input type="submit" value="${_buttonLabel}" /> 
			<mny:deleteButtonEnabler entity="payee" />

	    <input type="hidden" name="id" value="${_payee.id}" />   
	    <input type="hidden" name="formMode" value="${_formMode}" />   
	</form>		  	
		
</mny:standardLayout>

<mny:entityDeletionDialog entity="payee" mode="${_formMode}" id="${_payee.id}"/>
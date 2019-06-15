<%@ 
	include file="/WEB-INF/jsp/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>
	
<mny:flash />
		
<mny:standardLayout>
	<c:set var="_buttonLabel" value="Add account" />
	<c:set var="_pageHeading" value="Add new account" />
	<c:if test="${_formMode eq 'update'}">
		<c:set var="_buttonLabel" value="Update account" />
	<c:set var="_pageHeading" value="Update account" />
	</c:if>
	

	<h2>Update saved search label <c:if test="${not empty param.flash}"><span 
		class="flash ${_flashType}">${_flashMessage}</span></c:if></h2>	
	
	<form method="post" action="">	  
	    <table>
		    <tr class="opaque50">
		        <td class="heading"><label for="identifier">Id</label></td>
		        <td><input type="text" readonly id="identifier" name="identifier" placeholder="Unique id" value="${_ss.id}" /></td>
		    </tr>
		    
		    <tr class="opaque50">
		        <td class="heading"><label for="created">Date created</label></td>
		        <td><input type="text" readonly id="created" name="created" placeholder="Date created" value="${_ss.saved}" /></td>
		    </tr>
		    
		    <tr>
		        <td class="heading"><label for="name">Name</label></td>
		        <td><input id="name" type="text" name="name" placeholder="Enter search label" value="${_ss.name}" /></td>
		    </tr>
		</table> 
			
	    <input type="submit" value="Update" /> 
    	<input type="button" value="Delete search?" id="delete-button" /> 
	    <input type="hidden" name="id" value="${_ss.id}" />   
	</form>		  	

</mny:standardLayout>

<mny:entityDeletionDialog entity="search/save" mode="update" id="${_ss.id}" />
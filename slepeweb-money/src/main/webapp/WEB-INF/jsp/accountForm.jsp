<%@ 
	include file="/WEB-INF/jsp/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>
	
<!-- accountForm.jsp -->

<mny:standardLayout>

	<mny:standardFormPageHeading entity="account" />
	
	<form method="post" action="${_ctxPath}/account/update">	  
	    <table>
	    	<c:if test="${_formMode eq 'update'}">
	    		<mny:tableRow heading="Id" trclass="opaque50">
			       <input type="text" readonly name="identifier" placeholder="Unique id" value="${_account.id}" /></td>
	    		</mny:tableRow>
		    </c:if>
		    
	    	<mny:tableRow heading="Name">
		    	<input type="text" name="name" placeholder="Enter account name" value="${_account.name}" /></td>
				</mny:tableRow>
				
	    	<mny:tableRow heading="Type">
    			<select name="type">
        		<option value=""></option>
        		<c:forTokens items="current,savings,credit,pension,other" var="_t" delims=",">
        			<option value="${_t}" <c:if test="${_t eq _account.type}">selected</c:if>>${_t}</option>
        		</c:forTokens>
        	</select>
				</mny:tableRow>
				
	    	<mny:tableRow heading="Status">
		        	<select name="status">
		        			<option value="open" <c:if test="${not _account.closed}">selected</c:if>>open</option>
		        			<option value="closed" <c:if test="${_account.closed}">selected</c:if>>closed</option>
		        	</select>
				</mny:tableRow>
				
	    	<mny:tableRow heading="Opening balance">
					<input type="text" name="opening" placeholder="Enter opening balance in pounds and pence" 
		        	value="${mon:formatPounds(_account.openingBalance)}" />
				</mny:tableRow>

	    	<mny:tableRow heading="Reconciled balance">
					<input type="text" name="reconciled" placeholder="Enter last reconciled balance, in pounds and pence" 
		        	value="${mon:formatPounds(_account.reconciled)}" />
				</mny:tableRow>

	    	<mny:tableRow heading="Notes">
					<textarea cols="40" rows="3" name="note" 
		        	placeholder="Enter relevant notes, eg. account number">${_account.note}</textarea>
		    </mny:tableRow>
			</table> 
			
			<mny:cautiousFormActionButtons label="${_buttonLabel}" entity="account" />

	    <input type="hidden" name="id" value="${_account.id}" />   
	    <input type="hidden" name="formMode" value="${_formMode}" />   
	</form>		  	
		
</mny:standardLayout>

<script>
$(function() {
	$('input[name=name]').focus()
})
</script>

<mny:entityDeletionDialog entity="account" mode="${_formMode}" id="${_account.id}" />
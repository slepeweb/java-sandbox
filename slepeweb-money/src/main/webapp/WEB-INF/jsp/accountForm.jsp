<%@ 
	include file="/WEB-INF/jsp/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>
	
<!-- accountForm.jsp -->

<c:set var="_extraJs" value="account.js,datepicker.js" scope="request" />

<c:set var="_extraInPageCss" scope="request">
	tr.hidden {
	 	display: none;
	}
</c:set>

<mny:standardLayout>

	<c:set var="menuadd"><a href="${_ctxPath}/account/list/savings">List only savings</a></c:set>
	<mny:standardFormPageHeading entity="account" menuadd="${menuadd}" />
	
	<form id="account-form" method="post" action="${_ctxPath}/account/update">	  
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
        		<c:forTokens items="current,savings,credit,pension,service,other" var="_t" delims=",">
        			<option value="${_t}" <c:if test="${_t eq _account.type}">selected</c:if>>${_t}</option>
        		</c:forTokens>
        	</select>
				</mny:tableRow>
				
		    <c:set var="clazz" value="accountnos" scope="request" />
		    <c:if test="${_account.type ne 'savings' and _account.type ne 'current'}"><c:set var="clazz" value="accountnos hidden" scope="request" /></c:if>
		    
	    	<mny:tableRow heading="Sort code" trclass="${clazz}">
		    	<input type="text" name="sortcode" value="${_account.sortCode}" /></td>
				</mny:tableRow>
				
	    	<mny:tableRow heading="Account no." trclass="${clazz}">
		    	<input type="text" name="accountno" value="${_account.accountNo}" /></td>
				</mny:tableRow>
				
	    	<mny:tableRow heading="Roll no." trclass="${clazz}">
		    	<input type="text" name="rollno" value="${_account.rollNo}" /></td>
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

		    <c:set var="clazz" value="savings" scope="request" />
		    <c:if test="${_account.type ne 'savings'}"><c:set var="clazz" value="savings hidden" scope="request" /></c:if>
		    
	    	<mny:tableRow heading="Owner" trclass="${clazz}">
    			<select name="owner">
    				<option value="">Choose ...</option>
        		<c:forTokens items="george,donna,joint" var="_t" delims=",">
        			<option value="${_t}" <c:if test="${_t eq _savings.owner}">selected</c:if>>${_t}</option>
        		</c:forTokens>
        	</select>
		    </mny:tableRow>
		    
	    	<mny:tableRow heading="Rate" trclass="${clazz}">
					<input type="text" name="rate" placeholder="Current interest rate" 
		        	value="${_savings.rate}" />
		    </mny:tableRow>
		    
	    	<mny:tableRow heading="Access terms" trclass="${clazz}">
    			<select name="access">
    				<option value="">Choose ...</option>
        		<c:forTokens items="fixed,notice,flexi" var="_t" delims=",">
        			<option value="${_t}" <c:if test="${_t eq _savings.access}">selected</c:if>>${_t}</option>
        		</c:forTokens>
        	</select>
		    </mny:tableRow>
		    
	    	<mny:tableRow heading="Interest payment schedule" trclass="${clazz}">
    			<select name="schedule">
    				<option value="">Choose ...</option>
        		<c:forTokens items="monthly,annually,none" var="_t" delims=",">
        			<option value="${_t}" <c:if test="${_t eq _savings.schedule}">selected</c:if>>${_t}</option>
        		</c:forTokens>
        	</select>
		    </mny:tableRow>
		    
	    	<mny:tableRow heading="Matures" trclass="${clazz}">
					<input class="datepicker" type="text" name="matures" placeholder="Maturity date" 
		        	value="${mon:formatTimestamp(_savings.matures)}" />
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
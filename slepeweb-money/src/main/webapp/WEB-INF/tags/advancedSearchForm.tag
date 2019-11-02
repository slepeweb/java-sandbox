<%@ tag %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>

<%-- <c:if test="${not empty _params}"> --%>
	<c:set var="_selectedAccountId" value="${_params.accountIdStr}" />
	<c:set var="_selectedPayeeId" value="${_params.payeeIdStr}" />
	<c:set var="_selectedPayeeName" value="${_params.payeeName}" />
	<c:set var="_selectedMajorCategory" value="${_params.majorCategory}" />
	<c:set var="_selectedMemoTerms" value="${_params.memo}" />
	<c:set var="_selectedPageSize" value="${_params.pageSize}" />
	<c:set var="_selectedFrom" value="${mon:formatTimestamp(_params.from)}" />
	<c:set var="_selectedTo" value="${mon:formatTimestamp(_params.to)}" />
<%-- </c:if> --%>

<mny:multiCategoryInputSupport />
<mny:multiCategoryJavascript />
<mny:minorCategoryUpdatesJavascript />
<mny:payeeAutocompleterJavascript />

<form id="advanced-search-form" class="multi-category-input" method="post" action="${_ctxPath}${_formActionUrl}">	  
    <table id="multi-category-groupings">
	    <c:if test="${_formMode ne 'adhoc'}">
		    <tr>
		        <td class="heading width25"><label for="name">Title</label></td>
		        <td>
		        	<input type="text" id="name" name="name"
	    					placeholder="Provide a title for this search" value="${_ss.name}" />	        	
		        </td>
		    </tr>
	    </c:if>
	    
	    <tr>
	        <td class="heading"><label for="pageSize">Page size</label></td>
	        <td>
	        	<input type="text" id="pageSize" name="pageSize"
    					placeholder="Specify the page size" value="${_selectedPageSize}" />	        	
	        </td>
	    </tr>
	    <tr>
	        <td class="heading"><label for="accountId">Account</label></td>
	        <td>
	        	<select id="accountId" name="accountId">
	        	<option value=""></option>
	        		<c:forEach items="${_allAccounts}" var="_a">
	        			<option value="${_a.id}" <c:if test="${_a.id eq _selectedAccountId}">selected</c:if>>${_a.name}</option>
	        		</c:forEach>
	        	</select>
	        </td>
	    </tr>
	    
	    <tr>
	        <td class="heading"><label for="payee">Payee</label></td>
	        <td><input id="payee" type="text" name="payee" value="${_selectedPayeeName}" /></td>
	    </tr>
	    
	    <tr class="multi-category-group">
	        <td class="heading"><label for="category">Category(s)</label></td>
	        <td>
	        	${mon:buildMinorCategoryInputMarkup(_categoryGroup, _innerTemplate, _categoryOptionsTemplate)}
						<button class="add-category-button" type="button" data-groupid="1">+ category</button>
	        </td>
	    </tr>
	    
	    <tr>
	        <td class="heading"><label for="memo">Memo</label></td>
	        <td><input id="memo" type="text" name="memo" placeholder="Terms that might be in the memo field" value="${_selectedMemoTerms}" /></td>
	    </tr>
	    
	    <tr>
	        <td class="heading"><label for="from">From date</label></td>
	        <td><input class="datepicker" id="from" type="text" name="from" value="${_selectedFrom}"
	        	placeholder="Optional search window start date" /></td>
	    </tr>
	    
	    <tr>
	        <td class="heading"><label for="to">To date</label></td>
	        <td><input class="datepicker" id="to" type="text" name="to" value="${_selectedTo}"
	        	placeholder="Optional search window end date" /></td>
	    </tr>
	    
	    <c:if test="${_formMode ne 'adhoc'}">
		    <tr>
		        <td class="heading"><label>Form submission option</label></td>
		        <td>		
						  <div>
								<span id="save-option" class="radio-option"><input type="radio" name="submit-option" value="save" checked="checked" /> Save</span>
								<span id="save-execute-option" class="radio-option"><input type="radio" name="submit-option" value="save-execute" /> Save then execute</span>
								<span id="execute-option" class="radio-option"><input type="radio" name="submit-option" value="execute" /> Execute</span>
							</div>
		        </td>
		    </tr>
	    </c:if>
	    
		</table> 
		
		<input id="counter-store" type="hidden" name="counterStore" value="" />		
		<input type="hidden" name="formMode" value="${_formMode}" />		

		<input type="submit" id="submit-button" value="Submit" title="Submit this form" />		
		
		<c:if test="${_formMode eq 'update'}">
			<input type="button" value="Delete search" id="delete-button" title="Delete this search" />
		</c:if>
			
		<input id="cancel-button" type="button" value="Cancel" title="Return to list" />

</form>		  	

<script>
	$(function() {
		$(".datepicker").datepicker({
			dateFormat: "yy-mm-dd",
			changeMonth: true,
			changeYear: true
		});
		
		$("#cancel-button").click(function(e){
			window.location = webContext + "/search/list"
		});		
	});
</script>
	
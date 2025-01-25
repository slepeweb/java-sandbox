<%@ tag %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>

<c:set var="_extraJs" scope="request" value="search.js,datepicker.js" />

<!-- advancedSearchForm.tag -->

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
		    <tr>
		        <td class="heading width25"><label for="description">Description</label></td>
		        <td>
		        	<textarea id="description" name="description" rows="3" cols="40"
	    					placeholder="Provide description to help reader understand content">${_ss.description}</textarea>	        	
		        </td>
		    </tr>
	    </c:if>
	    
	    <tr>
	        <td class="heading"><label for="pageSize">Page size</label></td>
	        <td>
	        	<input type="text" id="pageSize" name="pageSize"
    					placeholder="Specify the page size" value="${_params.pageSize}" />	        	
	        </td>
	    </tr>
		  
		  <tsf:account accountId="${_params.accountIdStr}" />
		  <tsf:payee payeeName="${_params.payeeName}" />
			<mny:categoryList heading="Categories" categories="${_categoryGroup}" />
	    	    
	    <tr>
	        <td class="heading"><label for="from">Dates</label></td>
	        <td>
	        	From: <input class="datepicker" id="from" type="text" name="from" value="${mon:formatTimestamp(_params.from)}"
	        		placeholder="Optional search window start date" />
	        	To: <input class="datepicker" id="to" type="text" name="to" value="${mon:formatTimestamp(_params.to)}"
	        		placeholder="Optional search window end date" />
	        </td>
	    </tr>
	    
	    <tr>
	        <td class="heading"><label for="from">Amounts</label></td>
	        <td>
	        	From: <input class="amount" id="from-amount" type="text" name="from-amount" 
	        		value="${not empty _params.fromAmount ? mon:formatPounds(_params.fromAmount) : ''}"
	        		placeholder="Optional minimum amount" />
	        	To: <input class="amount" id="to-amount" type="text" name="to-amount" 
	        		value="${not empty _params.toAmount ? mon:formatPounds(_params.toAmount) : ''}"
	        		placeholder="Optional maximum amount" />
	        	
						<c:set var="_selectedDebitOrCredit" value="${_params.debit ? '-1' : '1'}" />
	        	<select name="debitorcredit" class="amount">
	        		<option value="-1" <c:if test="${_selectedDebitOrCredit eq '-1'}">selected</c:if>>Debit</option>
	        		<option value="1" <c:if test="${_selectedDebitOrCredit eq '1'}">selected</c:if>>Credit</option>
	        	</select>
	        </td>
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
		
		<input type="hidden" name="formMode" value="${_formMode}" />		

		<input type="submit" id="submit-button" value="Submit" title="Submit this form" />		
		
		<c:if test="${_formMode eq 'update'}">
			<input type="button" value="Delete search?" id="delete-button" title="Delete this search" />
		</c:if>
			
		<input id="cancel-button" type="button" value="Cancel" title="Return to list" />

</form>		  	

	
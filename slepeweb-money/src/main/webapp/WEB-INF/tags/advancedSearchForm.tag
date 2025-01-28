<%@ tag %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>

<c:set var="_extraJs" scope="request" value="search.js,datepicker.js" />

<!-- advancedSearchForm.tag -->

<form id="advanced-search-form" class="multi-category-input" method="post" action="${_ctxPath}${_formActionUrl}">	  
    <table id="multi-category-groupings">
	    <c:if test="${_formMode ne 'adhoc'}">
		    <mny:tableRow heading="Title" tdclass="width25">
        	<input type="text" id="name" name="name"
   					placeholder="Provide a title for this search" value="${_ss.name}" />	        	
		    </mny:tableRow>
		    
		    <mny:tableRow heading="Description" tdclass="width25">
        	<textarea id="description" name="description" rows="3" cols="40"
   					placeholder="Provide description to help reader understand content">${_ss.description}</textarea>
	    	</mny:tableRow>   	
	    </c:if>
	    
	    <mny:tableRow heading="Page size">
      	<input type="text" id="pageSize" name="pageSize"
 					placeholder="Specify the page size" value="${_params.pageSize}" />	        	
			</mny:tableRow>
		  
		  <tsf:account accountId="${_params.accountIdStr}" />
		  <tsf:payee payeeName="${_params.payeeName}" />
			<mny:categoryList heading="Categories" categories="${_categoryGroup}" />
	    	    
	    <mny:tableRow heading="Dates">
       	From: <input class="datepicker" id="from" type="text" name="from" value="${mon:formatTimestamp(_params.from)}"
       		placeholder="Optional search window start date" />
       	To: <input class="datepicker" id="to" type="text" name="to" value="${mon:formatTimestamp(_params.to)}"
       		placeholder="Optional search window end date" />
			</mny:tableRow>
	    
	    <mny:tableRow heading="Amounts">
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
			</mny:tableRow>
			
	    <c:if test="${_formMode ne 'adhoc'}">
	    	<mny:searchAndExecuteOptions />
	    </c:if>
	    
		</table> 
		
		<mny:standardFormFooter />

</form>		  	

	
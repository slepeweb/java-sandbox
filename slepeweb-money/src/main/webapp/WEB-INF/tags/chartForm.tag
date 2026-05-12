<%@ tag %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>
	
<!-- chartForm.tag -->

<form id="chart-form" class="multi-category-input" method="post" action="${_ctxPath}${_formActionUrl}">
	<table id="year-ranges">
    <mny:tableRow heading="Title" tdclass="width25">
      	<input type="text" id="name" name="name"
 					placeholder="Provide a title for this chart" value="${_ss.name}" />	        	
    </mny:tableRow>
    
    <mny:tableRow heading="Description" tdclass="width25">
      	<textarea id="description" name="description" rows="3" cols="40"
 					placeholder="Provide description to help reader understand content">${_ss.description}</textarea>
   	</mny:tableRow>
   	
	  <tsf:account accountId="${_chartProps.accountIdStr}" />
	  <tsf:payeeOrTransferAccount payeeName="${_chartProps.payeeName}" 
	  	accountId="${_chartProps.transferAccountIdStr}" direction="${_chartProps.transferDirection}" />
		
		<tr>
	    <td class="heading"><label>Date range</label></td>
	    <td class="payee-or-transfer">
				<mny:yearSelector id="from" heading="From" selected="${_chartProps.fromYear}" />
				<div class="or-spacer"></div>
				<mny:yearSelector id="to" heading="To" selected="${_chartProps.toYear}" />
			</td>
		</tr>
	  <tr><td colspan="2"> </td></tr>
	  
		<mny:categoryList heading="Categories" categories="${_categoryGroup}" />
		<tr class="add-category-group <c:if test="${_chartProps.transferAccountId gt 0}">invisible</c:if>"><td><button id="add-group-button" type="button" title="Add another category set" ><i class="fa-solid fa-plus" ></i> Add another category set</button></td></tr>
		<mny:searchAndExecuteOptions />
			
	</table>
		
	<mny:standardFormActionButtons submit="Submit selected action" cancel="Cancel" delete="Delete chart definition?" />
	
</form>			

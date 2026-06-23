<%@ tag %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>
	
<!-- chartForm.tag -->

<form id="chart-form" class="multi-category-input" method="post" action="${_ctxPath}${_formActionUrl}">
	<table id="chart-table">
    <mny:tableRow heading="Title">
      	<input type="text" id="name" name="name"
 					placeholder="Provide a title for this chart" value="${_chart.name}" />	        	
    </mny:tableRow>
    
    <mny:tableRow heading="Description">
      	<textarea id="description" name="description" rows="3" cols="40"
 					placeholder="Provide description to help reader understand content">${_chart.description}</textarea>
   	</mny:tableRow>
   	
    <mny:tableRow heading="Years range" tdclass="payee-or-transfer">
				<mny:yearSelector id="from" heading="From" selected="${_chart.fromYear}" />
				<div class="or-spacer"></div>
				<mny:yearSelector id="to" heading="To" selected="${_chart.toYear}" />
		</mny:tableRow>	
		
    <mny:tableRow heading="Search components">
			<table id="searches">
				<tr><th>Search title</th><th>Selected?</th></tr>
				
				<c:forEach items="${_searchOptions}" var="_sso" varStatus="_stat">
					<tr id="tr${_sso.savedSearch.id}" class="${_sso.selected ? '' : 'invisible'}">
						<td>${_sso.savedSearch.name}</td>
						<td><input type="checkbox" name="chk${_sso.savedSearch.id}" ${_sso.selected ? 'checked' : ''} /></td>
					</tr>
				</c:forEach>				
			</table>  
	  </mny:tableRow>
	  
    <mny:tableRow heading="Add another search">
    	<input type="text" id="search-selector" />
    </mny:tableRow>
    
    <tr><td>&nbsp;</td></tr>

    <mny:tableRow heading="Notes">
    	<textarea name="notes" rows="4">${_chart.notes}</textarea>
    </mny:tableRow>
	  
		<mny:searchAndExecuteOptions />
			
	</table>
		
	<mny:standardFormActionButtons submit="Submit selected action" cancel="Cancel" delete="Delete chart definition?" />
	<input id="idlist" type="hidden" name="idlist" />
</form>			

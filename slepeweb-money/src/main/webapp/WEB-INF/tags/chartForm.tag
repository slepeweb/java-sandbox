<%@ tag %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>
	
<!-- chartForm.tag -->

<form id="chart-form" class="multi-category-input" method="post" action="${_ctxPath}${_formActionUrl}">
	<table id="year-ranges">
	    <tr>
	        <td class="heading width25"><label for="name">Title</label></td>
	        <td>
	        	<input type="text" id="name" name="name"
    				placeholder="Provide a title for this chart" value="${_ss.name}" />
	        	
	        </td>
	    </tr>
	    <tr>
	        <td class="heading width25"><label for="description">Description</label></td>
	        <td>
		        	<textarea id="description" name="description" rows="3" cols="40"
	    					placeholder="Provide description to help reader understand content">${_ss.description}</textarea>	        	
	        </td>
	    </tr>
	    <tr>
	        <td class="heading width25"><label for="from">From year</label></td>
	        <td>
	        	<select id="from" name="from">
							<c:forEach items="${_yearRange}" var="_year">
								<option value="${_year}" <c:if test="${_chartProps.fromYear eq _year}">selected</c:if>>${_year}</option>
							</c:forEach>
	        	</select>
	        </td>
	    </tr>
	    <tr>
	        <td class="heading width25"><label for="to">To year</label></td>
	        <td>
						<select id="to" name="to">
							<c:forEach items="${_yearRange}" var="_year">
								<option value="${_year}" <c:if test="${_chartProps.toYear eq _year}">selected</c:if>>${_year}</option>
							</c:forEach>
	        	</select>
	        </td>
	    </tr>
	
			<mny:categoryList heading="Categories" categories="${_categoryGroup}" />			
			<tr><td><button id="add-group-button" type="button" title="Add a group" ><i class="fa-solid fa-chevron-down" ></i> set</button></td></tr>
			<mny:searchAndExecuteOptions />
			
	</table>
		
	<mny:standardFormActionButtons submit="Submit selected action" cancel="Cancel" delete="Delete chart definition?" />
	
</form>			

<%@ tag %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>
	
<!-- chartForm.tag -->

<mny:multiCategoryInputSupport />
<mny:multiCategoryJavascript />
<mny:minorCategoryUpdatesJavascript />

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
	</table>
	
	<table id="multi-category-groupings">
		<%-- The parameters to this function call are created by <mny:multiCategoryInputSupport />, called earlier --%>
		${mon:buildChartCategoryInputMarkup(_chartProps, _outerTemplate, _innerTemplate, _categoryOptionsTemplate)}
	</table>
	
	<button id="add-group-button" type="button">+ group</button>
	<br />
	
	<input id="counter-store" type="hidden" name="counterStore" value="" />
	
	<c:choose><c:when test="${_formMode eq 'create'}">
		<input type="submit" value="Save" />
		<input id="cancel-button" type="button" value="Cancel" />
	</c:when><c:when test="${_formMode eq 'update'}">
		<input type="submit" value="Update" />
		<input type="button" value="Cancel" id="cancel-button" />
		<input type="button" value="Delete chart?" id="delete-button" />
	</c:when><c:when test="${_formMode eq 'execute'}">
		<input type="submit" value="Update and re-execute" /> 
	</c:when></c:choose>
</form>			

<script>
	$(function() {
		$("#cancel-button").click(function(e){
			window.location = webContext + "/chart/list"
		});
	});
</script>

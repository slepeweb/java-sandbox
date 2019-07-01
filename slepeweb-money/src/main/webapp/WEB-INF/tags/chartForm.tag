<%@ tag %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>

<mny:multiCategoryInputSupport />

<form id="chart-form" class="multi-category-input" method="post" action="${_ctxPath}/chart/by/categories/out">
	<table id="year-ranges">
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
	<input type="submit" value="Submit" />
	
   <c:if test="${not empty _chartSVG}">
   	OR <input id="save-search-button" type="button" value="Save" /> 
    <input id="saved-search-identifier" type="text" name="save-identifier" 
    	placeholder="Provide an identifier for this search" value="" />
   </c:if>
</form>			

<%@ tag %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>

<mny:multiCategoryInputSupport />

<form id="chart-form" class="multi-category-input" method="post" action="${_ctxPath}/chart/by/categories/out">
	<table>
	    <tr>
	        <td class="heading"><label for="from">From year</label></td>
	        <td><input id="from" type="text" name="from" placeholder="Enter (for example) '2000'"
	        	value="${_chartProps.fromYear}" /></td>
	    </tr>
	    <tr>
	        <td class="heading"><label for="numYears">No. of years</label></td>
	        <td><input id="numYears" type="text" name="numYears" placeholder="Enter (for example) '10'"
	        	value="${_chartProps.numYears}" /></td>
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

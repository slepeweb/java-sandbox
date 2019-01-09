<%@ 
	include file="/WEB-INF/jsp/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>
	
<mny:flash />
	
<mny:standardLayout>

	<c:set var="_buttonLabel" value="Add category" />
	<c:set var="_pageHeading" value="Add new category" />
	<c:if test="${_formMode eq 'update'}">
		<c:set var="_buttonLabel" value="Update category" />
	<c:set var="_pageHeading" value="Update category" />
	</c:if>
	
	<h2>${_pageHeading} <c:if test="${not empty param.flash}"><span 
		class="flash ${_flashType}">${_flashMessage}</span></c:if></h2>	
	
	<form method="post" action="${_ctxPath}/category/update">	  
	    <table>
	    	<c:if test="${_formMode eq 'update'}">
			    <tr class="opaque50">
			        <td class="heading"><label for="identifier">Id</label></td>
			        <td><input type="text" readonly name="identifier" placeholder="Unique id" value="${_category.id}" /></td>
			    </tr>
		    </c:if>
		    
		    <tr>
		        <td class="heading"><label for="major">Category</label></td>
		        <td><input type="text" name="major" placeholder="Enter category, or choose from selection"
		        	value="${_category.major}" /></td>
		        <td><select id="all-major-categories-list">
		        	<option value="">Choose an existing value ...</option>
		        	<c:forEach items="${_allMajorCategories}" var="_name">
		        		<option value="">${_name}</option>
		        	</c:forEach>
		        </select></td>
		    </tr>
		    <tr>
		        <td class="heading"><label for="minor">Sub-category</label></td>
		        <td><input type="text" name="minor" placeholder="Enter sub-category, if required"
		        	value="${_category.minor}" /></td>
		    </tr>
			</table> 
			
	    <input type="submit" value="${_buttonLabel}" /> 
			<c:if test="${_formMode eq 'update'}">
	    	<input type="button" value="Delete category?" id="delete-button" /> 
	    </c:if>
	    <input type="hidden" name="id" value="${_category.id}" />   
	    <input type="hidden" name="formMode" value="${_formMode}" />   
	</form>		  	
		
</mny:standardLayout>


<mny:entityDeletionDialog entity="category" mode="${_formMode}" id="${_category.id}"/>

<script>
$(function() {
	$("#all-major-categories-list").change(function(e){
		var value = $("#all-major-categories-list option:selected").text();
		$("input[name=major]").val(value);
	});

});
</script>

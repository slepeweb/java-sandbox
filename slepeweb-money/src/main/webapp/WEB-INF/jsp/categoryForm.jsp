<%@ 
	include file="/WEB-INF/jsp/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>
	
<!-- categoryForm.jsp -->

<mny:standardLayout>

	<mny:standardFormPageHeading entity="category" many="categories" />	
	
	<c:set var="_formModeTest" value="${_formMode ne 'update' or not _category.root}" />
	
	<form id="category-form" method="post" action="${_ctxPath}/category/update">	  
	    <table>
	    	<c:if test="${_formMode eq 'update'}">
	    		<mny:tableRow heading="Id" trclass="opaque50">
						<input type="text" readonly name="identifier" placeholder="Unique id" value="${_category.id}" />
					</mny:tableRow>
		    </c:if>
		    
		    <tr>
		        <td class="heading"><label>Category</label></td>
		        <td><input type="text" name="major" placeholder="Enter a category"
		        	value="${_category.major}" /></td>
		        	
		        <td class="extra">
			        <c:choose><c:when test="${_formModeTest}">
				        <select id="all-major-categories-list">
				        	<option value="">Choose an existing value ...</option>
				        	<c:forEach items="${_allMajorCategories}" var="_name">
				        		<option value="">${_name}</option>
				        	</c:forEach>
				        </select>
			        </c:when><c:when test="${_category.root}">
			        		<u>This is a root category.</u>
			        		Changing this value will update all sub-categories too.
			        	</c:when></c:choose>
		        </td>
		    </tr>
		    
		    <c:choose><c:when test="${_formModeTest}">
		    	<mny:tableRow heading="Sub-category">
			    	<input type="text" name="minor" placeholder="Leave empty if this is a root category"
			        	value="${_category.minor}" />
					</mny:tableRow>
				</c:when><c:otherwise>
					<input type="hidden" name="minor" value="" />
				</c:otherwise></c:choose>
				
	    	<mny:tableRow heading="Type">
					<span class="radio-horiz"><input id="expense_cat" type="radio" name="categorytype" value="expense" 
        		${mon:tertiaryOp(_category.expense, 'checked=checked', '')} /> Expense</span>
        	<span class="radio-horiz"><input id="income_cat" type="radio" name="categorytype" value="income" 
        		${mon:tertiaryOp(not _category.expense, 'checked=checked', '')} /> Income</span>
				</mny:tableRow>
				
			</table> 
			
			<mny:cautiousFormActionButtons label="${_buttonLabel}" entity="category" />
	    
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

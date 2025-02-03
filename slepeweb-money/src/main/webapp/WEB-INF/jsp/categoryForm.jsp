<%@ 
	include file="/WEB-INF/jsp/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>
	
<!-- categoryForm.jsp -->

<mny:standardLayout>

	<mny:standardFormPageHeading entity="category" many="categories" />	
	
	<form method="post" action="${_ctxPath}/category/update">	  
	    <table>
	    	<c:if test="${_formMode eq 'update'}">
	    		<mny:tableRow heading="Id" trclass="opaque50">
						<input type="text" readonly name="identifier" placeholder="Unique id" value="${_category.id}" />
					</mny:tableRow>
		    </c:if>
		    
		    <tr>
		        <td class="heading"><label>Category</label></td>
		        <td><input type="text" name="major" placeholder="Enter category, or choose from selection"
		        	value="${_category.major}" /></td>
		        <td><select id="all-major-categories-list">
		        	<option value="">Choose an existing value ...</option>
		        	<c:forEach items="${_allMajorCategories}" var="_name">
		        		<option value="">${_name}</option>
		        	</c:forEach>
		        </select></td>
		    </tr>
		    
	    	<mny:tableRow heading="Sub-category">
		    	<input type="text" name="minor" placeholder="Enter sub-category, if required"
		        	value="${_category.minor}" />
				</mny:tableRow>
				
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

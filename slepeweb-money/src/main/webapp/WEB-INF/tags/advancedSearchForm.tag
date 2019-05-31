<%@ tag %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>

<c:if test="${not empty _response}">
	<c:set var="_selectedAccountId" value="${_response.params.accountIdStr}" />
	<c:set var="_selectedPayeeId" value="${_response.params.payeeIdStr}" />
	<c:set var="_selectedPayeeName" value="${_response.params.payeeName}" />
	<c:set var="_selectedMajorCategory" value="${_response.params.majorCategory}" />
	<c:set var="_selectedMemoTerms" value="${_response.params.memo}" />
	<c:set var="_selectedFrom" value="${mon:formatTimestamp(_response.params.from)}" />
	<c:set var="_selectedTo" value="${mon:formatTimestamp(_response.params.to)}" />
</c:if>

<mny:multiCategoryInputSupport />

<form class="multi-category-input" method="post" action="${_ctxPath}/search/">	  
    <table id="multi-category-groupings">
	    <tr>
	        <td class="heading"><label for="accountId">Account</label></td>
	        <td>
	        	<select id="accountId" name="accountId">
	        	<option value=""></option>
	        		<c:forEach items="${_allAccounts}" var="_a">
	        			<option value="${_a.id}" <c:if test="${_a.id eq _selectedAccountId}">selected</c:if>>${_a.name}</option>
	        		</c:forEach>
	        	</select>
	        </td>
	    </tr>
	    
	    <tr>
	        <td class="heading"><label for="payee">Payee</label></td>
	        <td><input id="payee" type="text" name="payee" value="${_selectedPayeeName}" /></td>
	    </tr>
	    
	    <tr class="multi-category-group">
	        <td class="heading"><label for="category">Category(s)</label></td>
	        <td>
	        	${mon:buildMinorCategoryInputMarkup(_categoryGroup, _innerTemplate, _categoryOptionsTemplate)}
						<button class="add-category-button" type="button" data-groupid="1">+ category</button>
	        <%--
	        	<select id="category" name="category">
	        		<option value=""></option>
	        		<c:forEach items="${_allMajorCategories}" var="_s">
	        			<option value="${_s}" <c:if test="${_s eq _selectedMajorCategory}">selected</c:if>>${_s}</option>
	        		</c:forEach>
	        	</select>
	        	 --%>
	        </td>
	    </tr>
	    
	    <tr>
	        <td class="heading"><label for="memo">Memo</label></td>
	        <td><input id="memo" type="text" name="memo" placeholder="Terms that might be in the memo field" value="${_selectedMemoTerms}" /></td>
	    </tr>
	    
	    <tr>
	        <td class="heading"><label for="from">From date</label></td>
	        <td><input class="datepicker" id="from" type="text" name="from" value="${_selectedFrom}"
	        	placeholder="Optional search window start date" /></td>
	    </tr>
	    
	    <tr>
	        <td class="heading"><label for="to">To date</label></td>
	        <td><input class="datepicker" id="to" type="text" name="to" value="${_selectedTo}"
	        	placeholder="Optional search window end date" /></td>
	    </tr>
	    
		</table> 
		
		<br />
		<input id="counter-store" type="hidden" name="counterStore" value="" />
    <input type="submit" value="Search" />    
</form>		  	

<script>
	$(function() {
		$(".datepicker").datepicker({
			dateFormat: "yy-mm-dd",
			changeMonth: true,
			changeYear: true
		});
		
		<mny:payeeAutocompleter />
	});
</script>
	
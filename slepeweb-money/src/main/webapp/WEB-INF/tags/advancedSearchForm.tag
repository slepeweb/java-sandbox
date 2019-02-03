<%@ tag %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>

<c:if test="${not empty _response}">
	<c:set var="_selectedAccountId" value="${_response.params.accountIdStr}" />
	<c:set var="_selectedPayeeId" value="${_response.params.payeeIdStr}" />
	<c:set var="_selectedPayeeName" value="${_response.params.payeeName}" />
	<c:set var="_selectedMajorCategory" value="${_response.params.majorCategory}" />
	<c:set var="_selectedMemoTerms" value="${_response.params.memo}" />
</c:if>

<form method="post" action="${_ctxPath}/search/">	  
    <table>
	    <tr>
	        <td class="heading"><label for="account">Account</label></td>
	        <td>
	        	<select id="account" name="account">
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
	    <tr>
	        <td class="heading"><label for="category">Main category</label></td>
	        <td>
	        	<select id="category" name="category">
	        		<option value=""></option>
	        		<c:forEach items="${_allMajorCategories}" var="_s">
	        			<option value="${_s}" <c:if test="${_s eq _selectedMajorCategory}">selected</c:if>>${_s}</option>
	        		</c:forEach>
	        	</select>
	        </td>
	    </tr>
	    <tr>
	        <td class="heading"><label for="memo">Memo</label></td>
	        <td><input id="memo" type="text" name="memo" placeholder="Terms that might be in the memo field" value="${_selectedMemoTerms}" /></td>
	    </tr>
		</table> 
		
		<br />
    <input type="submit" value="Search" />    
</form>		  	

<script>
	$(function() {
		<mny:payeeAutocompleter />
	});
</script>
	
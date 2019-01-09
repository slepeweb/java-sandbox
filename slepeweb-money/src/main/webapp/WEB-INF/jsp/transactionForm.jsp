<%@ 
	include file="/WEB-INF/jsp/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>
	
<mny:flash />
	
<mny:standardLayout>

	<c:set var="_buttonLabel" value="Add transaction" />
	<c:set var="_pageHeading" value="Add new transaction" />
	<c:if test="${_formMode eq 'update'}">
		<c:set var="_buttonLabel" value="Update transaction" />
	<c:set var="_pageHeading" value="Update transaction" />
	</c:if>
	
	<h2>${_pageHeading} <c:if test="${not empty param.flash}"><span 
		class="flash ${_flashType}">${_flashMessage}</span></c:if></h2>	
	
	<form method="post" action="${_ctxPath}/transaction/update">	  
	    <table>
	    	<c:if test="${_formMode eq 'update'}">
			    <tr class="opaque50">
			        <td class="heading"><label for="identifier">Id</label></td>
			        <td><input type="text" readonly name="identifier" value="${_transaction.id}" /></td>
			    </tr>
			    
			    <c:if test="${_transaction.origId gt 0}">
				    <tr class="opaque50">
				        <td class="heading"><label for="origid">Original id</label></td>
				        <td><input type="text" readonly name="origid" value="${_transaction.origId}" /></td>
				    </tr>
			    </c:if>
		    </c:if>
		    
		    <tr>
		        <td class="heading"><label for="entered">Date</label></td>
		        <td><input type="text" class="datepicker" name="entered" 
		        	placeholder="Enter transaction date" value="${mon:formatTimestamp(_transaction.entered)}" /></td>
		    </tr>

		    <tr>
		        <td class="heading"><label for="account">Account</label></td>
		        <td>
		        	<select name="account">
			        	<option value="">Choose ...</option>
			        	<c:forEach items="${_allAccounts}" var="_a">
			        		<option value="${_a.id}" <c:if test="${_a.id eq _transaction.account.id}">selected</c:if>>${_a.name}</option>
			        	</c:forEach>
		        	</select>
		        </td>
		    </tr>

		    <tr>
		        <td class="heading"><label for="payee">Payee</label></td>
		        <td>
		        	<select name="payee">
			        	<option value="">Choose ...</option>
			        	<c:forEach items="${_allPayees}" var="_p">
			        		<option value="${_p.id}" <c:if test="${_p.id eq _transaction.payee.id}">selected</c:if>>${_p.name}</option>
			        	</c:forEach>
		        	</select>
		        </td>
		    </tr>

		    <tr>
		        <td class="heading"><label for="major">Category</label></td>
		        <td>
		        	<select name="major">
			        	<option value="">Choose ...</option>
			        	<c:forEach items="${_allMajorCategories}" var="_c">
			        		<option value="${_c}" <c:if test="${_c eq _transaction.category.major}">selected</c:if>>${_c}</option>
			        	</c:forEach>
		        	</select>
		        </td>
		    </tr>

		    <tr>
		        <td class="heading"><label for="minor">Sub-category</label></td>
		        <td>
		        	<select name="minor">
			        	<option value="">Choose ...</option>
			        	<c:forEach items="${_allMinorCategories}" var="_c">
			        		<option value="${_c}" <c:if test="${_c eq _transaction.category.minor}">selected</c:if>>${_c}</option>
			        	</c:forEach>
		        	</select>
		        </td>
		    </tr>

		    <tr>
		        <td class="heading"><label for="memo">Notes</label></td>
		        <td><input type="text" name="memo" placeholder="Enter any relevant notes" value="${_transaction.memo}" /></td>
		    </tr>

		    <tr>
		        <td class="heading"><label for="amount">Amount</label></td>
		        <td><input type="text" name="amount" placeholder="Enter amount" value="${mon:formatPounds(_transaction.amount)}" /></td>
		    </tr>

			</table> 
			
	    <input type="submit" value="${_buttonLabel}" /> 
			<c:if test="${_formMode eq 'update'}">
	    	<input type="button" value="Delete transaction?" id="delete-button" /> 
	    </c:if>
	    <input type="hidden" name="id" value="${_transaction.id}" />   
	    <input type="hidden" name="formMode" value="${_formMode}" />   
	</form>		  	
		
</mny:standardLayout>

<mny:entityDeletionDialog entity="transaction" mode="${_formMode}" id="${_transaction.id}"/>

<script>
	$(function() {
		$(".datepicker").datepicker({
			dateFormat: "yy-mm-dd",
			changeMonth: true,
			changeYear: true
		});
	});
</script>

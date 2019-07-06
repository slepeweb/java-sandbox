<%@ 
	include file="/WEB-INF/jsp/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>
	
<c:set var="_extraCss" scope="request">
	.ui-autocomplete-loading {
		background: white url("${_ctxPath}/resources/images/progress-indicator.gif") right center no-repeat;
	}
</c:set>
<mny:flash />
	
<mny:standardLayout>

	<c:set var="_buttonLabel" value="Add transaction" />
	<c:set var="_pageHeading" value="Add new transaction" />
	<c:if test="${_formMode eq 'update'}">
		<c:set var="_buttonLabel" value="Update transaction" />
	<c:set var="_pageHeading" value="Update transaction" />
	</c:if>
	
	<div class="right">
		<c:if test="${_formMode eq 'update'}">
			<a href="../add/${_transaction.account.id}">New transaction</a><br />
			<a href="../copy/${_transaction.id}">Copy this transaction</a><br />
		</c:if>
		<a href="../list/${_transaction.account.id}">List transactions</a>
	</div>
	
	<h2>${_pageHeading} <c:if test="${not empty param.flash}"><span 
		class="flash ${_flashType}">${_flashMessage}</span></c:if></h2>	
	
	<form method="post" action="${_ctxPath}/transaction/update">	  
	    <table id="trn-form">
	    	<c:if test="${_formMode eq 'update'}">
			    <tr class="opaque50">
			        <td class="heading"><label for="identifier">Id</label></td>
			        <td><input id="identifier" type="text" readonly name="identifier" value="${_transaction.id}" /></td>
			    </tr>
			    
			    <c:if test="${_transaction.origId gt 0}">
				    <tr class="opaque50">
				        <td class="heading"><label for="origid">Original id</label></td>
				        <td><input id="origid" type="text" readonly name="origid" value="${_transaction.origId}" /></td>
				    </tr>
			    </c:if>
		    </c:if>
		    
		    <tr>
		        <td class="heading"><label for="entered">Date</label></td>
		        <td><input id="entered" type="text" class="datepicker" name="entered" 
		        	placeholder="Enter transaction date" value="${mon:formatTimestamp(_transaction.entered)}" /></td>
		    </tr>

		    <tr>
		        <td class="heading"><label for="account">Account</label></td>
		        <td>
		        	<select id="account" name="account">
			        	<option value="">Choose ...</option>
			        	<c:forEach items="${_allAccounts}" var="_a">
			        		<option value="${_a.id}" <c:if test="${_a.id eq _transaction.account.id}">selected</c:if>>${_a.name}</option>
			        	</c:forEach>
		        	</select>
		        </td>
		    </tr>
		    
		    <tr>
		        <td class="heading"><label>Payment type</label></td>
		        <td>
		        	<span class="radio-horiz"><input id="standard" type="radio" name="paymenttype" value="standard" 
		        		${mon:tertiaryOp(_formMode eq 'add' or (not _transaction.split and not _transaction.transfer), 'checked=checked', '')} /> Standard</span>
		        	<span class="radio-horiz"><input id="split" type="radio" name="paymenttype" value="split" 
		        		${mon:tertiaryOp(_transaction.split, 'checked=checked', '')} /> Split</span>
		        	<span class="radio-horiz"><input id="transfer" type="radio" name="paymenttype" value="transfer" 
		        		${mon:tertiaryOp(_transaction.transfer, 'checked=checked', '')} /> Transfer</span>
		        		
		        	<c:if test="${_transaction.transfer}"><span class="radio-horiz right"><a href="${_ctxPath}/transaction/form/${_transaction.transferId}">Mirror</a></span></c:if>
		        </td>
		    </tr>

		    <tr class="transfer">
		        <td class="heading"><label for="xferaccount">Transfer a/c</label></td>
		        <td>
		        	<select id="xferaccount" name="xferaccount">
			        	<option value="">Choose ...</option>
			        	<c:forEach items="${_allAccounts}" var="_a">
			        		<option value="${_a.id}" <c:if test="${_transaction.transfer and _a.id eq _transaction.mirrorAccount.id}">selected</c:if>>${_a.name}</option>
			        	</c:forEach>
		        	</select>
		        </td>
		    </tr>

		    <tr class="payee">
		        <td class="heading"><label for="payee">Payee</label></td>
		        <td>
		         	 <input id="payee" type="text" name="payee" value="${_transaction.payee.name}" />
		        </td>
		    </tr>

		    <tr class="category">
		        <td class="heading"><label for="major">Category</label></td>
		        <td>
		        	<select id="major" name="major">
			        	<c:forEach items="${_allMajorCategories}" var="_c">
			        		<option value="${_c}" <c:if test="${_c eq _transaction.category.major}">selected</c:if>>${_c}</option>
			        	</c:forEach>
		        	</select>
		        </td>
		    </tr>

		    <tr class="category">
		        <td class="heading"><label for="minor">Sub-category</label></td>
		        <td>
		        	<select id="minor" name="minor">
			        	<c:forEach items="${_allMinorCategories}" var="_c">
			        		<option value="${_c}" <c:if test="${_c eq _transaction.category.minor}">selected</c:if>>${_c}</option>
			        	</c:forEach>
		        	</select>
		        </td>
		    </tr>

		    <tr class="splits-list">
		        <td class="heading"><label>Splits</label></td>
		        <td>
		        	<table>
		        		<c:forEach items="${_allSplits}" var="_split" varStatus="_status">
		        			<tr>
		        				<td>
						        	<select name="major_${_status.count}">
							        	<c:forEach items="${_split.allMajors}" var="_c">
							        		<option value="${_c}" <c:if test="${_c eq _split.category.major}">selected</c:if>>${_c}</option>
							        	</c:forEach>
						        	</select>
		        				</td>
		        				<td>
						        	<select name="minor_${_status.count}">
							        	<c:forEach items="${_split.allMinors}" var="_c">
							        		<option value="${_c}" <c:if test="${_c eq _split.category.minor}">selected</c:if>>${_c}</option>
							        	</c:forEach>
						        	</select>
		        				</td>
		        				<td>
		        					<input type="text" name="memo_${_status.count}" placeholder="Enter any relevant notes" value="${_split.memo}" />
		        				</td>
		        				<td>
		        					<input type="text" name="amount_${_status.count}" placeholder="Enter amount" value="${mon:formatPounds(_split.amountValue)}" />
		        				</td>
		        			</tr>
		        		</c:forEach>
	        		</table>
		        </td>
		    </tr>
		    
		    <tr>
		        <td class="heading"><label for="memo">Notes</label></td>
		        <td><input id="memo" type="text" name="memo" placeholder="Enter any relevant notes" value="${_transaction.memo}" /></td>
		    </tr>
		    
		    <tr>
		        <td class="heading"><label for="amount">Total amount</label></td>
		        <td>
		        	<span class="inline-block radio-horiz"><input id="amount" type="text" name="amount" placeholder="Enter amount" 
		        		value="${mon:formatPounds(_transaction.amountValue)}" /></span>
		        	<span class="radio-horiz"><input id="debit" type="radio" name="debitorcredit" value="debit" 
		        		${mon:tertiaryOp(_formMode eq 'add' or _transaction.debit, 'checked=checked', '')} /> Debit</span>
		        	<span class="radio-horiz"><input id="credit" type="radio" name="debitorcredit" value="credit" 
		        		${mon:tertiaryOp(not _transaction.debit, 'checked=checked', '')} /> Credit</span>
		        </td>
		    </tr>

			</table> 
			
			<input id="submit-button" type="submit" value="${_buttonLabel}" /> 
			<input id="cancel-button" type="button" value="Cancel" /> 
			
		<c:if test="${_formMode eq 'update'}">
	    	<input type="button" value="Delete transaction?" id="delete-button" /> 
	    </c:if>
	    
	    <input type="hidden" name="id" value="${_transaction.id}" />   
	    <input type="hidden" name="formMode" value="${_formMode}" />   
	    <input type="hidden" name="origxferid" value="${_transaction.transferId}" />   
	</form>		  	
		
	<div id="splits-error-dialog" title="Splits error">
		The split amounts do NOT match the total amount. Please correct in
		order to submit the form. (Total = __totalamount__, Splits = __splitamounts__)
	</div>

	<mny:entityDeletionDialog entity="transaction" mode="${_formMode}" id="${_transaction.id}"/>
	
	<script>
		<mny:transactionFormRuntimeFunctions />
		$(function() {		
			<mny:transactionFormOnReady />
		});
	</script>

</mny:standardLayout>

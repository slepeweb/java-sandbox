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

	<c:set var="_buttonLabel" value="Add schedule" />
	<c:set var="_pageHeading" value="Add new schedule" />
	<c:if test="${_formMode eq 'update'}">
		<c:set var="_buttonLabel" value="Update schedule" />
	<c:set var="_pageHeading" value="Update schedule" />
	</c:if>
	
	<c:if test="${_formMode eq 'update'}">
		<div class="right">
			<a href="${_ctxPath}/schedule/add" title="Define a new scheduled transaction">New schedule</a><br />
		</div>
	</c:if>
	
	<mny:multiSplitInputSupport />
	<mny:multiSplitJavascript />
	
	<h2>${_pageHeading} <c:if test="${not empty param.flash}"><span 
		class="flash ${_flashType}">${_flashMessage}</span></c:if></h2>	
	
	<form method="post" action="${_ctxPath}/schedule/save">	  
	    <table id="schedule-form">
	    	<c:if test="${_formMode eq 'update'}">
			    <tr class="opaque50">
			        <td class="heading"><label for="identifier">Id</label></td>
			        <td><input id="identifier" type="text" readonly name="identifier" value="${_schedule.id}" /></td>
			    </tr>			    
		    </c:if>
		    
		    <tr>
		        <td class="heading"><label for="label">Name</label></td>
		        <td>
		        	<input id="label" type="text" name="label" placeholder="Provide an identifier for this schedule" value="${_schedule.label}">
		        </td>
		    </tr>

		    <tr>
		        <td class="heading"><label for="day">Day of month</label></td>
		        <td>
		        	<select id="day" name="day">
			        	<option value="">Choose ...</option>
		        		<c:forEach items="${_daysOfMonth}" var="_day">
		        			<option value="${_day}" <c:if test="${_day eq _schedule.day}">selected</c:if>>${_day}</option>
		        		</c:forEach>
		        	</select>
		        </td>
		    </tr>

		    <tr>
		        <td class="heading"><label for="account">Account</label></td>
		        <td>
		        	<select id="account" name="account">
			        	<option value="">Choose ...</option>
			        	<c:forEach items="${_allAccounts}" var="_a">
			        		<option value="${_a.id}" <c:if test="${_a.id eq _schedule.account.id}">selected</c:if>>${_a.name}</option>
			        	</c:forEach>
		        	</select>
		        </td>
		    </tr>
		    
		    <tr>
		        <td class="heading"><label>Payment type</label></td>
		        <td>
		        	<span class="radio-horiz"><input id="standard" type="radio" name="paymenttype" value="standard" 
		        		${mon:tertiaryOp(_formMode eq 'add' or (not _schedule.split and not _schedule.transfer), 'checked=checked', '')} /> Standard</span>
		        	<span class="radio-horiz"><input id="split" type="radio" name="paymenttype" value="split" 
		        		${mon:tertiaryOp(_schedule.split, 'checked=checked', '')} /> Split</span>
		        	<span class="radio-horiz"><input id="transfer" type="radio" name="paymenttype" value="transfer" 
		        		${mon:tertiaryOp(_schedule.transfer, 'checked=checked', '')} /> Transfer</span>
		        </td>
		    </tr>

		    <tr class="transfer">
		        <td class="heading"><label for="xferaccount">Transfer a/c</label></td>
		        <td>
		        	<select id="xferaccount" name="xferaccount">
			        	<option value="">Choose ...</option>
			        	<c:forEach items="${_allAccounts}" var="_a">
			        		<option value="${_a.id}" <c:if test="${_schedule.transfer and _a.id eq _schedule.mirror.id}">selected</c:if>>${_a.name}</option>
			        	</c:forEach>
		        	</select>
		        </td>
		    </tr>

		    <tr class="payee">
		        <td class="heading"><label for="payee">Payee</label></td>
		        <td>
		         	 <input id="payee" type="text" name="payee" value="${_schedule.payee.name}" />
		        </td>
		    </tr>

		    <tr class="category">
		        <td class="heading"><label for="major">Category</label></td>
		        <td>
						 	<input class="width25 inline" 
						 		id="major" 
						 		type="text" 
						 		name="major" 
						 		list="majors" 
						 		value="${_schedule.category.major}" />
		        </td>
		    </tr>

		    <tr class="category">
		        <td class="heading"><label for="minor">Sub-category</label></td>
		        <td>
		        	<select id="minor" name="minor">
			        	<c:forEach items="${_allMinorCategories}" var="_c">
			        		<option value="${_c}" <c:if test="${_c eq _schedule.category.minor}">selected</c:if>>${_c}</option>
			        	</c:forEach>
		        	</select>
		        </td>
		    </tr>

		    <tr class="splits-list">
		        <td class="heading"><label>Splits</label></td>
		        <td>
		        	<table>
		        		<c:forEach items="${_allSplits}" var="_split" varStatus="_status">
		        			<%--
		        				_split is a SplitTransactionFormComponent. It comprises lists of major categories
		        				and corresponding minor categories
		        			 --%>
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
		        <td><input id="memo" type="text" name="memo" placeholder="Enter any relevant notes" value="${_schedule.memo}" /></td>
		    </tr>
		    
		    <tr>
		        <td class="heading"><label for="amount">Total amount</label></td>
		        <td>
		        	<span class="inline-block radio-horiz"><input id="amount" type="text" name="amount" placeholder="Enter amount" 
		        		value="${mon:formatPounds(_schedule.amountValue)}" /></span>
		        	<span class="radio-horiz"><input id="debit" type="radio" name="debitorcredit" value="debit" 
		        		${mon:tertiaryOp(_formMode eq 'add' or _schedule.debit, 'checked=checked', '')} /> Debit</span>
		        	<span class="radio-horiz"><input id="credit" type="radio" name="debitorcredit" value="credit" 
		        		${mon:tertiaryOp(not _schedule.debit, 'checked=checked', '')} /> Credit</span>
		        </td>
		    </tr>

			</table> 
			
			<input id="submit-button" type="submit" value="${_buttonLabel}" /> 
			<input id="cancel-button" type="button" value="Cancel" /> 
			<c:if test="${_formMode eq 'update'}">
	    		<input type="button" value="Delete scheduled transaction?" id="delete-button" /> 
			</c:if>
			<input type="hidden" name="id" value="${_schedule.id}" />   
			<input type="hidden" name="formMode" value="${_formMode}" />   
	</form>	
	
	<div id="splits-error-dialog" title="Splits error">
		The split amounts do NOT match the total amount. Please correct in
		order to submit the form. (Total = __totalamount__, Splits = __splitamounts__)
	</div>
	
</mny:standardLayout>

<mny:entityDeletionDialog entity="schedule" mode="${_formMode}" id="${_schedule.id}"/>
<mny:transactionFormJavascript />
<mny:minorCategoryUpdatesJavascript />
<mny:payeeAutocompleterJavascript />

<script>
	$(function() {
		$(".datepicker").datepicker({
			dateFormat: "yy-mm-dd",
			changeMonth: true,
			changeYear: true
		});
		
		$("#cancel-button").click(function(e){
			window.location = webContext + "/schedule/list"
		});
	});
</script>


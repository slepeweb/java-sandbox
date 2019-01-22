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
	
	<c:if test="${_formMode eq 'update'}"><div class="right"><a href="../add">New transaction</a></div></c:if>
	
	<h2>${_pageHeading} <c:if test="${not empty param.flash}"><span 
		class="flash ${_flashType}">${_flashMessage}</span></c:if></h2>	
	
	<form method="post" action="${_ctxPath}/transaction/update">	  
	    <table id="trn-form">
	    	<c:if test="${_formMode eq 'update'}">
			    <tr class="opaque50">
			        <td class="heading"><label>Id</label></td>
			        <td><input type="text" readonly name="identifier" value="${_transaction.id}" /></td>
			    </tr>
			    
			    <c:if test="${_transaction.origId gt 0}">
				    <tr class="opaque50">
				        <td class="heading"><label>Original id</label></td>
				        <td><input type="text" readonly name="origid" value="${_transaction.origId}" /></td>
				    </tr>
			    </c:if>
		    </c:if>
		    
		    <tr>
		        <td class="heading"><label>Date</label></td>
		        <td><input type="text" class="datepicker" name="entered" 
		        	placeholder="Enter transaction date" value="${mon:formatTimestamp(_transaction.entered)}" /></td>
		    </tr>

		    <tr>
		        <td class="heading"><label>${mon:tertiaryOp(_transaction.debit, 'From', 'To')} account</label></td>
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
		        <td class="heading"><label>Payment type</label></td>
		        <td>
		        	<span class="radio-horiz"><input type="radio" name="paymenttype" value="payment" ${mon:tertiaryOp(_transaction.transfer, '', 'checked=checked')} /> Payment</span>
		        	<span class="radio-horiz"><input type="radio" name="paymenttype" value="transfer" ${mon:tertiaryOp(_transaction.transfer, 'checked=checked', '')} /> Transfer</span>
		        	<c:if test="${_transaction.transfer}"><span class="radio-horiz right"><a href="${_ctxPath}/transaction/form/${_transaction.transferId}">Mirror</a></span></c:if>
		        </td>
		    </tr>

		    <tr class="transfer">
		        <td class="heading"><label>${mon:tertiaryOp(_transaction.debit, 'To', 'From')} account</label></td>
		        <td>
		        	<select name="xferaccount">
			        	<option value="">Choose ...</option>
			        	<c:forEach items="${_allAccounts}" var="_a">
			        		<option value="${_a.id}" <c:if test="${_transaction.transfer and _a.id eq _transaction.mirrorAccount.id}">selected</c:if>>${_a.name}</option>
			        	</c:forEach>
		        	</select>
		        </td>
		    </tr>

		    <tr class="payee">
		        <td class="heading"><label>Payee</label></td>
		        <td>
		         	 <input type="text" id="payee-selector" name="payee" value="${_transaction.payee.name}" />
		        </td>
		    </tr>

				<tr class="splits-q">
		      <td class="heading"><label>Split?</label></td>
					<td><input type="checkbox" name="split" ${mon:tertiaryOp(_transaction.split, 'checked=checked', '')} /></td>
				</tr>
								
		    <tr class="category">
		        <td class="heading"><label>Category</label></td>
		        <td>
		        	<select name="major">
			        	<c:forEach items="${_allMajorCategories}" var="_c">
			        		<option value="${_c}" <c:if test="${_c eq _transaction.category.major}">selected</c:if>>${_c}</option>
			        	</c:forEach>
		        	</select>
		        </td>
		    </tr>

		    <tr class="category">
		        <td class="heading"><label>Sub-category</label></td>
		        <td>
		        	<select name="minor">
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
		        					<input type="text" name="amount_${_status.count}" placeholder="Enter amount" value="${mon:formatPounds(_split.amount)}" />
		        				</td>
		        			</tr>
		        		</c:forEach>
	        		</table>
		        </td>
		    </tr>
		    
		    <tr>
		        <td class="heading"><label>Notes</label></td>
		        <td><input type="text" name="memo" placeholder="Enter any relevant notes" value="${_transaction.memo}" /></td>
		    </tr>
		    
		    <tr>
		        <td class="heading"><label>Total amount</label></td>
		        <td><input type="text" name="amount" placeholder="Enter amount" value="${mon:formatPounds(_transaction.amount)}" /></td>
		    </tr>

			</table> 
			
	    <input type="submit" value="${_buttonLabel}" /> 
			<c:if test="${_formMode eq 'update'}">
	    	<input type="button" value="Delete transaction?" id="delete-button" /> 
	    </c:if>
	    <input type="hidden" name="id" value="${_transaction.id}" />   
	    <input type="hidden" name="formMode" value="${_formMode}" />   
	    <input type="hidden" name="origxferid" value="${_transaction.transferId}" />   
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
		
	  $.ajax({
	    url: webContext + "/rest/payee/list/all",
	    type: "GET",
	    contentType: "application/json",
	    dataType: "json",
	    success: function(data) {
	      // init the widget with response data and let it do the filtering
	      $("#payee-selector").autocomplete({
	        source: data,
	        minLength: 2
	      });
	    },
	    error: function(x, t, m) {
	      console.trace();
	      if (!(console == 'undefined')) {
	        console.log("ERROR: " + x + t + m);
	      }
	      console.log(" At the end");
	    }
	  });
		  
	  $("select[name^='major']").change(function(e) {	
			var major = $(this).find(":selected").val();
			var name = $(this).attr("name");
			var split = name.length > 5;
			var index = -1;
			if (split) {
				index = name.substring("major".length + 1);
			}
			
			$.ajax(webContext + "/rest/category/minor/list/" + major, {
				type: "GET",
				cache: false,
				dataType: "json",
				success: function(obj, status, z) {
					var select = $("select[name='minor" + (split ? "_" + index : "") + "']");
					select.empty();
					$.each(obj.data, function(index, minor) {
						select.append("<option value='" + minor + "'>" + minor + "</option>");
					});
				}
			});
		});
		
		var _setComponentVisibilities = function() {
				
				if ($("input[name='paymenttype']:checked").val() == "transfer") {
					// Set from for transfer between accounts
					$(".payee td, .category td, .splits-q td, .splits-list td").css("display", "none");
					$(".transfer td").css("display", "table-cell");
				}
				else {
					// Set form for a payment
					$(".payee td, .category td, .splits-q td").css("display", "table-cell");
					$(".transfer td").css("display", "none");
					
					// Is it a split-payment?
					if ($("input[name='split']").prop("checked")) {
						$(".category td").css("display", "none");
						$(".splits-list td").css("display", "table-cell");
					}
					else {
						$(".splits-list td").css("display", "none");
						$(".category td").css("display", "table-cell");
					}
				}
		}
		
		$("input[name='split'], input[name='paymenttype']").change(function(e) {	
			_setComponentVisibilities();
		});
		
		_setComponentVisibilities();
	});
</script>

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
	
	<div class="right">
		<c:if test="${_formMode eq 'update'}">
			<a href="${_ctxPath}/schedule/add">New schedule</a><br />
		</c:if>
		<a href="${_ctxPath}/schedule/list">List schedules</a>
	</div>
	
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
		        <td class="heading"><label for="day">Day of month</label></td>
		        <td>
		        	<select id="day" type="text" name="day" placeholder="Enter day of month" value="${_schedule.day}">
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
			        		<option value="${_a.id}" <c:if test="${_a.id eq _schedule.accountId}">selected</c:if>>${_a.name}</option>
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
			        		<option value="${_a.id}" <c:if test="${_schedule.transfer and _a.id eq _schedule.mirrorId}">selected</c:if>>${_a.name}</option>
			        	</c:forEach>
		        	</select>
		        </td>
		    </tr>

		    <tr class="payee">
		        <td class="heading"><label for="payee">Payee</label></td>
		        <td>
		         	 <input id="payee" type="text" name="payee" value="${_schedule.payee}" />
		        </td>
		    </tr>

		    <tr class="category">
		        <td class="heading"><label for="major">Category</label></td>
		        <td>
		        	<select id="major" name="major">
			        	<c:forEach items="${_allMajorCategories}" var="_c">
			        		<option value="${_c}" <c:if test="${_c eq _schedule.majorCategory}">selected</c:if>>${_c}</option>
			        	</c:forEach>
		        	</select>
		        </td>
		    </tr>

		    <tr class="category">
		        <td class="heading"><label for="minor">Sub-category</label></td>
		        <td>
		        	<select id="minor" name="minor">
			        	<c:forEach items="${_allMinorCategories}" var="_c">
			        		<option value="${_c}" <c:if test="${_c eq _schedule.minorCategory}">selected</c:if>>${_c}</option>
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
			
	    <input type="submit" value="${_buttonLabel}" /> 
			<c:if test="${_formMode eq 'update'}">
	    	<input type="button" value="Delete scheduled transaction?" id="delete-button" /> 
	    </c:if>
	    <input type="hidden" name="id" value="${_schedule.id}" />   
	    <input type="hidden" name="formMode" value="${_formMode}" />   
	</form>		  	
		
</mny:standardLayout>

<mny:entityDeletionDialog entity="transaction" mode="${_formMode}" id="${_schedule.id}"/>

<script>
	$(function() {
	  <mny:payeeAutocompleter />
	  
	  var _updateMinorCategories = function() {
		  var deferred = $.Deferred();
		  var majorEle = $("select[name^='major']");
			var majorVal = majorEle.find(":selected").val();
			var name = majorEle.attr("name");
			var split = name.length > 5;
			var index = -1;
			if (split) {
				index = name.substring("major".length + 1);
			}
			
			$.ajax(webContext + "/rest/category/minor/list/" + majorVal, {
				type: "GET",
				cache: false,
				dataType: "json",
				success: function(obj, status, z) {
					var select = $("select[name='minor" + (split ? "_" + index : "") + "']");
					select.empty();
					$.each(obj.data, function(index, minor) {
						select.append("<option value='" + minor + "'>" + minor + "</option>");
					});
					
					deferred.resolve("Categories updated");
				},
				error: function(x, t, m) {
					deferred.reject(x + t + m);
				}
			});
			
			return deferred.promise();
	  }
		  
	  $("select[name^='major']").change(function(e) {	
		  var promet = _updateMinorCategories();
		  promet.done(function(res){
			  //window.alert(res);
		  });
		  
		  promet.fail(function(res){
			  //window.alert(res);
		  });
		});
		
	  $("#payee").change(function(e) {	
			var payeeName = $(this).val();
			var major = $("select[name='major']").find(":selected").val();
			var memo = $("input[name='memo']").val();
			
			if (! major) {
		  	$.ajax({
			    url: webContext + "/rest/transaction/latest/bypayee/" + payeeName,
			    type: "GET",
			    contentType: "application/json",
			    dataType: "json",
			    success: function(trn) {
			      $("select[name='major']").val(trn.majorCategory);
					  var promet = _updateMinorCategories();
					  promet.done(function(res) {						  
					      if (! memo) {
						      $("input[name='memo']").val(trn.memo);
					      }
					      $("select[name='minor']").val(trn.minorCategory);
					      
					      var amountStr = trn.amountInPounds;
					      var len = amountStr.length;					      
					      if (len > 0 && amountStr.substring(0, 1) == '-') {
					    	  amountStr = amountStr.substring(1);
					      }
					      
					      if (trn.amount < 0) {
					    	  $("#debit").prop("checked", true);
					    	  //$("#credit").prop("checked", false);
					      }
					      else {
					    	  //$("#debit").prop("checked", false);
					    	  $("#credit").prop("checked", true);
					      }
					      
					      $("#amount").val(amountStr);
					  });
			    },
			    error: function(x, t, m) {
			      console.trace();
			      /*
			      if (!(console == 'undefined')) {
			        console.log("ERROR: " + x + t + m);
			      }
			      console.log(" At the end");
			      */
			    }
			  });
			}
	  });
	  
		var _setComponentVisibilities = function() {				
			var paymentType = $("input[name='paymenttype']:checked").val();
			
			if (paymentType == "standard") {
				// Set form for a standard/normal transaction
				$(".payee td, .category td").css("display", "table-cell");
				$(".transfer td, .splits-list td").css("display", "none");
			}
			else if (paymentType == "transfer") {
				$(".payee td, .category td, .splits-list td").css("display", "none");
				$(".transfer td").css("display", "table-cell");
			}
			else if (paymentType == "split") {				
				$(".category td, .transfer td").css("display", "none");
				$(".splits-list td").css("display", "table-cell");
			}
		}
		
		$("input[name='paymenttype']").change(function(e) {	
			_setComponentVisibilities();
		});
		
		_setComponentVisibilities();
	});
</script>

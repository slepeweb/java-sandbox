<%@ tag %><%@ include file="/WEB-INF/jsp/tagDirectives.jsp" %>

<tr class="splits-list">
    <td class="heading"><label>Splits</label></td>
    <td>
    	<div class="splits-heading">
    		<span class="arrow"> </span>
    		<span class="category">Category</span>
    		<span class="sub-category">Sub-category</span>
    		<span class="memo">Notes</span>
    		<span class="amount">Amount</span>
    	</div>
    		   		
   		<input type="hidden" name="numsplits" value="${fn:length(_allSplits)}" />
   		
   		<c:forEach items="${_allSplits}" var="_split" varStatus="_status">
   			<%--
   				_split is a SplitTransactionFormComponent. It comprises lists of major categories
   				and corresponding minor categories
   			 --%>
    		<div id="split-${_status.count}" ${mon:tertiaryOp(not _split.visible, 'class="hidden"', '')}>
					<span class="next-category arrow ${mon:tertiaryOp(_split.visible and not _split.lastVisible, 'hidden', '')}" 
						data-id="${_status.count}"><i class="fa-solid fa-chevron-down" title="Add a category"></i></span>					

					<select class="category" name="major_${_status.count}">
						<c:forEach items="${_split.allMajors}" var="_c">
							<option value="${_c}" <c:if test="${_c eq _split.category.major}">selected</c:if>>${_c}</option>
						</c:forEach>
					</select>
					
					<select class="sub-category" name="minor_${_status.count}">
						<c:forEach items="${_split.allMinors}" var="_c">
							<option value="${_c}" <c:if test="${_c eq _split.category.minor}">selected</c:if>>${_c}</option>
						</c:forEach>
					</select>
   					
   				<input class="memo" type="text" name="memo_${_status.count}" placeholder="Enter any relevant notes" value="${_split.memo}" />
 					<input class="amount" type="text" name="amount_${_status.count}" placeholder="Enter amount" value="${mon:formatPounds(_split.amountValue)}" />
 					<span class="trash-category" data-id="${_status.count}"><i class="far fa-trash-alt" title="Remove this category"></i></span>
 				</div>
   		</c:forEach>
    </td>
</tr>

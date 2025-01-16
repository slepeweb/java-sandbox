<%@ tag %><%@ include file="/WEB-INF/jsp/tagDirectives.jsp" %>

<tr class="splits-list">
    <td class="heading"><label>Splits</label></td>
    <td>
    	<table>
    		<thead>
    		<tr>
    			<th>Category</th>
    			<th>Sub-category</th>
    			<th>Notes</th>
    			<th>Amount</th>
    		</tr>
    		</thead>
    		
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

<%@ tag %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %><%@ 
	attribute name="heading" required="true" rtexprvalue="true" %><%@ 
	attribute name="ctx" required="false" rtexprvalue="true" %><%@ 
	attribute name="group" required="true" rtexprvalue="true" type="com.slepeweb.money.bean.Category_Group" %>

<!-- categoryList.tag -->

<c:set var="_wider" value="${not empty ctx and ctx eq 'search' ? 'wider' : ''}" />

<input type="hidden" name="numCategories" value="${group.size}" />

<tr class="category-list <c:if test="${not group.visible}">invisible</c:if>">   				    		   		
	<td class="heading"><label>Categories</label></td>

	<td>
		<div class="category-list-heading">
			<span class="arrow"> </span>
			<span class="category ${_wider}">Category</span>
			<span class="sub-category ${_wider}">Sub-category</span>

			<c:choose><c:when test="${ctx eq 'transaction'}">
				<span class="memo">Notes</span>
				<span class="amount">Amount</span>
			</c:when><c:when test="${ctx eq 'search'}">
				<span class="logic">Logic</span>
			</c:when></c:choose>
		</div>

		<c:forEach items="${group.categories}" var="_cat" varStatus="_catStatus">	
			<c:set var="ident" value="${_catStatus.count}" />
	
			<div id="split_${ident}" ${not _cat.visible ? 'class="hidden"' : ''}>
				<span class="next-category arrow ${_cat.visible and not _cat.lastVisible ? 'invisible' : ''}"
					data-id="${ident}"><i class="fa-solid fa-chevron-down" title="Add a category"></i></span>					
	
				<select class="category ${_wider}" name="major_${ident}">
					<c:forEach items="${_allMajorCategories}" var="_c">
						<option value="${_c}" <c:if test="${_c eq _cat.major}">selected</c:if>>${_c}</option>
					</c:forEach>
				</select>
		
				<select class="sub-category ${_wider}" name="minor_${ident}">
					<c:forEach items="${group.options[_cat.major]}" var="_c">
						<option value="${_c}" <c:if test="${_c eq _cat.minor}">selected</c:if>>${_c}</option>
					</c:forEach>
				</select>
					
				<c:choose><c:when test="${ctx eq 'transaction'}">
					<input class="memo" type="text" name="memo_${ident}" placeholder="Enter any relevant notes" value="${_cat.memo}" />
					<input class="amount" type="text" name="amount_${ident}" placeholder="Enter amount" value="${mon:formatPounds(_cat.amount)}" />
				</c:when><c:when test="${ctx eq 'search'}">
					<select class="logic" id="logic_${ident}" name="logic_${ident}">
						<option value="no" ${not _cat.exclude ? 'selected' : ''}>Include</option>
						<option value="yes" ${_cat.exclude ? 'selected' : ''}>Exclude</option>
					</select>	 	
				</c:when></c:choose>
			
				<span class="trash-category" data-id="${ident}"><i class="far fa-trash-alt" title="Remove this category"></i></span>
			</div>
		</c:forEach>
  </td>
</tr>

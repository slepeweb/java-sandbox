<%@ tag %><%@ include file="/WEB-INF/jsp/tagDirectives.jsp" %>

<!-- multiSplitInputSupport.tag -->

<c:set var="_innerTemplate" scope="request">
	<div class="split-inputs">
		<span class="inline">[counter]</span>
	 	<input class="width25 inline" 
	 		id="major-[counter]" 
	 		type="text" 
	 		name="major-[counter]" 
	 		list="majors" 
	 		value="[major]" />
	 		
	 	<select class="width25 inline" id="minor-[counter]" name="minor-[counter]">
	 	__splitOptionsTemplate__
	 	</select>
	 	
		<input class="width25 inline" type="text" name="memo-[counter]" placeholder="Enter any relevant notes" value="[memo]" />
		<input class="width15 inline" type="text" name="amount-[counter]" placeholder="Enter amount" value="[amount]" />
 		<span class="trash-split"><i class="far fa-trash-alt" title="Remove this split"></i></span>
	</div>
</c:set>
	
<c:set var="_minorCategoryOptionsTemplate" scope="request">
	<option value="[minor]" [selected]>[minor]</option>
</c:set>

<datalist id="majors">
		<c:forEach items="${_allMajorCategories}" var="_major">
				<option value="${_major}" />
		</c:forEach>
</datalist>


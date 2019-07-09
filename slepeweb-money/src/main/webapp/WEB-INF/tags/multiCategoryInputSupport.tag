<%@ tag %><%@ include file="/WEB-INF/jsp/tagDirectives.jsp" %>

<!-- multiCategoryInputSupport.tag -->

<c:set var="_outerTemplate" scope="request">
   <tr class="multi-category-group">
       <td class="width25">
       	<input id="group-[groupId]-name" type="text" name="group-[groupId]-name" value="[label]" />
       </td>
       <td>
					__innerTemplate__
	       	
					<button class="add-category-button" type="button" data-groupid="[groupId]">+ category</button>
       </td>
       <td class="trash-category-cell">
       		<div class="trash-multi-category-group" data-groupid="[groupId]"><i class="far fa-trash-alt" title="Remove this group"></i></div>
       </td>
   </tr>
</c:set>
	
<c:set var="_innerTemplate" scope="request">
	<div class="category-inputs">
		<span class="inline">[counter]</span>
	 	<input class="width25 inline" 
	 		id="major-[groupId]-[counter]" 
	 		type="text" 
	 		name="major-[groupId]-[counter]" 
	 		list="majors" 
	 		value="[major]" />
	 		
	 	<select class="width50 inline" id="minor-[groupId]-[counter]" name="minor-[groupId]-[counter]">
	 	__categoryOptionsTemplate__
	 	</select>
	 	
	 	<select class="width15 inline" id="logic-[groupId]-[counter]" name="logic-[groupId]-[counter]">
	 		<option value="no" [include-selected]>Include</option>
	 		<option value="yes" [exclude-selected]>Exclude</option>
	 	</select>	 	
	 		 	
	 	<span class="trash-category" data-groupid="[groupId]"><i class="far fa-trash-alt" title="Remove this category"></i></span>
	</div>
</c:set>
	
<c:set var="_categoryOptionsTemplate" scope="request">
	<option value="[minor]" [selected]>[minor]</option>
</c:set>

<datalist id="majors">
		<c:forEach items="${_allMajorCategories}" var="_major">
				<option value="${_major}" />
		</c:forEach>
</datalist>


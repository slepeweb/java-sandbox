<%@ tag %><%@ include file="/WEB-INF/jsp/tagDirectives.jsp" %><%@ 
	attribute name="entity" required="true" rtexprvalue="true" type="com.slepeweb.money.bean.Transaction" %>

<tr class="category">
    <td class="heading"><label for="major">Category</label></td>
    <td>
 		<select id="major" name="major">
	     	<option value="">Choose ...</option>
	     	<c:forEach items="${_allMajorCategories}" var="_name">
	     		<option value="${_name}" <c:if test="${_name eq entity.category.major}">selected</c:if>>${_name}</option>
	     	</c:forEach>
	 	</select>
    </td>
</tr>

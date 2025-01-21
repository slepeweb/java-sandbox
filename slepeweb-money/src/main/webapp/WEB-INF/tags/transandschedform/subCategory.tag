<%@ tag %><%@ include file="/WEB-INF/jsp/tagDirectives.jsp" %><%@ 
	attribute name="entity" required="true" rtexprvalue="true" type="com.slepeweb.money.bean.Transaction" %>

<tr class="category">
    <td class="heading"><label for="minor">Sub-category</label></td>
    <td>
    	<select id="minor" name="minor">
     	<c:forEach items="${_allMinorCategories}" var="_c">
     		<option value="${_c}" <c:if test="${_c eq entity.category.minor}">selected</c:if>>${_c}</option>
     	</c:forEach>
    	</select>
    </td>
</tr>

<%@ tag %><%@ include file="/WEB-INF/jsp/tagDirectives.jsp" %><%@ 
	attribute name="entity" required="true" rtexprvalue="true" type="com.slepeweb.money.bean.Transaction" %>

<!-- transandschedform/subCategory.tag -->

<mny:tableRow heading="Sub-category" trclass="category">
	<select id="minor" name="minor">
   	<c:forEach items="${_allMinorCategories}" var="_c">
   		<option value="${_c}" <c:if test="${_c eq entity.category.minor}">selected</c:if>>${_c}</option>
   	</c:forEach>
  </select>
</mny:tableRow>
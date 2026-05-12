<%@ tag %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %><%@ 
	attribute name="id" required="true" rtexprvalue="true" %><%@ 
	attribute name="heading" required="true" rtexprvalue="true" %><%@ 
	attribute name="selected" required="true" rtexprvalue="true" %>
	
<!-- yearSelector.tag -->

<div>
	<span class="subheading">${heading}:</span>
	<select id="${id}" name="${id}">
		<c:forEach items="${_yearRange}" var="_year">
			<option value="${_year}" <c:if test="${selected eq _year}">selected</c:if>>${_year}</option>
		</c:forEach>
	</select>
</div>
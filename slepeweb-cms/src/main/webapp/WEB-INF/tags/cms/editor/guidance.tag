<%@ tag %><%@ 
	attribute name="validator" required="true" rtexprvalue="true" type="com.slepeweb.cms.bean.guidance.IValidator" %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>
        
<cms:debug><!-- tags/cms/editor/guidance.tag --></cms:debug>
	
<c:if test="${not empty validator.heading}">
	<h2>${validator.heading}</h2></c:if>
<c:if test="${not empty validator.teaser}">
	<p>${validator.teaser}</p></c:if>
<c:if test="${not empty validator.format}">
	<h3>Format</h3><p>${validator.format}</p></c:if>

<c:if test="${not empty validator.examples}">
	<h3>Examples</h3><table>
	<c:forEach items="${validator.examples}" var="_ex">
		<tr><td>${_ex.example}</td><td>${_ex.explanation}</td></tr>
	</c:forEach>
	</table>
</c:if>

<c:if test="${not empty validator.details}">
	<h3>Details</h3><ul>
	<c:forEach items="${validator.details}" var="_detail">
		<li>${_detail}</li>
	</c:forEach>
	</ul>
</c:if>

<c:if test="${not empty validator.regExp}">
	<p class="regexp hide">${validator.regExp}</p>
</c:if>
	
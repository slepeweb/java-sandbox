<%@ tag %><%@ 
	attribute name="guidance" required="true" rtexprvalue="true" type="com.slepeweb.cms.bean.guidance.IGuidance" %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>
        
<cms:debug><!-- tags/cms/editor/guidance.tag --></cms:debug>
	
<c:if test="${not empty guidance.heading}">
	<h2>${guidance.heading}</h2></c:if>
<c:if test="${not empty guidance.teaser}">
	<p>${guidance.teaser}</p></c:if>
<c:if test="${not empty guidance.format}">
	<h3>Format</h3><p>${guidance.format}</p></c:if>

<c:if test="${not empty guidance.examples}">
	<h3>Examples</h3><table>
	<c:forEach items="${guidance.examples}" var="_ex">
		<tr><td>${_ex.example}</td><td>${_ex.explanation}</td></tr>
	</c:forEach>
	</table>
</c:if>

<c:if test="${not empty guidance.details}">
	<h3>Details</h3><ul>
	<c:forEach items="${guidance.details}" var="_detail">
		<li>${_detail}</li>
	</c:forEach>
	</ul>
</c:if>

<c:if test="${not empty guidance.regExp}">
	<p class="regexp hide">${guidance.regExp}</p>
</c:if>
	
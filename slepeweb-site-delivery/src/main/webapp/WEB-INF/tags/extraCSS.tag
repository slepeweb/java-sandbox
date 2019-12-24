<%@ tag %><%@ 
	attribute name="inpage" required="false" rtexprvalue="true" type="java.lang.Boolean" %><%@ 
	include file="/WEB-INF/jsp/common/tagDirectives.jsp" %>

<gen:debug><!-- tags/extraCSS.tag --></gen:debug>

<c:if test="${not empty _extraCss}">
	<c:choose><c:when test="${not empty inpage}">
		<style>
			${_extraCss}
		</style>
	</c:when><c:otherwise>
		<c:forTokens items="${_extraCss}" delims=", " var="href">
			<link rel="stylesheet" href="${href}" type="text/css">
		</c:forTokens>
	</c:otherwise></c:choose>
</c:if>
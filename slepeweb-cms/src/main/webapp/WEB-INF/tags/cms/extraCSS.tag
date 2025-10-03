<%@ tag %><%@ 
	taglib prefix="c" uri="jakarta.tags.core" %><%@ 
	taglib prefix="fn" uri="jakarta.tags.functions"%>

<c:if test="${not empty _extraCSS}">
	<c:forTokens items="${_extraCSS}" delims=", " var="href">
		<link rel="stylesheet" href="${href}" type="text/css">
	</c:forTokens>
</c:if>
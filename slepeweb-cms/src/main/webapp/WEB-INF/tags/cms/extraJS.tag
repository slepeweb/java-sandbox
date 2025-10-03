<%@ tag %><%@ 
	taglib prefix="c" uri="jakarta.tags.core" %><%@ 
	taglib prefix="fn" uri="jakarta.tags.functions"%>

<c:if test="${not empty _extraJS}">
	<c:forTokens items="${_extraJS}" delims=", " var="src">
		<script src="${src}"></script>
	</c:forTokens>
</c:if>
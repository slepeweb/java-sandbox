<%@ tag %><%@ include file="/WEB-INF/jsp/common/tagDirectives.jsp" %>

<gen:debug><!-- tags/extraJS.tag --></gen:debug>

<c:if test="${not empty _extraJs}">
	<c:forTokens items="${_extraJs}" delims=", " var="src">
		<script src="${src}"></script>
	</c:forTokens>
</c:if>
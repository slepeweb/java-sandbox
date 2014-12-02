<%@ tag %><%@ include file="/WEB-INF/jsp/common/tagDirectives.jsp" %>

<c:forTokens items="${_extraJs}" delims=", " var="src">
	<script src="${src}"></script>
</c:forTokens>
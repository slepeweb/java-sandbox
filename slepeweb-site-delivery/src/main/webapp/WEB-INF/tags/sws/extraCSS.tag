<%@ tag %><%@ include file="/WEB-INF/jsp/common/tagDirectives.jsp" %>

<c:forTokens items="${_extraCss}" delims=", " var="href">
	<link rel="stylesheet" href="${href}" type="text/css">
</c:forTokens>
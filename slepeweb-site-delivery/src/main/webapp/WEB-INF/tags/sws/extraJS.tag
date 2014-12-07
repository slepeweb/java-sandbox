<%@ tag %><%@ include file="/WEB-INF/jsp/common/tagDirectives.jsp" %>

<gen:debug><!-- tags/sws/extraJS.tag --></gen:debug>

<c:forTokens items="${_extraJs}" delims=", " var="src">
	<script src="${src}"></script>
</c:forTokens>
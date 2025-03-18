<%@ tag %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>

<gen:debug><!-- tags/extraInpageJS.tag --></gen:debug>

<c:if test="${not empty _extraInpageJs}">
	<script>
		${_extraInpageJs}
	</script>
</c:if>
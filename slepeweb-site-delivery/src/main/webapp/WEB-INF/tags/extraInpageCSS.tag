<%@ tag %><%@ 
	include file="/WEB-INF/jsp/common/tagDirectives.jsp" %>

<gen:debug><!-- tags/extraInpageCSS.tag --></gen:debug>

<c:if test="${not empty _extraInpageCss}">
	<style>
		${_extraInpageCss}
	</style>
</c:if>
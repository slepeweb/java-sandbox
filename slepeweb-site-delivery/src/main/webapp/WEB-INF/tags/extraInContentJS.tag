<%@ tag %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>

<gen:debug><!-- tags/extraInContentJS.tag --></gen:debug>

<c:set var="_script" value="" />

<c:forEach items="${_page.header.components}" var="_comp">
	<c:if test="${_comp.type eq 'script'}">
		<c:set var="_script">${_script}
			${_comp.js}</c:set>
	</c:if>
</c:forEach>

<c:if test="${not empty _script}">
	<script>${_script}</script>
</c:if>
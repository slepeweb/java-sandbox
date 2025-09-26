<%@ tag %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>

<gen:debug><!-- tags/extraInContentCSS.tag --></gen:debug>

<c:set var="_style" value="" />

<c:forEach items="${_page.header.components}" var="_comp">
	<c:if test="${_comp.type eq 'style'}">
		<c:set var="_style">${_style} ${_comp.css}</c:set>
	</c:if>
</c:forEach>

<c:if test="${not empty _style}">
	<style>${_style}</style>
</c:if>
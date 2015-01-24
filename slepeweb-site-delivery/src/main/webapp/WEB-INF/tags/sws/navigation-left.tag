<%@ tag %><%@ include file="/WEB-INF/jsp/common/tagDirectives.jsp" %>

<gen:debug><!-- tags/sws/navigation-left.tag --></gen:debug>

<c:set var="_root" value="${_page.leftSidebar.navigation[0]}" />

<nav id="left-nav">
	<h3>In this section</h3>
	<sw:navigation-level list="${_root.children}" level="1" />
</nav>

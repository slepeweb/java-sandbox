<%@ tag %><%@ include file="/WEB-INF/jsp/common/tagDirectives.jsp" %>

<gen:debug><!-- tags/sws/navigation-left.tag --></gen:debug>

<section>
	<sw:navigation-level list="${_page.leftSidebar.navigation}" />
</section>

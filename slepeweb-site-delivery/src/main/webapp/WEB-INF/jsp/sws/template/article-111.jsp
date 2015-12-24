<%@ 
	include file="/WEB-INF/jsp/common/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/common/tagDirectives.jsp" %>
	
<sw:standardLayout>
	<gen:debug><!-- jsp/sws/article-111.jsp --></gen:debug>
	
	<!-- Left Sidebar -->
	<div class="3u 12u(2)">
		<site:insertComponents site="${_item.site.shortname}" list="${_page.leftSidebar.components}" /> 
	</div>

	<!-- Main content -->	
	<div class="6u 12u(2) important(2)">	
		<sw:standardBody />	
		<site:insertComponents site="${_item.site.shortname}" list="${_page.components}" /> 
	</div>
	
	<!-- Right sidebar -->
	<div class="3u 12u(2)">	
			<site:insertComponents site="${_item.site.shortname}" list="${_page.rightSidebar.components}" /> 
	</div>
	
</sw:standardLayout>
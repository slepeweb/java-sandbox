<%@ 
	include file="/WEB-INF/jsp/common/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/common/tagDirectives.jsp" %>
	
<ntc:standardLayout>
	<gen:debug><!-- jsp/ntc/article-111.jsp --></gen:debug>
	
	<!-- Left Sidebar -->
	<div class="3u 6u(2) 12u(3)">
		<site:insertComponents site="${_item.site.shortname}" list="${_page.leftSidebar.components}" /> 
	</div>

	<!-- Main content -->	
	<div class="6u 6u(2) 12u(3) important(3)">	
		<sw:standardBody />	
	</div>
	
	<!-- Right sidebar -->
	<div class="3u 6u(2) 12u(3)">	
			<site:insertComponents site="${_item.site.shortname}" list="${_page.rightSidebar.components}" /> 
	</div>
	
</ntc:standardLayout>
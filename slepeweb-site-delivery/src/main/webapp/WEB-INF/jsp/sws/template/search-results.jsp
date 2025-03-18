<%@ 
	include file="/WEB-INF/jsp/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>
	
<sw:standardLayout>
	<gen:debug><!-- jsp/sws/search-results.jsp --></gen:debug>

		<!-- Main content -->	
		<div class="col-3-4 primary-col pull-right-sm">	
			<sw:standardBody />	
			<sw:search-results /> 
		</div>					
	
		<!-- Left Sidebar -->
		<div class="col-1-4 primary-col">
			<site:insertComponents site="${_item.site.shortname}" list="${_page.leftSidebar.components}" /> 
		</div>

</sw:standardLayout>
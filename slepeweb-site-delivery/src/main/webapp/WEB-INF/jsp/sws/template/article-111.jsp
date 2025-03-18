<%@ 
	include file="/WEB-INF/jsp/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>
	
<sw:standardLayout>
	<gen:debug><!-- jsp/sws/article-111.jsp --></gen:debug>
	
	<div class="col-3-4 pull-right-sm">	
		<div>
			<!-- Main content -->	
			<div class="col-2-3 primary-col">
				<sw:standardBody />	
				<site:insertComponents site="${_item.site.shortname}" list="${_page.components}" /> 
			</div>
			
			<!-- Right sidebar -->
			<div class="col-1-3 primary-col">	
					<site:insertComponents site="${_item.site.shortname}" list="${_page.rightSidebar.components}" /> 
			</div>
		</div>
	</div>
	
	<!-- Left Sidebar -->
	<div class="col-1-4 primary-col">
		<site:insertComponents site="${_item.site.shortname}" list="${_page.leftSidebar.components}" /> 
	</div>

</sw:standardLayout>
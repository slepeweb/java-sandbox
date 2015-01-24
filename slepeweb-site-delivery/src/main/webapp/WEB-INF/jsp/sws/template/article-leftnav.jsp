<%@ 
	include file="/WEB-INF/jsp/common/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/common/tagDirectives.jsp" %>
	
<sw:standardLayout>
	<gen:debug><!-- jsp/sws/article-leftnav.jsp --></gen:debug>
	
		<!-- Left Sidebar -->
		<div class="3u 12u(3)">
			<sw:navigation-left />
		</div>
		
		<!-- Main content -->	
		<div class="9u 12u(3) important(3)">	
			<sw:standardBody />	
			<site:insertComponents site="${_item.site.shortname}" list="${_page.components}" /> 
		</div>					
	
</sw:standardLayout>
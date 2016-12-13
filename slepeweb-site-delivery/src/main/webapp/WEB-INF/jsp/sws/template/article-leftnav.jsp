<%@ 
	include file="/WEB-INF/jsp/common/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/common/tagDirectives.jsp" %>
	
<sw:standardLayout>
	<gen:debug><!-- jsp/sws/article-leftnav.jsp --></gen:debug>
	
		<!-- Main content -->	
		<div class="col-3-4 primary-col pull-right-sm">	
			<sw:standardBody />	
			
			<site:insertComponents site="${_item.site.shortname}" 
				list="${_page.components}" 
				view="main" /> 
		</div>					
	
		<!-- Left Sidebar -->
		<div class="col-1-4 primary-col grey-gradient left2right">
			<sw:navigation-left />
		</div>
		
</sw:standardLayout>
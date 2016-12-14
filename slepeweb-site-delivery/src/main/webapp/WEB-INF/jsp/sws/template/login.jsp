<%@ 
	include file="/WEB-INF/jsp/common/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/common/tagDirectives.jsp" %>
	
<sw:standardLayout>
	<gen:debug><!-- jsp/sws/login.jsp --></gen:debug>
			
			<!-- Left Sidebar -->
			<div class="col-1-4 primary-col">
				<site:insertComponents site="${_item.site.shortname}" list="${_page.leftSidebar.components}" /> 
			</div>
			
			<!-- Main content -->	
			<div class="col-1-2 primary-col">	
				<sw:loginForm />
			</div>					
	
</sw:standardLayout>    

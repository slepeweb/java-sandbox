<%@ 
	include file="/WEB-INF/jsp/common/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/common/tagDirectives.jsp" %>
	
<sw:standardLayout>
	<gen:debug><!-- jsp/sws/login.jsp --></gen:debug>
			
			<!-- Left Sidebar -->
			<div class="3u 12u(3)">
				<site:insertComponents site="${_item.site.shortname}" list="${_page.leftSidebar.components}" /> 
			</div>
			
			<!-- Main content -->	
			<div class="6u 12u(3) important(3)">	
				<sw:loginForm />
			</div>					
	
</sw:standardLayout>    

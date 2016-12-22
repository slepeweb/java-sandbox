<%@ 
	include file="/WEB-INF/jsp/common/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/common/tagDirectives.jsp" %>
	
<sw:standardLayout>
	<gen:debug><!-- jsp/sws/login.jsp --></gen:debug>
			
	<div class="col-3-4 pull-right-sm">	
		<div>
			<!-- Main content -->	
			<div class="col-2-3 primary-col">
				<sw:standardBody />
				<sw:loginForm />	
			</div>
			
			<!-- *Empty* Right sidebar -->
			<div class="col-1-3"></div>
		</div>
	</div>
	
	<!-- Left Sidebar -->
	<div class="col-1-4 primary-col">
				<site:insertComponents site="${_item.site.shortname}" list="${_page.leftSidebar.components}" /> 
	</div>

</sw:standardLayout>    

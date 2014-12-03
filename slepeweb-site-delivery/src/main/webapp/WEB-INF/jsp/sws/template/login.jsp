<%@ 
	include file="/WEB-INF/jsp/common/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/common/tagDirectives.jsp" %>
	
<c:set var="_extraJs" scope="request"></c:set>
<c:set var="_extraCss" scope="request"></c:set>

<sw:standardLayout>
	<gen:debug><!-- jsp/sws/login.jsp --></gen:debug>
			
			<!-- Left Sidebar -->
			<div class="3u">
				<site:insertComponents site="${_item.site.shortname}" list="${_page.leftSidebar.components}" /> 
			</div>
			
			<!-- Main content -->	
			<div class="6u skel-cell-mainContent">	
				<sw:loginForm />
			</div>					
	
</sw:standardLayout>    

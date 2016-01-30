<%@ 
	include file="/WEB-INF/jsp/common/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/common/tagDirectives.jsp" %>
	
<sw:standardLayout>
	<gen:debug><!-- jsp/sws/article-011.jsp --></gen:debug>

	<!-- Main content -->	
	<div class="8u 12u(3)">	
		<sw:standardBody />	
		<site:insertComponents site="${_item.site.shortname}" list="${_page.components}" /> 
	</div>
	
	<!-- Right sidebar -->
	<div class="4u 12u(3) grey-gradient right2left">	
		<section class="smaller">
			<site:insertComponents site="${_item.site.shortname}" list="${_page.rightSidebar.components}" /> 
		</section>
	</div>
	
</sw:standardLayout>
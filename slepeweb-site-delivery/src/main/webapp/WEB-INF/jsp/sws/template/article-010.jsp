<%@ 
	include file="/WEB-INF/jsp/common/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/common/tagDirectives.jsp" %>
	
<c:set var="_extraJs" scope="request"></c:set>
<c:set var="_extraCss" scope="request"></c:set>

<sw:standardLayout>
	<gen:debug><!-- jsp/sws/article-010.jsp --></gen:debug>

	<!-- Main content -->	
	<div class="12u skel-cell-mainContent">	
		<site:insertComponents site="${_item.site.shortname}" list="${_page.components}" /> 
	</div>					

</sw:standardLayout>
<%@ 
	include file="/WEB-INF/jsp/common/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/common/tagDirectives.jsp" %>
	
<c:set var="_extraJs" scope="request"></c:set>
<c:set var="_extraCss" scope="request"></c:set>

<sw:standardLayout>
	<gen:debug><!-- jsp/sws/article-011.jsp --></gen:debug>

	<!-- Main content -->	
	<div class="9u skel-cell-mainContent">	
		<sw:standardBody />	
	</div>
	
	<!-- Right sidebar -->
	<div class="3u">	
		<section>
		<!-- 
					_page:        [${_page}]
					rightSidebar: [${_page.rightSidebar}]
					components:   [${_page.rightSidebar.components}]
		 -->
			<site:insertComponents site="${_item.site.shortname}" list="${_page.rightSidebar.components}" /> 
		</section>
	</div>
	
</sw:standardLayout>
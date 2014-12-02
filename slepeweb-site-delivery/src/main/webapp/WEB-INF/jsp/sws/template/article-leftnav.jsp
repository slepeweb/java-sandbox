<%@ 
	include file="/WEB-INF/jsp/common/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/common/tagDirectives.jsp" %>
	
<c:set var="_extraJs" scope="request"></c:set>
<c:set var="_extraCss" scope="request"></c:set>

<sw:standardLayout>
	<gen:debug><!-- jsp/sws/article-leftnav.jsp --></gen:debug>
	
	<!-- Main -->	
	<div id="main-wrapper" class="subpage">
		<div class="container">
			<div class="row">
				<!-- Left Sidebar -->
				<div class="3u">
					<sw:navigation-left />
				</div>
				
				<!-- Main content -->	
				<div class="9u skel-cell-mainContent">	
					<sw:standardBody />	
				</div>					
			</div>
		</div>
	</div>
	
</sw:standardLayout>
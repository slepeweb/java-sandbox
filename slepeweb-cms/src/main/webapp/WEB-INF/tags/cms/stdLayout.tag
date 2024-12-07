<%@ tag %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>

<cms:basicLayout loadjs="${true}">

		<div id="header-wrapper">
			<cms:navigation-top />
		</div>
		
		<div id="main-wrapper">
			<div class="readonly-layer"></div>
			
			<jsp:doBody />
			
			<cms:widefield />
		</div>
	
		<cms:dialogs />
		
</cms:basicLayout>
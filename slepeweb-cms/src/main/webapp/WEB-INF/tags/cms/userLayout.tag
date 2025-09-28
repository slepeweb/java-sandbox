<%@ tag %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>

<cms:basicLayout loadjs="${false}">

		<div id="header-wrapper">
			<header>
				<cms:userCorner />
			</header>
		</div>
		
		<div id="main-wrapper">			
			<jsp:doBody />
		</div>
</cms:basicLayout>
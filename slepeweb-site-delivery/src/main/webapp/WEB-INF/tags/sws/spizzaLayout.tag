<%@ tag %><%@ include file="/WEB-INF/jsp/common/tagDirectives.jsp" %><!DOCTYPE html>

<html>
	
	<%-- 
			This takes the page attribute store in conversationScope, and assigns its value to
			the _page attribute that is expected by the sw: tags on this page.
	 --%>
	<c:set var="_page" value="${page}" scope="request" />	
	
	<head>
		<sw:head />
	</head>
	<body>
		
		<!-- Header -->
		<div id="header-wrapper">
			<sw:navigation-top />
		</div>
	
		<div id="main-wrapper" class="subpage">
			<div class="container">
				<div class="row">
				
					<!-- Left Sidebar -->
					<div class="3u">
						<sw:navigation-left />
					</div>
					
					<!-- Main content -->	
					<div class="9u skel-cell-mainContent">	
						<jsp:doBody />
					</div>
				</div>
			</div>
		</div>

		<!-- Footer -->	
		<div id="footer-wrapper">
			<sw:footer />
		</div>
	
	</body>
</html>
<%@ tag %><%@ 
	attribute name="mainCols" required="false" rtexprvalue="true" %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %><!DOCTYPE html>

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
	
		<div id="main-wrapper">
			<div id="main" class="container">
				<div class="row">
				
					<!-- Main content -->	
					<c:if test="${empty mainCols}"><c:set var="mainCols" value="col-3-4" /></c:if>
					<div class="${mainCols} pull-right-sm">	
						<div>
							<div class="col-1-1 primary-col">
								<jsp:doBody />
							</div>							
						</div>
					</div>
					
					<!-- Left Sidebar -->
					<div class="col-1-4 primary-col grey-gradient left2right">
						<sw:navigation-left />
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
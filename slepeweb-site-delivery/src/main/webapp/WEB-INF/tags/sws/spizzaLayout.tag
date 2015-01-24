<%@ tag %><%@ 
	attribute name="mainCols" required="false" rtexprvalue="true" %><%@ 
	include file="/WEB-INF/jsp/common/tagDirectives.jsp" %><!DOCTYPE html>

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
					<div class="3u 6u(3)">
						<sw:navigation-left />
					</div>
					
					<!-- Main content -->	
					<c:if test="${empty mainCols}"><c:set var="mainCols" value="6u 9u(2) 12u(3) important(3)" /></c:if>
					<div class="${mainCols}">	
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
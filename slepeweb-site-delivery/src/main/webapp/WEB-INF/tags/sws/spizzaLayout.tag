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
				
					<!-- Main content -->	
					<c:if test="${empty mainCols}"><c:set var="mainCols" value="col-1-2 primary-col" /></c:if>
					<div class="${mainCols}">	
						<jsp:doBody />
					</div>
					
					<!-- Left Sidebar -->
					<div class="col-1-4 primary-col pull-right-sm">
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
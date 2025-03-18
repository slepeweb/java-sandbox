<%@ tag %><%@ include file="/WEB-INF/jsp/tagDirectives.jsp" %><!DOCTYPE html>

<html>
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
							<jsp:doBody />
					</div>
			</div>
		</div>

		<!-- Footer -->	
		<div id="footer-wrapper">
			<sw:footer />
		</div>
	
	</body>
</html>
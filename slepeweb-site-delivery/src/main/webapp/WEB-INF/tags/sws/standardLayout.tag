<%@ tag %><%@ include file="/WEB-INF/jsp/common/tagDirectives.jsp" %><!DOCTYPE html>

<html>
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
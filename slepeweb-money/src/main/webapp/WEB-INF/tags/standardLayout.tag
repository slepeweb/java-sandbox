<%@ tag %><%@ include file="/WEB-INF/jsp/tagDirectives.jsp" %><!DOCTYPE html>

<html>
	<head>
		<mny:head />
	</head>
	<body>

		<!-- Header -->
		<div id="header-wrapper">
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
			<mny:footer />
		</div>
	
	</body>
</html>
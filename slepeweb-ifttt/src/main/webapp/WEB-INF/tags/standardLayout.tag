<%@ tag %><%@ include file="/WEB-INF/jsp/tagDirectives.jsp" %><!DOCTYPE html>

<html>
	<head>
		<ifta:head />
	</head>
	<body>

		<!-- Header -->
		<div id="header-wrapper" class="container">
			<header>
				<ifta:navigation />
			</header>
		</div>
	
		<div id="main-wrapper">
			<div id="main" class="container">
				
				<div class="row">
					<div class="col-1-1">
						<jsp:doBody />
					</div>
				</div>
			</div>
		</div>

		<!-- Footer -->	
		<div id="footer-wrapper">
			<ifta:footer />
		</div>
	
	</body>
</html>
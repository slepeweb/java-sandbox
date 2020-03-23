<%@ tag %><%@ include file="/WEB-INF/jsp/common/tagDirectives.jsp" %><!DOCTYPE html>

<html lang="${_item.language}">
	<head>
		<anc:head />
	</head>
	<body>

		<!-- Header -->
		<div id="header-wrapper">
			<div class="container">
				<h1><a href="/${_item.language}">My Ancestry</a></h1>
				<anc:searchBar />				
				<anc:personBreadcrumbs />
			</div>
		</div>
	
		<div id="main-wrapper">
			<div id="main" class="container">
					<div class="row">
							<jsp:doBody />
					</div>
			</div>
		</div>

		<!-- Footer -->	
		<%--
		<div id="footer-wrapper">
			<sw:footer />
		</div>
		 --%>
	
	</body>
</html>
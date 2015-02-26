<%@ tag %><%@ include file="/WEB-INF/jsp/common/tagDirectives.jsp" %><!DOCTYPE html>

<html>
	<head>
		<ntc:head />
	</head>
	<body>

		<ntc:navigation-top />
	
		<div id="main" class="container">
				<jsp:doBody />
		</div>

		<ntc:footer />
	
	</body>
</html>
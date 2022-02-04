<%@ tag %><%@ 
	attribute name="type" required="true" rtexprvalue="true" %><%@ include 
	file="/WEB-INF/jsp/common/tagDirectives.jsp" %><!DOCTYPE html>

<html lang="${_item.language}">
	<head>
		<pho:head />
	</head>
	
	<body>
		<div class="layout-${type}">
			<pho:header />
			<jsp:doBody />
			<pho:footer />
		</div>
	</body>
</html>
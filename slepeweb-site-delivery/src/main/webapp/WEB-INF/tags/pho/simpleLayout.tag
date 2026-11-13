<%@ tag %><%@ 
	attribute name="type" required="true" rtexprvalue="true" %><%@ include 
	file="/WEB-INF/jsp/tagDirectives.jsp" %><!DOCTYPE html>

<html lang="${_item.language}">
	<head>
		<pho:head />
	</head>
	
	<body>
		<div class="layout-${type}">
			<jsp:doBody />
		</div>
		
		<gen:sessionExpiry />
	</body>
</html>
<%@ tag %><%@ 
	attribute name="type" required="true" rtexprvalue="true" %><%@ include 
	file="/WEB-INF/jsp/tagDirectives.jsp" %><!DOCTYPE html>

<html lang="${_item.language}">
	<head>
		<anc:head />
	</head>
	
	<body>
		<div class="layout-${type}">
			<anc:header />
			<jsp:doBody />
			<gen:footer />
		</div>
	</body>
</html>
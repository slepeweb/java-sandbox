<%@ tag %><%@ 
	attribute name="type" required="true" rtexprvalue="true" %><%@ include 
	file="/WEB-INF/jsp/common/tagDirectives.jsp" %><!DOCTYPE html>

<html lang="${_item.language}">
	<head>
		<geo:head />
	</head>
	
	<body>
		<div class="layout-${type}">
			<geo:header />
			<jsp:doBody />
			<geo:footer />
			<geo:externalImages />
		</div>
	</body>
</html>
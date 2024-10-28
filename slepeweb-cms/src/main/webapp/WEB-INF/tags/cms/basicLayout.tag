<%@ tag %><%@ 
	attribute name="loadjs" required="true" rtexprvalue="true" type="java.lang.Boolean" %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %><!DOCTYPE html>

<html>
	<head>
		<cms:head loadjs="${loadjs}" />
	</head>
	<body>

	<jsp:doBody />
		
	<div id="footer-wrapper">
		<cms:footer />
	</div>

	</body>
</html>
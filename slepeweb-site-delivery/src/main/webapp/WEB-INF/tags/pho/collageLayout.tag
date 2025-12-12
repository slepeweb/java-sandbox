<%@ tag %><%@ include 
	file="/WEB-INF/jsp/tagDirectives.jsp" %><!DOCTYPE html>

<html lang="${_item.language}">
	<head>
		<script src="//ajax.googleapis.com/ajax/libs/jquery/3.7.1/jquery.min.js"></script>
		<link rel="stylesheet" href="/resources/pho/css/collage.css" type="text/css">
	</head>
	
	<body>
		<div class="collage">
			<jsp:doBody />
		</div>
	</body>
</html>
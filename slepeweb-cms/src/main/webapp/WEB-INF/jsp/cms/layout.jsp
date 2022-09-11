<!DOCTYPE html><%@ 
	page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false"%><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>

<html>
	<head>
		<cms:head loadjs="${true}" />
	</head>
	<body>

		<div id="header-wrapper">
			<cms:navigation-top />
		</div>
		
		<div id="main-wrapper">
			<div class="readonly-layer"></div>
			<!-- The body of the next div is populated by an ajax call, so should be empty initially. -->
			<div id="item-editor"></div>
		</div>
	
		<div id="footer-wrapper">
			<cms:footer />
		</div>
	
		<cms:dialogs />
	</body>
</html>
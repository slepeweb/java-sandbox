<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd"><%@ 
	page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false"%><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>

<html>
	<head>
		<cms:head loadjs="${true}" />
	</head>
	<body>

		<!-- Header -->
		<div id="header-wrapper">
			<cms:navigation-top />
		</div>
		
		<!-- Main -->	
		<div id="main-wrapper">
			<!-- Left navigation -->
			<!-- The body of the next div is populated by an ajax call, so should be empty initially. -->
			<div id="leftnav" class="hide"></div>

			<!-- Main content -->	
			<!-- The body of the next div is populated by an ajax call, so should be empty initially. -->
			<div id="item-editor"></div>
		</div>
	
		<!-- Footer -->
	
		<div id="footer-wrapper">
			<cms:footer />
		</div>
	
		<cms:dialogs />
	</body>
</html>
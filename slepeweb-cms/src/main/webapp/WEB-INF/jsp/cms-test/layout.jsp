<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false"%><%@ 
    taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%><%@ 
    taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%><%@ 
    taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%><%@
    taglib uri="http://www.springframework.org/tags" prefix="spring"%><%@ 
    taglib prefix="cms" tagdir="/WEB-INF/tags/cms"%><!DOCTYPE html>

<html>
	<head>
		<cms:head  loadjs="${false}" />
	</head>
	<body>

		<!-- Header -->
		<div id="header-wrapper">
			<cms:navigation-top />
		</div>
	
		<!-- Main -->	
		<div id="main-wrapper" class="subpage">
			<div class="container">
				<div class="row">
					<!-- Main content -->	
					<div class="12u skel-cell-mainContent">	
						<tiles:insertAttribute name="body" />	
					</div>					
				</div>
			</div>
		</div>
	
		<!-- Footer -->
	
		<div id="footer-wrapper">
			<br />
			<cms:footer />
		</div>
	
	</body>
</html>
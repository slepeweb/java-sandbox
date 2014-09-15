<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false"%><%@ 
    taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%><%@ 
    taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%><%@ 
    taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%><%@
    taglib uri="http://www.springframework.org/tags" prefix="spring"%><%@ 
    taglib prefix="sw" tagdir="/WEB-INF/tags/sws"%><!DOCTYPE html>

<!-- jsp/sws/layout-2col-left.jsp -->
<html>
	<head>
		<sw:head />
	</head>
	<body>

		<!-- Header -->
		<div id="header-wrapper">
			<sw:navigation-top />
		</div>
	
		<!-- Main -->	
		<div id="main-wrapper" class="subpage">
			<div class="container">
				<div class="row">
					<!-- Left Sidebar -->
					<div class="3u">
						<tiles:insertAttribute name="left" />
					</div>
					
					<!-- Main content -->	
					<div class="9u skel-cell-mainContent">	
						<tiles:insertAttribute name="body" />	
					</div>					
				</div>
			</div>
		</div>
	
		<!-- Footer -->
	
		<div id="footer-wrapper">
			<sw:footer />
		</div>
	
	</body>
</html>
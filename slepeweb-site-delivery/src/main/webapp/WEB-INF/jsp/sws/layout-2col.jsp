<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false"%><%@ 
    taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%><%@ 
    taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%><%@ 
    taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%><%@
    taglib uri="http://www.springframework.org/tags" prefix="spring"%><%@ 
    taglib prefix="sw" tagdir="/WEB-INF/tags/sws"%><!DOCTYPE html>

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
					<!-- Main content -->	
					<div class="9u skel-cell-mainContent">	
						<article class="first">
							<tiles:insertAttribute name="body" />	
						</article>
					</div>
					
					<!-- Sidebar -->
					<div class="3u">	
						<section>
							<tiles:insertAttribute name="right" />
						</section>
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
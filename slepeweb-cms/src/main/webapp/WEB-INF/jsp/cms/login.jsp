<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false"%><%@ 
    taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%><%@ 
    taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%><%@ 
    taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%><%@
    taglib uri="http://www.springframework.org/tags" prefix="spring"%><%@ 
    taglib prefix="cms" tagdir="/WEB-INF/tags/cms"%>

<html>
	<head>
		<cms:head />
	</head>
	<body>

		<!-- Main -->	
		<div id="main-wrapper">
			<h2>CMS Login</h2>
			
			<c:if test="${not empty error}">
				<div class="underline red"><p>${error}</p></div>
			</c:if>
			<c:if test="${not empty msg}">
				<div class="underline green"><p>${msg}</p></div>
			</c:if>	
			
			<p>Please enter your login details to gain access to the CMS:</p>
			
			<c:if test="${not _isAuthor}">	
				<form id="login" method="post" action="<c:url value="/j_spring_security_check" />">
					<table class="two-col-table">
						<tr>
							<td class="heading"><label for="alias">User name</label></td>
							<td><input name="alias" size="32" /></td>
						</tr>
						<tr>
							<td class="heading"><label for="password">Password</label></td>
							<td><input type="password" name="password" size="32" /></td>
						</tr>
					</table>
					<br />
					<input class="button" type="submit" name="Submit" />
				</form>	
			</c:if>		
		</div>
	
		<!-- Footer -->
	
		<div id="footer-wrapper">
			<br />
			<cms:footer />
		</div>
	
	</body>
</html>	

	
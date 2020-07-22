<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd"><%@ 
	page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false"%><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>

<html>
	<head>
		<cms:head loadjs="${false}"/>
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
					<div>
						<label for="alias">User name: </label><input name="alias" size="32" />
					</div>
					<div>
						<label for="password">Password: </label><input type="password" name="password" size="32" />
					</div>
					<button class="action" type="submit">Login</button>
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

	
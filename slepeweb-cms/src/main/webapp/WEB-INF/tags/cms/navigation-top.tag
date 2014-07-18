<%@ tag %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<header class="container" id="site-header">
	<div class="row">
		<div class="12u">
			<div id="logo">
				<h1><a href="/about">slepe web solutions</a></h1>
			</div>
			<nav id="nav">
				<ul>
<%-- 					<c:forEach items="${_page.header.topNavigation}" var="link"> --%>
<%-- 						<li<c:if test="${link.selected}"> class="current_page_item"</c:if>><a href="${link.href}">${link.title}</a></li> --%>
<%-- 					</c:forEach> --%>
				</ul>
			</nav>
			
<%-- 			<c:if test="${not empty _loggedInUser}"> --%>
<!-- 				<div class="user-info"> -->
<%-- 					<p>Logged in as: ${_loggedInUser.alias} (  --%>
<%-- 						<c:forEach items="${_loggedInUser.roles}" var="role" varStatus="status"> --%>
<%-- 							<c:if test="${not status.first}">, </c:if>${role.name} --%>
<%-- 						</c:forEach> ) --%>
<!-- 					</p> -->
<!-- 				</div> -->
<%-- 			</c:if> --%>
		</div>
	</div>
</header>


<%@ tag %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<header class="container" id="site-header">
	<div class="row">
		<div class="12u">
			<div id="logo">
				<h1><a href="/home">slepe web solutions</a></h1>
			</div>
			<nav id="nav">
				<ul>
					<c:forEach items="${_page.header.topNavigation}" var="link">
						<li<c:if test="${link.selected}"> class="current_page_item"</c:if>><a href="${link.href}">${link.label}</a></li>
					</c:forEach>
				</ul>
			</nav>
		</div>
	</div>
</header>

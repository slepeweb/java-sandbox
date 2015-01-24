<%@ tag %><%@ include file="/WEB-INF/jsp/common/tagDirectives.jsp" %>

<gen:debug><!-- tags/sws/navigation-top.tag --></gen:debug>

<header id="header">
	<div class="container">
		<div class="row">
			<div class="3u">
				<h1><a href="/about">slepe web solutions</a></h1>
			</div>
			<nav class="9u" id="nav">
				<ul class="fr">
					<c:forEach items="${_page.header.topNavigation}" var="link">
						<li<c:if test="${link.selected}"> class="selected"</c:if>><a href="${link.href}">${link.title}</a></li>
					</c:forEach>
				</ul>
				
				<%-- This bit is not cache-friendly 
				<c:if test="${not empty _user}">
					<span class="user-info">Logged in as: ${_user.username}</span>
				</c:if>
				--%>
			</nav>
		</div>
	</div>
</header>

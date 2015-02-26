<%@ tag %><%@ include file="/WEB-INF/jsp/common/tagDirectives.jsp" %>

<gen:debug><!-- tags/ntc/navigation-top.tag --></gen:debug>

<header id="header" class="skel-layers-fixed">
	<div class="container">
		<div class="row">
			<div class="3u">
				<h1><a href="/">Needingworth Tennis Club</a></h1>
			</div>
			<nav class="9u" id="nav">
				<ul>
					<c:forEach items="${_page.header.topNavigation}" var="link">
						<li<c:if test="${link.selected}"> class="selected"</c:if>><a href="${link.href}">${link.title}</a></li>
					</c:forEach>
				</ul>				
			</nav>
		</div>
	</div>
</header>

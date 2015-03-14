<%@ tag %><%@ include file="/WEB-INF/jsp/common/tagDirectives.jsp" %>

<gen:debug><!-- tags/ntc/navigation-top.tag --></gen:debug>

<header id="header" class="skel-layers-fixed">
	<div class="container">
		<div class="row">
			<div class="3u">
				<h1><a href="/">Needingworth Tennis Club</a></h1>
			</div>
			<div class="9u">
				<nav id="nav">
					<ul>
						<c:forEach items="${_page.header.topNavigation}" var="link">
							<li<c:if test="${link.selected}"> class="selected"</c:if>><a href="${link.href}">${link.title}</a>
								<c:if test="${fn:length(link.children) > 0}">
									<ul class="nav-level-2">
										<c:forEach items="${link.children}" var="link2">
											<li<c:if test="${link2.selected}"> class="selected"</c:if>><a href="${link2.href}">${link2.title}</a></li>
										</c:forEach>
									</ul>
								</c:if>
							</li>
						</c:forEach>
					</ul>				
				</nav>
			</div>
		</div>
	</div>
</header>

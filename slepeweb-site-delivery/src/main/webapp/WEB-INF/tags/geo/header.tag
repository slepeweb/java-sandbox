<%@ tag %><%@ include file="/WEB-INF/jsp/common/tagDirectives.jsp" %>

<header>
	<nav class="navbar">
		<div class="brand-title"><a href="/">GeorgieB</a></div>
		<a href="#" class="menu-bars">
			<span class="bar"></span>
			<span class="bar"></span>
			<span class="bar"></span>
		</a>
		
		<div class="navbar">
			<ul>
			
				<c:forEach items="${_page.header.topNavigation}" var="lk0">
					<li>
						<a class="toplevel" href="${lk0.href}">${lk0.title}</a>
						<c:if test="${fn:length(lk0.children) > 0}">
							<div class="submenu-1">
								<c:forEach items="${lk0.children}" var="lk1">
									<div>
										<a href="${lk1.href}">${lk1.title}</a>
										<c:if test="${fn:length(lk1.children) > 0}">
											<div class="submenu-2">
												<c:forEach items="${lk1.children}" var="lk2">
													<a href="${lk2.href}">${lk2.title}</a>
												</c:forEach>
											</div>
										</c:if>
									</div>
								</c:forEach>
							</div>
						</c:if>
					</li>
				</c:forEach>
				
       </ul>
    </div>
	</nav>		
    
	<div id="breadcrumbs">
		<ul>
			<c:set var="first" value="${true}" />
		 	<c:forEach items="${_page.header.breadcrumbs}" var="i">
		 		<c:if test="${i.href ne '/'}">	 		
			 		<li><a href="${i.href}"><c:if test="${not first}"><span><i class="fa-solid fa-arrow-right"></i></span></c:if> ${i.title}</a></li>
					<c:set var="first" value="${false}" />
				</c:if>
		 	</c:forEach>
		</ul>
	</div>
</header>

<%@ tag %><%@ include file="/WEB-INF/jsp/tagDirectives.jsp" %>

<!-- 
	id: 		${_item.id}
	origId:	${_item.origId}
-->
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
    
  <div id="below-nav">
		<div id="breadcrumbs">
			<ul>
				<c:set var="first" value="${true}" />
			 	<c:forEach items="${_page.header.breadcrumbs}" var="i">
			 		<c:if test="${i.href ne '/'}">
				 		<c:if test="${not first}"><li><i class="fa-solid fa-arrow-right"></i></li></c:if> 
				 		<li><a href="${i.href}">${i.title}</a></li>
						<c:set var="first" value="${false}" />
					</c:if>
			 	</c:forEach>
			</ul>
		</div>
		
		<div id="search-input">
			<input value="" placeholder="Enter search terms" />
			<button type="button"><span><i class="fa fa-search" aria-hidden="true"></i></span></button>
		</div>
	</div>
</header>

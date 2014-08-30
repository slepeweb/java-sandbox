<%@ tag %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<header class="container" id="site-header">
	<div class="row">
		<div class="12u">
			<div id="logo">
				<h1>slepe web solutions CMS
				<select id="site-selector">
					<option value="0">Choose site ...</option>
					<c:forEach items="${allSites}" var="_site">
						<option value="${_site.id}"<c:if 
							test="${not empty editingItem and editingItem.site.id eq _site.id}"> selected</c:if>>${_site.name}</option>
					</c:forEach>
				</select></h1>
			</div>
<!-- 			<nav id="nav"> -->
<!-- 				<ul> -->
<%-- 					<c:forEach items="${_page.header.topNavigation}" var="link"> --%>
<%-- 						<li<c:if test="${link.selected}"> class="current_page_item"</c:if>><a href="${link.href}">${link.title}</a></li> --%>
<%-- 					</c:forEach> --%>
<!-- 				</ul> -->
<!-- 			</nav> -->
			
		</div>
	</div>
</header>


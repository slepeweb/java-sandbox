<%@ tag %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<header class="container" id="site-header">
	<div class="row">
		<div class="12u">
			<div id="logo">
				<div class="inline">
					<h1 class="main-heading">slepe web solutions CMS</h1>
					<c:if test="${not empty _user}"><p class="main-heading">You are logged in as  
										'${_user.username}'&nbsp;&nbsp;&nbsp;[<a href="/cms/j_spring_security_logout">Logout</a>]</p>
								</c:if>
				</div>
				<select id="site-selector">
					<option value="0">Choose site ...</option>
					<c:forEach items="${allSites}" var="_site">
						<option value="${_site.id}"<c:if 
							test="${not empty editingItem and editingItem.site.id eq _site.id}"> selected</c:if>>${_site.name}</option>
					</c:forEach>
				</select>
				<div id="status-block" class="inline"></div>
			</div>
			
		</div>
	</div>
</header>


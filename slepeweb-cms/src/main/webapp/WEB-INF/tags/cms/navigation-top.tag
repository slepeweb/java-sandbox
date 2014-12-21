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
				</select><c:if test="${not empty _user}"><span class="user-info">Hello 
					${_user.username}&nbsp;&nbsp;&nbsp;[<a href="/cms/j_spring_security_logout">Logout</a>]</span>
			</c:if></h1>
			</div>
			
		</div>
	</div>
</header>


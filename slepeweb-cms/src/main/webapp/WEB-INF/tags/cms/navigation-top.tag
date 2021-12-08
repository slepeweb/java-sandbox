<%@ tag %><%@ 
	taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%><%@ 
  taglib prefix="cms" tagdir="/WEB-INF/tags/cms"%><%@ 
  taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<header>
		<div class="ha">
			<h1 class="main-heading">slepe web solutions CMS</h1>
			<c:if test="${not empty _user}"><p class="main-heading">You are logged in as  
								'${_user.fullName}'&nbsp;&nbsp;&nbsp;[<a href="/cms/page/login?logout">Logout</a>]</p>
						</c:if>
		</div>
		
		<div class="hb">
			<span>Site selection: </span>
			<select id="site-selector">
				<option value="0">Choose site ...</option>
				<c:forEach items="${_allEditableSites}" var="_site">
					<option value="${_site.id}"<c:if 
						test="${not empty editingItem and editingItem.site.id eq _site.id}"> selected</c:if>>${_site.name}</option>
				</c:forEach>
			</select>
		</div>		
		
		<!-- History -->
		<div id="history-div">
			<span>Breadcrumbs: </span>
			<cms:navigation-history />
		</div>
		
		<cms:searchBar />
		
		<div id="leftnav-hider"><i class="fas fa-bars fa-2x"></i></div>
		
		<div class="hf">
			<p id="currently-editing" class="current-item-name"></p>						
			<p id="status-block"></p>
		</div>
</header>


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
		
		<!-- History -->
		<div id="history-div">
			<span>Breadcrumbs: </span>
			<cms:navigation-history />
			
			<!-- Item flagging controls -->
			<div id="misc-controls">
				<!-- Flag the current item -->
				<div id="item-flag">
					<i class="fa-solid fa-flag item-flag <c:if test='${_itemIsFlagged}'>flagged</c:if>" title="Flag this item"></i>
				</div>
				
				<!-- Flag all sibling items -->
				<div id="item-sibling-flag">
					<i class="fa-solid fa-flag" title="Flag this and ALL sibling items"></i>
					<div>
						<i class="fa-solid fa-flag"></i>
					</div>
				</div>
				
				<!-- Clear all item flags -->
				<div id="item-flag-clear">
					<i class="fa-solid fa-flag" title="Clear all item flags"></i>
					<div title="Clear all item flags">
						<i class="fa-solid fa-xmark"></i>
					</div>
				</div>
				
				<!-- Show list of flagged items -->
				<div id="item-flag-show">
					<i class="fa-solid fa-eye" title="Show list of flagged items"></i>
				</div>
			</div>			
		</div>
		
		<!-- Search bar - includes undo/redo and trash controls -->
		<cms:searchBar />	
		
		<div id="leftnav-hider">
			<i></i>
			<i class="fa-solid fa-angle-up fa-2x"></i>
			<i></i>
			<i class="fa-solid fa-angle-left fa-2x"></i>
			<i class="fa-solid fa-sitemap fa-2x"></i>
			<i class="fa-solid fa-angle-right fa-2x"></i>
			<i></i>
			<i class="fa-solid fa-angle-down fa-2x"></i>
			<i></i>
		</div>
		
		<div class="hf">
			<p id="currently-editing" class="current-item-name"></p>						
			<p id="status-block"></p>
		</div>
</header>


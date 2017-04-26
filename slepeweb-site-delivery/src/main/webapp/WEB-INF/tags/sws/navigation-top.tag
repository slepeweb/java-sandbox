<%@ tag %><%@ include file="/WEB-INF/jsp/common/tagDirectives.jsp" %>

<gen:debug><!-- tags/sws/navigation-top.tag --></gen:debug>

<header id="header">
	<div class="container">
		<div class="row">
			<div class="left">
				<h1><a href="/about">slepe web </a></h1>
			</div>
			<div class="right nav anchor">
				<ul id="primary-nav">
					<c:forEach items="${_page.header.topNavigation}" var="link">
						<li<c:if test="${link.selected}"> class="selected"</c:if>><a href="${link.href}">${link.title}</a></li>
					</c:forEach>
					<li><i class="fa fa-search" aria-hidden="true"></i></li>
				</ul>
				<div id="tiny-nav"><i class="fa fa-bars fa-2x"></i></div>
				<div id="search-bar">
					<form method="post" action="/search-results">
						<input type="submit" value="Go" />
						<input type="text" name="searchText" 
							placeholder="Enter search terms here" 
							value="${_searchResults.params.searchText}" />
					</form>
				</div>
			</div>
		</div>
	</div>
</header>

<script>
$(function() {
	$.ajax({
		url : "/ws/login/user",
		dataType : "json",
		cache : false
	}).done(function(resp) {
		if (resp && resp.username) {
			$("#primary-nav a").each(function(){
				var link = $(this);
				if (link.text() == "Login") {
					link.attr("href", "/j_spring_security_logout");
					link.text("Logout");					
				}
			});
		}
	}).fail(function(jqXHR, status) {
		//console.log(status);
	});		
	
	$(".fa-search").click(function() {
		var visibility = $("#search-bar").css("visibility");
		visibility == 'visible' ? visibility = 'hidden' : visibility = 'visible';
		$("#search-bar").css("visibility", visibility);
	});
});
</script>
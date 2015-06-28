<%@ tag %><%@ include file="/WEB-INF/jsp/common/tagDirectives.jsp" %>

<gen:debug><!-- tags/sws/navigation-top.tag --></gen:debug>

<header id="header">
	<div class="container">
		<div class="row">
			<div class="3u">
				<h1><a href="/about">slepe web solutions</a></h1>
			</div>
			<nav class="9u" id="nav">
				<ul class="fr">
					<c:forEach items="${_page.header.topNavigation}" var="link">
						<li<c:if test="${link.selected}"> class="selected"</c:if>><a class="topnav" href="${link.href}">${link.title}</a></li>
					</c:forEach>
				</ul>
			</nav>
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
			$("a.topnav").each(function(){
				var ele = $(this);
				if (ele.text() == "Login") {
					ele.attr("href", "/j_spring_security_logout");
					ele.text("Logout");
				}
			});
		}
	}).fail(function(jqXHR, status) {
		//console.log(status);
	});		
	
	$("#header").css("visibility", "visible");
});
</script>
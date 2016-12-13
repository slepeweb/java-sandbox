<%@ tag %><%@ include file="/WEB-INF/jsp/common/tagDirectives.jsp" %>

<gen:debug><!-- tags/sws/navigation-top.tag --></gen:debug>

<header id="header">
	<div class="container">
		<div class="row">
			<div class="left">
				<h1><a href="/about">slepe web solutions</a></h1>
			</div>
			<div class="right nav anchor">
				<ul id="primary-nav">
					<c:forEach items="${_page.header.topNavigation}" var="link">
						<li<c:if test="${link.selected}"> class="selected"</c:if>><a href="${link.href}">${link.title}</a></li>
					</c:forEach>
				</ul>
				<div id="tiny-nav"><i class="fa fa-bars fa-2x"></i></div>
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
			$("a.topnav").each(function(){
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
	
	//$("#header").css("visibility", "visible");
});
</script>
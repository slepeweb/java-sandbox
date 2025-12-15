<%@ tag %><%@ 
	taglib uri="jakarta.tags.core" prefix="c"%><%@ 
  taglib prefix="cms" tagdir="/WEB-INF/tags/cms"%><%@ 
  taglib prefix="fn" uri="jakarta.tags.functions" %>

<div class="ha">
	<h1 class="main-heading">slepe web solutions CMS</h1>
	
	<c:if test="${not empty _user}">
		<div id="user-corner">
			<p id="user-icon">
				<i class="fa-regular fa-user"></i> Welcome ${_user.firstName}
			<p>
				
			<div id="user-menu" class="hidden">
				<ul>
					<li><a href="/cms_/user/update/form/${_user.id}">&gt; Update account</a></li>
					<li><a href="/cms_/page/login?logout">&gt; Logout</a></li>
				</ul>
			</div>
		</div>
	</c:if>
</div>

<script>
	$(function() {
		// User menu
		$('div#user-corner i').click(function() {
			$('div#user-menu').removeClass('hidden');
		})

		$('div#user-menu').mouseleave(function() {
			$('div#user-menu').addClass('hidden');
		})
	})
</script>
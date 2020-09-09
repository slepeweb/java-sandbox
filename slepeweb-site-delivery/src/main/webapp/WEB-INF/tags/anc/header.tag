<%@ tag %><%@ include file="/WEB-INF/jsp/common/tagDirectives.jsp" %>

<c:set var="_userLoggedIn" value="${not empty _user and _user.loggedIn}" />

<header>
	<div id="site-identifier">
		<h1><a href="/${_item.language}">My Ancestry</a></h1>
		<div class="user-block">
			<i class="far fa-user fa-2x"></i>
			
			<div id="user-menu" class="hide">
				<c:if test="${_userLoggedIn}"><p>${_user.firstName} is logged in</p></c:if>
				<ul>
					<c:if test="${not _userLoggedIn}">
						<li><a href="/${_item.language}/login">Login</a></li>
						<li><a href="/${_item.language}/login/register?view=form">Register</a></li>
						<li><a href="/${_item.language}/login/forgotten?view=forgotten">Forgotten password</a></li>
					</c:if>					
					
					<c:if test="${_userLoggedIn}">
						<li><a href="/${_item.language}/login/changepassword">Change password</a></li>
						<li><a href="/${_item.language}/login/profile">Update profile</a></li>
						<li><a href="/${_item.language}/login?logout">Logout</a></li>
					</c:if>
				</ul>
			</div>
		</div>
	</div>	
	
	<anc:languageSwitcher />
	<anc:searchBar />	
	<anc:personBreadcrumbs />
</header>

<script>
	$(function(){
		$("#history-selector").change(function(){
			var path = $(this).val();
			if (path != 'unset') {
				window.location = path;
			}
		});
		
		$(".user-block i").click(function() {
			var menu = $("#user-menu");
			if (menu.hasClass("hide")) {
				menu.removeClass("hide");
			}
			else {
				menu.addClass("hide");
			}
		});
		
		$("#user-menu").mouseleave(function() {
			$(this).addClass("hide");
		});

	});
</script>
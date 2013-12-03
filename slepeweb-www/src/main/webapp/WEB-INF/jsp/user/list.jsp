<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
 
<script>
$(document).ready(function(){
 	$(".delete-user-link").colorbox({
 		inline: true,
 		width: "400px",
 		opacity:0.5,
 		onOpen: function() {
 			var parts = $(this).attr("rel").split("|");
 			$("#delete-user-alert strong").html("Are you sure you want to delete this user [" + parts[0] + "]?");
 			$("#delete-user-alert button").click(function() {
 				window.location = "/sandbox/user/delete/" + parts[1];
 			});
 		}
 	});
});
</script>

<article class="first">
	<h2>User listing</h2>
	<jsp:include page="./flash-messages.jsp" />

	<c:choose><c:when test="${not empty userList}">
		<table class="two-col-table user-table compact">
		<c:forEach items="${userList}" var="user">
		    <tr>
		        <td>${user.alias}</td>
		        <td>${user}</td>
						<td><a href="/sandbox/user/update/${user.id}"><img
								src="/resources/images/pencil-icon.jpg"
								alt="Update ${user.alias}"
								title="Update ${user.alias}"></a></td>
						<td><a class="delete-user-link" href="#delete-user-alert"
							rel="${user.alias}|${user.id}"><img
								src="/resources/images/delete-icon.jpg"
								alt="Delete ${user.alias}"
								title="Delete ${user.alias}"></a></td>
					</tr>
		</c:forEach>
		</table>
	</c:when><c:otherwise>
		<p>No users in the database.</p>
	</c:otherwise></c:choose>
	
</article>

<div style="display: none">
	<div id="delete-user-alert">
		<p class="compact">
			<strong>Template</strong><br />
		</p>
		<button type="button" class="button">Yes, I'm, sure</button>
	</div>
</div>

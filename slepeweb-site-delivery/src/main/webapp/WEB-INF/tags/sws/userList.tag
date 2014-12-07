<%@ tag %><%@ include file="/WEB-INF/jsp/common/tagDirectives.jsp" %>

<gen:debug><!-- tags/sws/userList.tag --></gen:debug>

<article class="first">
	<h2>${_item.fields.title}</h2>
	<jsp:include page="/WEB-INF/jsp/sws/template/user/flash-messages.jsp" />

	<c:choose><c:when test="${not empty userList}">
		<table class="two-col-table user-table compact">
		<c:forEach items="${userList}" var="user">
		    <tr>
		        <td>${user.alias}</td>
		        <td>${user}</td>
						<td><a href="/sandbox/hibernate/form?userId=${user.id}"><img
								src="/resources/sws/images/pencil-icon.jpg"
								alt="Update ${user.alias}"
								title="Update ${user.alias}"></a></td>
						<td><a class="delete-user-link" href="#delete-user-alert"
							rel="${user.alias}|${user.id}"><img
								src="/resources/sws/images/delete-icon.jpg"
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
 				window.location = "/spring/user/del/" + parts[1];
 			});
 		}
 	});
});
</script>

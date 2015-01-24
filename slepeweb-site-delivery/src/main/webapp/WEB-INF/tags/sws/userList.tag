<%@ tag %><%@ include file="/WEB-INF/jsp/common/tagDirectives.jsp" %>

<gen:debug><!-- tags/sws/userList.tag --></gen:debug>

<h2>${_item.fields.title}</h2>
<sw:flash-messages />

<c:choose><c:when test="${not empty userList}">
	<table class="alt">
	<c:forEach items="${userList}" var="user">
	    <tr>
	        <td>${user.alias}</td>
	        <td>${user}</td>
					<td><a href="/sandbox/hibernate/form?userId=${user.id}" 
						title="Update ${user.alias}"><i class="fa fa-pencil"></i></a></td>
					<td><a class="delete-user-link" href="#delete-user-alert"
						rel="${user.alias}|${user.id}" title="Delete ${user.alias}"><i class="fa fa-trash"></i></a></td>
				</tr>
	</c:forEach>
	</table>
</c:when><c:otherwise>
	<p>No users in the database.</p>
</c:otherwise></c:choose>
	

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

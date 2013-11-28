<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
  
<article class="first">
	<h2>User listing</h2>

	<c:choose><c:when test="${not empty userList}">
		<table class="two-col-table user-table compact">
		<c:forEach items="${userList}" var="user">
		    <tr>
		        <td>${user.alias}</td>
		        <td>${user}</td>
		        <td><a href="/user/update/${user.id}">Update</a></td>
		        <td><a href="/user/delete/${user.id}">Delete</a></td>
		    </tr>
		</c:forEach>
		</table>
	</c:when><c:otherwise>
		<p>No users in database.</p>
	</c:otherwise></c:choose>
	
</article>

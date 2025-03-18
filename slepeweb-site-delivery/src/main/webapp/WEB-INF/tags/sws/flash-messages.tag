<%@ tag %><%@ include file="/WEB-INF/jsp/tagDirectives.jsp" %>
  
<c:choose><c:when test="${empty _flashMsg and empty _flashError}">
	<c:if test="${not _isGuest and not _isAdmin}">
		<p class="flash-msg orange compact">Please note: You can modify form data, but you do not have sufficient privileges to update the database.</p>
	</c:if>
</c:when><c:when test="${not empty _flashMsg}">
	<p class="flash-msg green">${_flashMsg}</p>
</c:when><c:when test="${not empty _flashError}">
	<p class="flash-msg red">${_flashError}</p>
</c:when></c:choose>

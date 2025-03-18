<%@ 
	include file="/WEB-INF/jsp/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>
	
<c:set var="_extraInpageCss" scope="request">
</c:set>

<anc:pageLayout type="std">
	<gen:debug><!-- jsp/anc/register/approve.jsp --></gen:debug>
	
	<div class="main">
		<c:if test="${not empty _error}">
			<div class="error-message"><p>${_error}</p></div>
		</c:if>
		
		<h2>Registration approval</h2>
		<p>${_message}</p>
	</div>
	
</anc:pageLayout>
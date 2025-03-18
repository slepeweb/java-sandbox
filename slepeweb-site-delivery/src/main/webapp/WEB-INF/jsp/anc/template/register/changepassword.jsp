<%@ 
	include file="/WEB-INF/jsp/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>
	
<c:set var="_extraInpageJs" scope="request">
	<anc:simpleDialogScript />
</c:set>

<anc:pageLayout type="std">
	<gen:debug><!-- jsp/anc/register/password.jsp --></gen:debug>
	<anc:passwordForm showFormIf="${_block ne 'processComplete'}" context="change" />
	
	<c:if test="${_block eq 'processComplete'}">
		<a href="/${_item.language}/login">Login</a>
	</c:if>
</anc:pageLayout>


<%@ 
	include file="/WEB-INF/jsp/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>
	
<c:set var="_extraInpageJs" scope="request">
	<anc:simpleDialogScript />
</c:set>

<anc:pageLayout type="std">
	<gen:debug><!-- jsp/anc/register/userform.jsp --></gen:debug>
	<anc:userForm showFormIf="${_block ne 'interimStatusMessage'}" context="register" />
</anc:pageLayout>

<%@ 
	include file="/WEB-INF/jsp/common/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/common/tagDirectives.jsp" %>
	
<c:set var="_extraInpageJs" scope="request">
	<anc:simpleDialogScript />
</c:set>

<anc:pageLayout type="std">
	<gen:debug><!-- jsp/anc/register/password.jsp --></gen:debug>
	<anc:passwordForm 
		showFormIf="${empty _error_2 and (pageContext.request.method eq 'GET' or not empty _error_1)}"
		context="register" />
</anc:pageLayout>


<%@ 
	include file="/WEB-INF/jsp/common/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/common/tagDirectives.jsp" %>
	
<c:set var="_extraInpageJs" scope="request">
	<anc:simpleDialogScript />
</c:set>

<anc:pageLayout type="std">
	<gen:debug><!-- jsp/anc/forgotten/passwordform.jsp --></gen:debug>
	<anc:passwordForm showFormIf="${empty _error_1}" context="forgotten" />
</anc:pageLayout>

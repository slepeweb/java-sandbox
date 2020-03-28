<%@ 
	include file="/WEB-INF/jsp/common/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/common/tagDirectives.jsp" %>
		
<c:set var="_extraInpageCss" scope="request">
	<anc:personMenuStyle/>
	<anc:personSubMenuStyle/>
</c:set>

<anc:pageLayout type="leftmenu">
	<gen:debug><!-- jsp/anc/personHistory.jsp --></gen:debug>
	
	<div class="side-wrapper">
		<anc:personSubMenu />
	</div>
	
	<div class="main-wrapper">
		<anc:personMenu />
		<h2>${_item.fields.heading}</h2>
		${site:resolveMarkupFieldValue(_item, 'bodytext', '')}
	</div>
	
</anc:pageLayout>
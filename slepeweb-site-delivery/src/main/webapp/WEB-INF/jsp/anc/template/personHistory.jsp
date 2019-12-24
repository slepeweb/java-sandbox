<%@ 
	include file="/WEB-INF/jsp/common/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/common/tagDirectives.jsp" %>
		
<c:set var="_extraInpageCss" scope="request">
	<anc:personMenuStyle/>
	<anc:personSubMenuStyle/>
</c:set>

<anc:standardLayout>
	<gen:debug><!-- jsp/anc/personHistory.jsp --></gen:debug>
	
	<div class="col-3-4 primary-col pull-right-sm">
		<anc:personMenu />
		<h2>${_item.fields.heading}</h2>
		${site:resolveMarkupFieldValue(_item, 'bodytext', '')}
	</div>
	
	<div class="col-1-4 primary-col">
		<anc:personSubMenu />
	</div>
	
</anc:standardLayout>
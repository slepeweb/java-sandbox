<%@ 
	include file="/WEB-INF/jsp/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>
		
<anc:pageLayout type="std">
	<gen:debug><!-- jsp/anc/error.jsp --></gen:debug>
	
	<div class="main">	
		<h2>${_item.fields.heading}</h2>
		${site:resolveMarkupFieldValue(_item, 'bodytext', '')}
	</div>
			
</anc:pageLayout>
<%@ 
	include file="/WEB-INF/jsp/common/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/common/tagDirectives.jsp" %>
		
<anc:standardLayout>
	<gen:debug><!-- jsp/anc/error.jsp --></gen:debug>
	
	<div class="col-1-3 primary-col">	
		<h2>${_item.fields.heading}</h2>
		${site:resolveMarkupFieldValue(_item, 'bodytext', '')}
	</div>
			
</anc:standardLayout>
<%@ 
	include file="/WEB-INF/jsp/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>

<geo:pageLayout type="std">
	<gen:debug><!-- jsp/geo/notfound.jsp --></gen:debug>
		
	<div class="main notfound">
		<h2>${_item.fields.title}</h2>
		<p>${_item.fields.bodytext}</p>
	</div>
</geo:pageLayout>
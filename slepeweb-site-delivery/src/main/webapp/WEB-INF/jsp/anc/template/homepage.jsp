<%@ 
	include file="/WEB-INF/jsp/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>
		
<anc:pageLayout type="std">
	<gen:debug><!-- jsp/anc/homepage.jsp --></gen:debug>
	
	<div class="main">
		<h2>${_item.fields.title}</h2>
		${site:resolveMarkupFieldValue(_item, 'bodytext', '')}
		
		<ul>
			<c:forEach items="${_rootEntries}" var="child">
				<li><a href="${child.item.url}">${child.fullName}</a></li>
			</c:forEach>
		</ul>
		
	</div>
	
</anc:pageLayout>
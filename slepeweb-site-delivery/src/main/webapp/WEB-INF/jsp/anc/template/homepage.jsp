<%@ 
	include file="/WEB-INF/jsp/common/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/common/tagDirectives.jsp" %>
		
<anc:standardLayout>
	<gen:debug><!-- jsp/anc/homepage.jsp --></gen:debug>
	
	<div class="col-1-3 primary-col">	
	</div>
		
	<div class="col-2-3 primary-col">
		<h2>${_item.fields.title}</h2>
		${site:resolveMarkupFieldValue(_item, 'bodytext', '')}
		
		<ul>
			<c:forEach items="${_rootEntries}" var="child">
				<li><a href="${child.item.url}">${child.fullName}</a></li>
			</c:forEach>
		</ul>
		
	</div>
	
</anc:standardLayout>
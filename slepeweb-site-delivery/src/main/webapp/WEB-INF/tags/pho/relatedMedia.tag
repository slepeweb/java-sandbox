<%@ tag %><%@ 
	include file="/WEB-INF/jsp/common/tagDirectives.jsp" %><%@ 
	attribute name="items" required="true" rtexprvalue="true" type="java.util.List" %>

<gen:debug><!-- tags/pho/relatedMedia.tag --></gen:debug>

<c:if test="${not empty items}">
	<div class="related-media hide">
		<ul>
			<c:forEach items="${items}" var="_doc">
				<li data-path="${_doc.path}">${_doc.title}</li>
			</c:forEach>
		</ul>
	</div>
</c:if>
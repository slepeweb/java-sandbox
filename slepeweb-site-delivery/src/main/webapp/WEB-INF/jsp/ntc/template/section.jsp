<%@ include file="/WEB-INF/jsp/common/tagDirectives.jsp" %>
	
<ntc:standardLayout>
	<gen:debug><!-- jsp/ntc/section.jsp --></gen:debug>

	<div class="row uniform">
		<div class="7u 12u(3)">
			<h2>${_item.fields.title}</h2>
			<p>${_item.fields.bodytext}</p>
								
			<c:if test="${empty _item.boundItems}"><p>Nothing in this section right now.</p></c:if>

			<c:forEach items="${_item.boundItems}" var="_child">
					<div class="row index">
						<div class="3u">
							<c:set var="_thumb" value="${_child.thumbnail}" />
							<c:if test="${empty _thumb}"><c:set 
								var="_thumb" value="${_defaultThumb}" /></c:if>
								
							<img src="${_thumb.path}" style="max-width: ${_thumb.fields.maxwidth}px" />
						</div>
						<div class="9u">
							<h3><a href="${_child.path}">${_child.fields.title}</a></h3>
							<p>${_child.fields.teaser}</p>
						</div>
					</div>
			</c:forEach>
		</div>
		
		<div class="1u 0u(3)"></div>
		
		<div class="4u 12u(3)">	
			<site:insertComponents site="${_item.site.shortname}" list="${_page.rightSidebar.components}" />
		</div>
	</div>
	
</ntc:standardLayout>
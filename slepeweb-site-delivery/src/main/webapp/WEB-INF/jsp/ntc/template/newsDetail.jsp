<%@ include file="/WEB-INF/jsp/common/tagDirectives.jsp" %>
	
<ntc:standardLayout>
	<gen:debug><!-- jsp/ntc/newsDetail.jsp --></gen:debug>

	<div class="row uniform">
		
		<div class="4u 6u(2) 12u(3)">	
				<div><img src="${_item.image.path}" alt="${_item.image.fields.title}" 
					style="width: 100%; max-width: ${_item.image.fields.maxwidth}px" /></div>				
		</div>
		
		<div class="5u 6u(2)$ 12u(3)">
			<h2>${_item.fields.title}</h2>
			<p>${_item.fields.bodytext}</p>
			<p><a href="${_item.parent.path}">Back to index</a></p>
		</div>
		
		<div class="3u 6u(2) 12u(3)">
			<div class="raised-box">
			<h3>Other news &amp; events</h3>
			<ul>
				<c:if test="${_siblingPager.start gt 0}"><li>... </li></c:if>
				<c:forEach items="${_siblingPager.list}" var="_sibling" begin="${_siblingPager.start}" 
					end="${_siblingPager.end}" varStatus="_status">
						<c:if test="${_siblingPager.current ne (_status.count - 1)}"><li><a 
							href="${_sibling.path}">${_sibling.fields.title}</a></li></c:if>
				</c:forEach>
				<c:if test="${_siblingPager.total gt _siblingPager.end}"><li>... </li></c:if>
			</ul>
			</div>
		</div>
	</div>
	
</ntc:standardLayout>
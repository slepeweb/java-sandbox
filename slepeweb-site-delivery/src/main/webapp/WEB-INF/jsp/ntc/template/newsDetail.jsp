<%@ include file="/WEB-INF/jsp/common/tagDirectives.jsp" %>
	
<ntc:standardLayout>
	<gen:debug><!-- jsp/ntc/newsDetail.jsp --></gen:debug>

	<div class="row uniform">
		<!-- 
		<div class="2u">	
			&nbsp;	
		</div>
		 -->
		
		<div class="4u 12u(3)">	
			<c:if test="${not empty _item.image}">
				<div><img src="${_item.image.path}" alt="${_item.image.fields.title}" 
					style="width: 100%; max-width: ${_item.image.fields.maxwidth}px" /></div>				
			</c:if>
		</div>
		<div class="6u">
			<h2>${_item.fields.title}</h2>
			<p>${_item.fields.bodytext}</p>
			<p><a href="${_item.parent.path}">Back to index</a></p>
		</div>
	</div>
	
</ntc:standardLayout>
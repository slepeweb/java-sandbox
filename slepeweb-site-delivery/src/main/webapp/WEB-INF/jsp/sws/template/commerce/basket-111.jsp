<%@ 
	include file="/WEB-INF/jsp/common/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/common/tagDirectives.jsp" %>
	
<sw:standardLayout>
	<gen:debug><!-- jsp/sws/commerce/basket-111.jsp --></gen:debug>
	
	<div class="col-2-3 pull-right-sm">	
		<div>
			<!-- Main content -->	
			<div class="col-2-3 primary-col">
				<h2>${_page.title}</h2>
				${_page.body}
				
				<c:if test="${_basket.notEmpty}">
					<table>
						<tr>
							<th></th><th>Item</th><th>Price</th><th>Quantity</th><th>Total</th>
							<c:forEach items="${_basket.items}" var="_oi">
								<td><img src="${_oi.item.thumbnail.path}" /></td>
							</c:forEach>
						</tr>
					</table>
				</c:if>
				
			</div>
			
			<!-- Right sidebar -->
			<div class="col-1-3 primary-col">	
				<c:if test="${_item.alphaAxisId > -1}">
					<label>${_item.alphaAxis.label}</label>
					<select id="alphaaxis-options">
						<option value="-1">Choose ...</option>
						<c:forEach items="${_item.alphaAxisValues.options}" var="_av">
							<option value="${_av.value}">${_av.body}</option>
						</c:forEach>
					</select>
					
					<c:if test="${_item.betaAxisId > -1}">
						<label>${_item.betaAxis.label}</label>
						<select id="betaaxis-options">
							<option value="-1">Choose ...</option>
						</select>
					</c:if>
				</c:if>	
				
				<div id="add2basket">
					<button type="button">Add to basket</button>	
					<span></span>		
				</div>
			</div>
		</div>
	</div>
	
	<!-- Left Sidebar -->
	<div class="col-1-3 primary-col">
			<c:if test="${not empty _item.image}">
				<c:set var="_hifiImage" value="${site:getMatchingHifiImage(_item, _item.image)}" />
				<div>
					<c:choose><c:when test="${empty _hifiImage}">
						<img class="main-image" src="${_item.image.path}" /> 
					</c:when><c:otherwise> 
						<img class="main-image zoom" src="${_item.image.path}" data-magnify-src="${_hifiImage.path}" />
					</c:otherwise></c:choose>
				</div>
				
				<c:if test="${not empty _item.imageCarousel and fn:length(_item.imageCarousel) gt 1}">
					<div>
						<c:forEach items="${_item.imageCarousel}" var="_image" varStatus="_stat">
							<img class="thumbnail<c:if test="${_stat.first}"> border</c:if>" src="${_image.path}?height=100" />
						</c:forEach>
					</div>
				</c:if>
			</c:if>
	</div>

</sw:standardLayout>
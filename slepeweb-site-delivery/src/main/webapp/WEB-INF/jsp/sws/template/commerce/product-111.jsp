<%@ 
	include file="/WEB-INF/jsp/common/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/common/tagDirectives.jsp" %>
	
<c:set var="_extraJs" scope="request" value="/resources/sws/js/commerce.js,/resources/js/jquery.magnify.js" />
<c:set var="_extraCss" scope="request" value="/resources/css/magnify.css" />

<script type="text/javascript">
	var _itemKey = "${_item.id}";
	var _zoomer;
</script>

<sw:standardLayout>
	<gen:debug><!-- jsp/sws/commerce/product-111.jsp --></gen:debug>
	
	<c:if test="${_serverConfig.commerceEnabled}">
		<div class="col-2-3 pull-right-sm">	
			<div>
				<!-- Main content -->	
				<div class="col-2-3 primary-col">
					<h2>${_page.title}</h2>
					${site:resolveMarkupFieldValue(_item, "brief", "")}
					
					<c:if test="${not empty _item.fields.description or not empty _item.fields.details}">
						<div id="accordion">
							<c:if test="${not empty _item.fields.description}">
								<h3>Description</h3>
								${site:resolveMarkupFieldValue(_item, "description", "")}
							</c:if>
							<c:if test="${not empty _item.fields.details}">
								<h3>Details</h3>
								${site:resolveMarkupFieldValue(_item, "details", "")}
							</c:if>
						</div>
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
					<div id="show-basket">
						<a href="/storefront/basket">Show basket</a>	
					</div>
				</div>
			</div>
		</div>
		
		<!-- Left Sidebar -->
		<div class="col-1-3 primary-col">
				<c:if test="${fn:length(_item.carouselImages) gt 0}">
					<c:set var="_hifiImage" value="${site:getMatchingHifiImage(_item, _item.carouselImages[0])}" />
					<div>
						<c:choose><c:when test="${empty _hifiImage}">
							<img class="main-image" src="${_item.carouselImages[0].path}" /> 
						</c:when><c:otherwise> 
							<img class="main-image zoom" src="${_item.carouselImages[0].path}" data-magnify-src="${_hifiImage.path}" />
						</c:otherwise></c:choose>
					</div>
					
					<c:if test="${fn:length(_item.carouselImages) gt 1}">
						<div>
							<c:forEach items="${_item.carouselImages}" var="_image" varStatus="_stat">
								<img class="thumbnail<c:if test="${_stat.first}"> border</c:if>" src="${_image.path}?height=100" />
							</c:forEach>
						</div>
					</c:if>
				</c:if>
		</div>
	</c:if>

</sw:standardLayout>
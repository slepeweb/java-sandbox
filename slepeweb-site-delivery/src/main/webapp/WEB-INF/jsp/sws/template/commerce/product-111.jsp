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
	
	<div class="col-3-4 pull-right-sm">	
		<div>
			<!-- Main content -->	
			<div class="col-2-3 primary-col">
				<sw:standardBody />	
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
				
				<site:insertComponents site="${_item.site.shortname}" list="${_page.components}" /> 
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
				
				<site:insertComponents site="${_item.site.shortname}" list="${_page.rightSidebar.components}" /> 
			</div>
		</div>
	</div>
	
	<!-- Left Sidebar -->
	<div class="col-1-4 primary-col">
		<site:insertComponents site="${_item.site.shortname}" list="${_page.leftSidebar.components}" /> 
	</div>

</sw:standardLayout>
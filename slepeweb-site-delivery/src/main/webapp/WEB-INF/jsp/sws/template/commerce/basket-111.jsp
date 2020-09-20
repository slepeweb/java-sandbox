<%@ 
	include file="/WEB-INF/jsp/common/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/common/tagDirectives.jsp" %>
	
<%-- TODO: de-couple magnify.js from commerce.js --%>
<c:set var="_extraJs" scope="request" value="/resources/sws/js/commerce.js,/resources/js/jquery.magnify.js" />

<sw:standardLayout>
	<gen:debug><!-- jsp/sws/commerce/basket-111.jsp --></gen:debug>
	
	<c:if test="${_cmsService.commerceEnabled}">
		<div class="col-1-1">	
			<h2>${_page.title}</h2>
			${_page.body}
			
			<%-- Basket is added by ajax call - see commerce.js --%>
			<div id="basket">
			</div>
			
		</div>		
		
		<script type="text/javascript">
		$(function() {_updateBasket();});
		</script>
	</c:if>
	
</sw:standardLayout>
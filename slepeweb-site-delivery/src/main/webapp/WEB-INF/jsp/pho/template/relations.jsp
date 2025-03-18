<%@ 
	include file="/WEB-INF/jsp/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>
		
<c:set var="_extraCss" scope="request">
	/resources/pho/css/gallery.css
</c:set>

<c:set var="_extraJs" scope="request">
	/resources/pho/js/gallery.js
</c:set>

<pho:pageLayout type="std">
	<gen:debug><!-- jsp/pho/template/relations.jsp --></gen:debug>
	
	<div class="main search">	
		<h2>Related media</h2>
	
		<c:choose><c:when test="${fn:length(_search.results) > 0}">
			<pho:searchResults urlPrefix="${_item.url}" />			
		</c:when><c:otherwise>
			<p>No related media for this item.</p>
		</c:otherwise></c:choose>
	</div>
			
</pho:pageLayout>

<script>
	$(function() {
		displayMedia(0)
		openModal()
	})
</script>
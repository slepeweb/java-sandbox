<%@ 
	include file="/WEB-INF/jsp/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %>
		
<c:set var="_extraCss" scope="request">
	/resources/pho/css/gallery.css
</c:set>

<c:set var="_extraJs" scope="request">
	/resources/pho/js/gallery.js
</c:set>

<pho:simpleLayout type="restricted">
	<gen:debug><!-- jsp/pho/template/bindings.jsp --></gen:debug>
	
	<div class="main search">	
		<c:choose><c:when test="${fn:length(_search.results) > 0}">
			<pho:searchResults urlPrefix="${_item.url}" privateCollection="${true}" />			
		</c:when><c:otherwise>
			<p>No media here.</p>
		</c:otherwise></c:choose>
	</div>
			
</pho:simpleLayout>

<script>
	$(function() {
		displayMedia(0)
		openModal()
	})
</script>
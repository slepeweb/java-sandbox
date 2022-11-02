<%@ 
	include file="/WEB-INF/jsp/common/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/common/tagDirectives.jsp" %>
		
<c:set var="_extraCss" scope="request">
	/resources/pho/css/gallery.css
</c:set>

<c:set var="_extraJs" scope="request">
	/resources/pho/js/gallery.js
</c:set>

<pho:pageLayout type="std">
	<gen:debug><!-- jsp/pho/template/search.jsp --></gen:debug>
	
	<div class="main search">	
		<h2>${_item.fields.title}</h2>
	
		<c:choose><c:when test="${fn:length(_search.results) > 0}">
			<pho:searchResults urlPrefix="${_item.url}" />			
		</c:when><c:otherwise>
			<p>No results found for your search terms &quot;${_params.searchText}&quot;. 
			<br /> 
			<a href="/">Please try again <i class="fa-solid fa-magnifying-glass"></i></a></p>
		</c:otherwise></c:choose>
	</div>
			
</pho:pageLayout>

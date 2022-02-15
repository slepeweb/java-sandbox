<%@ tag %><%@ 
	include file="/WEB-INF/jsp/common/tagDirectives.jsp" %><%@ 
	attribute name="urlPrefix" required="true" rtexprvalue="true" %>

<gen:debug><!-- tags/pho/searchResults.tag --></gen:debug>

<p>
	<strong>Found ${_search.totalHits} results for your search terms &quot;${_params.searchText}&quot;</strong>
	
	<c:if test="${_search.pager.maxPages gt 1}">
		<br />
		Showing page ${_search.pager.selectedPage} of ${_search.pager.maxPages} pages:
	</c:if>
</p>

<div id="thumbnail-gallery">
                      
	<c:forEach items="${_search.results}" var="_result" varStatus="_stat">
		<c:set var="_teaser" value="${_result.teaser}" />
		<c:set var="_dateish" value="${site:toDateish(_result.extraStr1)}" />		
		<c:if test="${not empty _dateish.year}"><c:set var="_teaser">${_result.teaser} (${_dateish.deliveryString})</c:set></c:if>
		
		<div class="search-result" data-id="${_stat.count - 1}">
		
	    <img 
	    	class="thumbnail" 
	    	src="${_result.path}?view=thumbnail"
	    	data-slide-src="${_result.path}"
	    	data-type="${_result.type}"
	    	data-id="${_stat.count - 1}"
	    	/>
	    
	    <div class="search-result-info hide">
				<p class="heading">${_result.title}</p>
				<p class="caption">${_teaser}</p>
			</div>
    
    </div>
    	
	</c:forEach>
	
</div>

<pho:searchResultsPager urlPrefix="${urlPrefix}" />

<div id="modal">
  <span class="open-slide-info cursor"><i class="fas fa-info"></i></span>
  <span class="close-modal cursor" onclick="closeModal()">&times;</span>
  
  <div class="slide-set">

		<c:forEach items="${_search.results}" var="_result" varStatus="_stat">
			<c:set var="_teaser" value="${_result.teaser}" />
			<c:set var="_dateish" value="${site:toDateish(_result.extraStr1)}" />		
			<c:if test="${not empty _dateish.year}"><c:set var="_teaser">${_result.teaser} (${_dateish.deliveryString})</c:set></c:if>
			
			<div class="slide-wrapper" data-type="${_result.type}" data-id="${_stat.count - 1}">
			
				<c:choose><c:when test="${_result.type eq 'Photo JPG'}">
		    	<img class="slide image" data-id="${_stat.count - 1}" />
		    </c:when><c:when test="${_result.type eq 'Video MP4'}">
		    	<video class="slide video" data-id="${_stat.count - 1}" controls>
		    	</video>
		    </c:when></c:choose>
		    
		    <div class="slide-info">
					<p class="heading">${_result.title} <span class="close-slide-info cursor">&times;</span></p>
					<p class="caption">${_teaser}</p>
				</div>
	    
	    </div>
	    	
		</c:forEach>
		    
  </div>
  
	<a class="prev cursor" data-inc="-1">&#10094;</a>
	<a class="next cursor" data-inc="1">&#10095;</a>

</div>

<script>
	$(function() {
		_thumbnails = $(".search-result img")
		_slides = $(".slide-set .slide")
		assignUIBehaviours()
	});
</script>

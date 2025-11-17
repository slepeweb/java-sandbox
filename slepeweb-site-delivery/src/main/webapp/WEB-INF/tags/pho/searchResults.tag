<%@ tag %><%@ 
	include file="/WEB-INF/jsp/tagDirectives.jsp" %><%@ 
	attribute name="urlPrefix" required="true" rtexprvalue="true" %>

<gen:debug><!-- tags/pho/searchResults.tag --></gen:debug>

<div id="search-results">
	<p><strong>Found ${_search.totalHits} results for your search terms &quot;${_params.searchText}&quot;</strong></p>
	<p class="right"><a href="/">Search again <i class="fa-solid fa-magnifying-glass"></i></a></p>
		
	<c:if test="${_search.pager.maxPages gt 1}">
		<p>Showing page ${_search.pager.selectedPage} of ${_search.pager.maxPages} pages:</p>
	</c:if>
	
	<pho:searchResultsPager urlPrefix="${urlPrefix}" />
</div>

<div id="thumbnail-gallery">
                      
	<c:forEach items="${_search.results}" var="_result" varStatus="_stat">
	
		<%-- Creates request-scope vars for _src, _heading and _teaser, depending on item accessibility --%>
		<pho:searchResult result="${_result}" />
		
		<div class="search-result" data-id="${_stat.count - 1}">
		
	    <img 
	    	class="thumbnail" 
	    	src="${_src}?view=thumbnail"
	    	data-slide-src="${_src}"
	    	data-type="${_result.type}"
	    	data-id="${_stat.count - 1}"
	    	data-itemid="${_result.id}"
	    	data-origid="${_result.origId}"
	    	/>
	    
	    <c:if test="${_result.type eq 'Movie MP4'}">
	    	<p class="video-icon"><i class="fa-solid fa-film"></i></p>
	    </c:if>
	    
	    <div class="search-result-info hide">
				<p class="heading">${_heading}</p>
				<p class="caption">${_teaser}</p>
				<pho:captionTagList list="${_result.tags}" />
				
				<%--TODO: styling reqd --%>
				<p class="caption">[${_result.origId}]</p>
			</div>
				
    </div>
    	
	</c:forEach>
	
</div>

<%-- This is the modal which displays the full size image / video player --%>
<div id="modal">
  <span class="open-slide-info cursor"><i class="fas fa-info"></i></span>
  <span class="close-modal cursor" onclick="closeModal()">&times;</span>
  
  <div class="slide-set">

		<c:forEach items="${_search.results}" var="_result" varStatus="_stat">
			
			<%-- Creates request-scope vars for _src, _heading and _teaser, depending on item accessibility --%>
			<pho:searchResult result="${_result}" />
			
			<div class="slide-wrapper" data-type="${_result.type}" data-id="${_stat.count - 1}">
			
					<c:choose><c:when test="${_result.type eq 'Photo JPG' or not _result.accessible}">
			    	<img class="slide image" data-id="${_stat.count - 1}" />
			    </c:when><c:when test="${_result.type eq 'Movie MP4'}">
			    	<video class="slide video" data-id="${_stat.count - 1}" controls>
			    	</video>
			    </c:when></c:choose>
			    
			    <c:set var="_relatedMedia" value="${site:json2SolrCmsDoc(_result.extraStr2)}" />
			    
			    <div class="slide-info">
						<p class="heading">${_heading} <span class="close-slide-info cursor">&times;</span></p>
						<p class="caption">${_teaser}</p>
						<pho:captionTagList list="${_result.tags}" />
	
						<c:if test="${not empty _relatedMedia}">
							<div class="related-media">
								<p>
									<a href="?view=related&id=${_result.id}" target="_blank">! Related media !</a>
								</p>
							</div>
						</c:if>					
					</div>
										
		  </div>
	    	
		</c:forEach>
		    
  </div>
  
	<a class="prev cursor" data-inc="-1">&#10094;</a>
	<a class="next cursor" data-inc="1">&#10095;</a>

</div>
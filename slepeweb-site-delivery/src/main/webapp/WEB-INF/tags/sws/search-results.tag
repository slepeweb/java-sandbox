<%@ tag %><%@ include file="/WEB-INF/jsp/common/tagDirectives.jsp" %>

<gen:debug><!-- tags/sws/search-results.tag --></gen:debug>

<c:choose><c:when test="${not _searchResults.error}">
	<p>Your search for '${_searchResults.params.searchText}' found ${_searchResults.totalHits} results.<br /> 
			Showing page ${_searchResults.params.pageNum} of ${_searchResults.pager.numPages}:</p>
	<hr />
	
	<c:forEach items="${_searchResults.results}" var="_result" varStatus="_stat">
	    <div class="search-results-div">
	    		<h3>${_searchResults.params.start + _stat.count}. 
	    				<a href="${_result.path}">${_result.title}</a></h3>
        	<p>${_result.teaser}</p>
        	<hr />
			</div>
	</c:forEach>
	
	<%-- Paging controls --%>
	<c:if test="${_searchResults.pager.numPages gt 1}">
		<div class="search-results-pager">			
			<div class="solr-page heading">Select another page:</div>			
			<sw:solr-page-link link="${_searchResults.pager.previous}" />
			
			<c:forEach items="${_searchResults.pager.pages}" var="_page">
				<sw:solr-page-link link="${_page}" />
			</c:forEach>
			
			<sw:solr-page-link link="${_searchResults.pager.next}" />			
		</div>
	</c:if>
	
</c:when><c:when test="${_searchResults.error and not empty _searchResults.params.searchText}">
	<h3>No items matching your search criteria.</h3>
	<p>${_searchResults.message}</p>
</c:when><c:otherwise>
	<p>${_searchResults.message}</p>
</c:otherwise></c:choose>

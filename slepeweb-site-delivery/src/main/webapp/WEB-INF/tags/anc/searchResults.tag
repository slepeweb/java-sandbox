<%@ tag %><%@ 
	include file="/WEB-INF/jsp/common/tagDirectives.jsp" %><%@ 
	attribute name="urlPrefix" required="true" rtexprvalue="true" %>

<gen:debug><!-- tags/anc/searchResults.tag --></gen:debug>

<p>
	<strong>Found ${_search.totalHits} results for your search terms: [${_params.searchText}].</strong>
	
	<c:if test="${_search.pager.maxPages gt 1}">
		<br />
		Showing page #${_search.pager.selectedPage} of results:
	</c:if>
</p>

<%-- Table of search results --%>
<c:forEach items="${_search.results}" var="_result" varStatus="stat">
	<div class="search-result">
		<p class="sr-header">${_search.pager.startResultId + stat.count - 1}) <a href="${_result.path}">${_result.title}</a></p>
		<p class="sr-teaser">${_result.teaser}</p>
	</div>
</c:forEach>

<%-- Menu for selecting other pages --%>
<c:if test="${_search.pager.visible}">
	<p id="search-results-pager">
		<span class="choose-page">Choose another page: <br /></span>
		
		<c:if test="${_search.pager.previous}">
			<c:if test="${_search.pager.selectedPage gt 2}">
				<span class="pagelink arrow left" data-page="${_search.pager.previousBlock}" title="Previous block"><i 
					class="fas fa-angle-double-left"></i></span>
			</c:if>
			<span class="pagelink arrow left" data-page="${_search.pager.previousSelection}" title="Previous page"><i 
					class="fas fa-angle-left"></i>&nbsp;Previous page</span>
		</c:if>
					
		<c:forEach items="${_search.pager.navigation}" var="_pagenum">
			<c:set var="_selected" value="" />
			<c:set var="_helpText">Go to page ${_pagenum}</c:set>
			<c:if test="${_pagenum eq _search.pager.selectedPage}">
				<c:set var="_selected" value="selected" />
				<c:set var="_helpText">You are on page ${_pagenum}</c:set>
			</c:if>
						
			<span class="pagelink ${_selected}" data-page="${_pagenum}" title="${_helpText}">${_pagenum}</span>
		</c:forEach>
		
		<c:if test="${_search.pager.next}">
			<span class="pagelink arrow right" data-page="${_search.pager.nextSelection}" title="Next page">Next&nbsp;<i 
				class="fas fa-angle-right"></i></span>
					
			<c:if test="${_search.pager.selectedPage lt (_search.pager.maxPages - 2)}">
				<span class="pagelink arrow right" data-page="${_search.pager.nextBlock}" title="Next block"><i 
					class="fas fa-angle-double-right"></i></span>
			</c:if>
		</c:if>
	</p>
	
	<form id="page-selector" class="hidden" action="${urlPrefix}" method="post">
		<input type="hidden" name="searchtext" value="${_params.searchText}" />
		<input type="hidden" name="page" value="1" />
	</form>
	
	<script>
		$(function() {
			$(".pagelink").click(function() {
				if (! $(this).attr("class").match(/selected/) > 0) {
					$("input[name='page']").val($(this).attr("data-page"));
					$("#page-selector").submit();
				}
			});
		});
	</script>
</c:if>
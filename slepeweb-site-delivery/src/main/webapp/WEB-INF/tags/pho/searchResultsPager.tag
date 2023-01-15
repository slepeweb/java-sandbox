<%@ tag %><%@ 
	include file="/WEB-INF/jsp/common/tagDirectives.jsp" %><%@ 
	attribute name="urlPrefix" required="true" rtexprvalue="true" %>

<gen:debug><!-- tags/pho/searchResultsPager.tag --></gen:debug>

<c:if test="${_search.pager.visible}">
	<p id="search-results-pager" class="right">
		<span class="choose-page">Page: </span>
		
		<c:if test="${_search.pager.previous}">
			<c:if test="${_search.pager.selectedPage gt 2}">
				<span class="pagelink arrow left" data-page="${_search.pager.previousBlock}" title="Previous block"><i 
					class="fas fa-angle-double-left"></i></span>
			</c:if>
			<span class="pagelink arrow left" data-page="${_search.pager.previousSelection}" title="Previous page"><i 
					class="fas fa-angle-left"></i></span>
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
			<span class="pagelink arrow right" data-page="${_search.pager.nextSelection}" title="Next page"><i 
				class="fas fa-angle-right"></i></span>
					
			<c:if test="${_search.pager.selectedPage lt (_search.pager.maxPages - 2)}">
				<span class="pagelink arrow right" data-page="${_search.pager.nextBlock}" title="Next block"><i 
					class="fas fa-angle-double-right"></i></span>
			</c:if>
		</c:if>
	</p>
	
	<form id="page-selector" class="hidden" action="${urlPrefix}" method="post">
		<input type="hidden" name="searchtext" value="${_params.searchText}" />
		<input type="hidden" name="from" value="${_params.from}" />
		<input type="hidden" name="to" value="${_params.to}" />
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

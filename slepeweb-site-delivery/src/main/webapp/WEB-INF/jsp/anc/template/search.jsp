<%@ 
	include file="/WEB-INF/jsp/common/pageDirectives.jsp" %><%@ 
	include file="/WEB-INF/jsp/common/tagDirectives.jsp" %>
		
<c:set var="_extraInpageCss" scope="request">
	.pagelink {
		margin-right: 2em;
		color: #24604e;
		cursor: pointer;
	}
	
	.pagelink:hover {
		opacity: 0.5;
	}

	.pagelink.selected {
		text-decoration: underline;
		cursor: default;
		opacity: 1.0;
	}
	.search-summary {
		margin-bottom: 1.5em;
	}
	
	#search-results-pager {
		margin-top: 1.5em;
	}
	
	.choose-page, .teaser {
		font-size: 0.9em;
	}
	
</c:set>

<anc:standardLayout>
	<gen:debug><!-- jsp/anc/template/search.jsp --></gen:debug>
	
	<div class="col-2-3 primary-col">	
		<h2>${_item.fields.heading}</h2>
	
		<c:choose><c:when test="${fn:length(_search.results) > 0}">
			<anc:searchResults urlPrefix="${_item.url}" />			
		</c:when><c:otherwise>
			<p>No results found for your search terms [${_params.searchText}]. <br /> Please try again.</p>
		</c:otherwise></c:choose>
	</div>
			
</anc:standardLayout>

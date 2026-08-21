<%@ tag %><%@ include file="/WEB-INF/jsp/tagDirectives.jsp" %>

<div id="search-bar">
	<span>Search: </span>
	<div class="search-comps">
		<input type="text" name="searchtext" value="${_params.searchText}" placeholder="Enter search terms" />
		<button type="button"><span><i class="fa fa-search"></i></span></button>	
	</div>			
		
	<div id="undo-redo">
		<div id="undo-icon" title="">
			<i class="fa-solid fa-rotate-left fa-2x"></i>
		</div>
		
		<div id="redo-icon" title="">
			<i class="fa-solid fa-rotate-right fa-2x"></i>
		</div>
		
		<div><!--  Empty div --></div>
		
	</div>
</div>

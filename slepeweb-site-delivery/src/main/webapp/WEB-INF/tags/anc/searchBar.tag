<%@ tag %><%@ include file="/WEB-INF/jsp/common/tagDirectives.jsp" %>

<gen:debug><!-- tags/anc/searchBar.tag --></gen:debug>

<form id="search-bar" action="/${_item.language}/search" method="post" 
	enctype="application/x-www-form-urlencoded" accept-charset="utf-8">

	<div>
		<input type="text" name="searchtext" value="${_params.searchText}"
			placeholder="Enter search terms here" />&nbsp;&nbsp;<button 
				type="submit"><span><i class="fa fa-search"></i></span></button>
				
		<input type="hidden" name="page" value="1" />
	</div>
</form>

<script>
	$(function() {
		$("#search-bar span").click(function() {
			$("#search-bar").submit();
		});
	});
</script>
